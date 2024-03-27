package works.nuty.calcite.widget;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import works.nuty.calcite.widget.suggestion.ListSuggestionWindow;
import works.nuty.calcite.widget.suggestion.SuggestionWindow;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CalciteInputSuggestor {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style ERROR_STYLE;
    private static final Style INFO_STYLE;
    private static final List<Style> HIGHLIGHT_STYLES;

    static {
        ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
        INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
        Stream<Formatting> HIGHLIGHT_COLORS = Stream.of(Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD);
        HIGHLIGHT_STYLES = HIGHLIGHT_COLORS.map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
    }

    public final MinecraftClient client;
    public final TextFieldWidget textField;
    public final TextRenderer textRenderer;
    public final int inWindowIndexOffset;
    public final int maxSuggestionSize;
    public final boolean chatScreenSized;
    public final int color;
    public final Screen owner;
    public final List<OrderedText> messages = Lists.newArrayList();
    private final boolean slashOptional;
    private final boolean suggestingWhenEmpty;
    public boolean completingSuggestions;
    @Nullable
    public ParseResults<CommandSource> parse;
    @Nullable
    public SuggestionWindow window;
    private int x;
    private int width;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    private boolean windowActive;
    private boolean canLeave = true;

    public CalciteInputSuggestor(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, boolean slashOptional, boolean suggestingWhenEmpty, int inWindowIndexOffset, int maxSuggestionSize, boolean chatScreenSized, int color) {
        this.client = client;
        this.owner = owner;
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.slashOptional = slashOptional;
        this.suggestingWhenEmpty = suggestingWhenEmpty;
        this.inWindowIndexOffset = inWindowIndexOffset;
        this.maxSuggestionSize = maxSuggestionSize;
        this.chatScreenSized = chatScreenSized;
        this.color = color;
        textField.setRenderTextProvider(this::provideRenderText);
    }

    private static int getStartOfCurrentWord(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return 0;
        } else {
            int i = 0;

            Matcher matcher = WHITESPACE_PATTERN.matcher(input);
            while (matcher.find()) {
                i = matcher.end();
            }

            return i;
        }
    }

    private static OrderedText formatException(CommandSyntaxException exception) {
        Text text = Texts.toText(exception.getRawMessage());
        String string = exception.getContext();
        return string == null ? text.asOrderedText() : Text.translatable("command.context.parse_error", text, exception.getCursor(), string).asOrderedText();
    }

    @Nullable
    public static String getSuggestionSuffix(String original, String suggestion) {
        return suggestion.startsWith(original) ? suggestion.substring(original.length()) : null;
    }

    private static OrderedText highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
        List<OrderedText> list = Lists.newArrayList();
        int prevEnd = 0;
        int styleIndex = -1;
        CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();

        for (ParsedArgument<CommandSource, ?> argument : commandContextBuilder.getArguments().values()) {
            ++styleIndex;
            if (styleIndex >= HIGHLIGHT_STYLES.size()) {
                styleIndex = 0;
            }

            int start = Math.max(argument.getRange().getStart() - firstCharacterIndex, 0);
            if (start >= original.length()) {
                break;
            }

            int end = Math.min(argument.getRange().getEnd() - firstCharacterIndex, original.length());
            if (end > 0) {
                list.add(OrderedText.styledForwardsVisitedString(original.substring(prevEnd, start), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(start, end), HIGHLIGHT_STYLES.get(styleIndex)));
                prevEnd = end;
            }
        }

        if (parse.getReader().canRead()) {
            int currentCursor = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
            if (currentCursor < original.length()) {
                int errorEnd = Math.min(currentCursor + parse.getReader().getRemainingLength(), original.length());
                list.add(OrderedText.styledForwardsVisitedString(original.substring(prevEnd, currentCursor), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(currentCursor, errorEnd), ERROR_STYLE));
                prevEnd = errorEnd;
            }
        }

        list.add(OrderedText.styledForwardsVisitedString(original.substring(prevEnd), INFO_STYLE));
        return OrderedText.concat(list);
    }

    public void setWindowActive(boolean windowActive) {
        this.windowActive = windowActive;
        if (!windowActive) {
            this.window = null;
        }

    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean hasWindow = this.window != null;
        if (hasWindow && this.window.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.owner.getFocused() != this.textField || keyCode != GLFW.GLFW_KEY_TAB || this.canLeave && !hasWindow) {
            return false;
        } else {
            this.show(true);
            return true;
        }
    }

    public boolean mouseScrolled(double amount) {
        return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.window != null && this.window.mouseClicked((int) mouseX, (int) mouseY, button);
    }

    public void show(boolean narrateFirstSuggestion) {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int maxWidth = 0;

                for (Suggestion suggestion : suggestions.getList()) {
                    maxWidth = Math.max(maxWidth, this.textRenderer.getWidth(suggestion.getText()));
                }

                int x = MathHelper.clamp(this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - maxWidth);
                int y = this.chatScreenSized ? this.owner.height - 12 : 72;
                if (false) {
                    // intended; add custom suggestion window
                } else {
                    this.window = new ListSuggestionWindow(this, Math.max(0, x), y, maxWidth, this.sortSuggestions(suggestions), narrateFirstSuggestion);
                }
            }
        }

    }

    public boolean isOpen() {
        return this.window != null;
    }

    public void clearWindow() {
        this.window = null;
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        int wordStart = getStartOfCurrentWord(string);
        String word = string.substring(wordStart).toLowerCase(Locale.ROOT);
        List<Suggestion> list = Lists.newArrayList();
        List<Suggestion> list2 = Lists.newArrayList();

        for (Suggestion suggestion : suggestions.getList()) {
            if (!suggestion.getText().startsWith(word) && !suggestion.getText().startsWith("minecraft:" + word)) {
                list2.add(suggestion);
            } else {
                list.add(suggestion);
            }
        }

        list.addAll(list2);
        return list;
    }

    public void refresh() {
        String string = this.textField.getText();
        if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
            this.parse = null;
        }

        if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
        }

        this.messages.clear();
        StringReader stringReader = new StringReader(string);
        boolean slashCommand = stringReader.canRead() && stringReader.peek() == '/';
        if (slashCommand) {
            stringReader.skip();
        }

        boolean isCommand = this.slashOptional || slashCommand;
        int cursor = this.textField.getCursor();
        int minSuggestionCursor;
        if (isCommand) {
            assert this.client.player != null;
            CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
            }

            minSuggestionCursor = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
            if (cursor >= minSuggestionCursor && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.showCommandSuggestions();
                    }
                });
            }
        } else {
            String string2 = string.substring(0, cursor);
            minSuggestionCursor = getStartOfCurrentWord(string2);
            assert this.client.player != null;
            Collection<String> collection = this.client.player.networkHandler.getCommandSource().getChatSuggestions();
            this.pendingSuggestions = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string2, minSuggestionCursor));
        }

    }

    private void showCommandSuggestions() {
        boolean hasException = false;
        if (this.textField.getCursor() == this.textField.getText().length()) {
            assert this.pendingSuggestions != null;
            assert this.parse != null;
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
                int unknownLiteralCount = 0;

                for (Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
                    CommandSyntaxException commandSyntaxException = entry.getValue();
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++unknownLiteralCount;
                    } else {
                        this.messages.add(formatException(commandSyntaxException));
                    }
                }

                if (unknownLiteralCount > 0) {
                    this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else {
                assert this.parse != null;
                if (this.parse.getReader().canRead()) {
                    hasException = true;
                }
            }
        }

        this.x = 0;
        this.width = this.owner.width;
        if (this.messages.isEmpty() && !this.showUsages(Formatting.GRAY) && hasException) {
            this.messages.add(formatException(Objects.requireNonNull(CommandManager.getException(this.parse))));
        }

        this.window = null;
        if (this.windowActive && this.client.options.getAutoSuggestions().getValue()) {
            this.show(false);
        }

    }

    private boolean showUsages(Formatting formatting) {
        assert this.parse != null;
        CommandContextBuilder<CommandSource> commandContextBuilder = this.parse.getContext();
        SuggestionContext<CommandSource> suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
        assert this.client.player != null;
        Map<CommandNode<CommandSource>, String> usages = this.client.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, this.client.player.networkHandler.getCommandSource());
        List<OrderedText> list = Lists.newArrayList();
        int maxUsageWidth = 0;
        Style style = Style.EMPTY.withColor(formatting);

        for (Map.Entry<CommandNode<CommandSource>, String> usage : usages.entrySet()) {
            if (!(usage.getKey() instanceof LiteralCommandNode)) {
                list.add(OrderedText.styledForwardsVisitedString(usage.getValue(), style));
                maxUsageWidth = Math.max(maxUsageWidth, this.textRenderer.getWidth(usage.getValue()));
            }
        }

        if (!list.isEmpty()) {
            this.messages.addAll(list);
            this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - maxUsageWidth);
            this.width = maxUsageWidth;
            return true;
        } else {
            return false;
        }
    }

    private OrderedText provideRenderText(String original, int firstCharacterIndex) {
        return this.parse != null ? highlight(this.parse, original, firstCharacterIndex) : OrderedText.styledForwardsVisitedString(original, Style.EMPTY);
    }

    public void render(DrawContext context, int screenY, int mouseX, int mouseY) {
        if (!this.tryRenderWindow(context, screenY, mouseX, mouseY)) {
            this.renderMessages(context, screenY);
        }
    }

    public boolean tryRenderWindow(DrawContext context, int screenY, int mouseX, int mouseY) {
        if (this.window != null) {
            this.window.render(context, screenY, mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    public void renderMessages(DrawContext context, int screenY) {
        // todo rollback to default behavior
        int row = 0;

        for (Iterator<OrderedText> messageIterator = this.messages.iterator(); messageIterator.hasNext(); ++row) {
            OrderedText message = messageIterator.next();
            int y = calculateMessageY(screenY);
            context.fill(this.x - 4, y, this.x + this.width - 2, y + 12, this.color);
            context.drawTextWithShadow(this.textRenderer, message, this.x - 3, y + 2, -1);
        }
    }

    private int calculateMessageY(int y) {
        return (this.owner.height / 2 - 6 < y
            ? y - 3 - this.messages.size() * 12
            : (y + 24) - (this.textField.drawsBackground() ? 1 : 0));
    }
}

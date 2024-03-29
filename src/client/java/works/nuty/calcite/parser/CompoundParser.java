package works.nuty.calcite.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CompoundParser<T> extends DefaultParser {
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.value"));
    private final Set<String> keys = new HashSet<>();
    private final Map<String, List<Option>> options = new HashMap<>();

    public CompoundParser(DefaultParser parent) {
        super(parent);
        registerOptions();
    }

    public CompoundParser(StringReader reader) {
        super(reader);
        registerOptions();
    }

    public void registerOptions() {
    }

    @Override
    public void parse() throws CommandSyntaxException {
        suggest(this::suggestOpenCompound);
        reader().expect('{');
        suggest(this::suggestOptionKey);
        reader().skipWhitespace();
        while (reader().canRead() && reader().peek() != '}') {
            int cursor = reader().getCursor();
            String key = reader().readString();
            ValueHandler handler = getValueHandler(key, reader().getCursor());

            if (key.isEmpty() || this.hasPotentialOptionKey(key)) {
                reader().setCursor(cursor);
                throw EXPECTED_KEY.createWithContext(reader());
            }
            this.keys.add(key);
            reader().skipWhitespace();
            cursor = reader().getCursor();

            if (!reader().canRead() || reader().peek() != ':') {
                reader().setCursor(cursor);
                suggest(this::suggestColon);
                throw EXPECTED_VALUE.createWithContext(reader());
            }
            reader().skip();
            reader().skipWhitespace();

            suggestNothing();
            handler.handle(this);
            reader().skipWhitespace();
            cursor = reader().getCursor();

            suggest(this::suggestOptionsNextOrClose);
            if (!reader().canRead()) continue;
            if (reader().peek() == ',') {
                reader().skip();
                reader().skipWhitespace();
                suggest(this::suggestOptionKey);
                continue;
            }
            if (reader().peek() == '}') break;
            reader().setCursor(cursor);
            throw EXPECTED_KEY.createWithContext(reader());
        }
        reader().expect('}');
        suggestNothing();
    }

    private CompletableFuture<Suggestions> suggestOpenCompound(SuggestionsBuilder builder) {
        builder.suggest("{");
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder builder) {
        builder.suggest(",");
        builder.suggest("}");
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionKey(SuggestionsBuilder builder) {
        String key = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (var optionList : getOptions().entrySet()) {
            for (var option : optionList.getValue()) {
                if (this.hasKey(optionList.getKey()) || !option.shouldSuggest() || !optionList.getKey().toLowerCase(Locale.ROOT).startsWith(key))
                    continue;
                builder.suggest(optionList.getKey() + ":");
            }
        }
        return builder.buildFuture();
    }

    private boolean hasPotentialOptionKey(String key) {
        boolean value = false;
        for (var optionList : getOptions().entrySet()) {
            for (var option : optionList.getValue()) {
                if (!option.shouldSuggest()) continue;
                if (optionList.getKey().equals(key)) return false;
                if (option.shouldSuggest()
                    && optionList.getKey().toLowerCase(Locale.ROOT).startsWith(key.toLowerCase(Locale.ROOT))) {
                    value = true;
                }
            }
        }
        return value;
    }

    private CompletableFuture<Suggestions> suggestColon(SuggestionsBuilder builder) {
        builder.suggest(":");
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        return super.getSuggestions().apply(builder.createOffset(this.reader().getCursor()));
    }

    public boolean hasKey(String key) {
        return keys.contains(key);
    }

    public void register(String key, Option option) {
        if (options.containsKey(key)) {
            options.get(key).add(option);
        } else {
            ArrayList<Option> list = new ArrayList<>();
            list.add(option);
            options.put(key, list);
        }
    }

    public Map<String, List<Option>> getOptions() {
        return options;
    }

    public ValueHandler getValueHandler(String key, int cursor) {
        if (options.containsKey(key)) {
            List<Option> options = getOptions().get(key);
            for (Option option : options) {
                if (option != null) {
                    if (option.shouldSuggest()) {
                        return option.getValueHandler();
                    }
                }
            }
        }

        this.reader().setCursor(cursor);

        return parser -> new AnyParser(parser).parse();
    }

    public interface ValueHandler {
        void handle(DefaultParser parser) throws CommandSyntaxException;
    }

    public interface Option {
        boolean shouldSuggest();
        ValueHandler getValueHandler();
    }
}

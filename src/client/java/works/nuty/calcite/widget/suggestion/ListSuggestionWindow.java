package works.nuty.calcite.widget.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.glfw.GLFW;
import works.nuty.calcite.widget.CalciteInputSuggestor;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ListSuggestionWindow implements SuggestionWindow {
    public final Rect2i area;
    public final List<Suggestion> suggestions;
    private final String typedText;
    final private CalciteInputSuggestor suggestor;
    boolean completed;
    private int inWindowIndex;
    private int selection;
    private Vec2f mouse;
    private int lastNarrationIndex;

    public ListSuggestionWindow(CalciteInputSuggestor suggestor, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion) {
        this.suggestor = suggestor;
        this.mouse = Vec2f.ZERO;
        int areaX = x - (this.suggestor.textField.drawsBackground() ? 0 : 1);
        int areaY = this.suggestor.chatScreenSized ? y - 3 - Math.min(suggestions.size(), this.suggestor.maxSuggestionSize) * 12 : y - (this.suggestor.textField.drawsBackground() ? 1 : 0);
        this.area = new Rect2i(areaX, areaY, width + 1, Math.min(suggestions.size(), this.suggestor.maxSuggestionSize) * 12);
        this.typedText = this.suggestor.textField.getText();
        this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
        this.suggestions = suggestions;
        this.select(0);
    }

    public void render(DrawContext context, int screenY, int mouseX, int mouseY) {
        this.area.setY(calculateSuggestionY(screenY));
        int rowSize = Math.min(this.suggestions.size(), this.suggestor.maxSuggestionSize);
        boolean bl = this.inWindowIndex > 0;
        boolean bl2 = this.suggestions.size() > this.inWindowIndex + rowSize;
        boolean bl3 = bl || bl2;
        boolean bl4 = this.mouse.x != (float) mouseX || this.mouse.y != (float) mouseY;
        if (bl4) {
            this.mouse = new Vec2f((float) mouseX, (float) mouseY);
        }

        if (bl3) {
            context.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), this.suggestor.color);
            context.fill(this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, this.suggestor.color);
            int k;
            if (bl) {
                for (k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        context.fill(this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
                    }
                }
            }

            if (bl2) {
                for (k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        context.fill(this.area.getX() + k, this.area.getY() + this.area.getHeight(), this.area.getX() + k + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                    }
                }
            }
        }

        boolean bl5 = false;

        for (int l = 0; l < rowSize; ++l) {
            Suggestion suggestion = this.suggestions.get(l + this.inWindowIndex);
            context.fill(this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, this.suggestor.color);
            if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY > this.area.getY() + 12 * l && mouseY < this.area.getY() + 12 * l + 12) {
                if (bl4) {
                    this.select(l + this.inWindowIndex);
                }

                bl5 = true;
            }

            context.drawTextWithShadow(this.suggestor.textRenderer, suggestion.getText(), this.area.getX() + 1, this.area.getY() + 2 + 12 * l, l + this.inWindowIndex == this.selection ? -256 : -5592406);
        }

        if (bl5) {
            Message message = this.suggestions.get(this.selection).getTooltip();
            if (message != null) {
                context.drawTooltip(this.suggestor.textRenderer, Texts.toText(message), mouseX, mouseY);
            }
        }

    }

    public boolean mouseClicked(int x, int y, int button) {
        if (!this.area.contains(x, y)) {
            return false;
        } else {
            int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
            if (i >= 0 && i < this.suggestions.size()) {
                this.select(i);
                this.complete();
            }

            return true;
        }
    }

    public boolean mouseScrolled(double amount) {
        int i = (int) (this.suggestor.client.mouse.getX() * (double) this.suggestor.client.getWindow().getScaledWidth() / (double) this.suggestor.client.getWindow().getWidth());
        int j = (int) (this.suggestor.client.mouse.getY() * (double) this.suggestor.client.getWindow().getScaledHeight() / (double) this.suggestor.client.getWindow().getHeight());
        if (this.area.contains(i, j)) {
            this.inWindowIndex = MathHelper.clamp((int) ((double) this.inWindowIndex - amount), 0, Math.max(this.suggestions.size() - this.suggestor.maxSuggestionSize, 0));
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP || (keyCode == GLFW.GLFW_KEY_P && (modifiers & GLFW.GLFW_MOD_CONTROL) > 0)) {
            this.scroll(-1);
            this.completed = false;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || (keyCode == GLFW.GLFW_KEY_N && (modifiers & GLFW.GLFW_MOD_CONTROL) > 0)) {
            this.scroll(1);
            this.completed = false;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER && !this.suggestor.chatScreenSized) {
            this.complete();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_TAB) {
            if (this.completed) {
                this.scroll(Screen.hasShiftDown() ? -1 : 1);
            }

            this.complete();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.suggestor.clearWindow();
            this.suggestor.textField.setSuggestion(null);
            return true;
        } else {
            return false;
        }
    }

    public void scroll(int offset) {
        this.select(this.selection + offset);
        int i = this.inWindowIndex;
        int j = this.inWindowIndex + this.suggestor.maxSuggestionSize - 1;
        if (this.selection < i) {
            this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - this.suggestor.maxSuggestionSize, 0));
        } else if (this.selection > j) {
            this.inWindowIndex = MathHelper.clamp(this.selection + this.suggestor.inWindowIndexOffset - this.suggestor.maxSuggestionSize, 0, Math.max(this.suggestions.size() - this.suggestor.maxSuggestionSize, 0)) + 1;
        }

    }

    public void select(int index) {
        this.selection = index;
        if (this.selection < 0) {
            this.selection += this.suggestions.size();
        }

        if (this.selection >= this.suggestions.size()) {
            this.selection -= this.suggestions.size();
        }

        Suggestion suggestion = this.suggestions.get(this.selection);
        this.suggestor.textField.setSuggestion(CalciteInputSuggestor.getSuggestionSuffix(this.suggestor.textField.getText(), suggestion.apply(this.typedText)));
        if (this.lastNarrationIndex != this.selection) {
            this.suggestor.client.getNarratorManager().narrate(this.getNarration());
        }

    }

    public void complete() {
        Suggestion suggestion = this.suggestions.get(this.selection);
        this.suggestor.completingSuggestions = true;
        this.suggestor.textField.setText(suggestion.apply(this.typedText));
        int i = suggestion.getRange().getStart() + suggestion.getText().length();
        this.suggestor.textField.setSelectionStart(i);
        this.suggestor.textField.setSelectionEnd(i);
        this.select(this.selection);
        this.suggestor.completingSuggestions = false;
        this.completed = true;
    }

    Text getNarration() {
        this.lastNarrationIndex = this.selection;
        Suggestion suggestion = this.suggestions.get(this.selection);
        Message message = suggestion.getTooltip();
        return message != null ? Text.translatable("narration.suggestion.tooltip", this.selection + 1, this.suggestions.size(), suggestion.getText(), Text.of(message)) : Text.translatable("narration.suggestion", this.selection + 1, this.suggestions.size(), suggestion.getText());
    }

    private int calculateSuggestionY(int y) {
        if (this.suggestor.owner.height / 2 - 6 < y) {
            assert this.suggestor.window != null;
            return y - 3 - Math.min(this.suggestions.size(), this.suggestor.maxSuggestionSize) * 12;
        } else {
            return (y + 24) - (this.suggestor.textField.drawsBackground() ? 1 : 0);
        }
    }

}

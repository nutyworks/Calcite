package works.nuty.calcite.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class AutoActivateButtonWidget extends PressableWidget {
    private final TextRenderer textRenderer;
    private final Callback callback;
    public boolean value = false;
    public AutoActivateButtonWidget(TextRenderer textRenderer, int x, int y, int width, int height, Callback callback) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.callback = callback;
    }

    @Override
    public void onPress() {
        this.value = !this.value;
        this.callback.onValueChange(this.value);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getRight(), getBottom(), value ? 0xFFC0C0C0 : 0xFF303030);
        context.drawBorder(getX(), getY(), getWidth(), getHeight(), isFocused() ? 0xFFFFFFFF : 0xFFA0A0A0);

        if (getX() <= mouseX && mouseX <= getRight() && getY() <= mouseY && mouseY <= getBottom()) {
            context.drawTooltip(this.textRenderer, Text.translatable(value ? "advMode.mode.autoexec.bat" : "advMode.mode.redstoneTriggered"), mouseX, mouseY);
        }
    }

    public interface Callback {
        void onValueChange(boolean value);
    }
}

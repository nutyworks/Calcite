package works.nuty.calcite.widget;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModeButtonWidget extends ClickableWidget {
    private final TextRenderer textRenderer;
    private final Callback callback;
    public CommandBlockBlockEntity.Type mode = CommandBlockBlockEntity.Type.REDSTONE;
    public boolean conditional = false;
    public ModeButtonWidget(TextRenderer textRenderer, int x, int y, int width, int height, Callback callback) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.callback = callback;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.clicked(mouseX, mouseY)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            if (button == 0) {
                this.onLeftClick();
            } else if (button == 1) {
                this.onRightClick();
            }
            return true;
        }
        return false;
    }

    public void onLeftClick() {
        if (Screen.hasShiftDown()) {
            this.mode = switch (this.mode) {
                case REDSTONE -> CommandBlockBlockEntity.Type.AUTO;
                case SEQUENCE -> CommandBlockBlockEntity.Type.REDSTONE;
                case AUTO -> CommandBlockBlockEntity.Type.SEQUENCE;
            };
        } else {
            this.mode = switch (this.mode) {
                case REDSTONE -> CommandBlockBlockEntity.Type.SEQUENCE;
                case SEQUENCE -> CommandBlockBlockEntity.Type.AUTO;
                case AUTO -> CommandBlockBlockEntity.Type.REDSTONE;
            };
        }
        this.callback.onModeChange(this.mode);
    }

    public void onRightClick() {
        this.conditional = !this.conditional;
        this.callback.onConditionalChange(this.conditional);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    private Identifier getIdentifier() {
        StringBuilder builder = new StringBuilder("textures/block/");
        switch (this.mode) {
            case REDSTONE -> {
            }
            case SEQUENCE -> builder.append("chain_");
            case AUTO -> builder.append("repeating_");
        }
        builder.append("command_block_");
        if (this.conditional) {
            builder.append("conditional");
        } else {
            builder.append("side");
        }
        builder.append(".png");

        return new Identifier("minecraft", builder.toString());
    }

    private List<Text> getTooltipMessage() {
        return List.of(
                Text.translatable(this.conditional ? "advMode.mode.conditional" : "advMode.mode.unconditional")
                        .append(" ")
                        .append(switch (this.mode) {
                            case SEQUENCE -> Text.translatable("advMode.mode.sequence");
                            case AUTO -> Text.translatable("advMode.mode.auto");
                            case REDSTONE -> Text.translatable("advMode.mode.redstone");
                        }),
                Text.translatable("key.mouse.left").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA8A8A8)))
                        .append(": ")
                        .append(Text.translatable("advMode.mode.redstone"))
                        .append("/")
                        .append(Text.translatable("advMode.mode.sequence"))
                        .append("/")
                        .append(Text.translatable("advMode.mode.auto")),
                Text.translatable("key.mouse.right").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA8A8A8)))
                        .append(": ")
                        .append(Text.translatable("advMode.mode.unconditional"))
                        .append("/")
                        .append(Text.translatable("advMode.mode.conditional"))
        );
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(this.getIdentifier(), getX(), getY(), 0, -1, getWidth(), getHeight(), 16, -64);

        if (getX() <= mouseX && mouseX <= getRight() && getY() <= mouseY && mouseY <= getBottom()) {
            context.drawTooltip(this.textRenderer, this.getTooltipMessage(), mouseX, mouseY);
        }
    }

    public interface Callback {
        void onModeChange(CommandBlockBlockEntity.Type mode);

        void onConditionalChange(boolean conditional);
    }
}

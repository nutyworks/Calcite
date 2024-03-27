package works.nuty.calcite.screen;

import com.mojang.brigadier.context.StringRange;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.nbt.NbtCompound;
import works.nuty.calcite.VerticalNbtTextFormatter;
import works.nuty.calcite.widget.CalciteTextFieldWidget;

public class CalciteNBTEditScreen extends Screen {
    final private Screen parent;
    final private NbtCompound nbt;
    final private CalciteTextFieldWidget textField;
    private StringRange range;

    protected CalciteNBTEditScreen(Screen parent, NbtCompound nbt, StringRange range, CalciteTextFieldWidget textField) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.nbt = nbt;
        this.range = range;
        this.textField = textField;
    }

    private void commitAndClose() {
        String originalText = textField.getText();
        textField.setText(originalText.substring(0, range.getStart()) + nbt.toString() + originalText.substring(range.getEnd()));
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTooltip(this.textRenderer, new VerticalNbtTextFormatter("  ", 0).apply(this.nbt), 1, 10);
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreenAndRender(this.parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // todo set this to false; temporarily set to true for testing.
        return true;
    }
}

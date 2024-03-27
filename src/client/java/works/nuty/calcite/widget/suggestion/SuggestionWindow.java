package works.nuty.calcite.widget.suggestion;

import net.minecraft.client.gui.DrawContext;

public interface SuggestionWindow {
    void render(DrawContext context, int screenY, int mouseX, int mouseY);

    boolean mouseClicked(int x, int y, int button);

    boolean mouseScrolled(double amount);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    void complete();
}

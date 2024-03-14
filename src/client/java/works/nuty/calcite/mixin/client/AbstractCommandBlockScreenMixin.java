package works.nuty.calcite.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.world.CommandBlockExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCommandBlockScreen.class)
public abstract class AbstractCommandBlockScreenMixin extends Screen {

	@Shadow protected TextFieldWidget consoleCommandTextField;

	@Shadow abstract CommandBlockExecutor getCommandExecutor();

	protected AbstractCommandBlockScreenMixin(Text title) {
		super(title);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return this.getCommandExecutor().getCommand().equals(this.consoleCommandTextField.getText());
	}
}
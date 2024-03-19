package works.nuty.calcite.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public abstract class SuggestionWindowMixin {
    @Shadow
    boolean completed;

    @Shadow
    public abstract void scroll(int offset);

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) > 0 && keyCode == GLFW.GLFW_KEY_N) {
            this.scroll(1);
            this.completed = false;
            cir.setReturnValue(true);
            cir.cancel();
        }
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) > 0 && keyCode == GLFW.GLFW_KEY_P) {
            this.scroll(-1);
            this.completed = false;
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}

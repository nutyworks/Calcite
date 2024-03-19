package works.nuty.calcite.mixin.client;

import com.mojang.brigadier.suggestion.Suggestion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public abstract class SuggestionWindowMixin {
    @Shadow
    boolean completed;

    @Shadow
    public abstract void scroll(int offset);

    @Shadow
    public abstract void complete();

    @Unique boolean chatScreenSized;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(ChatInputSuggestor chatInputSuggestor, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion, CallbackInfo ci) {
        this.chatScreenSized = ((ChatInputSuggestorFields) chatInputSuggestor).getChatScreenSized();
    }

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
        if (!chatScreenSized && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            this.complete();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}

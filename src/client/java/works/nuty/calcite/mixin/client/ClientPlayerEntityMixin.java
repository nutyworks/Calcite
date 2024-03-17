package works.nuty.calcite.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import works.nuty.calcite.screen.CalciteCommandScreen;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "openCommandBlockScreen", cancellable = true)
    public void openCommandBlockScreenMixin(CommandBlockBlockEntity commandBlock, CallbackInfo ci) {
        this.client.setScreen(new CalciteCommandScreen(commandBlock));
        ci.cancel();
    }
}

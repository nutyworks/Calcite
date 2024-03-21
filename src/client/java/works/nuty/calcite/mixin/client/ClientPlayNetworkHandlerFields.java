package works.nuty.calcite.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerFields {
    @Accessor
    DynamicRegistryManager.Immutable getCombinedDynamicRegistries();
}

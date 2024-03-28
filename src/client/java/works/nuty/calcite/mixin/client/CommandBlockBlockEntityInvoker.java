package works.nuty.calcite.mixin.client;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CommandBlockBlockEntity.class)
public interface CommandBlockBlockEntityInvoker {
    @Invoker("readNbt")
    void invokeReadNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);
}

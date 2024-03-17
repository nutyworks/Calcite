package works.nuty.calcite.mixin.client;

import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(DataQueryHandler.class)
public class DataQueryHandlerMixin {
    @Shadow
    private @Nullable Consumer<NbtCompound> callback;
    @Shadow
    private int expectedTransactionId;

    @Inject(at = @At("HEAD"), method = "handleQueryResponse", cancellable = true)
    public void queryBlockNbt(int transactionId, @Nullable NbtCompound nbt, CallbackInfoReturnable<Boolean> cir) {
        @Nullable Consumer<NbtCompound> previousCallback = this.callback;
        if (this.expectedTransactionId == transactionId && this.callback != null) {
            this.callback.accept(nbt);
            if (this.callback.equals(previousCallback)) {
                this.callback = null;
            }
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
        cir.cancel();
    }
}

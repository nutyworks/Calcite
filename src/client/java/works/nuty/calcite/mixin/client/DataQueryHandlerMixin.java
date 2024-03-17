package works.nuty.calcite.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(DataQueryHandler.class)
public abstract class DataQueryHandlerMixin {
    @Unique
    private final Map<Integer, Consumer<NbtCompound>> callbacks = new HashMap<>();
    @Shadow
    private int expectedTransactionId;
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Shadow
    protected abstract int nextQuery(Consumer<NbtCompound> callback);

    @Inject(at = @At("HEAD"), method = "handleQueryResponse", cancellable = true)
    public void handleQueryResponse(int transactionId, @Nullable NbtCompound nbt, CallbackInfoReturnable<Boolean> cir) {
        if (this.callbacks.containsKey(transactionId)) {
            this.callbacks.get(transactionId).accept(nbt);
            this.callbacks.remove(transactionId);
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "nextQuery", cancellable = true)
    public void nextQuery1(Consumer<NbtCompound> callback, CallbackInfoReturnable<Integer> cir) {
        ++this.expectedTransactionId;
        this.callbacks.put(this.expectedTransactionId, callback);
        cir.setReturnValue(this.expectedTransactionId);
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "queryBlockNbt", cancellable = true)
    public void queryBlockNbt(BlockPos pos, Consumer<NbtCompound> callback, CallbackInfo ci) {
        int i = this.nextQuery(callback);
        this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(i, pos));
        ci.cancel();
    }
}

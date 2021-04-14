package dev.semisol.quasarclient.module.tps.mixin;

import dev.semisol.quasarclient.module.dcol.Globals;
import dev.semisol.quasarclient.module.tps.TPS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(WorldTimeUpdateS2CPacket.class)
public class WorldTimeUpdateS2CPacketMixin {
    @Shadow
    private long time;
    @Inject(at = @At("TAIL"), method = "apply")
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        long cnt = System.nanoTime();
        boolean r = TPS.recvFirst;
        TPS.recvFirst = true;
        if (!r) {
            TPS.recv = cnt;
            TPS.ogt = time;
            return;
        }
        long timediff = this.time - TPS.ogt;
        if (timediff == 0) return;
        System.arraycopy(TPS.last30, 1, TPS.last30, 0, TPS.last30.length - 1);
        System.arraycopy(TPS.ls10, 0, TPS.ls10, 1, TPS.ls10.length - 1);
        System.arraycopy(TPS.ls10t, 1, TPS.ls10t, 0, TPS.ls10t.length - 1);
        double tps = TPS.calcTpsAdvanced((double) (cnt - TPS.recv), timediff);
        if (TPS.capOn){
            tps = Math.min(tps, 20);
        }
        TPS.ls10[0] = tps;
        TPS.last30[29] = tps;
        TPS.ls10t[29] = Arrays.stream(TPS.ls10).sum() / 10d;
        TPS.recv = cnt;
        TPS.ogt = time;
    }
}

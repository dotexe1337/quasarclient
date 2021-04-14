package dev.semisol.quasarclient.module.tps.mixin;

import dev.semisol.quasarclient.module.tps.TPS;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(GameJoinS2CPacket.class)
public class GameJoinS2CPacketMixin {
    @Inject(at = @At("TAIL"), method = "apply")
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        TPS.recvFirst = false;
        Arrays.fill(TPS.last30, 20d);
        Arrays.fill(TPS.ls10, 20d);
        Arrays.fill(TPS.ls10t, 20d);
    }
}

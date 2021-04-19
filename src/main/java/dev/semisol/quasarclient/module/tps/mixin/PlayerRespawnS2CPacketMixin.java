package dev.semisol.quasarclient.module.tps.mixin;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.semisol.quasarclient.module.tps.TPS;

@Mixin(PlayerRespawnS2CPacket.class)
public class PlayerRespawnS2CPacketMixin {
    @Inject(at = @At("TAIL"), method = "apply")
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        TPS.recvFirst = false;
    }
}

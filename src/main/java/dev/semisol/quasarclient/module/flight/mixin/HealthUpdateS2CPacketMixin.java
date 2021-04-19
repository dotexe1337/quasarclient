package dev.semisol.quasarclient.module.flight.mixin;

import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.module.flight.Flight;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HealthUpdateS2CPacket.class})
public class HealthUpdateS2CPacketMixin {
    @Inject(at = @At("TAIL"), method = "apply")
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        if (Flight.bypassing){
            Vec3d pos = QuasarClient.minecraft.player.getPos();
            Utils.sendPosUpdate(pos.x, pos.y + 0.5, pos.z, true, true);
            Flight.bypassing = false;
            QuasarClient.minecraft.player.abilities.flying = true;
            QuasarClient.minecraft.player.abilities.allowFlying = true;
            QuasarClient.minecraft.player.abilities.setFlySpeed(0.032f);
        }
    }
}

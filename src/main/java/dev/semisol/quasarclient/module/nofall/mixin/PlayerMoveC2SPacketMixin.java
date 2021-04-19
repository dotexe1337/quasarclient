package dev.semisol.quasarclient.module.nofall.mixin;

import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.module.flight.Flight;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerMoveC2SPacket.class})
public class PlayerMoveC2SPacketMixin {
    @Shadow
    protected boolean onGround;

    @Inject(at = @At("HEAD"), method = "write")
    public void init(PacketByteBuf b, CallbackInfo ci){
        if (ModuleRegistry.isOn(ModuleRegistry.getModule("nofall")) && !Flight.bypassing && !Utils.damageInProgress) this.onGround = true;
    }
}

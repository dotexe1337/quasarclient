package dev.semisol.quasarclient.module.dcol.mixin;

import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameJoinS2CPacket.class)
public class GameJoinS2CPacketMixin {
    @Inject(at = @At("TAIL"), method = "apply")
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        Globals.hashseed = this.getSha256Seed();
    }
    @Shadow
    public long getSha256Seed(){return 0l;};
}

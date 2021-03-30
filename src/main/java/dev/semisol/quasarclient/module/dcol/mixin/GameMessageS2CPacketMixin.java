package dev.semisol.quasarclient.module.dcol.mixin;

import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.network.MessageType;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMessageS2CPacket.class)
public class GameMessageS2CPacketMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        if (Globals.inProg && this.location == MessageType.SYSTEM && Globals.ticks < 0){
            Globals.currentCollected.add(this.message.getString());
            Globals.gotMessage = true;
        }
    }
    @Shadow
    public Text message;
    @Shadow
    public MessageType location;

}

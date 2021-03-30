package dev.semisol.quasarclient.module.autocf.mixin;

import dev.semisol.quasarclient.module.autocf.AutoCF;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenScreenS2CPacket.class)
public class OpenScreenS2CPacketMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        AutoCF.cn = this.name.getString();
    }
    @Shadow
    private int syncId;
    @Shadow
    private int screenHandlerId;
    @Shadow
    private Text name;
}

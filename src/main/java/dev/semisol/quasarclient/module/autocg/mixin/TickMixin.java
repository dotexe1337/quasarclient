/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: TickMixin
# Created by constantin at 19:55, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.autocg.mixin;

import dev.semisol.quasarclient.module.autocg.AutoCG;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerEntity.class)
public class TickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (AutoCG.ticks > -1 && AutoCG.on){
            AutoCG.ticks--;
            if (AutoCG.ticks < 0 && MinecraftClient.getInstance().player != null){
                ChatMessageC2SPacket cmp = new ChatMessageC2SPacket(AutoCG.word);
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(cmp);
            }
        }
    }
}

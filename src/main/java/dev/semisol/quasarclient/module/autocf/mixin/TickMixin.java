package dev.semisol.quasarclient.module.autocf.mixin;

import dev.semisol.quasarclient.module.autocf.AutoCF;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerEntity.class)
public class TickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (AutoCF.ticks > -1 && AutoCF.on && AutoCF.cn.equals("§lCOIN FLIP")){
            AutoCF.ticks--;
            try {
                if (AutoCF.ticks < 0 && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.currentScreenHandler != null && MinecraftClient.getInstance().player.currentScreenHandler.getType().equals(ScreenHandlerType.GENERIC_9X1)){
                    MinecraftClient.getInstance().player.currentScreenHandler.slots.forEach(s -> {
                        if (s.id == 8){
                            ClickSlotC2SPacket cs = new ClickSlotC2SPacket(MinecraftClient.getInstance().player.currentScreenHandler.syncId, s.id, 0, SlotActionType.PICKUP, s.getStack(), (short) 0x7f0f);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cs);
                        }
                    });
                }
            } catch (Exception ignored){}
        }
        if (AutoCF.ticks2 > -1 && AutoCF.on){
            AutoCF.ticks2--;
            if (AutoCF.ticks2 < 0){
                ChatMessageC2SPacket cm = new ChatMessageC2SPacket("/cf");
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(cm);
            }
        }
    }
}

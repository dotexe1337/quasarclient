package dev.semisol.quasarclient.module.anticaptcha.mixin;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.semisol.quasarclient.module.anticaptcha.AntiCaptcha;
import dev.semisol.quasarclient.module.autocf.AutoCF;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InventoryS2CPacket.class)
public class InventoryS2CPacketMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        try {
            if (MinecraftClient.getInstance().player.currentScreenHandler != null && MinecraftClient.getInstance().player.currentScreenHandler.getType().equals(ScreenHandlerType.GENERIC_9X3) && AntiCaptcha.cn.equals("Quick Captcha") && AntiCaptcha.on) {
                final boolean[] found = {false};
                MinecraftClient.getInstance().player.currentScreenHandler.slots.forEach(s -> {
                    if (s.id == 4 && s.hasStack() && s.getStack().getName().getString().equals("How much wool do you need to craft a bed?")){
                        found[0] = true;
                    }
                });
                if (found[0])
                    MinecraftClient.getInstance().player.currentScreenHandler.slots.forEach(s -> {
                        if (s.hasStack() && s.getStack().getName().getString().equals("Answer: 3")){
                            ClickSlotC2SPacket cs = new ClickSlotC2SPacket(MinecraftClient.getInstance().player.currentScreenHandler.syncId, s.id, 0, SlotActionType.PICKUP, s.getStack(), (short) 0x7f0f);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cs);
                        }
                    });
            }
        } catch (Exception ignored){}
    }
}

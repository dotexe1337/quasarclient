package dev.semisol.quasarclient.module.autocf.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.semisol.quasarclient.module.autocf.AutoCF;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
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
            if (MinecraftClient.getInstance().player.currentScreenHandler != null && MinecraftClient.getInstance().player.currentScreenHandler.getType().equals(ScreenHandlerType.GENERIC_9X1) && AutoCF.cn.equals("§lCOIN FLIP") && AutoCF.on) {
                final boolean[] found = {false};
                MinecraftClient.getInstance().player.currentScreenHandler.slots.forEach(s -> {
                    if (s.id >= 0 && s.id < 9 && s.hasStack() && !found[0]) {
                        if (Registry.ITEM.getId(s.getStack().getItem()).getPath().equals("player_head") && MinecraftClient.getInstance().player.currentScreenHandler != null) {
                            CompoundTag ct = s.getStack().getTag();
                            System.out.println();
                            if (!ct.contains("display") || ct.getType("display") != 10 || ct.getCompound("display").getType("Lore") != 9 || ct.getCompound("display").getList("Lore", 8).size() < 3)
                                return;
                            try {
                                JsonElement e = new JsonParser().parse(ct.getCompound("display").getList("Lore", 8).getString(2));
                                System.out.println(ct.getCompound("display").getList("Lore", 8).getString(2));
                                if (!e.isJsonObject()) return;
                                JsonObject o = e.getAsJsonObject();
                                if (!o.has("extra") || !o.get("extra").isJsonArray()) return;
                                JsonArray a = o.get("extra").getAsJsonArray();
                                if (a.size() < 2 || !a.get(1).isJsonObject() || !a.get(1).getAsJsonObject().has("text")) return;
                                JsonObject o2 = a.get(1).getAsJsonObject();
                                if (!o2.has("text") || !o2.get("text").isJsonPrimitive()) return;
                                String t = o2.get("text").getAsString();
                                if (t == null) return;
                                t = t.replaceAll(",", "");
                                Integer i = Integer.parseInt(t.split(" ", 2)[0]);
                                if (i > AutoCF.max) return;
                            } catch (Exception e) {
                                return;
                            }
                            found[0] = true;
                            ClickSlotC2SPacket cs = new ClickSlotC2SPacket(MinecraftClient.getInstance().player.currentScreenHandler.syncId, s.id, 0, SlotActionType.PICKUP, s.getStack(), (short) 0x7f0f);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cs);
                            AutoCF.ticks2 = 10;
                        }
                    }
                });
                if (!found[0]) {
                    AutoCF.ticks = 5;
                }
            }
        } catch (Exception ignored){}
    }
}

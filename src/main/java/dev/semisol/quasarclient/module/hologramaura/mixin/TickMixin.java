/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: TickMixin
# Created by constantin at 19:55, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.hologramaura.mixin;

import com.google.gson.JsonPrimitive;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.module.flight.Flight;
import dev.semisol.quasarclient.module.hologramaura.HologramAura;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerEntity.class)
public class TickMixin {
    private final Item SPAWN_EGG = Registry.ITEM.get(new Identifier("minecraft", "bat_spawn_egg"));
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (HologramAura.runningSpam){
            if (QuasarClient.minecraft.player != null){
                Vec3d b = QuasarClient.minecraft.player.getPos();
                b = new Vec3d(b.x, QuasarClient.minecraft.player.getEyeY(), b.z);
                b = b.add(QuasarClient.minecraft.player.getRotationVector().multiply(2));
                b = new Vec3d(Math.floor(b.x), Math.floor(b.y), Math.floor(b.z));
                PlayerInteractBlockC2SPacket pib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.DOWN, new BlockPos(b.x, b.y, b.z), false));

                for (int i = 0; i < HologramAura.spt; i++){
                    QuasarClient.minecraft.player.networkHandler.sendPacket(pib);
                    HologramAura.spamRemaining--;
                    if (HologramAura.spamRemaining <= 0) {
                        HologramAura.runningSpam = false;
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §aDone!"), false);
                        return;
                    }
                }
                QuasarClient.minecraft.player.sendMessage(Text.of((HologramAura.spamCount - HologramAura.spamRemaining) + "/" + HologramAura.spamCount), true);
            } else {
                HologramAura.stop();
            }
        }
        if (HologramAura.running) {
            if (QuasarClient.minecraft.player != null){
                Vec3d b = QuasarClient.minecraft.player.getPos();
                b = new Vec3d(b.x, QuasarClient.minecraft.player.getEyeY(), b.z);
                b = b.add(QuasarClient.minecraft.player.getRotationVector().multiply(2));
                b = new Vec3d(Math.floor(b.x), Math.floor(b.y), Math.floor(b.z));
                PlayerInteractBlockC2SPacket pib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.DOWN, new BlockPos(b.x, b.y, b.z), false));
                for (int i = 0; i < HologramAura.spt; i++){
                    ItemStack as = new ItemStack(SPAWN_EGG);
                    CompoundTag ct = as.getOrCreateSubTag("EntityTag");
                    ct.putString("id", "minecraft:armor_stand");
                    ct.putByte("CustomNameVisible", (byte) 1);
                    ct.putByte("NoGravity", (byte) 1);
                    ct.putByte("Invulnerable", (byte) 1);
                    ct.putByte("Small", (byte) 1);
                    ct.putByte("Marker", (byte) 1);
                    ct.putByte("Invisible", (byte) 1);
                    ct.putByte("CustomNameVisible", (byte) 1);
                    ct.putString("CustomName", HologramAura.en /*escaping bruh*/);
                    ListTag pos = new ListTag();
                    Vec3d v = HologramAura.vecs.get(HologramAura.index);
                    pos.add(0, DoubleTag.of(v.x + 0.5));
                    pos.add(1, DoubleTag.of(v.y + 0.5));
                    pos.add(2, DoubleTag.of(v.z + 0.5));
                    ct.put("Pos", pos /*bruh*/);
                    as.putSubTag("EntityTag", ct);
                    QuasarClient.minecraft.player.inventory.setStack(QuasarClient.minecraft.player.inventory.selectedSlot, as);
                    QuasarClient.minecraft.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(QuasarClient.minecraft.player.inventory.selectedSlot + 36, as));
                    QuasarClient.minecraft.player.networkHandler.sendPacket(pib);
                    if (HologramAura.index >= HologramAura.vecs.size() - 1) {
                        HologramAura.running = false;
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §aDone!"), false);
                        return;
                    } else {
                        HologramAura.index++;
                    }
                }
                QuasarClient.minecraft.player.sendMessage(Text.of(HologramAura.index + "/" + HologramAura.vecs.size()), true);
            } else {
                HologramAura.stop();
            }
        }
    }
}

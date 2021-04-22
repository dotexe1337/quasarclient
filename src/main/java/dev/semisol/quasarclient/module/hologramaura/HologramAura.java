package dev.semisol.quasarclient.module.hologramaura;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.RenderHelper;
import dev.semisol.quasarclient.registry.Keybind;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HologramAura extends Module {
    @Override
    public String getId() {
        return "hologramaura";
    }
    private static Vec3d center;
    private static Keybind[] keybinds = new Keybind[]{
            new Keybind(HologramAura::setCenter, "setcenter")
    };
    @Override
    public Keybind[] getKeybinds() {
        return keybinds;
    }
    public static Item SPAWN_EGG = Registry.ITEM.get(new Identifier("minecraft", "bat_spawn_egg"));
    public static int radius = 5;
    public static double prec = 1;
    public static boolean running = false;
    public static boolean runningSpam = false;
    public static int spamRemaining = 0;
    public static int spamCount = 0;
    public static int spt = 4;
    public static List<Vec3d> vecs = new ArrayList<>();
    public static int index = 0;
    public static String text = "test";
    public static String en = "\"test\"";
    @Override
    public void onRender(){
        if (HologramAura.running){
            RenderHelper.renderLine(center, vecs.get(index), Color.getHSBColor(((float) index) / 40f, 1, 1), 1);
            RenderHelper.renderBlockOutline(center.subtract(-0.1, -0.1, -0.1), new Vec3d(0.2, 0.2, 0.2), 255, 0, 0, 255);
            RenderHelper.renderBlockOutline(vecs.get(index).subtract(-0.1, -0.1, -0.1), new Vec3d(0.2, 0.2, 0.2), 0, 255, 0, 255);;
        }
        if (HologramAura.runningSpam || HologramAura.running){
            Vec3d b = QuasarClient.minecraft.player.getPos();
            b = new Vec3d(b.x, QuasarClient.minecraft.player.getEyeY(), b.z);
            b = b.add(QuasarClient.minecraft.player.getRotationVector().multiply(2));
            b = new Vec3d(Math.floor(b.x), Math.floor(b.y), Math.floor(b.z));
            RenderHelper.renderBlockOutline(b, new Vec3d(1, 1, 1), 0, 0, 255, 255);
        }
    }

    @Override
    public void onTick() {
        if (HologramAura.runningSpam){
            if (QuasarClient.minecraft.getNetworkHandler() != null){
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

    @Override
    public void onRegistered() {
        LiteralArgumentBuilder<CommandSource> lab2 = LiteralArgumentBuilder.literal("holoaura");
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("setcenter")
                .executes(c -> {
                    setCenter();
                    return 0;
                })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("radius")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Integer>argument("radius", IntegerArgumentType.integer(2, 60))
                                .executes(c -> {
                                    radius = IntegerArgumentType.getInteger(c, "radius");
                                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Radius is now set to §6" + radius + "§7."), false);
                                    return 0;
                                })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Radius is §6" + radius + "§7."), false);
                            return 0;
                        })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("spam")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Integer>argument("count", IntegerArgumentType.integer(1, 50000))
                                        .executes(c -> {
                                            runSpam(IntegerArgumentType.getInteger(c, "count"));
                                            return 0;
                                        })
                        )
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("prec")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Double>argument("precision", DoubleArgumentType.doubleArg(0.01, 4))
                                        .executes(c -> {
                                            prec = DoubleArgumentType.getDouble(c, "precision");
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Precision is now set to §6" + prec + "§7."), false);
                                            return 0;
                                        })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Precision is §6" + prec + "§7."), false);
                            return 0;
                        })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("spt")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Integer>argument("spt", IntegerArgumentType.integer(1, 40))
                                        .executes(c -> {
                                            spt = IntegerArgumentType.getInteger(c, "spt");
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Stands per tick set to §6" + spt + "§7."), false);
                                            return 0;
                                        })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Stands per tick is §6" + spt + "§7."), false);
                            return 0;
                        })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("text")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, String>argument("text", StringArgumentType.greedyString())
                                        .executes(c -> {
                                            text = StringArgumentType.getString(c, "text");
                                            en = new JsonPrimitive(text.replaceAll("&", "§").replaceAll("§§", "&")).toString();
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Text is set to: §a" + text), false);
                                            return 0;
                                        })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Text is: §a" + text), false);
                            return 0;
                        })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("run")
                        .executes(c -> {
                            run();
                            return 0;
                        })
        );
        lab2.then(
                LiteralArgumentBuilder.<CommandSource>literal("stop")
                        .executes(c -> {
                            stop();
                            return 0;
                        })
        );
        ModuleRegistry.dispatcher.register(lab2);
    }

    @Override
    public boolean isPassive() {
        return true;
    }
    private static void setCenter(){
        if (QuasarClient.minecraft.player == null) return;
        center = QuasarClient.minecraft.player.getPos();
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Center set."), false);
    }
    public static void runSpam(int c){
        if (running || runningSpam){
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §cAlready running."), false);
            return;
        }
        ItemStack as = new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "bat_spawn_egg")));
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
        Vec3d v = center;
        pos.add(0, DoubleTag.of(v.x));
        pos.add(1, DoubleTag.of(v.y));
        pos.add(2, DoubleTag.of(v.z));
        ct.put("Pos", pos /*bruh*/);
        as.putSubTag("EntityTag", ct);
        QuasarClient.minecraft.player.inventory.setStack(QuasarClient.minecraft.player.inventory.selectedSlot, as);
        QuasarClient.minecraft.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(QuasarClient.minecraft.player.inventory.selectedSlot + 36, as));
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Running..."), false);
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Expected time: §6" + (c / (20 * HologramAura.spt)) + "s"), false);
        spamCount = c;
        spamRemaining = c;
        runningSpam = true;
    }
    public static void run(){
        if (QuasarClient.minecraft.player == null) return;
        if (running || runningSpam){
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §cAlready running."), false);
            return;
        }
        if (center == null){
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §cPlease set the center."), false);
            return;
        }
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Running..."), false);
        vecs.clear();
        double res = prec * 20;
        for (double i = 0; i <= Math.PI; i += Math.PI / res) {
            double radius2 = Math.sin(i) * ((double) radius);
            double y = Math.cos(i) * ((double) radius);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / res) {
                double x = Math.cos(a) * radius2;
                double z = Math.sin(a) * radius2;
                vecs.add(new Vec3d(x + center.x, y + center.y, z + center.z));
            }
        }
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Expected time: §6" + (vecs.size() / (20*spt)) + "s"), false);
        index = 0;
        running = true;
    }
    public static void stop(){
        if (runningSpam){
            runningSpam = false;
            spamCount = 0;
            spamRemaining = 0;
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Stopped."), false);
            return;
        }
        if (!running){
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §cNot running."), false);
            return;
        }
        running = false;
        vecs.clear();
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Stopped."), false);
        index = 0;
    }
    public static Set<Vec3d> sphere(Vec3d Vec3d, int radius, boolean hollow){
        Set<Vec3d> blocks = new HashSet<>();
        int X = (int) Vec3d.x;
        int Y = (int) Vec3d.y;
        int Z = (int) Vec3d.z;
        int radiusSquared = radius * radius;

        if(hollow){
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Vec3d block = new Vec3d(x, y, z);
                            blocks.add(block);
                        }
                    }
                }
            }
            return makeHollow(blocks);
        } else {
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Vec3d block = new Vec3d(x, y, z);
                            blocks.add(block);
                        }
                    }
                }
            }
            return blocks;
        }
    }
    private static Set<Vec3d> makeHollow(Set<Vec3d> blocks){
        Set<Vec3d> edge = new HashSet<>();
        for(Vec3d l : blocks){
            int X = (int) l.x;
            int Y = (int) l.y;
            int Z = (int) l.z;
            Vec3d front = new Vec3d(X + 1, Y, Z);
            Vec3d back = new Vec3d(X - 1, Y, Z);
            Vec3d left = new Vec3d(X, Y, Z + 1);
            Vec3d right = new Vec3d(X, Y, Z - 1);
            Vec3d top = new Vec3d(X, Y + 1, Z);
            Vec3d bottom = new Vec3d(X, Y - 1, Z);
            if(!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom))){
                edge.add(l);
            }
        }
        return edge;
    }

}

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
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

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
    public static int radius = 5;
    public static double prec = 1;
    public static boolean running = false;
    public static List<Vec3d> vecs = new ArrayList<>();
    public static int index = 0;
    public static String text = "test";
    public static String en = "\"test\"";
    @Override
    public void onRender(){
        if (HologramAura.running){
            RenderHelper.renderLine(center, vecs.get(index), Color.getHSBColor(((float) index) / 40f, 1, 1), 3);
        }
    }

    @Override
    public boolean requireRendering() {
        return true;
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
    public static void run(){
        if (QuasarClient.minecraft.player == null) return;
        if (running){
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
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramaura§7] §7Expected time: §6" + (vecs.size() / 20) + "s"), false);
        index = 0;
        running = true;
    }
    public static void stop(){
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

package dev.semisol.quasarclient.module.hologramimage;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.RenderHelper;
import dev.semisol.quasarclient.module.hologramaura.HologramAura;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Arrays;

public class HologramImage extends Module {
    public boolean running = false;
    public BufferedImage img;
    public Vec3d center;
    public int y;
    public int x;
    public static int m = 0;
    public int spt;
    public ImageObserver io = (img, infoflags, x, y, width, height) -> true;
    public String[] TYPES = {
            "+X",
            "-X",
            "+Z",
            "-Z"
    };

    @Override
    public String getId() {
        return "hologramimage";
    }

    @Override
    public void onTick() {
        if (running){
            if (QuasarClient.minecraft.getNetworkHandler() == null){
                running = false;
                return;
            }
            for (int z = 0; z < spt; z++){
                if (x >= img.getWidth(io)){
                    y++;
                    x = 0;
                }
                if (y >= img.getHeight(io)){
                    running = false;
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §aDone!"), false);
                    return;
                }
                int co = img.getRGB(x, y);
                int ci = co & 0xffffff;
                String s = Integer.toString(ci, 16);
                StringBuilder c = new StringBuilder(s);
                for (int i = 0; i < 6 - s.length(); i++){
                    c.insert(0, "0");
                }
                Vec3d b = QuasarClient.minecraft.player.getPos();
                b = new Vec3d(b.x, QuasarClient.minecraft.player.getEyeY(), b.z);
                b = b.add(QuasarClient.minecraft.player.getRotationVector().multiply(2));
                b = new Vec3d(Math.floor(b.x), Math.floor(b.y), Math.floor(b.z));
                PlayerInteractBlockC2SPacket pib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.DOWN, new BlockPos(b.x, b.y, b.z), false));
                ItemStack as = new ItemStack(HologramAura.SPAWN_EGG);
                CompoundTag ct = as.getOrCreateSubTag("EntityTag");
                ct.putString("id", "minecraft:armor_stand");
                ct.putByte("CustomNameVisible", (byte) 1);
                ct.putByte("NoGravity", (byte) 1);
                ct.putByte("Invulnerable", (byte) 1);
                ct.putByte("Small", (byte) 1);
                ct.putByte("Marker", (byte) 1);
                ct.putByte("Invisible", (byte) 1);
                ct.putByte("CustomNameVisible", (byte) 1);
                ct.putString("CustomName", "{\"text\":\"⬛\",\"color\":\"#" + c.toString() + "\"}");
                ListTag pos = new ListTag();
                Vec3d p = center.add(m > 1?0:(m == 1?-0.21 * x:0.21 * x), (img.getHeight() - y) * 0.21, m > 1?(m == 3?-0.21 * x:0.21 * x):0);
                pos.add(0, DoubleTag.of(p.x));
                pos.add(1, DoubleTag.of(p.y));
                pos.add(2, DoubleTag.of(p.z));
                ct.put("Pos", pos /*bruh*/);
                as.putSubTag("EntityTag", ct);
                QuasarClient.minecraft.player.inventory.setStack(QuasarClient.minecraft.player.inventory.selectedSlot, as);
                QuasarClient.minecraft.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(QuasarClient.minecraft.player.inventory.selectedSlot + 36, as));
                QuasarClient.minecraft.player.networkHandler.sendPacket(pib);
                x++;
            }
            QuasarClient.minecraft.player.sendMessage(Text.of((x + (y * img.getWidth())) + "/" + (img.getHeight() * img.getWidth())), true);
        }
    }
    @Override
    public void onRender() {
        if (running){
            Vec3d b = QuasarClient.minecraft.player.getPos();
            b = new Vec3d(b.x, QuasarClient.minecraft.player.getEyeY(), b.z);
            b = b.add(QuasarClient.minecraft.player.getRotationVector().multiply(2));
            b = new Vec3d(Math.floor(b.x), Math.floor(b.y), Math.floor(b.z));
            RenderHelper.renderBlockOutline(b, new Vec3d(1, 1, 1), 0, 0, 255, 255);
        }
    }

    @Override
    public void onRegistered() {
        LiteralArgumentBuilder<CommandSource> lab = LiteralArgumentBuilder.<CommandSource>literal("holoimg");
        lab.then(
                LiteralArgumentBuilder.<CommandSource>literal("run")
                .then(
                        RequiredArgumentBuilder.<CommandSource, Integer>argument("standsPerTick", IntegerArgumentType.integer(1, 200))
                                .then(
                                        RequiredArgumentBuilder.<CommandSource, Integer>argument("scaleFactor", IntegerArgumentType.integer(1, 200))
                                                .executes(c -> {
                                                    if (center == null){
                                                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §cPlease set the center."), false);
                                                        return 0;
                                                    }
                                                    spt = IntegerArgumentType.getInteger(c, "standsPerTick");
                                                    int sf = IntegerArgumentType.getInteger(c, "scaleFactor");
                                                    x = 0;
                                                    y = 0;
                                                    try {
                                                        img = ImageIO.read(new FileInputStream(new File(QuasarClient.minecraft.runDirectory, "image.png")));
                                                        if (img.getWidth() / sf < 1){
                                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §cScale too high."), false);
                                                            return 0;
                                                        }
                                                        BufferedImage resized = new BufferedImage(img.getWidth() / sf, img.getHeight() / sf, img.getType());
                                                        Graphics2D g = resized.createGraphics();
                                                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                                        g.drawImage(img, 0, 0, resized.getWidth(), resized.getWidth(), 0, 0, img.getWidth(),
                                                                img.getHeight(), null);
                                                        g.dispose();
                                                        img = resized;
                                                        running = true;
                                                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §7Estimated time: §6" + ((img.getHeight() * img.getWidth()) / (spt * 20)) + "s"), false);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §cFailed. Are you sure your image is valid and exists at '.minecraft/image.png'?"), false);
                                                    }
                                                    return 0;
                                                })
                                )
                )
        );
        lab.then(
                LiteralArgumentBuilder.<CommandSource>literal("stop")
                        .executes(c -> {
                            if (!running){
                                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §cNot running."), false);
                                return 0;
                            }
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §7Stopped."), false);
                            running = false;
                            return 0;
                        })
        );
        lab.then(
                LiteralArgumentBuilder.<CommandSource>literal("toggleaxis")
                        .executes(c -> {
                            if (running){
                                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §cSetting locked while running."), false);
                                return 0;
                            }
                            m++;
                            if (m > 3) m = 0;
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §7Axis: §a" + TYPES[m]), false);
                            return 0;
                        })
        );
        lab.then(
                LiteralArgumentBuilder.<CommandSource>literal("setcenter")
                        .executes(c -> {
                            center = QuasarClient.minecraft.player.getPos();
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6hologramimage§7] §7Center set."), false);
                            return 0;
                        })
        );
        ModuleRegistry.dispatcher.register(
                lab
        );

    }

    @Override
    public boolean isPassive() {
        return true;
    }
}

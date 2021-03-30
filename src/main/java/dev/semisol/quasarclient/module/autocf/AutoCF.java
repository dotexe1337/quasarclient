package dev.semisol.quasarclient.module.autocf;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;

import java.util.Arrays;

public class AutoCF {
    public static String cn = "";
    public static int ticks = -1;
    public static int max = 10000;
    public static boolean on = false;
    public static void init(){
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("autocf")
                .then(
                        RequiredArgumentBuilder.<CommandSource, Boolean>argument("on", BoolArgumentType.bool())
                        .executes(c -> {
                            on = BoolArgumentType.getBool(c, "on");
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocf§7] AutoCF is now " + (on?"§aON":"§cOFF")+"§7."), false);
                            return 0;
                        })
                )
                .executes(c -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocf§7] AutoCF is " + (on?"§aON":"§cOFF")+"§7."), false);
                    return 0;
                })
        );
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("autocfmax")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Integer>argument("max", IntegerArgumentType.integer())
                                        .executes(c -> {
                                            max = IntegerArgumentType.getInteger(c, "max");
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocf§7] AutoCF maximum wager is now §6" + max +"§7."), false);
                                            return 0;
                                        })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocf§7] AutoCF maximum wager is §6" + max +"§7."), false);
                            return 0;
                        })
        );
    }
}

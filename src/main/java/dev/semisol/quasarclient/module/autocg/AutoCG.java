package dev.semisol.quasarclient.module.autocg;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class AutoCG {
    public static String word = "acg";
    public static int ticks = -1;
    public static boolean on = false;
    public static void init(){
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("autocg")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Boolean>argument("on", BoolArgumentType.bool())
                                        .executes(c -> {
                                            on = BoolArgumentType.getBool(c, "on");
                                            QuasarClient.saveConfig();
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocg§7] AutoCG is now " + (on?"§aON":"§cOFF")+"§7."), false);
                                            return 0;
                                        })
                        )
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6autocg§7] AutoCG is " + (on?"§aON":"§cOFF")+"§7."), false);
                            return 0;
                        })
        );
    }
}

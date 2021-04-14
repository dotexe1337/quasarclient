package dev.semisol.quasarclient.module.say;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

public class Say {
    public static void init(){
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("say")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
                                        .executes(c -> {
                                            ChatMessageC2SPacket cmp = new ChatMessageC2SPacket(StringArgumentType.getString(c, "message"));
                                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cmp);
                                            return 0;
                                        })
                        )
        );
    }
}

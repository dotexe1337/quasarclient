package dev.semisol.quasarclient.module.say;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class Say extends Module {
    @Override
    public String getId() {
        return "say";
    }

    @Override
    public void onRegistered() {
        ModuleRegistry.dispatcher.register(
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

    @Override
    public boolean isPassive() {
        return true;
    }
}

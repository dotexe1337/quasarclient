/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: CommandTreeS2CPacketMixin
# Created by Semisol at 20:41, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.dcol.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandTreeS2CPacket.class)
public class CommandTreeS2CPacketMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    public void apply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        load("/");
    }
    public void load(String s){
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Got CommandTree packet"), false);
        MinecraftClient inst = MinecraftClient.getInstance();
        ClientCommandSource csrc = inst.player.networkHandler.getCommandSource();
        CommandDispatcher<CommandSource> cd = inst.player.networkHandler.getCommandDispatcher();
        ParseResults<CommandSource> bruh = cd.parse(s, csrc);
        CompletableFuture<Suggestions> c = cd.getCompletionSuggestions(bruh);
        c.thenAccept(suggestions -> {
            Globals.commands.clear();
            Globals.plugins.clear();
            suggestions.getList().stream().map(Suggestion::getText).forEach(Globals.commands::add);
            Globals.commands.forEach(cmd -> {
                if (cmd.contains(":")) {
                    String plugin = cmd.split(":", 2)[0];
                    if (!Globals.plugins.contains(plugin)) Globals.plugins.add(plugin);
                }
            });
            if (Globals.plugins.size() == 0 && s.equals("/")) load("");
        });
    }
}

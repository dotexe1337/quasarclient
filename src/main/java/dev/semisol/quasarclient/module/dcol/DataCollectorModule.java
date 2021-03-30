package dev.semisol.quasarclient.module.dcol;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;

import java.util.Arrays;

public class DataCollectorModule {
    public static void init(){
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("dcol")
                        .executes(cc -> {
                            if (Globals.inProg){
                                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] §cGathering already in progress."), false);
                                return 0;
                            }
                            IntegratedServer srv = MinecraftClient.getInstance().getServer();
                            if (srv == null) {
                                Globals.sbrand = MinecraftClient.getInstance().player.getServerBrand();
                            } else Globals.sbrand = "Integrated server, vanilla";
                            Globals.inProg = true;
                            Globals.commandsLeft.addAll(Arrays.asList("pl", "ncp", "matrix", "/", "negativity", "pl", "version", "bal", "nonexistent", "sudo", "gmc", "gms", "warp", "tpa", "buycraft", "tebex", "help", "bungee", "server", "glist", "fly", "claim", "vote", "discordsrv", "co", "griefprevention", "sv", "v", "alert", "broadcast", "ban", "kick", "mute", "warn", "note", "gnote", "tempban", "tempmute", "unmute", "unban", "history", "tokens", "claims", "msg", "trade"));
                            //Globals.commands.stream().filter(x->!x.contains(":")).filter(x -> ThreadLocalRandom.current().nextFloat() < 0.01).forEach(Globals.commandsLeft::add);
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Time expected: §6" + ((Globals.commandsLeft.size() * 16) / 20) + "s"), false);
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Please do not run anything until this process is done."), false);

                            String c = Globals.commandsLeft.remove(0);
                            ChatMessageC2SPacket cmp = new ChatMessageC2SPacket("/" + c);
                            Globals.command = c;
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cmp);
                            return 0;
                        })
        );
    }
}

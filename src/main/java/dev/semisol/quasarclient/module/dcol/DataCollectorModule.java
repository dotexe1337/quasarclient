package dev.semisol.quasarclient.module.dcol;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;

import java.util.Arrays;

public class DataCollectorModule extends Module {
    @Override
    public String getId() {
        return "dcol";
    }

    @Override
    public void onRegistered() {
        ModuleRegistry.dispatcher.register(
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
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Time expected: §6" + ((Globals.commandsLeft.size() * 16) / 20) + "s"), false);
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Please do not run anything until this process is done."), false);

                            String c = Globals.commandsLeft.remove(0);
                            ChatMessageC2SPacket cmp = new ChatMessageC2SPacket("/" + c);
                            Globals.command = c;
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(cmp);
                            return 0;
                        })
        );
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("plugins")
                        .executes(cc -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Plugins: " + String.join(", ", Globals.plugins)), false);
                            return 0;
                        })
        );
    }

    @Override
    public boolean isPassive() {
        return true;
    }
}

package dev.semisol.quasarclient.module.tps;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;

public class TPS extends Module {
    static public boolean recvFirst = false;
    static public long recv = 0l;
    static public double[] ls10 = new double[10];
    static public double[] ls10t = new double[30];
    static public double[] last30 = new double[30];
    static public long ogt = 0l;
    static public boolean capOn = false;
    public static double calcTps(double n, double td) {
        return 1000 / (n / 20);
    }
    public static double calcTps(double n) {
        return (20.0 / Math.max((n - 1000.0) / (500.0), 1.0));
    }
    public static double calcTpsAdvanced(double n, double t) {
        return 1e+9d / (n / t);
    }
    public static double calcTpsAdvanced(double n) {
        return 1e+9d / (n / 20);
    }

    @Override
    public String getId() {
        return "tps";
    }

    @Override
    public boolean isPassive() {
        return true;
    }

    @Override
    public void onRegistered(){
        Arrays.fill(last30, 20d);
        Arrays.fill(ls10, 20d);
        Arrays.fill(ls10t, 20d);
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("cleartps")
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6tps§7] §7Cleared TPS data."), false);
                            Arrays.fill(last30, 20d);
                            Arrays.fill(ls10, 20d);
                            Arrays.fill(ls10t, 20d);
                            recvFirst = false;
                            return 0;
                        })
        );
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("tps")
                        .executes(c -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6tps§7] §7Avg: " + (Arrays.stream(ls10).sum() / 10) + " | Latest: " + ls10[0]), false);
                            return 0;
                        })
        );
    }
}

package dev.semisol.quasarclient;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.module.anticaptcha.AntiCaptcha;
import dev.semisol.quasarclient.module.autocf.AutoCF;
import dev.semisol.quasarclient.module.dcol.DataCollectorModule;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
public class QuasarClient implements ModInitializer {
    static public CommandDispatcher<CommandSource> disp = new CommandDispatcher<>();
    public static final String MOD_ID = "quasarclient";
    public static final String MOD_NAME = "Quasar Client";
    public static Logger LOGGER = LogManager.getLogger();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Loading the mod...");
        DataCollectorModule.init();
        AutoCF.init();
        AntiCaptcha.init();
        log(Level.INFO, "Should be ready.");
        //TODO: Initializer
    }

}
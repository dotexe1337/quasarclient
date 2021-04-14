package dev.semisol.quasarclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.module.anticaptcha.AntiCaptcha;
import dev.semisol.quasarclient.module.autocf.AutoCF;
import dev.semisol.quasarclient.module.autocg.AutoCG;
import dev.semisol.quasarclient.module.bookdupe.BookDupe;
import dev.semisol.quasarclient.module.dcol.DataCollectorModule;
import dev.semisol.quasarclient.module.say.Say;
import dev.semisol.quasarclient.module.tps.TPS;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
public class QuasarClient implements ModInitializer {
    static public CommandDispatcher<CommandSource> disp = new CommandDispatcher<>();
    public static final String MOD_ID = "quasarclient";
    public static final String MOD_NAME = "Quasar Client";
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftClient minecraft = MinecraftClient.getInstance();
    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static File conf;
    public static Gson g = new GsonBuilder().create();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Loading the mod...");
        conf = new File(MinecraftClient.getInstance().runDirectory, "quasar_client.json");
        DataCollectorModule.init();
        AutoCF.init();
        AutoCG.init();
        AntiCaptcha.init();
        Say.init();
        BookDupe.init();
        TPS.init();
        loadConfig();
        log(Level.INFO, "Should be ready.");
        //TODO: Initializer
    }
    public static void loadConfig(){
        if (conf.exists() && !conf.isFile()){
            log(Level.FATAL, "Config exists and is not a file.");
            return;
        }
        if (!conf.exists()){
            try {
                conf.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log(Level.WARN, "Config does not exist, creating one now.");
            saveConfig();
        }
        JsonObject j;
        try {
            FileReader fr = new FileReader(conf);
            j = new JsonParser().parse(fr).getAsJsonObject();
            fr.close();
        } catch (Exception e){
            log(Level.FATAL, "Malformed config JSON.");
            return;
        }
        // autocg
        try {
            AutoCG.on = j.get("autocg_on").getAsBoolean();
        } catch (Exception e){ log(Level.FATAL, "Error in AutoCG configuration."); return;}
        // autocf
        try {
            AutoCF.on = j.get("autocf_on").getAsBoolean();
            AutoCF.max = j.get("autocf_maxwager").getAsInt();
        } catch (Exception e){ log(Level.FATAL, "Error in AutoCF configuration."); return;}
        // anticaptcha
        try {
            AntiCaptcha.on = j.get("anticaptcha_on").getAsBoolean();
        } catch (Exception e){ log(Level.FATAL, "Error in AntiCaptcha configuration."); return;}
        // TPS
        try {
            TPS.chartOn = j.get("tps_chart_on").getAsBoolean();
            TPS.capOn = j.get("tps_cap_on").getAsBoolean();
        } catch (Exception e){ log(Level.FATAL, "Error in TPS configuration."); return;}
    }
    public static void saveConfig(){
        JsonObject o = new JsonObject();
        // autocg
        o.addProperty("autocg_on", AutoCG.on);
        // autocf
        o.addProperty("autocf_on", AutoCF.on);
        o.addProperty("autocf_maxwager", AutoCF.max);
        // anticaptcha
        o.addProperty("anticaptcha_on", AntiCaptcha.on);
        // TPS
        o.addProperty("tps_chart_on", TPS.chartOn);
        o.addProperty("tps_cap_on", TPS.capOn);
        try {
            FileWriter fw = new FileWriter(conf);
            QuasarClient.g.toJson(o, fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
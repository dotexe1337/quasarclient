package dev.semisol.quasarclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.semisol.quasarclient.module.bookdupe.BookDupe;
import dev.semisol.quasarclient.module.dcol.DataCollectorModule;
import dev.semisol.quasarclient.module.flight.Flight;
import dev.semisol.quasarclient.module.hologramaura.HologramAura;
import dev.semisol.quasarclient.module.lagback.Lagback;
import dev.semisol.quasarclient.module.nofall.NoFall;
import dev.semisol.quasarclient.module.tps.TPS;
import dev.semisol.quasarclient.module.tpschart.TPSChart;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class QuasarClient implements ModInitializer {
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
        ModuleRegistry.setConfigFile(new File(MinecraftClient.getInstance().runDirectory, "quasar_client.json"));
        ModuleRegistry.register(new TPS());
        ModuleRegistry.register(new TPSChart());
        ModuleRegistry.register(new DataCollectorModule());
        ModuleRegistry.register(new Flight());
        ModuleRegistry.register(new HologramAura());
        ModuleRegistry.register(new NoFall());
        ModuleRegistry.register(new Lagback());
        ModuleRegistry.register(new BookDupe());
        ModuleRegistry.loadConfiguration();
        ModuleRegistry.moduleInitDone();
        log(Level.INFO, "Should be ready.");
        //TODO: Initializer
    }
}
package dev.semisol.quasarclient.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleRegistry {
    private static File confF;
    private static List<Module> modules = new ArrayList<>();
    private static HashMap<String, Module> modMap = new HashMap<>();
    private static HashMap<Module, Boolean> enabled = new HashMap<>();;
    public static CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    public static void setConfigFile(File f){
        confF = f;
    }
    public static void loadConfiguration(){
        if (confF.exists() && !confF.isFile()){
            QuasarClient.log(Level.FATAL, "Config exists and is not a file.");
            return;
        }
        if (!confF.exists()){
            try {
                confF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            QuasarClient.log(Level.WARN, "Config does not exist, creating one now.");
            saveConfiguration();
        }
        JsonObject j;
        try {
            FileReader fr = new FileReader(confF);
            j = new JsonParser().parse(fr).getAsJsonObject();
            fr.close();
        } catch (Exception e){
            QuasarClient.log(Level.FATAL, "Malformed config JSON.");
            return;
        }
        modules.forEach(k -> {
            try {
                JsonObject j2 = j.get(k.getId()).getAsJsonObject();
                if (k.persistEnabling()) setOn(k, j2.get("on").getAsBoolean());
                if (!k.loadConfig(j2)){
                    QuasarClient.log(Level.WARN, "Incomplete config for: " + k.getId() + ". New config changes?");
                }
            } catch(Exception ignored) {
                QuasarClient.log(Level.WARN, "Malformed config for: " + k.getId());
            }
        });
    }
    public static void moduleInitDone(){
        LiteralArgumentBuilder<CommandSource> lab = LiteralArgumentBuilder.literal("module");
        modules.forEach(m -> {
            if (m.isPassive()) return;
            lab.then(
                    LiteralArgumentBuilder.<CommandSource>literal(m.getId())
                    .then(
                            RequiredArgumentBuilder.<CommandSource, Boolean>argument("enabled", BoolArgumentType.bool())
                            .executes(c -> {
                                boolean on = BoolArgumentType.getBool(c, "enabled");
                                setOn(m, on);
                                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Module " + m.getId() + " is now " + (on?"§aON":"§cOFF")), false);
                                saveConfiguration();
                                return 0;
                            })
                    )
                    .executes(c -> {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Module " + m.getId() + " is " + (isOn(m)?"§aON":"§cOFF")), false);
                        return 0;
                    })
            );
        });

        dispatcher.register(
               lab
        );
    }
    public static void saveConfiguration(){
        JsonObject o = new JsonObject();
        modules.forEach(k -> {
            JsonObject j2 = new JsonObject();
            if (k.persistEnabling()) j2.addProperty("on", isOn(k));
            k.saveConfig(j2);
            o.add(k.getId(), j2);
        });
        try {
            FileWriter fw = new FileWriter(confF);
            dev.semisol.quasarclient.QuasarClient.g.toJson(o, fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void register(Module m){
        if (modules.contains(m)) return;
        modules.add(m);
        enabled.put(m, false);
        modMap.put(m.getId(), m);
        m.onRegistered();
        saveConfiguration();
    }
    public static boolean isOn(Module m){
        return enabled.getOrDefault(m, false);
    }
    public static void setOn(Module m, boolean on){
        if (!enabled.containsKey(m)) return;
        if (enabled.get(m) == on) return;
        enabled.put(m, on);
        m.onToggle();
    }
    public static Module getModule(String s){
        return modMap.getOrDefault(s, null);
    }
    public static void onRender(){
        modules.forEach(k -> {
            if (ModuleRegistry.isOn(k) && !k.isPassive()) k.onHudRender();
        });
    }
}

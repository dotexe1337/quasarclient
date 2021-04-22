package dev.semisol.quasarclient.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
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
import java.util.Locale;

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
                enabled.put(k, false);
                JsonObject j2 = j.get(k.getId()).getAsJsonObject();
                if (k.persistEnabling()) enabled.put(k, j2.get("on").getAsBoolean());
                if (!k.loadConfig(j2)){
                    QuasarClient.log(Level.WARN, "Incomplete config for: " + k.getId() + ". New config changes?");
                }
                for (Keybind kb: k.getKeybinds()){
                    kb.key = j2.get("keybind_" + kb.name).getAsInt();
                }
                for (ConfigOpt c: k.getOpts()){
                    c.setJSON(j2.get("conf_" + c.name));
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
        LiteralArgumentBuilder<CommandSource> lab2 = LiteralArgumentBuilder.literal("keybind");
        modules.forEach(m -> {
            if (m.getKeybinds().length == 0) return;
            LiteralArgumentBuilder<CommandSource> lab3 = LiteralArgumentBuilder.literal(m.getId());
            for (Keybind k: m.getKeybinds()){
                LiteralArgumentBuilder<CommandSource> lab4;
                if (m.getKeybinds().length > 1){
                    lab4 = LiteralArgumentBuilder.literal(k.name);
                } else {
                    lab4 = lab3;
                }
                lab4
                    .then(
                        LiteralArgumentBuilder.<CommandSource>literal("none")
                                .executes(c -> {
                                    k.key = -1;
                                    saveConfiguration();
                                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Keybind " + m.getId() + ":" + k.name + " is now bound to §cNONE§7."), false);
                                    return 0;
                                })
                    )
                    .then(
                            RequiredArgumentBuilder.<CommandSource, String>argument("keybind", StringArgumentType.string())
                                    .executes(c -> {
                                        String s = StringArgumentType.getString(c, "keybind").toUpperCase(Locale.ROOT);
                                        if (s.length() > 1){
                                            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §cKeybind should be 1 character long."), false);
                                            return 0;
                                        }
                                        k.key = s.charAt(0);
                                        saveConfiguration();
                                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Keybind " + m.getId() + ":" + k.name + " is now bound to §a" + s + "§7."), false);
                                        return 0;
                                    })
                    )
                    .executes(c -> {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Keybind " + m.getId() + ":" + k.name + " is bound to " + (k.key != -1?("§a" + ((char)k.key)):"§cNONE") + "§7."), false);
                        return 0;
                    });
                if (m.getKeybinds().length > 1){
                    lab3.then(
                            lab4
                    );
                }
            }
            lab3
                .executes(c -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Keybinds:"), false);
                    for (Keybind k: m.getKeybinds()){
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7" + k.name + ": " + ((k.key != -1?("§a" + ((char)k.key)):"§cNONE"))), false);
                    }
                    return 0;
                });
            lab2.then(
                    lab3
            );
        });

        dispatcher.register(
                lab2
        );
        LiteralArgumentBuilder<CommandSource> lab3 = LiteralArgumentBuilder.literal("config");
        modules.forEach(m -> {
            LiteralArgumentBuilder<CommandSource> lab4 = LiteralArgumentBuilder.literal(m.getId());
            if (m.getOpts().length < 1) return;
            for (ConfigOpt c: m.getOpts()){
                LiteralArgumentBuilder<CommandSource> lab5 = LiteralArgumentBuilder.literal(c.name);
                lab5.then(c.getRAB().executes(ctx -> {
                    c.setFromCtx(ctx);
                    saveConfiguration();
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Value set: §a" + c.value), false);
                    return 0;
                }));
                lab5.executes(ctx -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Value: §a" + c.value), false);
                    return 0;
                });
                lab4.then(lab5);
            }
            lab4.executes(c -> {
                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Configuration:"), false);
                for (ConfigOpt k: m.getOpts()){
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7" + k.name + ": §a" + k.value), false);
                }
                return 0;
            });
            lab3.then(
                    lab4
            );
        });
        dispatcher.register(lab3);
    }
    public static void saveConfiguration(){
        JsonObject o = new JsonObject();
        modules.forEach(k -> {
            JsonObject j2 = new JsonObject();
            if (k.persistEnabling()) j2.addProperty("on", isOn(k));
            for (Keybind kb: k.getKeybinds()) {
                j2.addProperty("keybind_" + kb.name, kb.key);
            }
            for (ConfigOpt c: k.getOpts()){
                j2.add("conf_" + c.name, c.getJSON());
            }
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
    }
    public static void handleKeybinds(){
        if (QuasarClient.minecraft.player == null) return;
        if (QuasarClient.minecraft.currentScreen != null) return;
        modules.forEach(m -> {
            for (Keybind k: m.getKeybinds()){
                if (k.key != -1){
                    if (InputUtil.isKeyPressed(QuasarClient.minecraft.getWindow().getHandle(), k.key)){
                        if (!k.held){
                            k.held = true;
                            k.runnable.run();
                        }
                    } else {
                        k.held = false;
                    }
                }
            }
        });
    }
    public static boolean isOn(Module m){
        return enabled.getOrDefault(m, false);
    }
    public static void setOn(Module m, boolean on){
        if (!enabled.containsKey(m)) return;
        if (enabled.get(m) == on) return;
        enabled.put(m, on);
        m.onToggle();
        saveConfiguration();
    }
    public static void setOnNE(Module m, boolean on){
        if (!enabled.containsKey(m)) return;
        if (enabled.get(m) == on) return;
        enabled.put(m, on);
        saveConfiguration();
    }
    public static Module getModule(String s){
        return modMap.getOrDefault(s, null);
    }
    public static void onHudRender(){
        modules.forEach(k -> {
            if (ModuleRegistry.isOn(k) || k.isPassive()) k.onHudRender();
        });
    }
    public static void onRender(){
        modules.forEach(k -> {
            if (ModuleRegistry.isOn(k) || k.isPassive()) k.onRender();
        });
    }
    public static void onTick(){
        modules.forEach(k -> {
            if (ModuleRegistry.isOn(k) || k.isPassive()) k.onTick();
        });
    }
}

package dev.semisol.quasarclient.registry;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public abstract class Module {
    public String getId(){
        return "";
    }
    public boolean loadConfig(JsonObject j){
        return true;
    }
    public void saveConfig(JsonObject j){
        return;
    }
    private Keybind[] toggleKeybinds = new Keybind[]{new Keybind(() -> {
        ModuleRegistry.setOn(this, !ModuleRegistry.isOn(this));
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §7Module " + this.getId() + " is now " + (ModuleRegistry.isOn(this)?"§aON":"§cOFF")), false);
    }, "toggle")};
    public static Keybind[] NO_KEYBINDS = new Keybind[]{};
    public Keybind[] getKeybinds(){
        return this.isPassive()?NO_KEYBINDS:this.toggleKeybinds;
    }
    public static ConfigOpt[] NO_CONFIGURATION = new ConfigOpt[]{};
    public ConfigOpt[] getOpts(){
        return NO_CONFIGURATION;
    }
    public void onHudRender(){

    }
    public void onRender(){

    }
    public void onTick(){

    }
    public void onRegistered(){

    }
    public void onToggle(){

    }
    public boolean isPassive(){
        return false;
    }
    public boolean persistEnabling() { return true; }
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Module && ((Module) obj).getId().equals(this.getId()));
    }
}

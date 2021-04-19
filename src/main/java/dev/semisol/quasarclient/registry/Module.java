package dev.semisol.quasarclient.registry;

import com.google.gson.JsonObject;

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
    public void onHudRender(){

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

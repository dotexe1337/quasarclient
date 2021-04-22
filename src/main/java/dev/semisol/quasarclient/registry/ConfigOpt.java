package dev.semisol.quasarclient.registry;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

public abstract class ConfigOpt<T> {
    public T value;
    public final String name;
    public ConfigOpt(String name, T v){
        this.value = v;
        this.name = name;
    }
    public abstract ConfigType getType();
    public abstract JsonElement getJSON();
    public abstract void setJSON(JsonElement j);
    public abstract RequiredArgumentBuilder<CommandSource, T> getRAB();
    public abstract void setFromCtx(CommandContext<CommandSource> ctx);
}

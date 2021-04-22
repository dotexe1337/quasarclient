package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigOpt;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class StringOpt extends ConfigOpt<String> {
    public StringOpt(String name, String v) {
        super(name, v);
    }

    @Override
    public ConfigType getType() {
        return ConfigType.STRING;
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsString();
    }

    @Override
    public RequiredArgumentBuilder<CommandSource, String> getRAB() {
        return RequiredArgumentBuilder.argument("value", StringArgumentType.greedyString());
    }

    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = StringArgumentType.getString(ctx, "value");
    }
}

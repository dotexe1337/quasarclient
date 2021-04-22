package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigOpt;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class BooleanOpt extends ConfigOpt<Boolean> {

    public BooleanOpt(String name, Boolean v) {
        super(name, v);
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsBoolean();
    }

    @Override
    public ConfigType getType() {
        return ConfigType.BOOLEAN;
    }
    @Override
    public RequiredArgumentBuilder<CommandSource, Boolean> getRAB() {
        return RequiredArgumentBuilder.argument("value", BoolArgumentType.bool());
    }
    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = BoolArgumentType.getBool(ctx, "value");
    }
}

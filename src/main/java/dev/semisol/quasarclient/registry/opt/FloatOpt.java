package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigOpt;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class FloatOpt extends RangedConfigOpt<Float> {


    public FloatOpt(String name, Float v, Float low, Float high, boolean b) {
        super(name, v, low, high, b);
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }
    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsFloat();
    }

    @Override
    public ConfigType getType() {
        return ConfigType.FLOAT;
    }
    @Override
    public RequiredArgumentBuilder<CommandSource, Float> getRAB() {
        return RequiredArgumentBuilder.argument("value", this.bounded()? FloatArgumentType.floatArg(this.getLowBound(), this.getHighBound()):FloatArgumentType.floatArg());
    }
    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = FloatArgumentType.getFloat(ctx, "value");
    }
}

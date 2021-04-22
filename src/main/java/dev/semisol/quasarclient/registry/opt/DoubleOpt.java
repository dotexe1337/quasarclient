package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class DoubleOpt extends RangedConfigOpt<Double> {

    public DoubleOpt(String name, Double v, Double low, Double high, boolean b) {
        super(name, v, low, high, b);
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }
    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsDouble();
    }
    @Override
    public ConfigType getType() {
        return ConfigType.DOUBLE;
    }
    @Override
    public RequiredArgumentBuilder<CommandSource, Double> getRAB() {
        return RequiredArgumentBuilder.argument("value", this.bounded()? DoubleArgumentType.doubleArg(this.getLowBound(), this.getHighBound()):DoubleArgumentType.doubleArg());
    }
    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = DoubleArgumentType.getDouble(ctx, "value");
    }
}

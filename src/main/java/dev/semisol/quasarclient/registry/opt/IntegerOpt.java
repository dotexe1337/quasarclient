package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigOpt;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class IntegerOpt extends RangedConfigOpt<Integer> {

    public IntegerOpt(String name, Integer v, Integer low, Integer high, boolean b) {
        super(name, v, low, high, b);
    }

    @Override
    public ConfigType getType() {
        return ConfigType.INTEGER;
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }
    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsInt();
    }
    public RequiredArgumentBuilder<CommandSource, Integer> getRAB() {
        return RequiredArgumentBuilder.argument("value", this.bounded()?IntegerArgumentType.integer(this.getLowBound(), this.getHighBound()):IntegerArgumentType.integer());
    }
    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = IntegerArgumentType.getInteger(ctx, "value");
    }
}

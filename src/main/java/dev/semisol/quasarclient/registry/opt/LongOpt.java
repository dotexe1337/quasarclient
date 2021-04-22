package dev.semisol.quasarclient.registry.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.semisol.quasarclient.registry.ConfigType;
import net.minecraft.command.CommandSource;

public class LongOpt extends RangedConfigOpt<Long> {
    public LongOpt(String name, Long v, Long low, Long high, boolean b) {
        super(name, v, low, high, b);
    }

    @Override
    public ConfigType getType() {
        return ConfigType.LONG;
    }

    @Override
    public JsonElement getJSON() {
        return new JsonPrimitive(this.value);
    }
    @Override
    public void setJSON(JsonElement j) {
        this.value = j.getAsLong();
    }
    @Override
    public RequiredArgumentBuilder<CommandSource, Long> getRAB() {
        return RequiredArgumentBuilder.argument("value", this.bounded()?LongArgumentType.longArg(this.getLowBound(), this.getHighBound()):LongArgumentType.longArg());
    }
    @Override
    public void setFromCtx(CommandContext ctx) {
        this.value = LongArgumentType.getLong(ctx, "value");
    }
}

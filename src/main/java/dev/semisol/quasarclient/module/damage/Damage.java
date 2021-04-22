package dev.semisol.quasarclient.module.damage;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class Damage extends Module {
    @Override
    public String getId() {
        return "damage";
    }

    @Override
    public void onRegistered() {
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("damage")
                        .then(
                                RequiredArgumentBuilder.<CommandSource, Integer>argument("amount", IntegerArgumentType.integer(1, 7))
                                        .executes(c -> {
                                            int rem = IntegerArgumentType.getInteger(c, "amount");
                                            Utils.damage(QuasarClient.minecraft.player.getPos(), rem);
                                            return 0;
                                        })
                        )
        );
    }

    @Override
    public boolean isPassive() {
        return true;
    }
}

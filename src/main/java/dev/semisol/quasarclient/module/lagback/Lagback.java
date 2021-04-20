package dev.semisol.quasarclient.module.lagback;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.registry.Keybind;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

public class Lagback extends Module {
    @Override
    public String getId() {
        return "lagback";
    }
    private static Keybind[] keybinds = new Keybind[]{
            new Keybind(Lagback::lagback, "lagback")
    };
    @Override
    public void onRegistered() {
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("lagback")
                        .executes(cc -> {
                            lagback();
                            return 0;
                        })
        );
    }

    @Override
    public Keybind[] getKeybinds() {
        return keybinds;
    }

    private static void lagback(){
        Vec3d pos = QuasarClient.minecraft.player.getPos();
        Utils.sendPosUpdate(pos.x, pos.y + 10, pos.z, true, false);
        Utils.sendPosUpdate(pos.x, pos.y, pos.z, true, false);
    }
    @Override
    public boolean isPassive() {
        return true;
    }
}

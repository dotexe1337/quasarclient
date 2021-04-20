package dev.semisol.quasarclient.module.flight;

import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.module.dflight.DFlight;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.util.math.Vec3d;

import static dev.semisol.quasarclient.etc.Utils.sendPosUpdate;

public class Flight extends Module {
    public static int ticks = 0;
    @Override
    public String getId() {
        return "flight";
    }
    @Override
    public boolean persistEnabling() {
        return false;
    }
    @Override
    public void onRegistered() {

    }

    @Override
    public void onToggle() {
        if (QuasarClient.minecraft.player == null) return;
        if (ModuleRegistry.isOn(this)){
            Vec3d pos = QuasarClient.minecraft.player.getPos();
            Utils.sendPosUpdate(pos.x, pos.y + 1, pos.z, true, true);
            QuasarClient.minecraft.player.abilities.flying = true;
            QuasarClient.minecraft.player.abilities.allowFlying = true;
            QuasarClient.minecraft.player.abilities.setFlySpeed(0.032f);
        } else {
            QuasarClient.minecraft.player.abilities.setFlySpeed(0.1f);
            QuasarClient.minecraft.player.abilities.allowFlying = false;
            QuasarClient.minecraft.player.abilities.flying = false;
        }
    }
}

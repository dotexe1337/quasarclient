package dev.semisol.quasarclient.module.flight;

import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.util.math.Vec3d;

import static dev.semisol.quasarclient.etc.Utils.sendPosUpdate;

public class Flight extends Module {
    public static boolean bypassing = false;
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
            bypassing = true;
            Vec3d pos = QuasarClient.minecraft.player.getPos();
            pos = pos.add(QuasarClient.minecraft.player.getRotationVector().normalize().multiply(0.1, 0, 0.1));
            sendPosUpdate(pos.x, pos.y + 0.05, pos.z, false, false);
            Utils.damage(pos, 1);
            QuasarClient.minecraft.player.abilities.setFlySpeed(0.1f);
        }
        if (!ModuleRegistry.isOn(this)) QuasarClient.minecraft.player.abilities.allowFlying = false;
        QuasarClient.minecraft.player.abilities.flying = false;
    }
}

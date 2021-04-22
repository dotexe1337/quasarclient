package dev.semisol.quasarclient.module.flight;

import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.PlayerUtils;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.registry.ConfigOpt;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.opt.FloatOpt;
import net.minecraft.block.AirBlock;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Flight extends Module {
    public static int ticks = 0;
    private FloatOpt HSPEED = new FloatOpt("hSpeed", 1.1f, 0f, 15f, true);
    private FloatOpt VSPEED = new FloatOpt("vSpeed", 0.1f, 0f, 15f, true);
    private ConfigOpt[] OPTS = new ConfigOpt[]{VSPEED, HSPEED};

    @Override
    public ConfigOpt[] getOpts() {
        return OPTS;
    }

    @Override
    public String getId() {
        return "flight";
    }
    @Override
    public boolean persistEnabling() {
        return false;
    }
    @Override
    public void onTick() {
        if(QuasarClient.minecraft.player.input.jumping) {
            QuasarClient.minecraft.player.setVelocity(QuasarClient.minecraft.player.getVelocity().x, this.VSPEED.value, QuasarClient.minecraft.player.getVelocity().z);
        } else if (QuasarClient.minecraft.player.input.sneaking) {
            QuasarClient.minecraft.player.setVelocity(QuasarClient.minecraft.player.getVelocity().x, -this.VSPEED.value, QuasarClient.minecraft.player.getVelocity().z);
        } else {
            QuasarClient.minecraft.player.setVelocity(QuasarClient.minecraft.player.getVelocity().x, 0, QuasarClient.minecraft.player.getVelocity().z);
        }
        if(!QuasarClient.minecraft.player.input.sneaking) PlayerUtils.setMoveSpeed(this.HSPEED.value); else PlayerUtils.setMoveSpeed(0f);
        if(Flight.ticks > 40) {
            if(Utils.getBlockAtPos(QuasarClient.minecraft.player.getPos().getX(), QuasarClient.minecraft.player.getPos().getY() - 0.034, QuasarClient.minecraft.player.getPos().getZ()) instanceof AirBlock) {
                QuasarClient.minecraft.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(QuasarClient.minecraft.player.getPos().getX(), QuasarClient.minecraft.player.getPos().getY() - 0.035, QuasarClient.minecraft.player.getPos().getZ(), true));
                Flight.ticks = 0;
            }
        } else {
            if(Utils.getBlockAtPos(QuasarClient.minecraft.player.getPos().getZ(), QuasarClient.minecraft.player.getPos().getY() + 0.034, QuasarClient.minecraft.player.getPos().getZ()) instanceof AirBlock) {
                QuasarClient.minecraft.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(QuasarClient.minecraft.player.getPos().getX(), QuasarClient.minecraft.player.getPos().getY() + 0.035, QuasarClient.minecraft.player.getPos().getZ(), true));
            }
            Flight.ticks++;
        }
    }
}

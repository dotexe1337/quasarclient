package dev.semisol.quasarclient.etc;

import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Utils {
    public static boolean damageInProgress = false;
    public static void damage(Vec3d pos, int amount){
        damageInProgress = true;
        sendPosUpdate(pos.x, pos.y + 1, pos.z, false, true);
        for (int i = 0; i < 80; i++){
            sendPosUpdate(pos.x, pos.y + 2.1 + amount, pos.z, false, false);
            sendPosUpdate(pos.x, pos.y + 1.05, pos.z, false, false);
        }
        sendPosUpdate(pos.x, pos.y + 0.15, pos.z, true, true);
        damageInProgress = false;
    }
    public static void sendPosUpdate(double x, double y, double z, boolean og, boolean rps){
        if (rps) QuasarClient.minecraft.player.updatePosition(x, y, z); else QuasarClient.minecraft.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(x, y, z, og));
    }
}

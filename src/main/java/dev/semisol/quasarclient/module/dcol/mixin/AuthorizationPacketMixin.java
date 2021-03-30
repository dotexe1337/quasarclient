/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: AuthorizationPacketMixin
# Created by constantin at 19:26, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.dcol.mixin;

import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket.class)
public class AuthorizationPacketMixin {
    @Inject(method = "<init>(Ljava/lang/String;ILnet/minecraft/network/NetworkState;)V", at = @At("TAIL"))
    public void init(String address, int port, NetworkState intendedState, CallbackInfo ci) {
        Globals.ip = address;
        Globals.port = port;
        Globals.gv = SharedConstants.getGameVersion();
    }

}
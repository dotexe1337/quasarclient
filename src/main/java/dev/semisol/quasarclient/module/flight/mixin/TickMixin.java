/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: TickMixin
# Created by constantin at 19:55, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.flight.mixin;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.module.flight.Flight;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerEntity.class)
public class TickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (ModuleRegistry.isOn(ModuleRegistry.getModule("flight")) && !Flight.bypassing && QuasarClient.minecraft.player != null) {
            Flight.ticks++;
            if (Flight.ticks > 20){
                Vec3d p = QuasarClient.minecraft.player.getPos();
                if (Flight.ticks > 21){
                    Flight.ticks = 0;
                    Utils.sendPosUpdate(p.x, p.y + 0.2, p.z, true, true);
                    return;
                }
                Utils.sendPosUpdate(p.x, p.y - 0.2, p.z, true, true);
            }
        }
    }
}

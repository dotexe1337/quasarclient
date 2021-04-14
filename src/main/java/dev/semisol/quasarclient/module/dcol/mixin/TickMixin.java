/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: TickMixin
# Created by constantin at 19:55, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.dcol.mixin;

import com.google.gson.*;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.ArrayList;


@Mixin(ClientPlayerEntity.class)
public class TickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (Globals.ticks >= 0){
            Globals.ticks++;
            if (Globals.ticks > 14){
                Globals.currentCollected.clear();
                String c = Globals.commandsLeft.remove(0);
                ChatMessageC2SPacket cmp = new ChatMessageC2SPacket("/" + c);
                Globals.command = c;
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(cmp);
                Globals.ticks = -1;
            }
        }
        if (Globals.gotMessage){
            if (Globals.tickPassed){
                done();
            } else {
                Globals.tickPassed = true;
            }
        } else if (Globals.inProg && Globals.ticks == -1){
            Globals.timeout++;
            if (Globals.timeout > 20){
                Globals.currentCollected.add("TIMEOUT");
                done();
            }
        }
    }
    public void done(){
        Globals.gotMessage = false;
        Globals.tickPassed = false;
        Globals.mapping.put(Globals.command, Globals.currentCollected);
        Globals.currentCollected = new ArrayList<>();
        Globals.timeout = 0;
        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] Gathered data for " + Globals.command), false);
        if (Globals.commandsLeft.size() == 0){
            Globals.inProg = false;
            MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] §aDone!"), false);
            // TODO: Output report
            JsonObject j = new JsonObject();
            j.addProperty("brand", Globals.sbrand);
            j.addProperty("ip", Globals.ip);
            j.addProperty("port", Globals.port);
            j.addProperty("hs", Long.toString(Globals.hashseed));
            j.addProperty("proto", Globals.gv.getProtocolVersion());
            j.addProperty("proton", Globals.gv.getName());
            JsonArray ja = new JsonArray();
            Globals.commands.forEach(ja::add);
            j.add("commands", ja);
            JsonArray ja2 = new JsonArray();
            Globals.plugins.forEach(ja2::add);
            j.add("plugins", ja2);
            JsonObject j2 = new JsonObject();
            Globals.mapping.forEach((k, v)->{
                JsonArray ja3 = new JsonArray();
                v.forEach(ja3::add);
                j2.add(k, ja3);
            });
            j.add("data", j2);
            File f = new File(MinecraftClient.getInstance().runDirectory, "dcol_report_" + System.currentTimeMillis() + ".json");
            FileWriter os = null;
            try {
                f.createNewFile();
                os = new FileWriter(f);
                QuasarClient.g.toJson(j, os);
            } catch (IOException e) {
                e.printStackTrace();
                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] §cSaving failed"), false);

            }finally{
                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7/§6dcol§7] File saved to .minecraft as " + f.getName()), false);try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Globals.timeout = 0;
            Globals.ticks = 0;
        }
    }
}

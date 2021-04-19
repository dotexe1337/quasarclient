package dev.semisol.quasarclient.registry.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    public ClientPlayerEntityMixin() {
    }

    @Inject(
            at = @At("HEAD"),
            method = "sendChatMessage",
            cancellable = true
    )
    public void onChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("*")){
            StringReader sr = new StringReader(message);
            sr.setCursor(1);
            ParseResults<CommandSource> pr = ModuleRegistry.dispatcher.parse(sr, MinecraftClient.getInstance().player.getCommandSource());
            pr.getExceptions().forEach((cn, cse)->{
                MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §c" + cse.getMessage()), false);
            });
            if (pr.getExceptions().size() == 0){
                try {
                    ModuleRegistry.dispatcher.execute(pr);
                } catch (CommandSyntaxException e) {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§9QuasarClient§7] §c" + e.getMessage()), false);
                }
            }
            ci.cancel();
        }
    }
}

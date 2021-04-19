package dev.semisol.quasarclient.registry.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public class CommandSuggestorMixin {
    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void refresh(CallbackInfo ci,  String string, StringReader reader){
        String t = this.textField.getText();
        if (reader.canRead(1) && reader.getString().startsWith("*")){
            assert MinecraftClient.getInstance().player != null;
            reader.setCursor(reader.getCursor() + 1);
            parse = ModuleRegistry.dispatcher.parse(reader, MinecraftClient.getInstance().player.getCommandSource());
            if (this.window == null || !this.completingSuggestions) {
                this.pendingSuggestions = ModuleRegistry.dispatcher.getCompletionSuggestions(parse, this.textField.getCursor());
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.show();
                    }
                });
            }
            ci.cancel();
        }
    }
    @Shadow
    private TextFieldWidget textField;
    @Shadow
    private CommandSuggestor.SuggestionWindow window;
    @Shadow
    private boolean completingSuggestions;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow
    public void show(){

    }
    @Shadow
    ParseResults<CommandSource> parse;
}

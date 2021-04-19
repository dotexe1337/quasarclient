package dev.semisol.quasarclient.registry.mixin;

import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderWorld(CallbackInfo ci) {
        ModuleRegistry.onRender();
    }
}


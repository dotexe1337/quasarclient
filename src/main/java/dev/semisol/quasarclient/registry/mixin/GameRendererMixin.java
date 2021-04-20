package dev.semisol.quasarclient.registry.mixin;

import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderWorld(CallbackInfo ci) {
        ModuleRegistry.onHudRender();
    }
    @Inject(at = @At("RETURN"), method = "renderWorld")
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        ModuleRegistry.onRender();
    }
}


package dev.semisol.quasarclient.module.tps.mixin;

import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.RenderHelper;
import dev.semisol.quasarclient.module.tps.TPS;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderWorld(CallbackInfo ci) {
        if (TPS.chartOn && QuasarClient.minecraft.world != null){
            int h = QuasarClient.minecraft.getWindow().getScaledHeight() - 10;
            int t = 2;
            double p = 3;
            int lt = (int) Math.min(5, Math.max((QuasarClient.minecraft.getWindow().getScaleFactor()), 1));
            double prev = 0;
            RenderHelper.renderLineScreen(new Vec3d(5, h - (20*p), 0), new Vec3d((29*t) + 5, h - (20*p), 0), new Color(0xFF00FF00, true), lt);
            RenderHelper.renderLineScreen(new Vec3d(5, h, 0), new Vec3d((29*t) + 5, h, 0), new Color(0xFFFF0000, true), lt);
            for (int i = 0; i < 30; i++){
                if (i == 0){
                    prev = TPS.last30[i];
                    continue;
                }
                double curr = TPS.last30[i];
                RenderHelper.renderLineScreen(new Vec3d(((i-1) * t) + 5, h - ((int) (prev * p)), 0), new Vec3d(i*t + 5, h - ((int) (curr * p)), 0), new Color(0xFFEEFF00, true), lt);
                prev = curr;
            }
            for (int i = 0; i < 30; i++){
                if (i == 0){
                    prev = TPS.ls10t[i];
                    continue;
                }
                double curr = TPS.ls10t[i];
                RenderHelper.renderLineScreen(new Vec3d(((i-1) * t) + 5, h - ((int) (prev * p)), 0), new Vec3d(i*t + 5, h - ((int) (curr * p)), 0), new Color(0xFF00ACFF, true), lt);
                prev = curr;
            }

        }
    }
}


package dev.semisol.quasarclient.module.autocg.mixin;

import dev.semisol.quasarclient.module.autocg.AutoCG;
import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.network.MessageType;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GameMessageS2CPacket.class)
public class GameMessageS2CPacketMixin {
    private static final Pattern pattern = Pattern.compile("^CHAT GAMES Whoever types ([^ ]+) first WINS!$");
    @Inject(method = "apply", at = @At("TAIL"))
    public void apply(ClientPlayPacketListener arg, CallbackInfo ci){
        if (AutoCG.on){

            final Matcher matcher = pattern.matcher(this.message.getString());
            if (matcher.find()) {
                AutoCG.word = matcher.group(1);
                AutoCG.ticks = 11 + (AutoCG.word.length() * 2) + ((int) Math.floor(Math.random() * 9));
            }
        }
    }
    @Shadow
    public Text message;
    @Shadow
    public MessageType location;

}

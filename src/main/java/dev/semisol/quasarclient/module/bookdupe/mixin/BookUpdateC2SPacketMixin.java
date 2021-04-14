package dev.semisol.quasarclient.module.bookdupe.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BookUpdateC2SPacket.class})
public class BookUpdateC2SPacketMixin {
    private static final String str1;
    private static final String str2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    @Shadow
    private ItemStack book;

    public BookUpdateC2SPacketMixin() {
    }

    @Inject(
            at = {@At("RETURN")},
            method = {"<init>(Lnet/minecraft/item/ItemStack;ZI)V"}
    )
    public void onInit(ItemStack book, boolean signed, int slot, CallbackInfo ci) {
        if (signed && book.getTag().getList("pages", 8).getString(0).equals("DUPE")) {
            ListTag listTag = new ListTag();
            listTag.add(0, StringTag.of(str1));

            for(int i = 1; i < 38; ++i) {
                listTag.add(i, StringTag.of("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
            }

            this.book.putSubTag("pages", listTag);
        }
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < 21845; ++i) {
            stringBuilder.append('ࠀ');
        }

        str1 = stringBuilder.toString();
    }
}

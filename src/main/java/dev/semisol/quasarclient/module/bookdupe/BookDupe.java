package dev.semisol.quasarclient.module.bookdupe;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.module.bookdupe.mixin.BookUpdateC2SPacketMixin;
import dev.semisol.quasarclient.module.dcol.Globals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BookDupe {
    public static void init(){
        QuasarClient.disp.register(
                LiteralArgumentBuilder.<CommandSource>literal("dupe")
                        .executes(cc -> {
                            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "writable_book")), 1);
                            ListTag pages = new ListTag();
                            pages.add(0, StringTag.of("DUPE"));
                            itemStack.putSubTag("pages", pages);
                            itemStack.putSubTag("title", StringTag.of("a"));
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new BookUpdateC2SPacket(itemStack, true, MinecraftClient.getInstance().player.inventory.selectedSlot));
                            return 0;
                        })
        );
    }
}
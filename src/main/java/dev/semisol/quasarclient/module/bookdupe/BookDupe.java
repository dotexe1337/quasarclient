package dev.semisol.quasarclient.module.bookdupe;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.semisol.quasarclient.QuasarClient;
import dev.semisol.quasarclient.etc.Utils;
import dev.semisol.quasarclient.registry.Module;
import dev.semisol.quasarclient.registry.ModuleRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BookDupe extends Module {
    @Override
    public String getId() {
        return "bookdupe";
    }

    @Override
    public void onRegistered() {
        ModuleRegistry.dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("dupe")
                        .executes(cc -> {
                            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "writable_book")), 1);
                            ListTag pages = new ListTag();
                            pages.add(0, StringTag.of("DUPE"));
                            itemStack.putSubTag("pages", pages);
                            itemStack.putSubTag("title", StringTag.of("a"));
                            QuasarClient.minecraft.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(itemStack, true, QuasarClient.minecraft.player.inventory.selectedSlot));
                            return 0;
                        })
        );
    }

    @Override
    public boolean isPassive() {
        return true;
    }
}

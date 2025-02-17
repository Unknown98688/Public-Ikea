package org.vined.ikea.modules.utility;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.item.Item;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;

import org.vined.ikea.IKEA;

import java.util.List;

public class AutoItemMove extends Module {
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Which items to put in the container.")
        .build()
    );

    public AutoItemMove() {
        super(IKEA.UTILITY, "auto-item-move", "Automatically puts items in a container.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;
        ScreenHandler handler = mc.player.currentScreenHandler;
        if (handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler) {
            moveItems(handler);
        }
    }

    private int getRows(ScreenHandler handler) {
        return (handler instanceof GenericContainerScreenHandler ? ((GenericContainerScreenHandler) handler).getRows() : 3);
    }

    public void moveItems(ScreenHandler handler) {
        int playerInvOffset = getRows(handler) * 9;
        MeteorExecutor.execute(() -> moveSlots(handler, playerInvOffset, playerInvOffset + 4 * 9));
    }

    private void moveSlots(ScreenHandler handler, int start, int end) {
        for (int i = start; i < end; i++) {
            Slot slot = handler.getSlot(i);
            if (!slot.hasStack()) continue;
            if (items.get().contains(slot.getStack().getItem())) {
                if (mc.currentScreen == null) break;

                InvUtils.quickMove().slotId(i);
            }
        }
    }
}

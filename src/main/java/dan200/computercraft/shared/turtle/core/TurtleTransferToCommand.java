/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.core;

import javax.annotation.Nonnull;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.ItemStorage;

import net.minecraft.item.ItemStack;

public class TurtleTransferToCommand implements ITurtleCommand {
    private final int m_slot;
    private final int m_quantity;

    public TurtleTransferToCommand(int slot, int limit) {
        this.m_slot = slot;
        this.m_quantity = limit;
    }

    @Nonnull
    @Override
    public TurtleCommandResult execute(@Nonnull ITurtleAccess turtle) {
        // Take stack
        ItemStorage storage = ItemStorage.wrap(turtle.getInventory());
        ItemStack stack = InventoryUtil.takeItems(this.m_quantity, storage, turtle.getSelectedSlot(), 1, turtle.getSelectedSlot());
        if (stack.isEmpty()) {
            turtle.playAnimation(TurtleAnimation.Wait);
            return TurtleCommandResult.success();
        }

        // Store stack
        ItemStack remainder = InventoryUtil.storeItems(stack, storage, this.m_slot, 1, this.m_slot);
        if (!remainder.isEmpty()) {
            // Put the remainder back
            InventoryUtil.storeItems(remainder, storage, turtle.getSelectedSlot(), 1, turtle.getSelectedSlot());
        }

        // Return true if we moved anything
        if (remainder != stack) {
            turtle.playAnimation(TurtleAnimation.Wait);
            return TurtleCommandResult.success();
        } else {
            return TurtleCommandResult.failure("No space for items");
        }
    }
}

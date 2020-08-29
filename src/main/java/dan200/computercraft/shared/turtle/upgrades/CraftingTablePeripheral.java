/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.upgrades;

import static dan200.computercraft.core.apis.ArgumentHelper.optInt;

import javax.annotation.Nonnull;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.TurtleCraftCommand;

public class CraftingTablePeripheral implements IPeripheral {
    private final ITurtleAccess turtle;

    public CraftingTablePeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    @Nonnull
    @Override
    public String getType() {
        return "workbench";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {
            "craft",
            };
    }

    @Override
    public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        switch (method) {
        case 0: {
            // craft
            final int limit = parseCount(arguments);
            return this.turtle.executeCommand(context, new TurtleCraftCommand(limit));
        }
        default:
            return null;
        }
    }

    private static int parseCount(Object[] arguments) throws LuaException {
        int count = optInt(arguments, 0, 64);
        if (count < 0 || count > 64) {
            throw new LuaException("Crafting count " + count + " out of range");
        }
        return count;
    }

    @Override
    public boolean equals(IPeripheral other) {
        return this == other || other instanceof CraftingTablePeripheral;
    }
}

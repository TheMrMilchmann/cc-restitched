/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network.server;

import javax.annotation.Nonnull;

import dan200.computercraft.shared.computer.core.IContainerComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;

import net.minecraft.network.PacketByteBuf;

public class ComputerActionServerMessage extends ComputerServerMessage {
    private Action action;

    public ComputerActionServerMessage(int instanceId, Action action) {
        super(instanceId);
        this.action = action;
    }

    public ComputerActionServerMessage() {
    }

    @Override
    public void toBytes(@Nonnull PacketByteBuf buf) {
        super.toBytes(buf);
        buf.writeEnumConstant(this.action);
    }

    @Override
    public void fromBytes(@Nonnull PacketByteBuf buf) {
        super.fromBytes(buf);
        this.action = buf.readEnumConstant(Action.class);
    }

    @Override
    protected void handle(@Nonnull ServerComputer computer, @Nonnull IContainerComputer container) {
        switch (this.action) {
        case TURN_ON:
            computer.turnOn();
            break;
        case REBOOT:
            computer.reboot();
            break;
        case SHUTDOWN:
            computer.shutdown();
            break;
        }
    }

    public enum Action {
        TURN_ON, SHUTDOWN, REBOOT
    }
}

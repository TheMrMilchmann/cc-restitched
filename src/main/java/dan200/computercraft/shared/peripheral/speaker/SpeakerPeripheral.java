/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.speaker;

import static dan200.computercraft.core.apis.ArgumentHelper.getString;
import static dan200.computercraft.core.apis.ArgumentHelper.optReal;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.block.enums.Instrument;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SpeakerPeripheral implements IPeripheral {
    private final AtomicInteger m_notesThisTick = new AtomicInteger();
    private long m_clock = 0;
    private long m_lastPlayTime = 0;

    public void update() {
        this.m_clock++;
        this.m_notesThisTick.set(0);
    }

    public boolean madeSound(long ticks) {
        return this.m_clock - this.m_lastPlayTime <= ticks;
    }

    @Nonnull
    @Override
    public String getType() {
        return "speaker";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {
            "playSound",
            // Plays sound at resourceLocator
            "playNote"
            // Plays note
        };
    }

    @Override
    public Object[] callMethod(@Nonnull IComputerAccess computerAccess, @Nonnull ILuaContext context, int methodIndex, @Nonnull Object[] args) throws LuaException {
        switch (methodIndex) {
        case 0: // playSound
        {
            String name = getString(args, 0);
            float volume = (float) optReal(args, 1, 1.0);
            float pitch = (float) optReal(args, 2, 1.0);

            Identifier identifier;
            try {
                identifier = new Identifier(name);
            } catch (InvalidIdentifierException e) {
                throw new LuaException("Malformed sound name '" + name + "' ");
            }

            return new Object[] {this.playSound(context, identifier, volume, pitch, false)};
        }

        case 1: // playNote
            return this.playNote(args, context);

        default:
            throw new IllegalStateException("Method index out of range!");
        }
    }

    private synchronized boolean playSound(ILuaContext context, Identifier name, float volume, float pitch, boolean isNote) throws LuaException {
        if (this.m_clock - this.m_lastPlayTime < TileSpeaker.MIN_TICKS_BETWEEN_SOUNDS && (!isNote || this.m_clock - this.m_lastPlayTime != 0 || this.m_notesThisTick.get() >= ComputerCraft.maxNotesPerTick)) {
            // Rate limiting occurs when we've already played a sound within the last tick, or we've
            // played more notes than allowable within the current tick.
            return false;
        }

        World world = this.getWorld();
        Vec3d pos = this.getPosition();

        context.issueMainThreadTask(() -> {
            MinecraftServer server = world.getServer();
            if (server == null) {
                return null;
            }

            float adjVolume = Math.min(volume, 3.0f);
            server.getPlayerManager()
                  .sendToAround(null,
                                pos.x,
                                pos.y,
                                pos.z,
                                adjVolume > 1.0f ? 16 * adjVolume : 16.0,
                                world.getDimension()
                                     .getType(),
                                new PlaySoundIdS2CPacket(name, SoundCategory.RECORDS, pos, adjVolume, pitch));
            return null;
        });

        this.m_lastPlayTime = this.m_clock;
        return true;
    }

    @Nonnull
    private synchronized Object[] playNote(Object[] arguments, ILuaContext context) throws LuaException {
        String name = getString(arguments, 0);
        float volume = (float) optReal(arguments, 1, 1.0);
        float pitch = (float) optReal(arguments, 2, 1.0);

        Instrument instrument = null;
        for (Instrument testInstrument : Instrument.values()) {
            if (testInstrument.asString()
                              .equalsIgnoreCase(name)) {
                instrument = testInstrument;
                break;
            }
        }

        // Check if the note exists
        if (instrument == null) {
            throw new LuaException("Invalid instrument, \"" + name + "\"!");
        }

        // If the resource location for note block notes changes, this method call will need to be updated
        boolean success = this.playSound(context,
                                         instrument.getSound()
                                              .getId(),
                                         volume,
                                         (float) Math.pow(2.0, (pitch - 12.0) / 12.0),
                                         true);

        if (success) {
            this.m_notesThisTick.incrementAndGet();
        }
        return new Object[] {success};
    }

    public abstract World getWorld();

    public abstract Vec3d getPosition();
}


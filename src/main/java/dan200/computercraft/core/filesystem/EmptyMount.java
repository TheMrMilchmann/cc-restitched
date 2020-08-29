/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import javax.annotation.Nonnull;

import dan200.computercraft.api.filesystem.IMount;

public class EmptyMount implements IMount {
    @Override
    public boolean exists(@Nonnull String path) {
        return path.isEmpty();
    }

    @Override
    public boolean isDirectory(@Nonnull String path) {
        return path.isEmpty();
    }

    @Override
    public void list(@Nonnull String path, @Nonnull List<String> contents) {
    }

    @Override
    public long getSize(@Nonnull String path) {
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public ReadableByteChannel openChannelForRead(@Nonnull String path) throws IOException {
        throw new IOException("/" + path + ": No such file");
    }

    @Nonnull
    @Override
    @Deprecated
    public InputStream openForRead(@Nonnull String path) throws IOException {
        throw new IOException("/" + path + ": No such file");
    }
}

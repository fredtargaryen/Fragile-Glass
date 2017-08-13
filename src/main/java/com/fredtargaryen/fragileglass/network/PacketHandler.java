package com.fredtargaryen.fragileglass.network;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DataReference.MODID);

    public static void init()
    {
        INSTANCE.registerMessage(MessageBreakerMovement.class, MessageBreakerMovement.class, 0, Side.SERVER);
    }
}

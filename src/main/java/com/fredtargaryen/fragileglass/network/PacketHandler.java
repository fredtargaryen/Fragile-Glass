package com.fredtargaryen.fragileglass.network;

import com.fredtargaryen.fragileglass.DataReference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DataReference.MODID, "channel"),
            () -> "1.0", //version that will be offered to the server
            (String s) -> s.equals("1.0"), //client accepted versions
            (String s) -> s.equals("1.0"));//server accepted versions

    public static void init()
    {
        INSTANCE.registerMessage(0, MessageBreakerMovement.class, MessageBreakerMovement::toBytes, MessageBreakerMovement::new, MessageBreakerMovement::onMessage);
    }
}

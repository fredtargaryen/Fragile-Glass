package com.fredtargaryen.fragileglass.network;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to handle player movement. See IPlayerBreakCapability, PlayerBreakStorage and PlayerBreakFactory for more
 * information.
 */
public class MessageBreakerMovement {
    public double motionx;
    public double motiony;
    public double motionz;
    public double speedSq;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ctx.get().getSender().getCapability(FragileGlassBase.PLAYERBREAKCAP, null)
                .ifPresent(pbc -> pbc.onMessage(this)));
        ctx.get().setPacketHandled(true);
    }

    public MessageBreakerMovement(){}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageBreakerMovement(ByteBuf buf) {
        this.motionx = buf.readDouble();
        this.motiony = buf.readDouble();
        this.motionz = buf.readDouble();
        this.speedSq = buf.readDouble();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.motionx);
        buf.writeDouble(this.motiony);
        buf.writeDouble(this.motionz);
        buf.writeDouble(this.speedSq);
    }
}

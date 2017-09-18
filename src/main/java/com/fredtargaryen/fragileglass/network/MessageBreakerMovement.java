package com.fredtargaryen.fragileglass.network;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Used to handle player movement. See IPlayerBreakCapability, PlayerBreakStorage and PlayerBreakFactory for more
 * information.
 */
public class MessageBreakerMovement implements IMessage, IMessageHandler<MessageBreakerMovement, IMessage>
{
    public double motionx;
    public double motiony;
    public double motionz;
    public double speed;

    @Override
    public IMessage onMessage(final MessageBreakerMovement message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IThreadListener serverWorld = player.getServerWorld();
        serverWorld.addScheduledTask(() -> {
            player.getCapability(FragileGlassBase.PLAYERBREAKCAP, null).onMessage(message);
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.motionx = buf.readDouble();
        this.motiony = buf.readDouble();
        this.motionz = buf.readDouble();
        this.speed = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeDouble(this.motionx);
        buf.writeDouble(this.motiony);
        buf.writeDouble(this.motionz);
        buf.writeDouble(this.speed);
    }
}

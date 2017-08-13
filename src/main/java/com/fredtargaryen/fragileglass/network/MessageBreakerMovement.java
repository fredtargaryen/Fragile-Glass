package com.fredtargaryen.fragileglass.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBreakerMovement implements IMessage, IMessageHandler<MessageBreakerMovement, IMessage>
{
    public double minx;
    public double maxx;
    public double miny;
    public double maxy;
    public double minz;
    public double maxz;
    public double motionx;
    public double motiony;
    public double motionz;
    public int blockx;
    public int blocky;
    public int blockz;

    @Override
    public IMessage onMessage(final MessageBreakerMovement message, MessageContext ctx)
    {
        final IThreadListener serverWorld = ctx.getServerHandler().player.getServerWorld();
        serverWorld.addScheduledTask(() -> {
            WorldServer castedServerWorld = (WorldServer)serverWorld;
            castedServerWorld.destroyBlock(new BlockPos(message.blockx, message.blocky, message.blockz), false);
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.blockx = buf.readInt();
        this.blocky = buf.readInt();
        this.blockz = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.blockx);
        buf.writeInt(this.blocky);
        buf.writeInt(this.blockz);
    }
}

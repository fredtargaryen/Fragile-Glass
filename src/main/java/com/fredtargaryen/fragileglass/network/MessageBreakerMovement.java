package com.fredtargaryen.fragileglass.network;

import com.fredtargaryen.fragileglass.entity.capability.CommonBreakingMethods;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBreakerMovement implements IMessage, IMessageHandler<MessageBreakerMovement, IMessage>
{
    public double motionx;
    public double motiony;
    public double motionz;
    public double distance;

    @Override
    public IMessage onMessage(final MessageBreakerMovement message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IThreadListener serverWorld = player.getServerWorld();
        serverWorld.addScheduledTask(() -> {
            CommonBreakingMethods.breakBlocksInWay(player, message.motionx, message.motiony, message.motionz, message.distance);
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.motionx = buf.readDouble();
        this.motiony = buf.readDouble();
        this.motionz = buf.readDouble();
        this.distance = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeDouble(this.motionx);
        buf.writeDouble(this.motiony);
        buf.writeDouble(this.motionz);
        buf.writeDouble(this.distance);
    }
}

package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class CommandData extends FragilityData implements ICommandSource {
    private boolean executeFromCrasherPos;
    private String command;

    public CommandData(double breakSpeed) {
        super(breakSpeed);
    }

    @Override
    public FragilityData.FragileBehaviour getBehaviour() {
        return FragilityData.FragileBehaviour.COMMAND;
    }

    @Override
    public void parseExtraData(@Nullable BlockState state, ConfigLoader cl, String[] extraData) throws FragilityDataParseException {
        if(extraData.length == 0) {
            throw new FragilityDataParseException("No position given for command behaviour!");
        }
        else if(extraData.length == 1) {
            throw new FragilityDataParseException("No command given for command behaviour!");
        }
        else if(extraData[1].charAt(0) != '/') {
            throw new FragilityDataParseException("The first word of the command must begin with a /");
        }
        else {
            //Validate first param
            if(extraData[0].toLowerCase().equals("block")) {
                this.executeFromCrasherPos = false;
            }
            else if(extraData[0].toLowerCase().equals("entity")) {
                this.executeFromCrasherPos = true;
            }
            else {
                throw new FragilityDataParseException("The first value (" + extraData[0] + ") should be block if you want to execute the command relative to the block, or entity if relative to the entity that crashed.");
            }
            //Don't validate the command; too troublesome
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < extraData.length - 1; ++i) {
                sb.append(extraData[i]);
                sb.append(" ");
            }
            sb.append(extraData[extraData.length - 1]);
            this.command = sb.toString();
        }
    }

    @Override
    public void onCrash(World world, BlockState state, @Nullable TileEntity te, BlockPos pos, @Nullable Entity crasher, double speedSq) {
        CommandSource cs = new CommandSource(
                this, //Some ICommandSource
                this.executeFromCrasherPos ? crasher.getPositionVec() : new Vec3d(pos.getX(), pos.getY(), pos.getZ()), //position
                this.executeFromCrasherPos ? crasher.getPitchYaw() : Vec2f.ZERO, //rotation
                (ServerWorld) world, //world
                2, //permission level
                "", //internal name
                new StringTextComponent(""), //name for display
                world.getServer(), //server
                crasher);
        world.getServer().getCommandManager().handleCommand(cs, this.command);
    }

    @Override
    public String toString() {
        return super.toString() + " " + (this.executeFromCrasherPos ? "entity" : "block") + " " + this.command;
    }

    /**
     * @return true only if the command executes relative to a block. Otherwise, this depends on an entity which may not
     * exist when the wait is over.
     */
    @Override
    public boolean canBeQueued() {
        return !this.executeFromCrasherPos;
    }

    /////////////////////////////////
    //ICOMMANDSOURCE IMPLEMENTATION//
    /////////////////////////////////
    @Override
    public void sendMessage(ITextComponent iTextComponent) {

    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldReceiveErrors() {
        return false;
    }

    @Override
    public boolean allowLogging() {
        return false;
    }
}

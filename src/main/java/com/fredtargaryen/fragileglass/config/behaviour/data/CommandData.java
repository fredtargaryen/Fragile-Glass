package com.fredtargaryen.fragileglass.config.behaviour.data;

import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class CommandData extends FragilityData implements ICommandSource {
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
            throw new FragilityDataParseException("No command given for command behaviour!");
        }
        else if(extraData[0].charAt(0) != '/') {
            throw new FragilityDataParseException("The first word of the command must begin with a /");
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < extraData.length - 1; ++i) {
                sb.append(extraData[i]);
                sb.append(" ");
            }
            sb.append(extraData[extraData.length - 1]);
            this.command = sb.toString();
        }
    }

    @Override
    public void onCrash(@Nullable BlockState state, @Nullable TileEntity te, BlockPos pos, Entity crasher, double speedSq) {
        CommandSource cs = new CommandSource(
                this, //Some ICommandSource
                crasher.getPositionVec(), //position
                crasher.getPitchYaw(), //rotation
                (ServerWorld) crasher.world, //world
                2, //permission level
                "", //internal name
                new StringTextComponent(""), //name for display
                crasher.world.getServer(), //server
                crasher);
        crasher.world.getServer().getCommandManager().handleCommand(cs, this.command);
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.command;
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

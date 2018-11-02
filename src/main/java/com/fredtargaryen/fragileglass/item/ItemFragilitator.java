package com.fredtargaryen.fragileglass.item;

import com.fredtargaryen.fragileglass.tileentity.TileEntityBlockMadeFragile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: Item icon
public class ItemFragilitator extends Item {
    @Override
    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
			if(!worldIn.getBlockState(pos).hasTileEntity()) worldIn.setTileEntity(pos, new TileEntityBlockMadeFragile());
		}
        return EnumActionResult.PASS;
    }
}

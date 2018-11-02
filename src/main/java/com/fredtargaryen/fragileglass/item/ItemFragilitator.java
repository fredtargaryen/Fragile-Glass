package com.fredtargaryen.fragileglass.item;

import com.fredtargaryen.fragileglass.tileentity.TileEntityBlockMadeFragile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFragilitator extends Item {
    public ItemFragilitator() {
        super();
        this.setUnlocalizedName("fragilitator");
        this.setCreativeTab(CreativeTabs.TOOLS);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            IBlockState state = worldIn.getBlockState(pos);
			if(!state.getBlock().hasTileEntity(state)) worldIn.setTileEntity(pos, new TileEntityBlockMadeFragile());
		}
        return EnumActionResult.PASS;
    }
}

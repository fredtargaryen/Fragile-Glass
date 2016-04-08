package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.client.particle.EntityMyBubbleFX;
import com.fredtargaryen.fragileglass.client.particle.EntityMySplashFX;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Sugar Cauldron Stage Guide:
 * 0 - Empty (nothing inside)
 * 1 - Has water (water inside)
 * 2 - Has sugar water (lighter coloured water)
 * 3 - Boiling 1 (Same, very rare bubbles)
 * 4 - Boiling 2 (Same, with about 3 bubbles)
 * 5 - Boiling 3 (Same, with about 6 bubbles)
 * 6 - Boiled (Glass on top)
 */
public class BlockSugarCauldron extends Block
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 6);

    private static final int thirdOfCookTime = 100;

    public BlockSugarCauldron()
    {
        super(Material.iron);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
        this.setStepSound(SoundType.METAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (Integer)(state.getValue(STAGE));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{STAGE});
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
        {
            if(state.getValue(STAGE).equals(1))
            {
                this.splash(w, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        else
        {
            int i1 = (Integer)(state.getValue(STAGE));
            ItemStack itemstack = player.inventory.getCurrentItem();
            if(i1 == 0)
            {
                if (itemstack == null)
                {
                    return true;
                }
                else
                {
                    if (itemstack.getItem() == Items.water_bucket)
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                        }
                        w.playSound((double) pos.getX(), (double) pos.getY(), (double)pos.getZ(), SoundEvent.soundEventRegistry.getObject(new ResourceLocation("random.splash")), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                        w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 1), 3);
                    }
                    return true;
                }
            }
            else if(i1 == 1)
            {
                if (itemstack == null)
                {
                    return true;
                }
                else
                {
                    Item i = itemstack.getItem();
                    if(i == Item.getItemFromBlock(FragileGlassBase.sugarBlock))
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            ItemStack newStack = ItemStack.copyItemStack(itemstack);
                            --newStack.stackSize;
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
                        }
                        w.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvent.soundEventRegistry.getObject(new ResourceLocation("random.splash")), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                        w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 2), 3);
                    }
                    return true;
                }
            }
            else if(i1 == 6)
            {
                w.spawnEntityInWorld(new EntityItem(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, new ItemStack(FragileGlassBase.fragileGlass, 16)));
                w.spawnEntityInWorld(new EntityXPOrb(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, 4));
                w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 0), 3);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockAdded(World w, BlockPos pos, IBlockState state)
    {
        w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 0), 3);
        w.scheduleUpdate(pos, this, 50);
    }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random r)
    {
        Block below = w.getBlockState(pos.offset(EnumFacing.DOWN, 1)).getBlock();
        int m = (Integer)(state.getValue(STAGE));
        if(m < 2 || m == 6)
        {
            w.scheduleUpdate(pos, this, 50);
        }
        else if(m == 2)
        {
            if(below == Blocks.lit_furnace || below == Blocks.fire || below == Blocks.lava)
            {
                w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 3), 3);
                w.scheduleUpdate(pos, this, thirdOfCookTime);
            }
            else
            {
                w.scheduleUpdate(pos, this, 10);
            }
        }
        else if(m > 6)
        {
            w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, 0), 3);
            w.scheduleUpdate(pos, this, 50);
        }
        else
        {
            if(below == Blocks.lit_furnace || below == Blocks.fire || below == Blocks.lava)
            {
                ++m;
                w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, m), 3);
                w.scheduleUpdate(pos, this, m == 6 ? 50 : thirdOfCookTime);
            }
            else
            {
                --m;
                w.setBlockState(pos, this.getDefaultState().withProperty(STAGE, m), 3);
                w.scheduleUpdate(pos, this, m == 2 ? 10 : thirdOfCookTime);
            }
        }
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random r)
    {
        int m = (Integer)(state.getValue(STAGE));
        if(m > 2 && m < 6)
        {
            boolean shouldBubble = true;
            if(m == 3)
            {
                shouldBubble = r.nextInt(4) == 0;
            }
            else if(m == 4)
            {
                shouldBubble = r.nextBoolean();
            }
            if(shouldBubble)
            {
                this.spawnEntityFX(new EntityMyBubbleFX(world, pos.getX() + 0.125 + r.nextFloat() * 0.75, pos.getY() + 1, pos.getZ() + 0.125 + r.nextFloat() * 0.75, 0.0D, 0.00D, 0.0D));
            }
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World w, BlockPos pos, IBlockState state)
    {
        if(state.getValue(STAGE).equals(6) && !w.isRemote)
        {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            EntityItem entityItem = new EntityItem(w, x + 0.5D, y + 1.0D, z + 0.5D, new ItemStack(FragileGlassBase.fragileGlass, 16));
            w.spawnEntityInWorld(entityItem);
            w.spawnEntityInWorld(new EntityXPOrb(w, x + 0.5, y + 0.5, z + 0.5, 4));
        }
        super.onBlockDestroyedByPlayer(w, pos, state);
    }

    @SideOnly(Side.CLIENT)
    private void splash(World w, int x, int y, int z)
    {
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
        this.spawnEntityFX(new EntityMySplashFX(w, x + 0.5, y + 1.0, z + 0.5));
    }

    /*
     * Makes particles not spawn if out of render range - thanks to LapisSea
     * For use instead of addEffect
     */
    @SideOnly(Side.CLIENT)
    private void spawnEntityFX(EntityFX particleFX)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (mc != null && renderViewEntity != null && mc.effectRenderer != null)
        {
//            int i = mc.gameSettings.particleSetting;
//            double d6 = renderViewEntity.posX - particleFX.posX;
//            double d7 = renderViewEntity.posY - particleFX.posY;
//            double d8 = renderViewEntity.posZ - particleFX.posZ;
//            double d9 = Math.sqrt(mc.gameSettings.renderDistanceChunks) * 45;
//            if (i <= 1)
//            {
//                if (d6 * d6 + d7 * d7 + d8 * d8 <= d9 * d9)
                    Minecraft.getMinecraft().effectRenderer.addEffect(particleFX);
           // }
        }
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
}

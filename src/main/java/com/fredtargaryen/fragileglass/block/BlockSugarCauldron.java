package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.client.particle.ParticleMyBubble;
import com.fredtargaryen.fragileglass.client.particle.ParticleMySplash;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static net.minecraft.state.properties.BlockStateProperties.AGE_0_7;

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
public class BlockSugarCauldron extends Block {
    private static final int thirdOfCookTime = 100;

    public BlockSugarCauldron()
    {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(5.0F, 10.0F));
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE_0_7, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(AGE_0_7);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World w, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
        {
            if(state.get(AGE_0_7).equals(1))
            {
                this.splash(w, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        else
        {
            int i1 = state.get(AGE_0_7);
            ItemStack itemstack = player.inventory.getCurrentItem();
            if(i1 == 0)
            {
                if (itemstack.getItem() == Items.WATER_BUCKET)
                {
                    if (!player.abilities.isCreativeMode)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BUCKET));
                    }
                    w.playSound(null, pos, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 1), 3);
                }
                return true;
            }
            else if(i1 == 1)
            {
                Item i = itemstack.getItem();
                if(i == Item.getItemFromBlock(FragileGlassBase.SUGAR_BLOCK))
                {
                    if (!player.abilities.isCreativeMode)
                    {
                        ItemStack newStack = itemstack.copy();
                        newStack.grow(-1);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
                    }
                    w.playSound(null, pos, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 2), 3);
                }
                return true;
            }
            else if(i1 == 6)
            {
                w.spawnEntity(new EntityItem(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, new ItemStack(FragileGlassBase.FRAGILE_GLASS, 16)));
                w.spawnEntity(new EntityXPOrb(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, 4));
                w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 0), 3);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState)
    {
        worldIn.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 0), 3);
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 50);
    }

    @Override
    public void tick(IBlockState state, World w, BlockPos pos, Random random)
    {
        IBlockState stateBelow = w.getBlockState(pos.down());
        Block below = stateBelow.getBlock();
        int m = state.get(AGE_0_7);
        if(m < 2 || m == 6)
        {
            w.getPendingBlockTicks().scheduleTick(pos, this, 50);
        }
        else if(m == 2)
        {
            if(below == Blocks.FIRE || below == Blocks.LAVA || (below == Blocks.FURNACE && stateBelow.get(BlockFurnace.LIT)))
            {
                w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 3), 3);
                w.getPendingBlockTicks().scheduleTick(pos, this, thirdOfCookTime);
            }
            else
            {
                w.getPendingBlockTicks().scheduleTick(pos, this, 10);
            }
        }
        else if(m > 6)
        {
            w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 0), 3);
            w.getPendingBlockTicks().scheduleTick(pos, this, 50);
        }
        else
        {
            if(below == Blocks.FIRE || below == Blocks.LAVA || (below == Blocks.FURNACE && stateBelow.get(BlockFurnace.LIT)))
            {
                ++m;
                w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, m), 3);
                w.getPendingBlockTicks().scheduleTick(pos, this, m == 6 ? 50 : thirdOfCookTime);
            }
            else
            {
                --m;
                w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, m), 3);
                w.getPendingBlockTicks().scheduleTick(pos, this, m == 2 ? 10 : thirdOfCookTime);
            }
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(IBlockState state, World world, BlockPos pos, Random r)
    {
        int m = state.get(AGE_0_7);
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
                this.spawnParticle(new ParticleMyBubble(world, pos.getX() + 0.125 + r.nextFloat() * 0.75, pos.getY() + 1, pos.getZ() + 0.125 + r.nextFloat() * 0.75));
            }
        }
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state)
    {
        World w = (World) worldIn;
        if(state.get(AGE_0_7).equals(6) && !w.isRemote)
        {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            EntityItem entityItem = new EntityItem(w, x + 0.5D, y + 1.0D, z + 0.5D, new ItemStack(FragileGlassBase.FRAGILE_GLASS, 16));
            w.spawnEntity(entityItem);
            w.spawnEntity(new EntityXPOrb(w, x + 0.5, y + 0.5, z + 0.5, 4));
        }
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    private void splash(World w, int x, int y, int z)
    {
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
        this.spawnParticle(new ParticleMySplash(w, x + 0.5, y + 0.8, z + 0.5));
    }

    /*
     * Makes particles not spawn if out of render range - thanks to LapisSea
     * For use instead of addEffect
     */
    @OnlyIn(Dist.CLIENT)
    private void spawnParticle(Particle particleFX)
    {
        Minecraft mc = Minecraft.getInstance();
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity != null && mc.particles != null)
        {
            int i = mc.gameSettings.particleSetting;
            AxisAlignedBB aabb = particleFX.getBoundingBox();
            double d6 = renderViewEntity.posX - aabb.minX;
            double d7 = renderViewEntity.posY - aabb.minY;
            double d8 = renderViewEntity.posZ - aabb.minZ;
            double d9 = Math.sqrt(mc.gameSettings.renderDistanceChunks) * 45;
            if (i <= 1)
            {
                if (d6 * d6 + d7 * d7 + d8 * d8 <= d9 * d9)
                    mc.particles.addEffect(particleFX);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
}

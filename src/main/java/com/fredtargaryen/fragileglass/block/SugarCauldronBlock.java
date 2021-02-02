package com.fredtargaryen.fragileglass.block;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.WorldgenConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_7;

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
public class SugarCauldronBlock extends Block {
    private static final int thirdOfCookTime = 100;

    public SugarCauldronBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(5.0F, 10.0F).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE_0_7, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE_0_7);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World w, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItemStack = player.inventory.getCurrentItem();
        Item heldItem = heldItemStack.getItem();
        if (w.isRemote) {
            if(state.get(AGE_0_7).equals(1) && heldItem == FragileGlassBase.ITEM_SUGAR_BLOCK) {
                this.splash(w, pos.getX(), pos.getY(), pos.getZ());
            }
            return ActionResultType.SUCCESS;
        }
        else {
            int i1 = state.get(AGE_0_7);
            if(i1 == 0){
                if (heldItem == Items.WATER_BUCKET) {
                    if (!player.abilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BUCKET));
                    }
                    w.playSound(null, pos, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    w.setBlockState(pos, state.with(AGE_0_7, 1));
                    return ActionResultType.SUCCESS;
                }
            }
            else if(i1 == 1)
            {
                if(heldItem == FragileGlassBase.ITEM_SUGAR_BLOCK)
                {
                    if (!player.abilities.isCreativeMode)
                    {
                        ItemStack newStack = heldItemStack.copy();
                        newStack.grow(-1);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
                    }
                    w.playSound(null, pos, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    w.setBlockState(pos, state.with(AGE_0_7, 2), 3);
                    return ActionResultType.CONSUME;
                }
            }
            else if(i1 == 6)
            {
                w.addEntity(new ItemEntity(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, new ItemStack(FragileGlassBase.FRAGILE_GLASS, WorldgenConfig.GLASS_YIELD.get())));
                w.addEntity(new ExperienceOrbEntity(w, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, 4));
                w.setBlockState(pos, state.with(AGE_0_7, 0), 3);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onBlockAdded(BlockState p_220082_1_, World worldIn, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 50);
    }

    @Override
    public void tick(BlockState state, ServerWorld w, BlockPos pos, Random random)
    {
        BlockState stateBelow = w.getBlockState(pos.down());
        Block below = stateBelow.getBlock();
        int m = state.get(AGE_0_7);
        if(m < 2 || m == 6)
        {
            w.getPendingBlockTicks().scheduleTick(pos, this, 50);
        }
        else if(m == 2)
        {
            if(below == Blocks.FIRE || below == Blocks.LAVA || (below == Blocks.FURNACE && stateBelow.get(AbstractFurnaceBlock.LIT)))
            {
                w.setBlockState(pos, this.getDefaultState().with(AGE_0_7, 3), 3);
                w.getPendingBlockTicks().scheduleTick(pos, this, thirdOfCookTime);
                w.playSound(null, pos, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.9F, 1.0F);
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
            if(below == Blocks.FIRE || below == Blocks.LAVA || (below == Blocks.FURNACE && stateBelow.get(AbstractFurnaceBlock.LIT)))
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
    public void animateTick(BlockState state, World world, BlockPos pos, Random r) {
        int m = state.get(AGE_0_7);
        if(m == 3) {
            if(r.nextInt(3) == 0) world.addParticle(FragileGlassBase.BUBBLE, pos.getX() + 0.125 + r.nextFloat() * 0.75, pos.getY() + 0.1, pos.getZ() + 0.125 + r.nextFloat() * 0.75, 0.0, 0.01, 0.0);
        }
        else if(m == 4) {
            if(r.nextBoolean()) world.addParticle(FragileGlassBase.BUBBLE, pos.getX() + 0.125 + r.nextFloat() * 0.75, pos.getY() + 0.1, pos.getZ() + 0.125 + r.nextFloat() * 0.75, 0.0, 0.016, 0.0);
        }
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        World w = (World) worldIn;
        if(state.get(AGE_0_7).equals(6) && !w.isRemote) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            ItemEntity entityItem = new ItemEntity(w, x + 0.5D, y + 1.0D, z + 0.5D, new ItemStack(FragileGlassBase.FRAGILE_GLASS, WorldgenConfig.GLASS_YIELD.get()));
            w.addEntity(entityItem);
            w.addEntity(new ExperienceOrbEntity(w, x + 0.5, y + 0.5, z + 0.5, 4));
        }
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    private void splash(World w, int x, int y, int z) {
        w.addParticle(ParticleTypes.SPLASH, x + 0.25, y + 0.8, z + 0.5, 0.0, 0.0, 0.0);
        w.addParticle(ParticleTypes.SPLASH, x + 0.5, y + 0.8, z + 0.5, 0.0, 0.0, 0.0);
        w.addParticle(ParticleTypes.SPLASH, x + 0.75, y + 0.8, z + 0.5, 0.0, 0.0, 0.0);
        w.addParticle(ParticleTypes.SPLASH, x + 0.5, y + 0.8, z + 0.25, 0.0, 0.0, 0.0);
        w.addParticle(ParticleTypes.SPLASH, x + 0.5, y + 0.8, z + 0.75, 0.0, 0.0, 0.0);
    }

    @Deprecated
    public boolean isFullCube(BlockState state)
    {
        return false;
    }
}

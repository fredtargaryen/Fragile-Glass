package com.fredtargaryen.fragileglass.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.block.Block.NULL_AABB;

public class EntityFollower extends EntityLivingBase
{
    private Entity target;

    public EntityFollower(World worldIn) {
        super(worldIn);
    }

    public void setTarget(Entity e)
    {
        this.target = e;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return null;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {

    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return null;
    }

    @Override
    public void onUpdate()
    {
        if(this.target != null) {
            this.addVelocity(this.target.posX - this.posX, this.target.posY - this.posY, this.target.posZ - this.posZ);
        }
        super.onUpdate();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.target == null ? NULL_AABB : this.target.getCollisionBoundingBox();
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox()
    {
        return this.target == null ? NULL_AABB : this.target.getEntityBoundingBox();
    }
}

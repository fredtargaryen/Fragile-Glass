package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import com.fredtargaryen.fragileglass.config.behaviour.data.WaitData;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.BlockDataManager;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.EntityDataManager;
import com.fredtargaryen.fragileglass.config.behaviour.datamanager.TileEntityDataManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.fredtargaryen.fragileglass.FragileGlassBase.BREAKCAP;

public class BreakSystem extends WorldSavedData {
    private World world;
    private BlockDataManager blockDataManager;
    private EntityDataManager entityDataManager;
    private TileEntityDataManager tileEntityDataManager;
    public HashMap<BlockPos, BehaviourQueue> queuedBehaviours;
    /**
     * Code inspection will tell you the access can be private, but it jolly well can't
     */
    public BreakSystem() { super(DataReference.MODID); }

    public static BreakSystem forWorld(World world) {
        ServerWorld serverWorld = world.getServer().getWorld(DimensionType.OVERWORLD);
        DimensionSavedDataManager storage = serverWorld.getSavedData();
        return storage.getOrCreate(BreakSystem::new, DataReference.MODID);
    }

    @Override
    public void read(CompoundNBT nbt) {
        for(int i = 0; i < nbt.size(); i++) {
            CompoundNBT queue = nbt.getCompound(Integer.toString(i));
            BlockPos bp = NBTUtil.readBlockPos(queue.getCompound("pos"));
            String[] rlParts = queue.getString("tileentitytype").split(":");
            TileEntityType<?> tet = ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(rlParts[0], rlParts[1]));
            BehaviourQueue bq = new BehaviourQueue(
                    queue.getInt("countdown"),
                    NBTUtil.readBlockState(queue.getCompound("state")),
                    tet,
                    queue.getDouble("speedsquared"),
                    queue.getInt("nextbehaviour")
            );
            this.queuedBehaviours.put(bp, bq);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        int i = 0;
        for(BlockPos bp : this.queuedBehaviours.keySet())
        {
            BehaviourQueue bq = this.queuedBehaviours.get(bp);
            CompoundNBT queue = new CompoundNBT();
            queue.put("pos", NBTUtil.writeBlockPos(bp));
            queue.putInt("countdown", bq.countdown);
            queue.put("state", NBTUtil.writeBlockState(bq.state));
            queue.putString("tileentitytype", bq.tileEntityType == null ? "null" : bq.tileEntityType.getRegistryName().toString());
            queue.putDouble("speedsquared", bq.speedSq);
            queue.putInt("nextbehaviour", bq.nextBehaviourIndex);
            compound.put(Integer.toString(i), queue);
            i++;
        }
        return compound;
    }

    public void init(World world) {
        this.world = world;
        this.queuedBehaviours = new HashMap<>();
        MinecraftForge.EVENT_BUS.register(this);
        this.blockDataManager = FragileGlassBase.getBlockDataManager();
        this.entityDataManager = FragileGlassBase.getEntityDataManager();
        this.tileEntityDataManager = FragileGlassBase.getTileEntityDataManager();
    }

    public void end(World world) {
        if(this.world == world) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void breakCheck(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            //Update all BehaviourQueues
            for(BlockPos pos : this.queuedBehaviours.keySet())
                this.updateBehaviourQueue(pos, this.queuedBehaviours.get(pos));
            //Intended to avoid ConcurrentModificationExceptions
            CopyOnWriteArrayList<Entity> entityList = new CopyOnWriteArrayList<>(((ServerWorld)event.world).getEntities().collect(Collectors.toList()));
            Iterator<Entity> i = entityList.iterator();
            while(i.hasNext()) {
                Entity e = i.next();
                if(!e.removed) {
                    //Entities must have an instance of IBreakCapability or they will never be able to break blocks with
                    //IFragileCapability.
                    e.getCapability(BREAKCAP).ifPresent(ibc -> {
                        //Update the capability before determining speed. Convenience method; not used by default
                        ibc.update(e);
                        //Get the squared speed; just to avoid performing a sqrt operation more often than necessary
                        double speedSq = ibc.getSpeedSquared(e);
                        if (this.isValidMoveSpeedSquared(speedSq)) {
                            //Check the entity is currently able to break blocks.
                            //Checking whether the block is currently able to break would happen in IFragileCapability#onCrash.
                            if (ibc.isAbleToBreak(e, speedSq)) {
                                this.breakBlocksInWay(e, ibc.getMotionX(e), ibc.getMotionY(e), ibc.getMotionZ(e),
                                        speedSq, ibc.getNoOfBreaks(e));
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Using a new physics system. It's an improvement in the crash physics by breaking blocks ahead of the entity,
     * so that they won't collide with the blocks before they break, and lose all speed. The ability to break blocks is
     * now tied to Capabilities. This is only called for entities which have a Capability extending IBreakCapability.
     * The calculations are more complicated and use the event bus, but are less frequent. I believe performance is
     * slightly improved this way - at the very least, the effect is better and modders can integrate with this mod more
     * easily.
     * The method loops according to noOfBreaks. See IBreakCapability#getNumberOfBreaks for advice on the value of
     * noOfBreaks. Each loop offsets the bounding box and performs another break at the given speed. If noOfBreaks is 3
     * and the speed is 0.3, this attempts 3 breaks, over a distance of 0.9 blocks.
     * NOTE: Depending on implementation this may not be the same as 1 break and a speed of 0.9. For example 1 break at
     * speed 0.6 would break any fragile glass 0.6 blocks away, but 3 breaks at speed 0.2 would not break any fragile
     * glass because 0.2 is too slow.
     * 1.   [motionX, motionY, motionZ] make up a 3D vector representing the amount by which the
     *      entity will move this tick. If this vector intersects the fragile block's bounding
     *      box, then the entity intends to pass through the block this tick so onCrash should be called.
     *      This avoids the problem of the previous system (see step P).
     * 2.   It is not enough to only look at the vector, as in general the vector will only pass
     *      through one block in a fragile glass wall (not enough for larger entities to get
     *      through). Instead the bounding box of the entity has to be "stretched" along the vector
     *      so that all blocks it intersects with will break, always providing a large enough gap.
     * 3.   If the entity is moving diagonally this creates a shape which is not a cube, so cannot
     *      be represented using AxisAlignedBB. Instead, AxisAlignedBB#offset(x, y, z) will be
     *      used to effectively move the entity bounding box along the movement vector, checking for
     *      intersections with block bounding boxes along the way. Upon any such intersections, onCrash is called.
     *      The implementation of this "algorithm" is explained further in inline comments below.
     * P.   This problem is most clear when a player falls onto a fragile glass ceiling. Rather than
     *      smoothly crashing through the ceiling and being damaged when they hit the floor, the
     *      player instead hits the glass ceiling (cancelling their downward movement), gets damaged,
     *      then crashes through to the floor. This problem makes shooting a fragile glass wall
     *      disappointing as well, because the arrow hits the wall (losing all its speed), then
     *      breaks the wall, then falls down as the block is no longer there.
     * @param e The entity that is moving.
     * @param xToUse The x motion value to use; not necessarily e.motionX, especially in the player's case.
     * @param yToUse The y motion value to use; not necessarily e.motionY, especially in the player's case.
     * @param zToUse The z motion value to use; not necessarily e.motionZ, especially in the player's case.
     * @param speedSq The distance in blocks that Entity e will travel in this current tick, squared.
     * @param noOfBreaks Effectively multiplies the range of blocks to call onCrash on, but does not multiply the
     *                   speed of e when onCrash is called.
     */
    private void breakBlocksInWay(Entity e, double xToUse, double yToUse, double zToUse, double speedSq, byte noOfBreaks) {
        AxisAlignedBB originalAABB = e.getBoundingBox();
        if(originalAABB != null) {
            AxisAlignedBB aabb;
            double distance = Math.sqrt(speedSq);
            for (byte breaks = 0; breaks < noOfBreaks; ++breaks) {
                aabb = originalAABB;
                double xComp = xToUse / distance;
                double yComp = yToUse / distance;
                double zComp = zToUse / distance;
                while (distance > 1.0) {
                    //The end of the movement vector is more than one block away from the current
                    //entity bounding box, so at the end of the tick it will have passed through
                    //at least one whole block. Offset the entity bounding box by a distance of
                    //1m (the length of a block), and check that it intersects with any fragile
                    //block bounding boxes.
                    aabb = aabb.offset(xComp, yComp, zComp);
                    distance -= 1.0;
                    this.breakNearbyFragileBlocks(e, aabb, distance * distance);
                }
                //The end of the movement vector is now less than one block away from the current
                //entity bounding box. Offset the entity bounding box right to the end of the
                //movement vector, and check that it intersects with the block bounding box.
                originalAABB = originalAABB.offset(xToUse, yToUse, zToUse);
                this.breakNearbyFragileBlocks(e, originalAABB, distance * distance);
            }
        }
    }

    /**
     * @param e The entity doing the breaking
     * @param aabb The bounding box to break blocks around
     * @param speedSq The square of the speed e is travelling at
     */
    private void breakNearbyFragileBlocks(Entity e, AxisAlignedBB aabb, double speedSq) {
        BlockPos blockPos;
        Block block;
        for (double x = Math.floor(aabb.minX); x < Math.ceil(aabb.maxX); ++x) {
            for (double y = Math.floor(aabb.minY); y < Math.ceil(aabb.maxY); ++y) {
                for (double z = Math.floor(aabb.minZ); z < Math.ceil(aabb.maxZ); ++z) {
                    blockPos = new BlockPos(x, y, z);
                    BlockState state = e.world.getBlockState(blockPos);
                    block = state.getBlock();
                    // Chances are the block will be an air block (pass through no question) so best check this first
                    if (!block.isAir(state, e.world, blockPos)) {
                        TileEntity te = e.world.getTileEntity(blockPos);
                        if(te == null) {
                            //No Tile Entity. The specific BlockState might be covered in the fragility data
                            ArrayList<FragilityData> fragilityDataList = this.blockDataManager.getData(state);
                            if(fragilityDataList != null) {
                                this.initialUpdateFragilityDataList(state, null, blockPos, e, speedSq, fragilityDataList);
                            }
                        }
                        else {
                            //Has a Tile Entity; call all FragilityDatas. Mod FragilityDatas call onCrash in
                            //the TileEntity's capability.
                            ArrayList<FragilityData> fragilityDataList = this.tileEntityDataManager.getData(te.getType());
                            if(fragilityDataList != null) {
                                this.initialUpdateFragilityDataList(state, te, blockPos, e, speedSq, fragilityDataList);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Go through all behaviours associated with the BlockState state or TileEntity te.
     * Execute all behaviours until a WAIT behaviour is encountered; at which point put a BehaviourQueue in
     * queuedBehaviours.
     * @param state
     * @param te
     * @param pos
     * @param e
     * @param speedSq
     * @param fragDataList
     */
    private void initialUpdateFragilityDataList(BlockState state, @Nullable TileEntity te, BlockPos pos, Entity e, double speedSq, ArrayList<FragilityData> fragDataList) {
        int i = 0;
        int listSize = fragDataList.size();
        boolean stop = false;
        while(!stop && i < listSize)
        {
            FragilityData fData = fragDataList.get(i);
            if(fData.getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                if(speedSq >= fData.getBreakSpeedSq()) {
                    this.queuedBehaviours.put(pos, new BehaviourQueue(
                            ((WaitData) fData).getTicks(),
                            state,
                            te == null ? null : te.getType(),
                            speedSq,
                            i + 1));
                    stop = true;
                }
            }
            else {
                fData.onCrash(state, te, pos, e, speedSq);
            }
            i++;
        }
    }

    /**
     * Moving faster than MAXIMUM_ENTITY_SPEED_SQUARED means moving faster than chunks can be loaded.
     * If this is happening there is not much point trying to break blocks.
     */
    private boolean isValidMoveSpeedSquared(double blocksPerTick) {
        return blocksPerTick <= DataReference.MAXIMUM_ENTITY_SPEED_SQUARED;
    }

    private void updateBehaviourQueue(BlockPos pos, BehaviourQueue bq) {
        if(bq.countdown == 0) {
            ArrayList<FragilityData> fragDataList;
            if(bq.tileEntityType == null) {
                fragDataList = this.blockDataManager.getData(bq.state);
            }
            else {
                fragDataList = this.tileEntityDataManager.getData(bq.tileEntityType);
            }
            if(fragDataList != null && bq.nextBehaviourIndex < fragDataList.size()) {
                // Execute all behaviours up to the end of the next WAIT
                int i = bq.nextBehaviourIndex;
                boolean stop = false;
                while(!stop && i < fragDataList.size()) {
                    FragilityData fd = fragDataList.get(i);
                    if(fd.getBehaviour() == FragilityData.FragileBehaviour.WAIT) {
                        if(bq.speedSq > fd.getBreakSpeedSq()) {
                            bq.countdown = ((WaitData) fd).getTicks();
                            bq.nextBehaviourIndex = i + 1;
                            stop = true;
                        }
                    }
                    else {
                        fd.onCrash(bq.state, null, pos, null, bq.speedSq);
                    }
                    i++;
                }
                if(i == fragDataList.size()) this.queuedBehaviours.remove(pos);
            }
            else {
                this.queuedBehaviours.remove(pos);
            }
        }
        else {
            bq.countdown--;
        }
    }

    private class BehaviourQueue {
        public int countdown;
        public BlockState state;
        public TileEntityType<?> tileEntityType;
        public double speedSq;
        public int nextBehaviourIndex;

        public BehaviourQueue(int countdown, BlockState state, TileEntityType<?> tileEntityType, double speedSq, int nextBehaviourIndex) {
            this.countdown = countdown;
            this.state = state;
            this.tileEntityType = tileEntityType;
            this.speedSq = speedSq;
            this.nextBehaviourIndex = nextBehaviourIndex;
        }
    }
}

package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.tileentity.capability.IFragileCapability;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.fredtargaryen.fragileglass.world.DataManager.FragileBehaviour.*;

public class TileEntityDataManager extends DataManager<TileEntityType, ArrayList<FragilityData>> {

    public TileEntityDataManager() { super("tileentities"); }

    public void addCapabilityIfPossible(TileEntity te, AttachCapabilitiesEvent<TileEntity> evt) {
        ArrayList<FragilityData> fragDataList = this.data.get(te.getType());
        if (fragDataList != null) {
            if (!evt.getCapabilities().containsKey(DataReference.FRAGILE_CAP_LOCATION)) {
                ICapabilityProvider iCapProv = new ICapabilityProvider() {
                    IFragileCapability inst = new IFragileCapability() {
                        @Override
                        public void onCrash(BlockState state, TileEntity te, Entity crasher, double speed) {
                            for (FragilityData fragData : fragDataList) {
                                BlockDataManager.FragileBehaviour fb = fragData.getBehaviour();
                                //If MOD, a mod will define the capability at some point, so ignore
                                if (fb != BlockDataManager.FragileBehaviour.MOD) {
                                    //If one of the other behaviours, and the capability has been defined, must ignore.
                                    if (fb == BlockDataManager.FragileBehaviour.BREAK) {
                                        if (speed > fragData.getBreakSpeed()) {
                                            te.getWorld().destroyBlock(te.getPos(), true);
                                        }
                                    } else if (fb == BlockDataManager.FragileBehaviour.UPDATE) {
                                        if (speed > fragData.getBreakSpeed()) {
                                            World w = te.getWorld();
                                            BlockPos tilePos = te.getPos();
                                            w.getPendingBlockTicks().scheduleTick(tilePos, w.getBlockState(tilePos).getBlock(), fragData.getUpdateDelay());
                                        }
                                    } else if (fb == CHANGE) {
                                        if (speed > fragData.getBreakSpeed()) {
                                            te.getWorld().setBlockState(te.getPos(), fragData.getNewBlockState());
                                        }
                                    } else if (fb == FALL) {
                                        if (speed > fragData.getBreakSpeed()) {
                                            World w = te.getWorld();
                                            BlockPos pos = te.getPos();
                                            if (FallingBlock.canFallThrough(w.getBlockState(pos.down()))) {
                                                FallingBlockEntity fallingBlock = new FallingBlockEntity(w, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
                                                fallingBlock.tileEntityData = te.write(new CompoundNBT());
                                                w.addEntity(fallingBlock);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    };

                    @Nonnull
                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                        return cap == FragileGlassBase.FRAGILECAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                    }
                };
                evt.addCapability(DataReference.FRAGILE_CAP_LOCATION, iCapProv);
            }
        }
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    @Override
    protected void loadDefaultData() {
        super.loadDefaultData();
        ArrayList<FragilityData> glassBehaviour = new ArrayList<>();
        glassBehaviour.add(new FragilityData(BREAK, 0.165, 0, Blocks.AIR.getDefaultState(), new String[]{}));
        this.data.put(FragileGlassBase.TEFG_TYPE, glassBehaviour);
        ArrayList<FragilityData> stoneBehaviour = new ArrayList<>();
        stoneBehaviour.add(new FragilityData(UPDATE, 0.0, 10, Blocks.AIR.getDefaultState(), new String[]{}));
        this.data.put(FragileGlassBase.TEWS_TYPE, stoneBehaviour);
    }

    /**
     * Detect and read all tile entity config files. MUST be called when all TileEntityTypes have been registered!
     */
    public void loadTileEntityData() { this.loadDataFromConfigDir(new TileEntityConfigLoader(this, this.data)); }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "########################################################\n",
            "#FRAGILE GLASS AND THIN ICE CONFIG FILE - TILE ENTITIES#\n",
            "########################################################\n",
            "#THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "#(You probably don't really want to make ENDER CHESTS fragile, for example.)\n",
            "#Here is where you can configure which tile entities are fragile and which are not, and modify basic behaviour.\n",
            "#Using tile entities has some advantages over using blocks:\n",
            "#* All blocks which have the tile entity will have the same behaviour, so less lines are needed.\n",
            "#* Tile entities from other mods are able to use code to achieve more complex crash behaviours.\n",
            "#\n#--Limitations--\n",
            "#* This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",
            "#\n#--How to customise--\n",
            "#To add a comment to the file, start the line with a # symbol.\n",
            "#To make a tile entity fragile, add a new row in this file following this format:\n",
            "#modid:ID BREAK/UPDATE/CHANGE/FALL/MOD minSpeed updateDelay newState extraValues\n",
            "#* modid:ID is the ResourceLocation string used to register with Forge.\n",
            "#  - modid can be found by looking in the 'modId' entry of the mod's mods.toml file. For vanilla\n",
            "     Minecraft this is just 'minecraft'. Finding the ID may need some investigation of the mod's code.\n",
            "#* For all crash behaviours, the 'breaker' entity must be travelling above its minimum speed. If so,\n",
            "#  it must then be above the speed defined for the block. Meeting both these conditions causes the\n",
            "#  crash behaviour to trigger.\n",
            "#  - 'BREAK': the block breaks immediately.\n",
            "#  - 'UPDATE': a block update is triggered.\n",
            "#  - 'CHANGE': the block changes into a specified blockstate.\n",
            "#  - 'FALL': the block falls immediately.\n",
            "#  - 'MOD': for mod tile entities with custom behaviours ONLY. Modders should make custom tile\n",
            "#           entities and implement IFragileCapability with the behaviour they want. The mod receives all\n",
            "#           the extra values and it is up to the modder how they are used. NOTE: If a tile entity has a\n",
            "#           custom behaviour it will be used regardless of the behaviour value.\n",
            "#* Crash behaviours can be combined, and will trigger (if fast enough) in the order they are listed in\n",
            "#  the config messages. However, only the first of each behaviour type will trigger.\n",
            "#* minSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "#  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "#  per tick, which is metres per second divided by 20.\n",
            "#* updateDelay is only used by the UPDATE behaviour. It must be an integer. It specifies the\n",
            "#  delay between the collision and the block update. Delays are measured in ticks and there are 20\n",
            "#  ticks per second.\n",
            "#* newState is only used by the CHANGE behaviour. It must have the format of a block or blockstate;\n",
            "#  you can see examples in fragileglassft_blocks.cfg. This is the state the block will change into. If\n",
            "#  you aren't using this value you can leave a - here.\n",
            "#* You can add extra values of any format, separated by spaces, for any mod blocks that might require\n",
            "#  them.\n",
            "#\n#--Fun example lines you may wish to uncomment--\n",
            "#Currently none, but I am open to suggestions!\n",
            "#\n#--Default values, in case you break something--\n",
            "#All fragile glass blocks:\n",
            "#fragileglassft:tefg BREAK 0.165 0 -\n",
            "#Weak stone:\n",
            "#fragileglassft:tews UPDATE 0.0 10 -\n",
            "fragileglassft:tefg BREAK 0.165 0 -\n",
            "fragileglassft:tews UPDATE 0.0 10 -\n"
    };
}

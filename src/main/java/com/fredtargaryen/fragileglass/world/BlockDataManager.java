package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

import static com.fredtargaryen.fragileglass.world.DataManager.FragileBehaviour.BREAK;

/**
 * Responsible for everything to do with block fragility data from fragileglassft_blocks.cfg.
 */
public class BlockDataManager extends DataManager<BlockState, ArrayList<FragilityData>> {

    public BlockDataManager() {
        super("blocks");
    }

    @Override
    protected String[] getDefaultConfigFileText() { return defaultFileData; }

    /**
     * Detect and read all block/tile entity config files. MUST be called when all Blocks and TileEntityTypes have been registered!
     */
    public void loadBlockData() {
        this.loadDataFromConfigDir(new BlockConfigLoader(this, this.data));
    }

    @Override
    protected void loadDefaultData() {
        super.loadDefaultData();
        ArrayList<FragilityData> iceBehaviour = new ArrayList<>();
        iceBehaviour.add(new FragilityData(BREAK, 0.0, 0, Blocks.AIR.getDefaultState(), new String[]{}));
        this.data.put(FragileGlassBase.THIN_ICE.getDefaultState(), iceBehaviour);
    }

    //Doesn't look like I can read from assets so sadly all this is needed for now
    private static final String[] defaultFileData = new String[] {
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@FRAGILE GLASS AND THIN ICE CONFIG FILE - BLOCKS@\n",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n",
            "@THINK VERY CAREFULLY AND BACK UP YOUR WORLDS BEFORE ADDING ENTRIES HERE!\n",
            "@(You probably don't really want to make ALL DIRT BLOCKS fragile, for example.)\n",
            "@Here is where you can configure which blocks are fragile and which are not, and modify basic behaviour.\n",
            "@\n@--Limitations--\n",
            "@* This will not work for blocks which are basically air blocks, e.g. Air blocks and 'logic' blocks.\n",
            "@* If you specify block states you should be as specific as possible; if you leave out a property it\n",
            "@  will only work for blocks with the properties you specified, and the default for everything else.\n",
            "@\n@--How to customise--\n",
            "@To add a comment to the file, start the line with a @ symbol.\n",
            "@To make a block fragile, add a new row in this file following this format:\n",
            "@modid:ID[properties] BREAK/UPDATE/CHANGE/FALL/MOD minSpeed updateDelay new state extraValues\n",
            "@* 'modid:ID' is the ResourceLocation string used to register with Forge.\n",
            "@  You can usually find this by looking at the block in-game with the F3 menu on - below it are the\n",
            "@  blockstate properties.\n",
            "@  - Only add the properties if you are specifying behaviour for specific blockstates.\n",
            "@    Unspecified properties carry over from the block being changed; see the door example below.\n",
            "@  - Any specified properties that the block doesn't have will be quietly ignored.\n",
            "@* For all crash behaviours, the 'breaker' entity must be travelling above its minimum speed. If so,\n",
            "@  it must then be above the speed defined for the block. Meeting both these conditions causes the\n",
            "@  crash behaviour to trigger.\n",
            "@  - 'BREAK': the block breaks immediately.\n",
            "@  - 'UPDATE': a block update is triggered.\n",
            "@  - 'CHANGE': the block changes into a specified blockstate.\n",
            "@  - 'FALL': the block falls immediately.\n",
            "@  - 'MOD': for mod tile entities with custom behaviours ONLY.\n",
            "@* Crash behaviours can be combined, and will trigger (if fast enough) in the order they are listed in\n",
            "@  the config messages. However, only the first of each behaviour type will trigger.\n",
            "@* minSpeed is a minimum speed (must be decimal). The breaker must be moving above their\n",
            "@  breaking speed, AND above this speed, to trigger the crash behaviour. Speed is measured in blocks\n",
            "@  per tick, which is metres per second divided by 20.\n",
            "@* updateDelay is only used by the UPDATE behaviour. It must be an integer. It specifies the delay\n",
            "@  between the collision and the block update. Delays are measured in ticks and there are 20 ticks per\n",
            "@  second.\n",
            "@* newState is only used by the CHANGE behaviour. It must have the format of a block or blockstate; you\n",
            "@  can see examples below. This is the state the block will change into. If you aren't using this value\n",
            "@  you can leave a - here.\n",
            "@* You can add extra values of any format, separated by spaces, for any mod blocks that might require\n",
            "@  them.\n",
            "@\n@--Fun example lines you may wish to uncomment--\n",
            "@Make vanilla glass and ice fragile too (stained panes won't work unless they are part of a custom tag)\n",
            "@minecraft:ice BREAK 0.165 0 -\n",
            "@#minecraft:impermeable BREAK 0.165 0 -\n",
            "@minecraft:glass_pane BREAK 0.165 0 -\n",
            "@Make obsidian as fragile as it is IRL\n",
            "@minecraft:obsidian BREAK 0.165 0 -\n",
            "@Weak sandstone\n",
            "@minecraft:sandstone FALL 0.0 0 minecraft:sandstone\n",
            "@minecraft:red_sandstone FALL 0.0 0 minecraft:red_sandstone\n",
            "@Burst through doors when sprinting into them\n",
            "@minecraft:oak_door[open=false] CHANGE 0.165 0 minecraft:oak_door[open=true]\n",
            "@minecraft:birch_door[open=false] CHANGE 0.165 0 minecraft:birch_door[open=true]\n",
            "@minecraft:acacia_door[open=false] CHANGE 0.165 0 minecraft:acacia_door[open=true]\n",
            "@minecraft:spruce_door[open=false] CHANGE 0.165 0 minecraft:spruce_door[open=true]\n",
            "@minecraft:jungle_door[open=false] CHANGE 0.165 0 minecraft:jungle_door[open=true]\n",
            "@minecraft:dark_oak_door[open=false] CHANGE 0.165 0 minecraft:dark_oak_door[open=true]\n",
            "@Cause suspended sand to fall when you are near it\n",
            "@#minecraft:sand UPDATE 0.0 10 -\n",
            "@Safe lava that turns into slime at the last minute\n",
            "@#minecraft:lava CHANGE 0.0 0 minecraft:slime_block\n",
            "@\n#--Default values, in case you break something--\n",
            "@Thin ice:\n",
            "@fragileglassft:thinice BREAK 0.0 0 -\n",
            "fragileglassft:thinice BREAK 0.0 0 -\n"
    };
}

# Fragile-Glass
Fragile Glass and Thin Ice is a Minecraft Forge mod which does the following things:
* Adds Fragile Glass (including stained and pane versions), made with sugar and water
* Adds Thin Ice which breaks underfoot unless stepped on carefully
* Adds Weak Stone (disabled by default) which crumbles into gravel on contact
* Adds configuration for practically any block (vanilla or any mod) so that it can break, update, change or fall down when something collides with it hard enough
* Adds configuration for how fast different entities have to be travelling to break a fragile block

This is for versions of the mod for Minecraft 1.8 upwards.
For Minecraft 1.7.10, see [the Fragile-Glass-1.7.10 repo](https://github.com/fredtargaryen/Fragile-Glass-1.7.10).

## Information for Players and Pack Developers
For detailed mod information, [visit the wiki](https://www.curseforge.com/minecraft/mc-mods/fragile-glass-and-thin-ice/pages/wiki).
### Customising Fragile Blocks
Full information on how to customise blocks' behaviour on collision can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/config/behaviour/datamanager/BlockDataManager.java).
* In your game folder, under `config/fragileglassft_blocks.cfg`.
#### Via Tile Entities
Full information on how to customise tile entities' behaviour on collision can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/config/behaviour/datamanager/TileEntityDataManager.java).
* In your game folder, under `config/fragileglassft_tileentities.cfg`.
### Customising Fragile Entities
Full information on how to customise entities' minimum and maximum collision speed can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/config/behaviour/datamanager/EntityDataManager.java).
* In your game folder, under `config/fragileglassft_entities.cfg`.
### Issues
Please report any issues on [the Issues page](https://github.com/fredtargaryen/Fragile-Glass/issues).

## Information for mod developers
### Compatibility with your mod
For your mod to work with Fragile Glass, add it as a dependency following the instructions [here](https://github.com/MinecraftForge/ForgeGradle/wiki/Dependencies). 

If you want the dependency to be optional, you can check if Fragile Glass was loaded with `ModList.get().isLoaded("fragileglassft")`.

You can find the latest Fragile Glass files [here](https://minecraft.curseforge.com/projects/fragile-glass-and-thin-ice/files).

To make a tile entity with custom collision behaviour, follow the comments [here](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/tileentity/capability/IFragileCapability.java).

Adding block and entity behaviours, or built-in behaviours for tile entities, shouldn't require any code: you can supply a file of config lines to your users called `fragileglassft_blocks_NAME.cfg`, where `NAME` is whatever you want, and it will be processed like the other `fragileglassft_*.cfg` files.
For instructions on writing config lines see the information in 'Information for Players and Pack Developers' above.

### Pull Requests
Any pull requests are very welcome. There are currently no standards for pull requests but clean code which
follows the existing patterns is appreciated. If you are making a new feature, message me first to see
if I will accept it!

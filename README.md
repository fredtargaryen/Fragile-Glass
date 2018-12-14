# Fragile-Glass
Fragile Glass and Thin Ice is a Minecraft Forge mod which does the following things:
* Adds Fragile Glass (including stained and pane versions), made with sugar and water
* Adds Thin Ice which breaks underfoot unless stepped on carefully
* Adds Weak Stone (disabled by default) which crumbles into gravel on contact
* Adds configuration for practically any block (vanilla or any mod) so that it can break, update, change or fall down when something collides with it hard enough
* Adds configuration for how fast different entities have to be travelling to break a fragile block

This is for versions of the mod for Minecraft 1.8 upwards.
For Minecraft 1.7.10, see [the Fragile-Glass-1.7.10 repo](https://github.com/fredtargaryen/Fragile-Glass-1.7.10).

## Information for players
### Customising Fragile Blocks
Full information on how to customise blocks' behaviour on collision can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/world/FragilityDataManager.java).
* In your game folder, under config/fragileglassft_blocks.cfg.
### Customising Fragile Entities
Full information on how to customise entities' minimum and maximum collision speed can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/blocklist/src/main/java/com/fredtargaryen/fragileglass/world/BreakerDataManager.java).
* In your game folder, under config/fragileglassft_entities.cfg.

### Issues
Please report any issues on [the Issues page](https://github.com/fredtargaryen/Fragile-Glass/issues).

## Information for mod developers
### Pull Requests
Any pull requests are very welcome. There are currently no standards for pull requests but clean code which
follows the existing patterns is appreciated. If the pull request is a new feature, message me first to see
if I will accept it!

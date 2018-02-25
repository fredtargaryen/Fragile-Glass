# Fragile-Glass
Fragile Glass and Thin Ice is a Minecraft Forge mod which adds those blocks.
This is for versions of the mod for Minecraft 1.8 upwards.
For version 1.7.10, see the repo Fragile-Glass-1.7.10.

## Developing
To work on this mod, once you have the source code:
* Download the Forge 1.11.2-13.20.1.2386 MDK from files.minecraftforge.net
* Copy the eclipse and gradle folders, and gradlew and gradlew.bat, into the folder where the source code is kept
* In a command line, run "gradlew setupDecompWorkspace" (Windows) or "./gradlew setupDecompWorkspace" (Linux)
* Open your IDE and import the project, either by selecting build.gradle or the folder itself
* Go back to the command line and run "gradlew genIntellijRuns" (Windows) or "./gradlew genIntellijRuns" (Linux)
You should now be able to develop the mod in an IDE, against the Minecraft source files.
I will accept pull requests if they work. If you change anything other than the source code please give a good
reason for the change or I may request that you revert it.

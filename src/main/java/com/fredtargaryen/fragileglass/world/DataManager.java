package com.fredtargaryen.fragileglass.world;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.HashMap;

public abstract class DataManager<E, D> {

    protected File configDir;
    protected File configFile;

    private String typeString;

    protected HashMap<E, D> data;

    public enum FragileBehaviour {
        //Break if above the break speed
        BREAK,
        //Update after the update delay if above the break speed
        UPDATE,
        //Change to a different BlockState
        CHANGE,
        //Change to an FallingBlockEntity of the given BlockState
        FALL,
        //Load the data but don't even construct the capability; let another mod deal with it all
        MOD
    }

    protected DataManager(String typeString) {
        this.data = new HashMap<>();
        this.typeString = typeString;
    }

    public final void clearData() {
        this.data.clear();
    }

    protected abstract String[] getDefaultConfigFileText();

    private void handleConfigFileException(Exception e) {
        FragileGlassBase.warn("Could not load "+DataReference.MODID+"_"+this.typeString+".cfg! " +
                "Default behaviour will be loaded. No custom data will take effect.");
        e.printStackTrace();
        this.loadDefaultData();
    }

    public final boolean hasData() {
        return !this.data.isEmpty();
    }

    protected void loadDataFromConfigDir(ConfigLoader cl) {
        try {
            File[] fileList = this.configDir.listFiles();
            if(fileList != null) {
                //First, try to load fragileglassft_<typeString>.cfg
                String fileName = this.configFile.getName();
                System.out.println("Found file " + fileName + "; now loading");
                BufferedReader br = new BufferedReader(new FileReader(this.configFile));
                cl.loadFile(br, this.configDir, fileName);
                br.close();
                //Iterate through all the config files
                for(File file : fileList) {
                    fileName = file.getName();
                    //Check for pattern fragileglassft_<typeString>_<anything>
                    String[] fileNameParts = fileName.split("_");
                    if(fileNameParts.length == 3) {
                        if(fileNameParts[0].equals(DataReference.MODID) && fileNameParts[1].equals(this.typeString)) {
                            System.out.println("Found file "+fileName+"; now loading");
                            br = new BufferedReader(new FileReader(file));
                            cl.loadFile(br, this.configDir, fileName);
                            br.close();
                        }
                    }
                }
            }
        }
        catch(IOException ioe) {
            this.handleConfigFileException(new Exception());
        }
    }

    protected void loadDefaultData() {
        this.data.clear();
    }

    public void setupDirsAndFiles() {
        this.configDir = FMLPaths.CONFIGDIR.get().toFile();
        this.configFile = new File(this.configDir, DataReference.MODID + "_"+this.typeString+".cfg");
        if(!this.configFile.exists()) {
            try {
                //Config file is not in config folder! Write from defaultFileData (see bottom of file)
                FragileGlassBase.warn("[FRAGILE GLASS CONFIG] "+DataReference.MODID+"_"+this.typeString+".cfg not found! Writing a new one.");
                FileWriter fw = new FileWriter(this.configFile);
                for(String s : this.getDefaultConfigFileText()) {
                    fw.write(s);
                }
                fw.close();
            }
            catch(IOException ioe) {
                this.handleConfigFileException(ioe);
            }
        }
    }
}

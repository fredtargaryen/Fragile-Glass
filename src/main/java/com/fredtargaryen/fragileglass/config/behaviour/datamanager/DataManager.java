package com.fredtargaryen.fragileglass.config.behaviour.datamanager;

import com.fredtargaryen.fragileglass.DataReference;
import com.fredtargaryen.fragileglass.FragileGlassBase;
import com.fredtargaryen.fragileglass.config.behaviour.configloader.ConfigLoader;
import com.fredtargaryen.fragileglass.config.behaviour.data.FragilityData;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Stream;

public abstract class DataManager<E, D> {
    protected File configDir;
    protected File configFile;

    private String typeString;

    protected HashMap<E, D> data;

    protected DataManager(String typeString) {
        this.data = new HashMap<>();
        this.typeString = typeString;
    }

    public final void clearData() {
        this.data.clear();
    }

    public final void export(String filecontents) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        FileWriter fw = new FileWriter(new File(this.configDir, DataReference.MODID + "_"+this.typeString+"_"+ now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(':', '-') +".cfg"));
        fw.write("@This config data was exported via a Fragile Glass and Thin Ice command on "+now.format(DateTimeFormatter.ISO_LOCAL_DATE)+" at "+now.format(DateTimeFormatter.ISO_LOCAL_TIME) + ".\n\n");
        fw.write(filecontents);
        fw.close();
    }

    public final D getData(E key) { return this.data.get(key); }

    public Stream<E> getKeys() { return this.data.keySet().stream(); }

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

    /**
     * Detect and read all block/tile entity config files. MUST be called when all Blocks and TileEntityTypes have been registered!
     */
    public abstract boolean loadData();

    protected boolean loadDataFromConfigDir(ConfigLoader cl) {
        boolean ok = true;
        try {
            File[] fileList = this.configDir.listFiles();
            if(fileList != null) {
                //First, try to load fragileglassft_<typeString>.cfg
                String fileName = this.configFile.getName();
                System.out.println("Found file " + fileName + "; now loading");
                BufferedReader br = new BufferedReader(new FileReader(this.configFile));
                ok &= cl.loadFile(br, this.configDir, fileName);
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
                            ok &= cl.loadFile(br, this.configDir, fileName);
                            br.close();
                        }
                    }
                }
            }
            return ok;
        }
        catch(IOException ioe) {
            this.handleConfigFileException(new Exception());
            return false;
        }
    }

    protected void loadDefaultData() {
        this.data.clear();
    }

    public abstract void parseConfigLine(String configLine, boolean add, int changeIndex) throws ConfigLoader.ConfigLoadException;

    /**
     * Remove a behaviour from a map entry, or if the behaviour is null, remove the whole key
     * @param key
     * @param behaviour
     */
    public abstract void removeBehaviour(E key, @Nullable FragilityData.FragileBehaviour behaviour);

    public void setupDirsAndFiles(MinecraftServer ms) {
        //Get path to serverconfigs folder of world as File object
        this.configDir = ms.getActiveAnvilConverter().getFile(ms.getFolderName(), "serverconfig");
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

    public abstract String stringifyBehaviours(E key, @Nullable FragilityData.FragileBehaviour behaviour, boolean showNumbers);
}

package com.fredtargaryen.fragileglass.proxy;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.util.Map;

@Mod.EventBusSubscriber
public class ServerProxy implements IProxy {
    @Override
    public void registerServerListeners() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Adds a listener which refreshes DataManager data whenever Tags are reloaded.
     * @param event
     */
    @SubscribeEvent
    public void addFragileConfigReloadListener(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(new ReloadListener<Map<ResourceLocation, Tag.Builder<EntityType<?>>>>() {
            @Override
            protected Map<ResourceLocation, Tag.Builder<EntityType<?>>> prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
                return null;
            }

            @Override
            protected void apply(Map<ResourceLocation, Tag.Builder<EntityType<?>>> resourceLocationBuilderMap, IResourceManager iResourceManager, IProfiler iProfiler) {
                FragileGlassBase.reloadDataManagers();
            }
        });
    }



    @SubscribeEvent
    public void onReloadCommand(CommandEvent ce) {
        if(ce.getParseResults().getReader().getString().equals("/reload")) {
            ce.getParseResults().getContext().getSource().sendFeedback(
                    new StringTextComponent("Reloading Fragile Glass behaviours. Please check the config folder for error logs."),
                    true);
        }
    }
}

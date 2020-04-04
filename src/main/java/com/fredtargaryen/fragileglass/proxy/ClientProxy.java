package com.fredtargaryen.fragileglass.proxy;

import com.fredtargaryen.fragileglass.FragileGlassBase;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientProxy implements IProxy {
    @Override
    public void setupRenderTypes() {
        RenderTypeLookup.setRenderLayer(FragileGlassBase.FRAGILE_GLASS, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.FRAGILE_PANE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.WHITE_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.ORANGE_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.MAGENTA_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIGHT_BLUE_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.YELLOW_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIME_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.PINK_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.GRAY_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIGHT_GRAY_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.CYAN_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.PURPLE_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BLUE_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BROWN_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.GREEN_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.RED_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BLACK_STAINED_FRAGILE_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.WHITE_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.ORANGE_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.MAGENTA_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIGHT_BLUE_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.YELLOW_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIME_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.PINK_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.GRAY_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.LIGHT_GRAY_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.CYAN_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.PURPLE_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BLUE_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BROWN_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.GREEN_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.RED_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.BLACK_STAINED_FRAGILE_GLASS_PANE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.SUGAR_CAULDRON, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FragileGlassBase.THIN_ICE, RenderType.getTranslucent());
    }
}
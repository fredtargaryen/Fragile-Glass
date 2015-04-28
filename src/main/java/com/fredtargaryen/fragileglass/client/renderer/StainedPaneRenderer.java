package com.fredtargaryen.fragileglass.client.renderer;

import com.fredtargaryen.fragileglass.block.BlockFragilePane;
import com.fredtargaryen.fragileglass.block.BlockStainedFragilePane;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class StainedPaneRenderer implements ISimpleBlockRenderingHandler {
    private int renderID;
    public StainedPaneRenderer(int rID)
    {
        this.renderID = rID;
    }
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer){}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int l = renderer.blockAccess.getHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        int i1 = block.colorMultiplier(renderer.blockAccess, x, y, z);
        float f = (float)(i1 >> 16 & 255) / 255.0F;
        float f1 = (float)(i1 >> 8 & 255) / 255.0F;
        float f2 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        boolean flag5 = block instanceof BlockStainedFragilePane;
        IIcon iicon;
        IIcon iicon1;

        if (renderer.hasOverrideBlockTexture())
        {
            iicon = renderer.overrideBlockTexture;
            iicon1 = renderer.overrideBlockTexture;
        }
        else
        {
            int j1 = renderer.blockAccess.getBlockMetadata(x, y, z);
            iicon = renderer.getBlockIconFromSideAndMetadata(block, 0, j1);
            iicon1 = flag5 ? ((BlockStainedFragilePane)block).func_150104_b(j1) : ((BlockFragilePane)block).func_150097_e();
        }

        double d22 = (double)iicon.getMinU();
        double d0 = (double)iicon.getInterpolatedU(7.0D);
        double d1 = (double)iicon.getInterpolatedU(9.0D);
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMinV();
        double d4 = (double)iicon.getMaxV();
        double d5 = (double)iicon1.getInterpolatedU(7.0D);
        double d6 = (double)iicon1.getInterpolatedU(9.0D);
        double d7 = (double)iicon1.getMinV();
        double d8 = (double)iicon1.getMaxV();
        double d9 = (double)iicon1.getInterpolatedV(7.0D);
        double d10 = (double)iicon1.getInterpolatedV(9.0D);
        double d11 = (double)x;
        double d12 = (double)(x + 1);
        double d13 = (double)z;
        double d14 = (double)(z + 1);
        double d15 = (double)x + 0.5D - 0.0625D;
        double d16 = (double)x + 0.5D + 0.0625D;
        double d17 = (double)z + 0.5D - 0.0625D;
        double d18 = (double)z + 0.5D + 0.0625D;
        boolean flag = flag5 ? ((BlockStainedFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x, y, z - 1)) : ((BlockFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x, y, z - 1));
        boolean flag1 = flag5 ? ((BlockStainedFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x, y, z + 1)) : ((BlockFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x, y, z + 1));
        boolean flag2 = flag5 ? ((BlockStainedFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x - 1, y, z)) : ((BlockFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x - 1, y, z));
        boolean flag3 = flag5 ? ((BlockStainedFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x + 1, y, z)) : ((BlockFragilePane)block).canPaneConnectToBlock(renderer.blockAccess.getBlock(x + 1, y, z));
        double d19 = 0.001D;
        double d20 = 0.999D;
        double d21 = 0.001D;
        boolean flag4 = !flag && !flag1 && !flag2 && !flag3;

        if (!flag2 && !flag4)
        {
            if (!flag && !flag1)
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d1, d3);
            }
        }
        else if (flag2 && flag3)
        {
            if (!flag)
            {
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d22, d3);
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d1, d3);
            }

            if (!flag1)
            {
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d0, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d2, d3);
            }

            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d6, d8);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d5, d8);
            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d5, d8);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d5, d7);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d6, d7);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d6, d8);
        }
        else
        {
            if (!flag && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d1, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d22, d3);
            }

            if (!flag1 && !flag4)
            {
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d0, d3);
            }

            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d6, d9);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d5, d9);
            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d5, d9);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d5, d7);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d6, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d6, d9);
        }

        if ((flag3 || flag4) && !flag2)
        {
            if (!flag1 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d2, d3);
            }

            if (!flag && !flag4)
            {
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d6, d10);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d5, d10);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d5, d8);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d5, d10);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d6, d10);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d6, d8);
        }
        else if (!flag3 && !flag && !flag1)
        {
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d1, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d1, d3);
        }

        if (!flag && !flag4)
        {
            if (!flag3 && !flag2)
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d1, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
            }
        }
        else if (flag && flag1)
        {
            if (!flag2)
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d2, d3);
            }

            if (!flag3)
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d6, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d5, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d5, d8);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d5, d7);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d6, d7);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d5, d8);
        }
        else
        {
            if (!flag2 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d1, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
            }

            if (!flag3 && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d22, d3);
            }

            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d6, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d5, d7);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d5, d9);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d6, d9);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d5, d7);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d6, d7);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d6, d9);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d5, d9);
        }

        if ((flag1 || flag4) && !flag)
        {
            if (!flag2 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d2, d3);
            }

            if (!flag3 && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d0, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d6, d10);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d5, d10);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d5, d8);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d5, d10);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d6, d10);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d5, d8);
        }
        else if (!flag1 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d1, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d1, d3);
        }

        tessellator.addVertexWithUV(d16, (double)y + 0.999D, d17, d6, d9);
        tessellator.addVertexWithUV(d15, (double)y + 0.999D, d17, d5, d9);
        tessellator.addVertexWithUV(d15, (double)y + 0.999D, d18, d5, d10);
        tessellator.addVertexWithUV(d16, (double)y + 0.999D, d18, d6, d10);
        tessellator.addVertexWithUV(d15, (double)y + 0.001D, d17, d5, d9);
        tessellator.addVertexWithUV(d16, (double)y + 0.001D, d17, d6, d9);
        tessellator.addVertexWithUV(d16, (double)y + 0.001D, d18, d6, d10);
        tessellator.addVertexWithUV(d15, (double)y + 0.001D, d18, d5, d10);

        if (flag4)
        {
            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d17, d0, d3);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d17, d0, d4);
            tessellator.addVertexWithUV(d11, (double)y + 0.001D, d18, d1, d4);
            tessellator.addVertexWithUV(d11, (double)y + 0.999D, d18, d1, d3);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d12, (double)y + 0.001D, d17, d1, d4);
            tessellator.addVertexWithUV(d12, (double)y + 0.999D, d17, d1, d3);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d13, d1, d3);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d13, d1, d4);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d13, d0, d4);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d13, d0, d3);
            tessellator.addVertexWithUV(d15, (double)y + 0.999D, d14, d0, d3);
            tessellator.addVertexWithUV(d15, (double)y + 0.001D, d14, d0, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.001D, d14, d1, d4);
            tessellator.addVertexWithUV(d16, (double)y + 0.999D, d14, d1, d3);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return this.renderID;
    }
}

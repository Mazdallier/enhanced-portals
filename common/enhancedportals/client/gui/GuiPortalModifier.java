package enhancedportals.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import alz.core.gui.GuiExtendedScreen;
import alz.core.gui.GuiItemStackButton;
import cpw.mods.fml.common.network.PacketDispatcher;
import enhancedportals.container.ContainerPortalModifier;
import enhancedportals.lib.BlockIds;
import enhancedportals.lib.Localization;
import enhancedportals.lib.Reference;
import enhancedportals.lib.Textures;
import enhancedportals.network.packet.PacketEnhancedPortals;
import enhancedportals.network.packet.PacketPortalModifierUpdate;
import enhancedportals.network.packet.PacketPortalModifierUpgrade;
import enhancedportals.portal.PortalTexture;
import enhancedportals.portal.upgrades.Upgrade;
import enhancedportals.tileentity.TileEntityPortalModifier;

public class GuiPortalModifier extends GuiExtendedScreen
{
    TileEntityPortalModifier portalModifier;
    public boolean           hasInteractedWith = false, isActive = false;
    GuiButton                okayButton;

    public GuiPortalModifier(InventoryPlayer player, TileEntityPortalModifier modifier)
    {
        super(new ContainerPortalModifier(player, modifier), null);
        portalModifier = modifier;
        isActive = portalModifier.isActive();
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 100)
        {
            mc.thePlayer.closeScreen();
        }
        else if (button.id == 10)
        {
            portalModifier.redstoneSetting = 0;
            ((GuiItemStackButton) buttonList.get(2)).isActive = false;
            ((GuiItemStackButton) buttonList.get(3)).displayString = "0";
            ((GuiItemStackButton) buttonList.get(3)).isActive = false;
            hasInteractedWith = true;
            ((GuiItemStackButton) button).isActive = true;
        }
        else if (button.id == 11)
        {
            portalModifier.redstoneSetting = 1;
            ((GuiItemStackButton) buttonList.get(1)).isActive = false;
            ((GuiItemStackButton) buttonList.get(3)).displayString = "0";
            ((GuiItemStackButton) buttonList.get(3)).isActive = false;
            hasInteractedWith = true;
            ((GuiItemStackButton) button).isActive = true;
        }
        else if (button.id == 12)
        {
            int num = 0;
            
            if (button.displayString != null && button.displayString != "")
            {
                num = Integer.parseInt(button.displayString);
            }
            
            if (num + 1 < 16)
            {
                button.displayString = "" + (num + 1);
                num++;
            }
            else
            {
                button.displayString = "1";
                num = 1;
            }
            
            portalModifier.redstoneSetting = (byte) (2 + num);
            ((GuiItemStackButton) buttonList.get(1)).isActive = false;
            ((GuiItemStackButton) buttonList.get(2)).isActive = false;
            hasInteractedWith = true;
            ((GuiItemStackButton) button).isActive = true;
        }
        else if (button.id == 50)
        {
            GuiItemStackButton guiButton = (GuiItemStackButton) button;
            
            for (Upgrade u : Upgrade.getAllUpgrades())
            {
                if (u.getDisplayItemStack().isItemEqual(guiButton.itemStack))
                {
                    if (portalModifier.upgradeHandler.removeUpgrade(u.getUpgradeID(), portalModifier))
                    {
                        buttonList.remove(button);
                        hasInteractedWith = true;
                    }
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Reference.GUI_LOCATION + "portalModifier.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2 - 3;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        int padding = 5, width = 10;

        if (portalModifier.thickness == 1)
        {
            padding = 4;
            width = 8;
        }
        else if (portalModifier.thickness == 2)
        {
            padding = 2;
            width = 4;
        }
        else if (portalModifier.thickness == 3)
        {
            padding = 0;
            width = 0;
        }

        drawTexturedModalRect(0, 0, 0, 0, 0, 0);
        drawRect(guiLeft + 134 + padding, guiTop + 15, guiLeft + 16 - width + 134 + padding, guiTop + 15 + 16, 0xFF555555);
        ItemStack itemstack = Textures.getItemStackFromTexture(portalModifier.texture);

        if (itemstack != null)
        {
            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, guiLeft + xSize - 24, guiTop + 15);
        }
        
        fontRenderer.drawString(Localization.localizeString("tile.portalModifier.name"), guiLeft + xSize / 2 - fontRenderer.getStringWidth(Localization.localizeString("tile.portalModifier.name")) / 2, guiTop + -15, 0xFFFFFF);
        fontRenderer.drawString(Localization.localizeString("gui.upgrades.title"), guiLeft + 8, guiTop + 3, 0xFF444444);
        fontRenderer.drawString(Localization.localizeString("gui.modifications.title"), guiLeft + xSize - 8 - fontRenderer.getStringWidth(Localization.localizeString("gui.modifications.title")), guiTop + 3, 0xFF444444);
        fontRenderer.drawString(Localization.localizeString("gui.network.title"), guiLeft + 8, guiTop + 35, 0xFF444444);
        
        if (portalModifier.network.equals(""))
        {
            fontRenderer.drawStringWithShadow(Localization.localizeString("gui.network.info"), guiLeft + xSize / 2 - fontRenderer.getStringWidth(Localization.localizeString("gui.network.info")) / 2, guiTop + 51, 0xFF00FF00);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        
    }

    @Override
    public void drawScreen(int x, int y, float par3)
    {
        if (!isActive)
        {
            super.drawScreen(x, y, par3);
            
            if (!portalModifier.network.equals(""))
            {
                String[] split = portalModifier.network.split(Reference.glyphSeperator);

                for (int i = 0; i < split.length; i++)
                {
                    for (int j = 0; j < Reference.glyphValues.size(); j++)
                    {
                        if (Reference.glyphValues.get(j).equalsIgnoreCase(split[i]))
                        {
                            itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, Reference.glyphItems.get(j), guiLeft + 8 + i * 18, guiTop + 47);
                        }
                    }
                }
            }

            if (isPointInRegion(134, 15, 16, 16, x, y))
            {
                String thickness = "";

                switch (portalModifier.thickness)
                {
                    case 0:
                        thickness = Localization.localizeString("gui.thickness.normal");
                        break;

                    case 1:
                        thickness = Localization.localizeString("gui.thickness.thick");
                        break;

                    case 2:
                        thickness = Localization.localizeString("gui.thickness.thicker");
                        break;

                    case 3:
                        thickness = Localization.localizeString("gui.thickness.fullblock");
                        break;
                }

                List<String> list = new ArrayList<String>();
                list.add(Localization.localizeString("gui.thickness.title"));
                list.add(EnumChatFormatting.GRAY + thickness);
                list.add(EnumChatFormatting.DARK_GRAY + "Click to change");
                drawText(list, x, y);
            }
            else if (isPointInRegion(152, 15, 16, 16, x, y))
            {
                String txt = "";

                if (Textures.getItemStackFromTexture(portalModifier.texture) != null)
                {
                    ItemStack stack = Textures.getItemStackFromTexture(portalModifier.texture);

                    if (stack.itemID == BlockIds.DummyPortal)
                    {
                        txt = Localization.localizeString("gui.portalColour." + ItemDye.dyeColorNames[stack.getItemDamage()]);
                    }
                    else
                    {
                        txt = Localization.localizeString(stack.getItemName() + ".name");
                    }
                }
                else
                {
                    txt = "Unknown";
                }

                List<String> list = new ArrayList<String>();
                list.add(Localization.localizeString("gui.facade.title"));
                list.add(EnumChatFormatting.GRAY + txt);
                list.add(EnumChatFormatting.DARK_GRAY + "Shift-click on a block to change");
                list.add(EnumChatFormatting.DARK_GRAY + "Right-click to reset");
                drawText(list, x, y);
            }
            else if (isPointInRegion(7, 46, 162, 18, x, y))
            {
                List<String> str = new ArrayList<String>();
                str.add(Localization.localizeString("gui.network.title"));
                str.add(EnumChatFormatting.GRAY + Localization.localizeString("gui.network.info"));
                drawText(str, x, y);
            }
        }
        else
        {
            String txt = Localization.localizeString("gui.activeModifier");

            drawDefaultBackground();
            fontRenderer.drawString(txt, width / 2 - fontRenderer.getStringWidth(txt) / 2, guiTop, 0xFFFFFF);
            okayButton.drawButton(mc, 0, 0);
        }
    }

    @SuppressWarnings("rawtypes")
    public void drawText(List<String> list, int x, int y)
    {
        if (!list.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = fontRenderer.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = x + 12;
            int j1 = y - 12;
            int k1 = 8;

            if (list.size() > 1)
            {
                k1 += 2 + (list.size() - 1) * 10;
            }

            if (i1 + k > width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > height)
            {
                j1 = height - k1 - 6;
            }

            zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int l1 = -267386864;
            drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < list.size(); ++k2)
            {
                String s1 = list.get(k2);
                fontRenderer.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public void drawTexturedRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.062F;
        float f1 = 0.062F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(par1 + 0, par2 + par6, zLevel, (par3 + 0) * f, (par4 + par6) * f1);
        tessellator.addVertexWithUV(par1 + par5, par2 + par6, zLevel, (par3 + par5) * f, (par4 + par6) * f1);
        tessellator.addVertexWithUV(par1 + par5, par2 + 0, zLevel, (par3 + par5) * f, (par4 + 0) * f1);
        tessellator.addVertexWithUV(par1 + 0, par2 + 0, zLevel, (par3 + 0) * f, (par4 + 0) * f1);
        tessellator.draw();
    }

    public int getGuiLeft()
    {
        return guiLeft;
    }

    public int getGuiTop()
    {
        return guiTop;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        okayButton = new GuiButton(100, width / 2 - 50, height / 2 - 10, 100, 20, Localization.localizeString("gui.close"));
        buttonList.add(okayButton);
        okayButton.drawButton = isActive;

        List<String> strList = new ArrayList<String>();
        strList.add(Localization.localizeString("gui.redstoneControl.title"));
        strList.add(EnumChatFormatting.GRAY + Localization.localizeString("gui.redstoneControl.normal"));
        buttonList.add(new GuiItemStackButton(10, guiLeft + xSize + 4, guiTop + 4, new ItemStack(Block.torchRedstoneActive), portalModifier.redstoneSetting == 0, strList));

        strList = new ArrayList<String>();
        strList.add(Localization.localizeString("gui.redstoneControl.title"));
        strList.add(EnumChatFormatting.GRAY + Localization.localizeString("gui.redstoneControl.inverted"));
        buttonList.add(new GuiItemStackButton(11, guiLeft + xSize + 4, guiTop + 24, new ItemStack(Block.torchRedstoneIdle), portalModifier.redstoneSetting == 1, strList));
        
        strList = new ArrayList<String>();
        strList.add(Localization.localizeString("gui.redstoneControl.title"));
        strList.add(EnumChatFormatting.GRAY + "Precise");
        buttonList.add(new GuiItemStackButton(12, guiLeft + xSize + 4, guiTop + 44, new ItemStack(Item.redstone), portalModifier.redstoneSetting > 1, strList, "" + (portalModifier.redstoneSetting > 2 ? (portalModifier.redstoneSetting - 2) : 0)));
        
        for (int i = portalModifier.upgradeHandler.getUpgrades().size() - 1; i >= 0; i--)
        {
            buttonList.add(new GuiItemStackButton(50, guiLeft + 8 + i * 18, guiTop + 15, portalModifier.upgradeHandler.getUpgrade(i).getDisplayItemStack(), false, portalModifier.upgradeHandler.getUpgrade(i).getText(true), true));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked)
    {
        super.mouseClicked(x, y, buttonClicked);

        if (isPointInRegion(134, 15, 16, 16, x, y))
        {
            portalModifier.thickness++;

            if (portalModifier.thickness >= 4)
            {
                portalModifier.thickness = 0;
            }

            hasInteractedWith = true;
        }
        else if (isPointInRegion(7, 46, 162, 18, x, y))
        {
            // TODO PacketDispatcher.sendPacketToServer(new PacketGui(true, false, GuiIds.PortalModifierNetwork, portalModifier).getPacket());
        }
        else if (isShiftKeyDown() && getSlotAtPosition(x, y) != null)
        {
            PortalTexture Text = Textures.getTextureFromItemStack(getSlotAtPosition(x, y).decrStackSize(0));

            if (Text != null)
            {
                String text = Text.getID();

                if (portalModifier.texture.equals(text))
                {
                    text = Textures.getTextureFromItemStack(getSlotAtPosition(x, y).decrStackSize(0), portalModifier.texture).getID();
                }

                portalModifier.texture = text;
            }
            
            hasInteractedWith = true;
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (hasInteractedWith)
        {
            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketPortalModifierUpdate(portalModifier)));
            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketPortalModifierUpgrade(portalModifier)));
            
            portalModifier.worldObj.markBlockForRenderUpdate(portalModifier.xCoord, portalModifier.yCoord, portalModifier.zCoord);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }
}
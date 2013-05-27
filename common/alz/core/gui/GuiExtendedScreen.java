package alz.core.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiExtendedScreen extends GuiContainer
{
    public class GuiExtendedItemSlot
    {
        public int xCoord, yCoord, width, height;
        
        List<ItemStack> requiredItems;
        List<ItemStack> excludedItems;
        
        ItemStack itemStack;
        IInventory tileEntity;
        int slot;
        
        public GuiExtendedItemSlot(int x, int y, IInventory tileentity, int Slot)
        {
            xCoord = x;
            yCoord = y;
            width = height = 16;
            tileEntity = tileentity;
            slot = Slot;
            
            excludedItems = new ArrayList<ItemStack>();
            requiredItems = new ArrayList<ItemStack>();
        }
        
        public GuiExtendedItemSlot(int x, int y, ItemStack stack)
        {
            xCoord = x;
            yCoord = y;
            width = height = 16;
            itemStack = stack;
            
            excludedItems = new ArrayList<ItemStack>();
            requiredItems = new ArrayList<ItemStack>();
        }
        
        public ItemStack getItemStack()
        {
            return itemStack != null ? itemStack : (tileEntity != null ? tileEntity.getStackInSlot(slot) : null);
        }
        
        public boolean setSlot(ItemStack stack)
        {
            if (itemStack != null)
            {
                itemStack = stack;
            }
            else if (tileEntity != null)
            {
                tileEntity.setInventorySlotContents(slot, stack);
            }
            
            return false;
        }
        
        public void drawSlot()
        {
            if (getItemStack() != null)
            {
                itemRenderer.zLevel = 300F;
                itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, getItemStack(), xCoord, yCoord);
                itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, getItemStack(), xCoord, yCoord);
                itemRenderer.zLevel = 0F;
            }
        }
        
        public void drawSlot(int bumpX, int bumpY)
        {
            if (getItemStack() != null)
            {
                itemRenderer.zLevel = 300F;
                itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, getItemStack(), xCoord + bumpX, yCoord + bumpY);
                itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, getItemStack(), xCoord + bumpX, yCoord + bumpY);
                itemRenderer.zLevel = 0F;
            }
        }
        
        public void setExcludeList(List<ItemStack> stackList)
        {
            
        }
        
        public void setRequireList(List<ItemStack> stackList)
        {
            
        }
        
        public void onClick(int x, int y, int button, ItemStack stack)
        {
            
        }
        
        public boolean isOnSelf(int x, int y)
        {
            return x >= xCoord && x <= xCoord + 16 && y >= yCoord && y <= yCoord + 16;
        }
        
        public boolean isOnSelf(int x, int y, int bumpX, int bumpY)
        {
            return x >= xCoord + bumpX && x <= xCoord + bumpX + 16 && y >= yCoord + bumpY && y <= yCoord + bumpY + 16;
        }
    }
    
    public class GuiFakeItemSlot extends GuiExtendedItemSlot
    {
        public GuiFakeItemSlot(int x, int y, ItemStack stack)
        {
            super(x, y, stack);
        }
        
        public GuiFakeItemSlot(int x, int y, IInventory inv, int slot)
        {
            super(x, y, inv, slot);
        }
                
        @Override
        public void onClick(int x, int y, int button, ItemStack stack)
        {
            if (!isOnSelf(x, y))
            {
                return;
            }
            
            if (button == 0 && stack != null)
            {
                itemStack = stack;
            }
            else if (button == 1)
            {
                itemStack = null;
            }
        }
    }
    
    public List<GuiFakeItemSlot> extendedSlots;
    protected TileEntity tileEntity;
    protected Container container;
    
    public GuiExtendedScreen(Container par1Container, IInventory inventory)
    {
        super(par1Container);
        
        if (inventory instanceof TileEntity)
        {
            tileEntity = (TileEntity) inventory;
        }
        
        container = par1Container;
        extendedSlots = new ArrayList<GuiFakeItemSlot>();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        drawExtendedSlots();
    }
    
    public GuiFakeItemSlot getSlotAt(int x, int y)
    {
        for (GuiFakeItemSlot slot : extendedSlots)
        {
            if (slot.xCoord < x && slot.xCoord + width > x && slot.yCoord < y && slot.yCoord + height > y)
            {
                return slot;
            }
        }
        
        return null;
    }
    
    public int getSlotIDAt(int x, int y)
    {
        for (int i = 0; i < extendedSlots.size(); i++)
        {
            GuiFakeItemSlot slot = extendedSlots.get(i);
            
            if (slot.xCoord <= x && slot.xCoord + width >= x && slot.yCoord <= y && slot.yCoord + height >= y)
            {
                return i;
            }
        }
        
        return -1;
    }
    
    private void drawExtendedSlots()
    {
        int bX = (width - xSize) / 2,
            bY = (height - ySize) / 2;
        
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        int i1 = 240;
        int k1 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1 / 1.0F, k1 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        for (GuiFakeItemSlot slot : extendedSlots)
        {
            slot.drawSlot(bX, bY);
        }
        
        GL11.glPopMatrix();
    }
    
    private void drawExtendedSlotsForeground(int mX, int mY)
    {
        int bX = (width - xSize) / 2,
            bY = (height - ySize) / 2;
        String str = "";
        
        GuiFakeItemSlot slot = getSlotAt(mX - bX, mY - bY);
        
        if (slot != null)
        {
            str = slot.getItemStack().getDisplayName();
        }
        
        if (str.length() > 0)
        {
            List<String> strList = new ArrayList<String>();
            strList.add(str);     
            
            drawHoveringText(strList, mX - bX, mY - bY, fontRenderer);
            RenderHelper.enableGUIStandardItemLighting();
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        super.drawGuiContainerForegroundLayer(par1, par2);
        
        drawExtendedSlotsForeground(par1, par2);
    }
    
    @SuppressWarnings("rawtypes")
    public void drawHoverText(List par1List, int par2, int par3, FontRenderer font)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
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

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String) par1List.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

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
    
    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        
        for (GuiFakeItemSlot slot : extendedSlots)
        {
            slot.onClick(par1, par2, par3, null);
        }
    }
}
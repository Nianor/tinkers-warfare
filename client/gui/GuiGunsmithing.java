package com.nianor.tinkersarsenal.client.gui;

import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.client.container.GunContainer;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

public class GuiGunsmithing extends GuiContainer {

    private float xSize_lo;
    private float ySize_lo;

    public static final int GUN_ID = 0;

    private static final ResourceLocation texture = new ResourceLocation(TinkersArsenal.MODID,"textures/guis/gunsmithing.png");

    private BaseGunItem.GunInventory inventory = null;

    public GuiGunsmithing(InventoryPlayer playerInv, IItemHandler itemInv) {
        super(new GunContainer(playerInv, itemInv));
    }

    public GuiGunsmithing(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float)par1;
        this.ySize_lo = (float)par2;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        //if (isTest) {TinkersArsenal.logger.info("Inventory Name: {}", inventory.getInventoryName()); }
        String s = "Gun Components";
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s)/2, this.fontRenderer.FONT_HEIGHT/2, 4210752);
        //this.fontRenderer.drawString("TEST_STRING", 26, this.ySize - 96 + 4, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        int i1;
        //drawPlayerModel(k + 51, l + 75, 30, (float)(k + 51) - this.xSize_lo, (float)(l + 75 - 50) - this.ySize_lo, this.mc.thePlayer);
    }
}

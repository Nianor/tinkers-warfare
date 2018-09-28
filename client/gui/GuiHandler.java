package com.nianor.tinkersarsenal.client.gui;

import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.client.container.GunContainer;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import sun.net.www.content.text.plain;

import javax.annotation.Nullable;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

public class GuiHandler implements IGuiHandler {

    public void registerRenders(){}

    public static final int GUN_ID = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(isTest) {TinkersArsenal.logger.info("Server has begun loading a GUI with an ID of {}!", ID);}
        if (ID==GUN_ID) {
            if (isTest) {TinkersArsenal.logger.info("GUI Loader has found the GUI to load!");}
            //if (player.getHeldItemMainhand().getClass().equals(BaseGunItem.class)) {
                if(isTest) {TinkersArsenal.logger.info("Server GUI initializing!");}
                GunContainer output = new GunContainer(player.inventory, player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
                if(isTest) {TinkersArsenal.logger.info("Server GUI has been successfully initialized.");}
                if(isTest) {TinkersArsenal.logger.info("The Server GUI has a window ID of {}!", output.windowId);}
                return output;
            //}
        }
        TinkersArsenal.logger.info("ERROR! Server GuiHandler could not match the requested ID with any known IDs!");
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(isTest) {TinkersArsenal.logger.info("Client has begun loading a GUI with an ID of {}!", ID);}
        if (ID==GUN_ID){
            if (isTest) {TinkersArsenal.logger.info("Client GUI Loader has found the GUI to load!");}
            //if (player.getHeldItemMainhand().getClass().equals(BaseGunItem.class)) {
            if(isTest) {TinkersArsenal.logger.info("Client GUI initializing! Item has NBT of {}", player.getHeldItemMainhand().serializeNBT());}
            GuiGunsmithing output = new GuiGunsmithing(new GunContainer(player.inventory, player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)));
            if(isTest) {TinkersArsenal.logger.info("Client GUI has been successfully initialized.");}
            if(isTest) {TinkersArsenal.logger.info("The Client GUI has a height of {}!", output.height);}
            return output;
            //}
        }
        TinkersArsenal.logger.info("ERROR! ClientGuiHandler could not match the requested ID with any known IDs!");
        return null;
    }
}

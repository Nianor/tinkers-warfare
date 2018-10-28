package com.nianor.tinkersarsenal.client.container;

import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.client.inventory.SlotSuperclassRestricted;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.inventory.SlotRestrictedItem;
import slimeknights.tconstruct.library.tools.ToolCore;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

public class GunContainer extends Container {


    private final int ARMOR_START = BaseGunItem.INV_SIZE, ARMOR_END = ARMOR_START+3, INV_START = ARMOR_END+1, INV_END = INV_START+26,
            HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

    int size = 6;

    boolean interact = true;

    public GunContainer(InventoryPlayer inventoryPlayer, IItemHandler itemInv) {
        if (isTest) {TinkersArsenal.logger.info("GUI Container has loaded.");}

        int i;


        for (i = 0; i < 6/*GunInventory.INV_SIZE*/; ++i)
        {
            Class allowedClass = (BaseGunItem.SlotClassList.get(i%6));
            if (isTest) {TinkersArsenal.logger.info("Slot {}: \nClass: {} \nX Position: {}", i, allowedClass);}
            this.addSlotToContainer(new SlotSuperclassRestricted(allowedClass, itemInv, i, 36 + (18*(i%6)), 59));
        }

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // PLAYER ACTION BAR - uses default locations for standard action bar texture file
        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return interact;
    }

    public void setInteract(boolean interactIn) {interact = interactIn;}


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
    /*@Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
    }

    @Override
    public void detectAndSendChanges() {
        if (!inventory.isEmpty()) {
            super.detectAndSendChanges();
        }
        //if (!player.world.isRemote) {
        //    saveInventory(player);
        //}

    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < this.size) {
                if (!mergeItemStack(itemstack1, this.size, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!acceptsStack(itemstack1)) {
                return null;
            } else if (!mergeItemStack(itemstack1, 0, this.size, false)) {
                return null;
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }*/


    public boolean acceptsStack(ItemStack itemstack) {
        if(isTest) {TinkersArsenal.logger.info("Attempting to check if item is permitted.");
        if (!itemstack.getClass().getSuperclass().getSuperclass().equals(ToolCore.class)) {
            if(isTest) {TinkersArsenal.logger.info("Granted!");}
            return true;
            }
        }
        if(isTest) {TinkersArsenal.logger.info("Denied.");}
            return false;
    }


    //public void saveInventory(EntityPlayer entityPlayer) {
    //    inventory.onGuiSaved(entityPlayer);
    //
}

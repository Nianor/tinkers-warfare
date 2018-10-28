package com.nianor.tinkersarsenal.client.inventory;

import com.nianor.tinkersarsenal.TinkersArsenal;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.SlotRestrictedItem;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

//Largely a tweaked version of Mantle's SlotRestrictedItem
public class SlotSuperclassRestricted extends SlotItemHandler {

    private final Class allowedClass;


    public SlotSuperclassRestricted(Class classIn, IItemHandler item, int index, int xPosition, int yPosition) {
        super(item, index, xPosition, yPosition);
        allowedClass = classIn;
    }


    @Override
    public boolean isItemValid(ItemStack stack) {
        if (isTest) {TinkersArsenal.logger.info("Filter: {} \nTested Item: {}", allowedClass, stack.getItem().getClass().getSuperclass());}
        return(allowedClass == stack.getItem().getClass().getSuperclass());
    }
}


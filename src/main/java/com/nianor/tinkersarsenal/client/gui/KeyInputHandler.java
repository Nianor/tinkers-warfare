package com.nianor.tinkersarsenal.client.gui;

import com.nianor.tinkersarsenal.ClientProxy;
import com.nianor.tinkersarsenal.CommonProxy;
import com.nianor.tinkersarsenal.TinkersArsenal;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import com.nianor.tinkersarsenal.network.KeyPressPacket;
import com.nianor.tinkersarsenal.network.SimpleNetworkWrapper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;
import static com.nianor.tinkersarsenal.TinkersArsenal.logger;
import static com.nianor.tinkersarsenal.TinkersArsenal.trackGunFiring;
import static com.nianor.tinkersarsenal.ClientProxy.keyBindings;

@Mod.EventBusSubscriber(Side.CLIENT)
public class KeyInputHandler {
    private static boolean selectorKey= false;
    private static boolean fireKey= false;
    private static boolean loadKey= false;
    private static boolean chamberKey= false;
    private static boolean changed;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(InputEvent.KeyInputEvent event)
    {
        changed = false;

        // DEBUG
        System.out.println("Key Input Event");

        // make local copy of key binding array


        // check each enumerated key binding type for pressed and take appropriate action
        if (keyBindings[0].isKeyDown())
        {
            // DEBUG
            if(trackGunFiring){logger.info("Selector Key Pressed");}
            selectorKey = true;
            changed = true;

            // do stuff for this key binding here
            // remember you may need to send packet to server
        }
        else {
            selectorKey = false;
        }
        if (keyBindings[1].isKeyDown()&&keyBindings[1].getKeyCode()!=-99)
        {
            // DEBUG
            if(trackGunFiring){logger.info("Fire Key Pressed");}
            fireKey = true;
            changed = true;

            // do stuff for this key binding here
            // remember you may need to send packet to server
        }
        else {
            fireKey = false;
        }
        if (keyBindings[2].isKeyDown()&&keyBindings[2].getKeyCode()!=-99) {
            if(trackGunFiring){logger.info("Chamber Key Pressed");}
            chamberKey = true;
            changed = true;
        }
        else {
            chamberKey = false;
        }
        if (keyBindings[3].isKeyDown()) {
            if(trackGunFiring){logger.info("Load Key Pressed");}
            loadKey = true;
            changed = true;
        }
        else {
            selectorKey = false;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        ItemStack item = event.player.getHeldItemMainhand();
        //if(trackGunFiring){logger.info("Evaluating need for Player Tick Update");}
        if (item.getItem().getClass() == BaseGunItem.class && changed) {
            if(trackGunFiring){logger.info("Player Tick Update being sent");}
            SimpleNetworkWrapper.INSTANCE.sendToServer(new KeyPressPacket(selectorKey, fireKey, chamberKey, loadKey));//, (EntityPlayerMP)event.player);
        }

    }
}

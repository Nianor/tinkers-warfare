package com.nianor.tinkersarsenal.items;

import com.google.common.collect.Multimap;
import com.nianor.tinkersarsenal.ClientProxy;
import com.nianor.tinkersarsenal.TinkersArsenal;

import com.nianor.tinkersarsenal.client.container.GunContainer;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.nianor.tinkersarsenal.network.VelocityPacket;
import com.nianor.tinkersarsenal.network.SimpleNetworkWrapper;
import com.nianor.tinkersarsenal.tinkers.tools.Action.ActionCore;
import com.nianor.tinkersarsenal.tinkers.tools.Barrel.BarrelCore;
import com.nianor.tinkersarsenal.tinkers.tools.Chamber.ChamberCore;
import com.nianor.tinkersarsenal.tinkers.tools.Feed.FeedCore;
import com.nianor.tinkersarsenal.tinkers.tools.Receiver.RecieverCore;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import sun.util.resources.cldr.kea.TimeZoneNames_kea;

import java.util.*;

import static com.nianor.tinkersarsenal.TinkersArsenal.logger;
import static com.nianor.tinkersarsenal.TinkersArsenal.trackGunFiring;


//THANK YOU SPYEEDY. Basically all of the inventory code is from you. The other fiddly bits come from other people, and some of it is original.

public class BaseGunItem extends Item{

    private ItemStack item;

    protected float weight;

    protected float recoil;

    protected float kick;

    protected float caliber;

    public boolean selectorSwitchKey;
    public boolean fireKey;
    public boolean chamberKey;
    public boolean loadKey;

    protected boolean isChambered;

    protected boolean isPrimed;

    protected boolean isMagLoaded;

    protected boolean isWorking;

    protected int actionType = 0;

    protected int loaderType=1;

    private EntityPlayer player;

    protected short selector = 3;

    private boolean selectorSwitch;

    protected boolean triggerDown;

    protected boolean hasReset;

    private boolean isChambering;

    public static int INV_SIZE = 6;

    protected GunInventory inventory;

    protected ItemStack GunItem;

    GunContainer WorkingContainer;

    public int shotsFired;

    public static ArrayList<Class<?>> SlotClassList = new ArrayList<Class<?>>();

    public BaseGunItem(/*int par1*/)
    {

        //super(par1);
        setMaxStackSize(1);

        //7.62
        //caliber=.312F;

        //30mm
        caliber=1.1811F;
        this.setCreativeTab(CreativeTabs.COMBAT);

        SlotClassList.add(BarrelCore.class);
        SlotClassList.add(ChamberCore.class);
        SlotClassList.add(ActionCore.class);
        SlotClassList.add(FeedCore.class);
        SlotClassList.add(RecieverCore.class);
        SlotClassList.add(BarrelCore.class); //REPLACE WITH FURNITURE LATER
    }



    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1; // return any value greater than zero
    }

    /*public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            playerIn.openGui(TinkersArsenal.INSTANCE, GuiHandler.GUN_ID, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
            if(trackGunFiring) {TinkersArsenal.logger.info("GUI commencing opening!");}
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);

    }*/

    public boolean isChambering(){return isChambering;}

    public float getCaliber() {return caliber;}

    public void refreshStats(EntityPlayer playerIn) {
        if (WorkingContainer==null) {WorkingContainer = new GunContainer(playerIn.inventory, playerIn.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));}
        boolean abort = false;
        int i = 0;
        while (i < 5/*change to 6 once furniture is added*/) {
            Item item = WorkingContainer.inventorySlots.get(i).getStack().getItem();
            if (trackGunFiring) {TinkersArsenal.logger.info("Inventory Slot {}: {}", i, WorkingContainer.inventorySlots.get(i));}
            if (trackGunFiring) {TinkersArsenal.logger.info("Inventory Contents {}: {}", i, item);}
            if (!(item.getClass().getSuperclass() == SlotClassList.get(i))) {
                TinkersArsenal.logger.info("ERROR, INCORRECT ITEM IN SLOT");
                abort = true;
                break;
            }
            i++;
        }
        if (!abort) {
            NBTTagCompound barrelStats = getItemStats(WorkingContainer.inventorySlots.get(0).getStack().getItem(), WorkingContainer.inventorySlots.get(0).getStack());
            NBTTagCompound chamberStats = getItemStats(WorkingContainer.inventorySlots.get(1).getStack().getItem(), WorkingContainer.inventorySlots.get(1).getStack());
            NBTTagCompound actionStats = getItemStats(WorkingContainer.inventorySlots.get(2).getStack().getItem(), WorkingContainer.inventorySlots.get(2).getStack());
            NBTTagCompound feedStats = getItemStats(WorkingContainer.inventorySlots.get(3).getStack().getItem(), WorkingContainer.inventorySlots.get(3).getStack());
            NBTTagCompound recieverStats = getItemStats(WorkingContainer.inventorySlots.get(4).getStack().getItem(), WorkingContainer.inventorySlots.get(4).getStack());
            if (trackGunFiring) {TinkersArsenal.logger.info("\nBarrel Stats: {}\nChamber Stats: {}\nAction Stats: {}\nFeed Stats: {}\nReceiver Stats: {}", barrelStats, chamberStats, actionStats, feedStats, recieverStats);}
        }

    }

    public NBTTagCompound getItemStats (Item item, ItemStack itemStack) {
        NBTTagCompound itemNBT = null;
        if (trackGunFiring) {TinkersArsenal.logger.info("{} NBT: {}", item.getUnlocalizedName(), item.getNBTShareTag(itemStack).getCompoundTag("Stats"));}
        itemNBT = item.getNBTShareTag(itemStack).getCompoundTag("Stats");
        if (trackGunFiring) {TinkersArsenal.logger.info("{} NBT: {}", item.getUnlocalizedName(), itemNBT);}
        return itemNBT;
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        inventory = new GunInventory();
        return inventory;
    }

    public EntityArsenalProjectile GunFire(World worldIn, EntityPlayer playerIn, double seed) {
        EntityArsenalProjectile projectile = null;
        if(/*selector!=0&&*/getShootCooldown(playerIn.getHeldItemMainhand())==0) {
            if(trackGunFiring) {TinkersArsenal.logger.info("Preparing to fire gun");}
            //Code to render the trigger pull.
            if (inventory.fireGun(this, playerIn)/*&&!worldIn.isRemote*/) {
                if (trackGunFiring) {
                    TinkersArsenal.logger.info("Trigger Pulled at pitch {} and yaw {}", playerIn.rotationPitch, playerIn.getRotationYawHead());
                }
                float yaw = playerIn.getRotationYawHead();

                projectile = new EntityArsenalProjectile(worldIn, playerIn, yaw, 39.85F, .1F, seed);
                worldIn.spawnEntity(projectile);
                Minecraft.getMinecraft().getRenderViewEntity().rotationYaw+=(((Math.random()-.5)*2));
                Minecraft.getMinecraft().getRenderViewEntity().rotationPitch+=-((Math.random()));

                playerIn.getHeldItemMainhand().serializeNBT().setInteger("cooldown", 0);
                shotsFired = shotsFired+1;
                System.out.println("Shots Fired: "+shotsFired);
                hasReset=false;
                //Firing animation code.
                if(loaderType==1) {
                    isChambering=true;
                }
            }
        }
        return projectile;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack BaseGunItem) {
        return EnumAction.BOW;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }



    public void GunChamber(World worldIn, EntityPlayer playerIn) {

        if(trackGunFiring) {TinkersArsenal.logger.info("Round chambered:");}

        if(isChambered) {/*This will later reference the code needed to eject the unfired round.*/}
        System.out.println(actionType);
        if (actionType!=-1) {
            isPrimed=true;
        }
        System.out.println(isPrimed);
        isChambered = true;
        //And then some code needed to trigger the animations.

    }

    public void GunLoad(World worldIn, EntityPlayer playerIn) {

            if(trackGunFiring) {TinkersArsenal.logger.info("Loading/Unloading Gun");}
            //code about dropping the spent mag, animations, so on and so forth. Since this is mostly placeholder, this is *very* temporary.
            isMagLoaded = !isMagLoaded;

    }

    public void GunSelector(World worldIn, EntityPlayer playerIn) {
        if(selector==3) {
            System.out.println("Selector set to full");
            selector = 1;
        }
        else if(selector==2) {
            System.out.println("Selector set to burst");
            selector=3;
        }
        else if(selector==1) {
            System.out.println("Selector set to semi");
            selector=2;
        }
        //selector = (short)(selector+1);
        //if (selector ==3) {selector=0;}
        //ALL of this needs proper implementation and stuff, selector values, so on and so forth.
        //That means some part of the gun needs a selector dictator to determine the number of fire modes, I need to add something that figures out what the selector should be on,
        //so on and so forth. For now, it does nothing.

    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        this.player = null;
        return true;
    }

    public GunInventory getInventory() {
        return inventory;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity ent, int slot, boolean inHand) {
        boolean button = false;

        super.onUpdate(stack, world, ent, slot, inHand);
        //<editor-fold desc="This checks to make sure the player is valid. If not, it re-assigns the player.">
        {
            if ((this.player == null || this.player.isDead || player.inventory.hasItemStack(this.getDefaultInstance())) && inHand && !world.isRemote) {
                int i = 0;
                while (i < world.getMinecraftServer().getCurrentPlayerCount()) {
                    EntityPlayerMP playerTemp = world.getMinecraftServer().getPlayerList().getPlayers().get(i);
                    if ((playerTemp.getHeldItem(EnumHand.MAIN_HAND) == stack || playerTemp.getHeldItem(EnumHand.OFF_HAND) == stack)) {
                        this.player = playerTemp;
                    }
                    i = i + 1;

                }
            }
        }
        //</editor-fold>

        //NOTE TO SELF "CULL THE SOUND MANAGER AND USE THE SOUND SYSTEM DIRECTLY."


        if (inHand&&player!=null) {

            EnumHand hand;

            if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof BaseGunItem) {
                hand = EnumHand.MAIN_HAND;
            } else {
                hand = EnumHand.OFF_HAND;
            }

            //<editor-fold desc="Setting NBT values.">
            stack.serializeNBT().setBoolean("chambering", isChambering);
            //</editor-fold>
            //<editor-fold desc="Setting key booleans.">
            //selectorSwitchKey=;
            if (ClientProxy.keyBindings[1].getKeyCode() < 0) {
                fireKey = Mouse.isButtonDown(100 + ClientProxy.keyBindings[1].getKeyCode());
            } else {
                fireKey = Keyboard.isKeyDown(ClientProxy.keyBindings[1].getKeyCode());
            }
            if (ClientProxy.keyBindings[1].getKeyCode() < 0) {
                chamberKey = Mouse.isButtonDown(100 + ClientProxy.keyBindings[2].getKeyCode());
            } else {
                chamberKey = Keyboard.isKeyDown(ClientProxy.keyBindings[2].getKeyCode());
            }
            //loadKey=;
            //</editor-fold>
            //<editor-fold desc="Output spam.">
            if (TinkersArsenal.trackGunFiring) {
                System.out.println("Button 0:" + Mouse.isButtonDown(0) + "\nButton 1:" + Mouse.isButtonDown(1));
                System.out.println("Button code used:" + (100 + ClientProxy.keyBindings[1].getKeyCode()));
                System.out.println("Reset: " + hasReset);
                System.out.println("Firing Pin: " + isPrimed);
                System.out.println("isChambering: " + isChambering);
                System.out.println("isChambered: " + isChambered);
                System.out.println("Item Chambering: " + stack.serializeNBT().getBoolean("chambering"));
            }
            //</editor-fold>

            if (ClientProxy.keyBindings[0].getKeyCode() < 0) {
                if (Mouse.isButtonDown(100 + ClientProxy.keyBindings[0].getKeyCode())) {
                    if (TinkersArsenal.trackGunFiring) {
                        System.out.println("Selector switch activated");
                    }
                    selectorSwitch = true;
                } else if (selectorSwitch) {
                    GunSelector(world, player);
                    selectorSwitch = false;
                    if (TinkersArsenal.trackGunFiring) {
                        System.out.println("Selector switch released, fire mode set to " + selector);
                    }
                }
            }
            if (player != null && world != null) {
                if (this.onItemRightClick(world, player, hand).getType() == EnumActionResult.SUCCESS || this.onItemRightClick(world, player, hand).getType() == EnumActionResult.PASS) {
                    //<editor-fold desc="Chambering">
                    if ((isChambering) && (!chamberKey ||/*pretty sure some of this is jank*/triggerDown)) {

                        //ALL OF THIS NEEDS TO BE BETTER CODED.

                        if (world.isRemote) {
                            world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, .5F, false);
                            Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, 1F);
                        } else {
                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, .5F);
                        }
                        GunChamber(world, player);
                        isChambering = false;
                    } else if (!isChambering && chamberKey && (!isChambered || ClientProxy.keyBindings[2] != ClientProxy.keyBindings[1])) {
                        if (world.isRemote) {
                            world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, 1F, false);
                            Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, 1F);
                        } else {
                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 2F, 1F);
                        }
                        isChambering = true;
                    }
                    //</editor-fold>

                    triggerDown = fireKey;

                    if (!isChambering) {
                        if (fireKey) {
                            triggerDown = true;
                            //logger.info("Cooldown: {}",getShootCooldown(player.getHeldItem(handIn)));
                            if (getShootCooldown(stack) == 0 && ((hasReset) || (selector == 3)) && isChambered) {
                                //logger.info("I would have fired right now, I just don't want to.");
                                System.out.println("Mouse firing, button: " + button + ", Reset: " + hasReset + ", selector: " + selector + ", chambered: " + isChambered);
                                if (world.isRemote) {
                                    //World worldServer = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
                                    ////world.getMinecraftServer().get;
                                    ////World worldServer = world.getMinecraftServer().getWorld(player.dimension);
                                    //GunFire(worldServer, player, false);
                                    double seed = Math.random();
                                    EntityArsenalProjectile entity = GunFire(world, player, seed);
                                    world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_BLAST/*ENTITY_LIGHTNING_IMPACT*/, SoundCategory.PLAYERS, 1F, .5F, true);
                                    ////world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_BLAST/*ENTITY_LIGHTNING_IMPACT*/, SoundCategory.PLAYERS, .75F, (float)(2F-(Math.random()*.25)), true);
                                    ////world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, SoundCategory.PLAYERS, .25F, (float)(.5F+(Math.random()*.25)), true);
                                }
                            }
                        }
                        if (triggerDown && !fireKey) {
                            triggerDown = false;
                        }
                        if(!fireKey) {
                            hasReset=true;
                        }
                        if(TinkersArsenal.trackGunFiring) {
                            logger.info("Reset: {}\nTrigger Down: {}\nSelectorSwitch: {}\nMouseButtonDown: {}", hasReset, triggerDown, selector, button);
                        }
                        //System.out.println("CAN RIGHT CLICK");
                    } else {
                        System.out.println("CANNOT RIGHT CLICK");
                    }
                } else {
                    System.out.println("PLAYER OR WORLD NULL");
                }

            }
            {
                if (stack.serializeNBT().hasKey("cooldown")) {
                    int cooldown = stack.serializeNBT().getInteger("cooldown") - 1;
                    if (cooldown <= 0)
                        stack.serializeNBT().removeTag("cooldown");
                    else
                        stack.serializeNBT().setInteger("cooldown", cooldown);
                }
            }
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack){
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return true; //!ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    public boolean isReset() {
        boolean answer = false;
        //if(selector==2) {
        //    answer=true;
        //}
        //else if (selector==1) {
            answer=hasReset;
        //}

        return answer;
    }

    //@Override
    //onUsingTick
    //This triggers on right click, every tick. Apparently the standard left click method already does the same, now that I blocked the animation. Try these as replacements for the overly complex and potentially laggy onUpdate.
    //Return true in Item#onLeftClickEntity to prevent entity damage, apparently.
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

    }

    //@Override
    //public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
    //    refreshStats(playerIn);
    //    if(playerIn.isSneaking() && handIn == EnumHand.MAIN_HAND) {
    //        playerIn.openGui(TinkersArsenal.INSTANCE, GuiGunsmithing.GUN_ID, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ)/*0, 0, 0)*/;
    //        return super.onItemRightClick(worldIn, playerIn, handIn);
    //    }
    //    //if(isPrimed&&isChambered&&ClientProxy.keyBindings[1].getKeyCode()==-99) {
    //    //    logger.info("Cooldown: {}",getShootCooldown(playerIn.getHeldItem(handIn)));
    //    //    if(getShootCooldown(playerIn.getHeldItem(handIn))==0&&hasReset) {
    //    //        GunFire(worldIn, playerIn);
    //    //    }
    //    //}     //Hopefully to be replaced with code in onUpdate. Can be deleted fully once that code is working.
    //    //if(ClientProxy.keyBindings[0].getKeyCode()==-99) {
    //    //    GunSelector(worldIn, playerIn);
    //    //}

    //    return super.onItemRightClick(worldIn, playerIn, handIn);
    //}

    public int getShootCooldown(ItemStack stack)
    {
        int answer = stack.serializeNBT().getInteger("cooldown");
        return answer;

    }


    public class GunInventory implements ICapabilitySerializable<NBTTagCompound> {

        private ItemStackHandler inventory = new ItemStackHandler(6);

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("inventory", inventory.serializeNBT());
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
            if(trackGunFiring) {TinkersArsenal.logger.info("Reading");}
        }

        public boolean fireGun(BaseGunItem gun, EntityPlayer player) {
            if(trackGunFiring) {TinkersArsenal.logger.info("Trigger Down");}
            if (gun.isPrimed) {
                if(trackGunFiring) {TinkersArsenal.logger.info("Firing Pin Was Ready");}

                /*if(player.world.isRemote){*///player.getEntityWorld().playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.PLAYERS, .5F, .2F, false);//}
                if(gun.isChambered) {
                    gun.isChambered=false;
                    gun.isPrimed=false;
                    if(trackGunFiring) {TinkersArsenal.logger.info("Round Was Chambered");}
                    return true;
                }
                if(trackGunFiring) {TinkersArsenal.logger.info("Round Wasn't Chambered");}
                return false;
            }
            if(trackGunFiring) {TinkersArsenal.logger.info("Firing Pin Wasn't Ready! gun.isPrimed: {}", gun.isPrimed);}
            return false;
        }

        protected int getActionType() {
            int action = 1;
            //0 means the striker is set by hand, -1 means the striker is set on trigger pull, and 1 means the striker is cycled automatically
            return action;
        }

        protected int getLoaderType() {
            int loader = 1;
            //0 means the chamber is cycled by hand, -1 means the chamber is cycled on trigger pull, and 1 means the chamber is cycled automatically
            return loader;
        }
    }
}

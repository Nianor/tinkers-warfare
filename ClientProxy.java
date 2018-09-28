package com.nianor.tinkersarsenal;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.nianor.tinkersarsenal.client.models.CustomGunModelLoader;
import com.nianor.tinkersarsenal.client.models.CustomItemRenderType;
import com.nianor.tinkersarsenal.client.models.ModelRegistry;
import com.nianor.tinkersarsenal.client.models.RenderGun;
import com.nianor.tinkersarsenal.client.render.EntityArsenalProjectileRender;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import com.nianor.tinkersarsenal.network.PacketHandler;
import com.nianor.tinkersarsenal.util.RenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import scala.xml.dtd.impl.Base;
import slimeknights.tconstruct.common.ModelRegisterUtil;

import static com.nianor.tinkersarsenal.TinkersArsenal.isTest;

//MOST OF THIS CODE COMES FROM DIREWOLF20
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TinkersArsenal.MODID)
public class ClientProxy extends CommonProxy {

    RenderGun gunRenderer = new RenderGun();

    public static KeyBinding[] keyBindings;

    public static ModelRegistry models;


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModEntities.initModels();
        registerEntityRenderers();
        //RenderHelper.registerEntityRenders();
        //ModelLoaderRegistry.registerLoader(new CustomGunModelLoader());
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        //Add a config option to replace the manual chambering with automatic chambering.
        keyBindings = new KeyBinding[4];
        keyBindings[0] = new KeyBinding("key.selectorSwitch", KeyConflictContext.IN_GAME, -98, "key.categories.Tinkers' Arsenal");
        keyBindings[1] = new KeyBinding("key.fire", KeyConflictContext.IN_GAME, -99, "key.categories.Tinkers' Arsenal");
        keyBindings[2] = new KeyBinding("key.chamber", KeyConflictContext.IN_GAME, 33, "key.categories.Tinkers' Arsenal");
        keyBindings[3] = new KeyBinding("key.load", KeyConflictContext.IN_GAME, 19, "key.categories.Tinkers' Arsenal");
        for (int i = 0; i < keyBindings.length; ++i)
        {
            ClientRegistry.registerKeyBinding(keyBindings[i]);
        }
        //registerEntityRenderers();
        super.init(event);
        //MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        //ModBlocks.initColorHandlers();
    }

    @SubscribeEvent
    protected void registerModels(ModelRegistryEvent event) {

        //Parts
        ModelRegisterUtil.registerPartModel(SHAFT);
        ModelRegisterUtil.registerPartModel(STURDY_TUBING);
        ModelRegisterUtil.registerPartModel(LARGE_TUBING);
        ModelRegisterUtil.registerPartModel(HINGE);
        ModelRegisterUtil.registerPartModel(GRIP_PADDING);
        ModelRegisterUtil.registerPartModel(HAND_GUARD);
        ModelRegisterUtil.registerPartModel(TRIGGER);
        ModelRegisterUtil.registerPartModel(LINEAR_SPRING);
        ModelRegisterUtil.registerPartModel(TORSION_SPRING);
        ModelRegisterUtil.registerPartModel(HEAVY_SPRING);
        ModelRegisterUtil.registerPartModel(SPROCKET);

        //Barrels
        ModelRegisterUtil.registerToolModel(LIGHT_BARREL);
        ModelRegisterUtil.registerToolModel(HEAVY_BARREL);
        ModelRegisterUtil.registerToolModel(EXPERIMENTAL_BARREL);
        ModelRegisterUtil.registerToolModel(SUPPRESSED_BARREL);
        ModelRegisterUtil.registerToolModel(AIR_COOLED_BARREL);
        ModelRegisterUtil.registerToolModel(WATER_COOLED_BARREL);

        //Chambers
        ModelRegisterUtil.registerToolModel(REVOLVING);
        ModelRegisterUtil.registerToolModel(SINGLE);
        ModelRegisterUtil.registerToolModel(PIVOTING);
        ModelRegisterUtil.registerToolModel(OPEN_BOLT);
        ModelRegisterUtil.registerToolModel(LOCKING_BOLT);

        //Actions
        ModelRegisterUtil.registerToolModel(GAS_PISTON);
        ModelRegisterUtil.registerToolModel(BLOWBACK);
        ModelRegisterUtil.registerToolModel(LEVER);
        ModelRegisterUtil.registerToolModel(PUMP);
        ModelRegisterUtil.registerToolModel(CRANK);
        ModelRegisterUtil.registerToolModel(DOUBLE_ACTION);

        //Feeds
        ModelRegisterUtil.registerToolModel(MUZZLELOADING);
        ModelRegisterUtil.registerToolModel(BREECHLOADING);
        ModelRegisterUtil.registerToolModel(BOLT_ACTION);
        ModelRegisterUtil.registerToolModel(ROTARY);

        //Recievers
        ModelRegisterUtil.registerToolModel(DETACHABLE_MAGAZINE);
        ModelRegisterUtil.registerToolModel(INTERNAL_MAGAZINE);
        ModelRegisterUtil.registerToolModel(BELT_FEED);

        ModelRegisterUtil.registerToolModel(BULLET_CORE);

    }

    public void registerEntityRenderers() {
        if (isTest) TinkersArsenal.logger.info("Registering Entities");
        RenderingRegistry.registerEntityRenderingHandler(EntityItem.class, new CustomGunModelLoader.Factory());
        models = new ModelRegistry();
        RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, EntityArsenalProjectileRender::new);
        //RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, new EntityArsenalProjectileRender(Minecraft.getMinecraft().getRenderManager()));
        //RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, new EntityArsenalProjectileRender.Factory());
        //RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new ConstructionBlockEntityRender.Factory());
    }

    @SubscribeEvent
    public void renderHeldItem(RenderSpecificHandEvent event) {
        ItemStack item = event.getItemStack();
        if (item.getItem() instanceof BaseGunItem) {
            event.setCanceled(true);
            //System.out.println(event.getHand());




            BaseGunItem.GunInventory renderInventory = ((BaseGunItem) item.getItem()).getInventory();
            gunRenderer.RenderGun(CustomItemRenderType.EQUIPPED_FIRST_PERSON, event.getHand(), item, event.getPartialTicks());

            GL11.glPopMatrix();



            //new RenderGun(Minecraft.getMinecraft().getRenderManager(), )
            //The render inventory just exists as a convenient way of checking what's in the gun's inventory so we know what to render.

        }
    }

    @SubscribeEvent
    public void entityFireSpread(LivingEvent event) {
        if(event.getEntityLiving()!=null && !event.getEntityLiving().getEntityWorld().isRemote) {
            if (event.getEntityLiving().isBurning()) {
                if (Math.random() < .025) {
                    World world = event.getEntity().getEntityWorld();
                    world.setBlockState(event.getEntity().getPosition(), Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    @SubscribeEvent
    public void test(RenderPlayerEvent event) {
        //System.out.println("RENDERING PLAYER");
        if(event.getEntityPlayer().getHeldItem(EnumHand.OFF_HAND).getItem() instanceof BaseGunItem){
            RenderGun gunRenderer = new RenderGun();
            //System.out.println("GUN IN OFF HAND");
            gunRenderer.renderGun(event.getEntityPlayer().getHeldItem(EnumHand.OFF_HAND), 0F, 0F, event.getPartialRenderTick());
        }

        if(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof BaseGunItem){
            RenderGun gunRenderer = new RenderGun();
            //System.out.println("GUN IN MAIN HAND");
            gunRenderer.renderGun(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND), 0F, 0F, event.getPartialRenderTick());
        }
    }

}

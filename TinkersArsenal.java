package com.nianor.tinkersarsenal;

import com.nianor.tinkersarsenal.client.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

import static java.lang.Math.abs;
import static org.apache.commons.lang3.BooleanUtils.and;

@Mod(modid = TinkersArsenal.MODID, name = TinkersArsenal.NAME, version = TinkersArsenal.VERSION, dependencies = "after:tconstruct;after:plustic")
public class TinkersArsenal
{

    @Mod.Instance(value = TinkersArsenal.MODID)
    public static TinkersArsenal INSTANCE;

    public static final String MODID = "tinkersarsenal";
    public static final String NAME = "Tinkers' Arsenal";
    public static final String VERSION = "0.01";

    public static Logger logger;

    @SidedProxy(clientSide = "com.nianor.tinkersarsenal.ClientProxy", serverSide = "com.nianor.tinkersarsenal.ServerProxy")
    public static CommonProxy proxy;


    public static boolean isTest = false;                //This boolean and the ones after it don't change any of how the mod functions, but they will flood your logs if they're turned on. Use only for debugging.
    public static boolean checkRender = true;
    public static boolean checkProjectiles = true;
    public static boolean checkPreStats = false;
    public static boolean checkMaterialAbilities = false;
    public static boolean checkTraits = false;
    public static boolean trackGunFiring = false;

    public static GuiHandler guiHandler = new GuiHandler();



    //public static TinkersProxy proxy;

    /*public TinkersArsenal () {
        MinecraftForge.EVENT_BUS.register(this); //DISABLED AS REQUESTED BY FERDI
    }*/

    /*@SubscribeEvent
    public static void materialConfig(File saveLoc)
    */


   /* @SubscribeEvent
    public static void addMaterialStats(MaterialEvent.MaterialRegisterEvent event)
    {
        Set<IMaterialStats> stats = MASTER_STATS.get(event.material.identifier);

        if (stats == null) return;

        for (IMaterialStats stat : stats)
            TinkerRegistry.addMaterialStats(event.material, stat);

        MASTER_STATS.remove(event.material.identifier);
    } */


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        TinkersArsenal.logger = event.getModLog();
        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Tinkers' Arsenal: Logger is ready!");
        if (isTest) logger.info("Attempting to start preinit!");
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, guiHandler);
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.preInit(event);

        /*if (TinkersArsenal.proxy.runningOnClient())
            proxy = new TinkersClientProxy();
        else
            proxy = new TinkersProxy();*/


    }

    /*@EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        for (Material material :materials) {
            String identifier = material.identifier;
            logger.info("Core stats for {}", identifier);
            logger.info("Has core stats: {}", material.hasStats("core"));
            if (material.hasStats("core")== true)
            {
                logger.info("Stats are {}", ((material.getStats("core").getLocalizedInfo())));
            }
        }
        registerToolBuilding();
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
*/
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (isTest) logger.info("Attempting to start init!");
        proxy.init(event);
    }
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        if (isTest) logger.info("Attempting to start postinit!");
        proxy.postInit(event);
        if (checkTraits) for (Material material : TinkerRegistry.getAllMaterials()) {
            for (ITrait trait: material.getAllTraits())
                logger.info("{}",trait.getIdentifier());
        }
        ///EntityEntry entry = EntityEntryBuilder.create()
        ///        .entity(EntityArsenalProjectile.class)
        ///        .id(resourceLocation, 1234)
        ///        .name("my_entity")
        ///        .egg(0xFFFFFF, 0xAAAAAA)
        ///        .tracker(64, 20, false)
        ///        .build();
        ///event.getRegistry().register(entry);
        /*for (Material material : TinkerRegistry.getAllMaterials()) {
            if (material.getAllTraits().contains(TraitLightweight)) { material.get }
        }*/
    }

}

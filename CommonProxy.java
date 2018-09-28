package com.nianor.tinkersarsenal;

import com.direwolf20.buildinggadgets.network.PacketHandler;
import com.google.common.collect.Lists;
import com.nianor.tinkersarsenal.common.entities.EntityArsenalProjectile;
import com.nianor.tinkersarsenal.client.render.EntityArsenalProjectileRender;
import com.nianor.tinkersarsenal.items.BaseGunItem;
import com.nianor.tinkersarsenal.network.VelocityPacket;
import com.nianor.tinkersarsenal.network.KeyPressPacket;
import com.nianor.tinkersarsenal.network.ParticlePacket;
import com.nianor.tinkersarsenal.network.SimpleNetworkWrapper;
import com.nianor.tinkersarsenal.tinkers.TestItem;
import com.nianor.tinkersarsenal.tinkers.material.CoreMaterialStats;
import com.nianor.tinkersarsenal.tinkers.material.FaceMaterialStats;
import com.nianor.tinkersarsenal.tinkers.material.MechMaterialStats;
import com.nianor.tinkersarsenal.tinkers.tools.Action.*;
import com.nianor.tinkersarsenal.tinkers.tools.Barrel.*;
import com.nianor.tinkersarsenal.tinkers.tools.Chamber.*;
import com.nianor.tinkersarsenal.tinkers.tools.Feed.BoltActionCore;
import com.nianor.tinkersarsenal.tinkers.tools.Feed.BreechloadingCore;
import com.nianor.tinkersarsenal.tinkers.tools.Feed.MuzzleloadingCore;
import com.nianor.tinkersarsenal.tinkers.tools.Feed.RotaryCore;
import com.nianor.tinkersarsenal.tinkers.tools.Receiver.BeltFeedCore;
import com.nianor.tinkersarsenal.tinkers.tools.Receiver.DetatchableMagazineCore;
import com.nianor.tinkersarsenal.tinkers.tools.Receiver.InternalMagazineCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

import java.io.File;
import java.util.*;

import static com.nianor.tinkersarsenal.TinkersArsenal.INSTANCE;
import static com.nianor.tinkersarsenal.TinkersArsenal.MODID;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

//MOST OF THIS CODE COMES FROM DIREWOLF20
@Mod.EventBusSubscriber(modid= MODID)
public class CommonProxy {

    public static final HashMap<String, Set<IMaterialStats>> MASTER_STATS = new HashMap<>();

    public static Item BOOK;

    public static Item SUPERSONIC_ARROW;
    public static Item BASE_GUN;

    public static Entity SUPERSONIC_ARROW_ENTITY;

    public static Mod.EventHandler TASoundEvent;

    //Component Parts
    public static ToolPart STURDY_TUBING;
    public static ToolPart HINGE;
    public static ToolPart LARGE_TUBING;
    public static ToolPart GRIP_PADDING;
    public static ToolPart SHAFT;
    public static ToolPart HAND_GUARD;
    public static ToolPart TRIGGER;
    public static ToolPart LINEAR_SPRING;
    public static ToolPart TORSION_SPRING;
    public static ToolPart HEAVY_SPRING;
    public static ToolPart SPROCKET;

    //Components

    //Barrels
    public static ToolCore LIGHT_BARREL;
    public static ToolCore HEAVY_BARREL;
    public static ToolCore EXPERIMENTAL_BARREL;
    public static ToolCore SUPPRESSED_BARREL;
    public static ToolCore AIR_COOLED_BARREL;
    public static ToolCore WATER_COOLED_BARREL;

    //Chambers
    public static ToolCore REVOLVING;
    public static ToolCore SINGLE;
    public static ToolCore PIVOTING;
    public static ToolCore OPEN_BOLT;
    public static ToolCore LOCKING_BOLT;

    //Actions
    public static ToolCore GAS_PISTON;
    public static ToolCore BLOWBACK;
    public static ToolCore LEVER;
    public static ToolCore PUMP;
    public static ToolCore CRANK;
    public static ToolCore DOUBLE_ACTION;

    //Feeds
    public static ToolCore MUZZLELOADING;
    public static ToolCore BREECHLOADING;
    public static ToolCore BOLT_ACTION;
    public static ToolCore ROTARY;

    //Recievers
    public static ToolCore DETACHABLE_MAGAZINE;
    public static ToolCore INTERNAL_MAGAZINE;
    public static ToolCore BELT_FEED;

    //Projectiles
    public static ToolCore BULLET_CORE;

    public static List<Item> modItems = Lists.newArrayList();

    public static List<ToolPart> toolParts = Lists.newArrayList();
    private static List<Pair<Item, ToolPart>> toolPartPatterns = Lists.newArrayList();

    public static Configuration config;

    public void preInit (FMLPreInitializationEvent event){
        TinkersArsenal.logger.info("Preinit started!");
        PacketHandler.registerMessages();
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "TinkersArsenal.cfg"));
        Config.readConfig();
    }


    public void init (FMLInitializationEvent event) {
        TinkersArsenal.logger.info("Init started!");
        ModEntities.registerEntities();
        //RenderingRegistry.registerEntityRenderingHandler(EntityArsenalProjectile.class, new EntityArsenalProjectileRender(Minecraft.getMinecraft().getRenderManager()));
    }


    public void postInit (FMLPostInitializationEvent event) {
        TinkersArsenal.logger.info("Postinit started!");
        if (config.hasChanged()) {
            config.save();
        }
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.VelocityPacketHandler.class, VelocityPacket.class, 1, Side.SERVER);
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.VelocityPacketHandler.class, VelocityPacket.class, 1, Side.CLIENT);
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.KeyPressPacketHandler.class, KeyPressPacket.class, 2, Side.CLIENT);
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.KeyPressPacketHandler.class, KeyPressPacket.class, 2, Side.SERVER);
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.ParticlePacketHandler.class, ParticlePacket.class, 3, Side.CLIENT);
        SimpleNetworkWrapper.INSTANCE.registerMessage(com.nianor.tinkersarsenal.network.PacketHandler.ParticlePacketHandler.class, ParticlePacket.class, 3, Side.SERVER);
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "arsenal_projectile"), EntityArsenalProjectile.class, "Arsenal Projectile", 1, INSTANCE, 512, 90, true);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        TinkersArsenal.logger.info("Attempting to start registering items now");
        Material.UNKNOWN.addStats(new CoreMaterialStats(3F, 1F, 100));
        Material.UNKNOWN.addStats(new MechMaterialStats(1.25F, 1F, 100));
        Material.UNKNOWN.addStats(new FaceMaterialStats(0.35F, 1F, 100));

        Collection<Material> materials = TinkerRegistry.getAllMaterials();

        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Starting to auto-assign values for TA characteristics");
        for (Material material : materials)
        {

            if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Looking at {}", material.identifier);
            if (TinkersArsenal.checkMaterialAbilities) TinkersArsenal.logger.info("Can it be used as a tool head...? {}!", material.hasStats("head"));
            if (TinkersArsenal.checkMaterialAbilities) TinkersArsenal.logger.info("Can it be used for bow limbs...? {}!", material.hasStats("bow"));
            if (material.hasStats("head") && material.hasStats("bow")) {
                float range = Float.parseFloat(((material.getStats("bow").getLocalizedInfo()).toArray()[1]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a range of {}", range);
                float speed = Float.parseFloat(((material.getStats("head").getLocalizedInfo()).toArray()[2]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a mining speed of {}", speed);
                float attack = Float.parseFloat(((material.getStats("head").getLocalizedInfo()).toArray()[3]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has an attack value of {}", attack);
                int durability = Integer.parseInt(((material.getStats("head").getLocalizedInfo()).toArray()[0]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a durability of {}", durability);
                float handle = Float.parseFloat(((material.getStats("handle").getLocalizedInfo()).toArray()[0]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a handle multiplier of {}", handle);
                float extra = Float.parseFloat(((material.getStats("extra").getLocalizedInfo()).toArray()[0]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has an extra durability of {}", extra);
                float draw = Float.parseFloat(((material.getStats("bow").getLocalizedInfo()).toArray()[0]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a draw speed of {}", draw);
                float bowDamage = Float.parseFloat(((material.getStats("bow").getLocalizedInfo()).toArray()[2]).toString().replaceAll("[^0-9.]", ""));
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a bonus bow damage of {}", bowDamage);
                String harvestTier = material.getStats("head").getLocalizedInfo().toArray()[1].toString();
                switch (harvestTier = harvestTier.replace("Mining Level: ", "").replace("§r", "").replaceAll("[^A-Za-z]", "")) {
                }
                while (harvestTier.toLowerCase().subSequence(0, 1).equals(harvestTier.subSequence(0,1))) {  //This makes sure that the harvest tier starts with an uppercase letter
                    if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Trimming from {}", harvestTier);
                    switch (harvestTier = harvestTier.substring(1)) {
                    }
                    if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Down to {}", harvestTier);
                }
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a harvest tier of {}", harvestTier);
                int baseHardness;
                if (harvestTier.equals("Stone")) { baseHardness = 1; }
                else if (harvestTier.equals("Iron")) { baseHardness = 2; }
                else if (harvestTier.equals("Diamond")) { baseHardness = 3; }
                else if (harvestTier.equals("Obsidian")) { baseHardness = 4; }
                else if (harvestTier.equals("Cobalt")) { baseHardness = 5; }
                else { baseHardness = 0; }
                if (TinkersArsenal.isTest) TinkersArsenal.logger.info("This translates to a base hardness of {}", baseHardness);
                if (TinkersArsenal.checkPreStats) TinkersArsenal.logger.info("Does this material already have core stats? {}", material.hasStats("core"));
                if (TinkersArsenal.checkPreStats) TinkersArsenal.logger.info("Does this material already have face stats? {}", material.hasStats("face"));
                if (TinkersArsenal.checkPreStats) TinkersArsenal.logger.info("Does this material already have mech stats? {}", material.hasStats("mech"));

                if (material.isCraftable()) {
                    if (TinkersArsenal.isTest) TinkersArsenal.logger.info("{} is craftable!", material.identifier);
                    if (material.isCastable()) {
                        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("{} is also castable! Special formula for you!", material.identifier);
                        float weight = (float) ((durability / 408.7) + (attack / 3.67));
                        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a weight of {}", weight);
                        TinkerRegistry.addMaterialStats(material, (new CoreMaterialStats(attack, weight, durability)));
                        TinkerRegistry.addMaterialStats(material, (new MechMaterialStats(mechStats(handle, baseHardness, draw, range, bowDamage), weight, durability)));
                        TinkerRegistry.addMaterialStats(material, (new FaceMaterialStats(hardness(baseHardness, speed, attack, handle, extra), weight, durability)));
                    }
                    else {
                        float weight = speed / range / 5;
                        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a weight of {}", weight);
                        TinkerRegistry.addMaterialStats(material, (new CoreMaterialStats(attack, weight, durability)));
                        TinkerRegistry.addMaterialStats(material, (new MechMaterialStats(mechStats(handle, baseHardness, draw, range, bowDamage), weight, durability)));
                        TinkerRegistry.addMaterialStats(material, (new FaceMaterialStats(hardness(baseHardness, speed, attack, handle, extra), weight, durability)));
                    }


                }
                else if (material.isCastable()) {
                    //(mining*drawing)/(handle*bonus)*0.55
                    float weight = (float) (((speed * draw) / (handle * bowDamage)) * 0.55);
                    if (TinkersArsenal.isTest) TinkersArsenal.logger.info("It has a weight of {}", weight);
                    TinkerRegistry.addMaterialStats(material, (new CoreMaterialStats(attack, weight, durability)));
                    TinkerRegistry.addMaterialStats(material, (new MechMaterialStats(mechStats(handle, baseHardness, draw, range, bowDamage), weight, durability)));
                    TinkerRegistry.addMaterialStats(material, (new FaceMaterialStats(hardness(baseHardness, speed, attack, handle, extra), weight, durability)));

                }
            }
        }


        registerToolParts();
        registerTools();

        //BOOK = registerItem(new ItemArsenalBook(), "book");

        BASE_GUN = registerItem(new BaseGunItem(), "base_gun_item");

        for (Pair<Item, ToolPart> toolPartPattern : toolPartPatterns)
        {
            registerStencil(toolPartPattern.getLeft(), toolPartPattern.getRight());
        }

        //Tool Mods would go here, but there aren't any right now.

        for (Item item : modItems)
            event.getRegistry().register(item);
        for (ToolPart toolPart :toolParts) TinkersArsenal.logger.info("Unlocalized Name: {}", toolPart.getUnlocalizedName());
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

        //Projectiles
        ModelRegisterUtil.registerToolModel(BULLET_CORE);

        //ProjectileModel = new ResourceLocation("tinkersarsenal:models/supersonic_arrow");
    }



    private void registerToolParts()
    {
        STURDY_TUBING = registerToolPart(new ToolPart(Material.VALUE_Ingot*3), "sturdy_tubing");
        HINGE = registerToolPart(new ToolPart(Material.VALUE_Ingot), "hinge");
        LARGE_TUBING = registerToolPart(new ToolPart(Material.VALUE_Ingot*4), "large_tubing");
        GRIP_PADDING = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "grip_padding");
        SHAFT = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "shaft");
        HAND_GUARD = registerToolPart(new ToolPart(Material.VALUE_Ingot), "hand_guard");
        TRIGGER = registerToolPart(new ToolPart(Material.VALUE_Ingot), "trigger");
        LINEAR_SPRING = registerToolPart(new ToolPart(Material.VALUE_Ingot),"linear_spring");
        TORSION_SPRING = registerToolPart(new ToolPart(Material.VALUE_Ingot),"torsion_spring");
        HEAVY_SPRING = registerToolPart(new ToolPart(Material.VALUE_Ingot),"heavy_spring");
        SPROCKET = registerToolPart(new ToolPart(Material.VALUE_Ingot*2), "sprocket");

    }

    private ToolPart registerToolPart(ToolPart toolPart, String name) {
        ToolPart ret = registerItem(toolPart, name);

        ModelRegisterUtil.registerItemModel(toolPart);//registerPartModel(toolPart);

        toolPartPatterns.add(Pair.of(TinkerTools.pattern, ret));

        toolParts.add(ret);

        return ret;
    }

    private static void registerTools()
    {
        //Barrels
        LIGHT_BARREL = registerItem(new LightBarrelCore(), "light_barrel");
        HEAVY_BARREL = registerItem(new HeavyBarrelCore(), "heavy_barrel");
        EXPERIMENTAL_BARREL = registerItem(new ExperimentalBarrelCore(), "experimental_barrel");
        SUPPRESSED_BARREL = registerItem(new SuppressedBarrelCore(), "suppressed_barrel");
        AIR_COOLED_BARREL = registerItem(new AirCooledBarrelCore(), "air_cooled_barrel");
        WATER_COOLED_BARREL = registerItem(new WaterCooledBarrelCore(), "water_cooled_barrel");

        //Chambers
        REVOLVING = registerItem(new RevolvingCore(), "revolving");
        SINGLE = registerItem(new SingleCore(), "single");
        PIVOTING = registerItem(new PivotingCore(), "pivoting");
        OPEN_BOLT = registerItem(new OpenBoltCore(), "open_bolt");
        LOCKING_BOLT = registerItem(new LockingBoltCore(), "locking_bolt");

        //Actions
        GAS_PISTON = registerItem(new GasPistonCore(), "gas_piston");
        BLOWBACK = registerItem(new BlowbackCore(), "blowback");
        LEVER = registerItem(new LeverCore(), "lever");
        PUMP = registerItem(new PumpCore(), "pump");
        CRANK = registerItem(new CrankCore(), "crank");
        DOUBLE_ACTION = registerItem(new DoubleActionCore(), "double_action");

        //Feeds
        MUZZLELOADING = registerItem(new MuzzleloadingCore(), "muzzleloading");
        BREECHLOADING = registerItem(new BreechloadingCore(), "breechloading");
        BOLT_ACTION = registerItem(new BoltActionCore(), "bolt_action");
        ROTARY = registerItem(new RotaryCore(), "rotary");

        //Recievers
        DETACHABLE_MAGAZINE = registerItem(new DetatchableMagazineCore(), "detachable_magazine");
        INTERNAL_MAGAZINE = registerItem(new InternalMagazineCore(), "internal_magazine");
        BELT_FEED = registerItem(new BeltFeedCore(), "belt_feed");
    }

    private static void registerStencil(Item pattern, ToolPart toolPart)
    {
        for (ToolCore toolCore : TinkerRegistry.getTools())
        {
            for (PartMaterialType partMaterialType : toolCore.getRequiredComponents())
            {
                if (partMaterialType.getPossibleParts().contains(toolPart))
                {
                    ItemStack stencil = new ItemStack(pattern);
                    Pattern.setTagForPart(stencil, toolPart);
                    TinkerRegistry.registerStencilTableCrafting(stencil);
                    return;
                }
            }
        }
    }

    private static <T extends Item> T registerItem(T item, String name)
    {
        if (!name.equals(name.toLowerCase(Locale.US)))
        {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name));
        }

        item.setUnlocalizedName(String.format("%s.%s", MODID, name));

        item.setRegistryName(new ResourceLocation(MODID, name));
        modItems.add(item);
        if (TinkersArsenal.isTest) TinkersArsenal.logger.info("Registered {}", name);
        return item;
    }



    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {

    }

    private float mechStats(float handle, int mining, float speed, float range, float bonusDamage)
    {
        float mechEffectiveness = (float) (sqrt(abs((range/speed+bonusDamage)*handle)) + mining/4);
        return mechEffectiveness;
    }

    private float hardness(int mining, float mineSpeed, float attack, float handle, float extraDur)
    {
        float hardness = (float) Math.pow((mining * mineSpeed/4) / (Math.pow((handle * extraDur),(1/4))) * ((((2*mining + mineSpeed/5)) * (Math.pow(attack, (1 / 10))) / 5)), (1/2));
        return hardness;
    }


}

package com.nianor.tinkersarsenal.common.entities;

import com.nianor.tinkersarsenal.ClientProxy;
import com.nianor.tinkersarsenal.CommonProxy;
import com.nianor.tinkersarsenal.network.SimpleNetworkWrapper;
import com.nianor.tinkersarsenal.network.VelocityPacket;
import com.nianor.tinkersarsenal.util.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3d;

import java.util.List;

import static com.nianor.tinkersarsenal.TinkersArsenal.*;

//Words of Don:
//Yeah, you should NEVER have seperate ents on client and server.  Das bad fer inter-mod support.
//As to runnables, check the forge packet info.  It's rather hard for me to explain.
//In a nutshell, however, a runnable makes the code in the packet wait to execute until the game thread is done processing to prevent a CME.

public class EntityArsenalProjectile extends EntityThrowable {

    
    public int roll = 0;
    public int previousRoll;
    public int rollSpeed = 0;
    public float yaw;
    public float pitch;
    protected int blockX = -1;
    protected int blockY = -1;
    protected int blockZ = -1;
    protected Block inBlock;
    protected int inMeta;
    public boolean inGround;
    public int ticksInGround;
    public int ticksInAir;
    protected double velocityPrivate;
    protected float velocity; //This is the vanilla velocityPrivate variable. We can't use this one, or MC will try to render it normally. And MC can't properly render things going this fast, so it'll screw it up.
    public static float xInit;
    public static float yInit;
    public static float zInit;
    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;
    public static double xPosQuiet;
    public static double yPosQuiet;
    public static double zPosQuiet;
    public static double xPosPrevQuiet;
    public static double yPosPrevQuiet;
    public static double zPosPrevQuiet;
    public Vector3d motionVector;
    private double dragAcceleration;
    private Vector3d motionVectorOld;
    private static float n;//This is n as used in Pejsa's ballistic formulas. While this is not a 100% accurate representation of real-life ballistics, it makes a decent effort.
    private static float ballisticCoefficient=1;
    //7.62x54R dimensions for testing.
    public float caliber = (.312F);
    public float bodyLength=(.25F);
    public float neckLength=(.25F+.125F);
    public float tipLength=(.5F);
    //.50 cal dimensions for testing.
    //public float caliber = (.5F);
    //public float bodyLength=(.75F);
    //public float neckLength=(1.125F);
    //public float tipLength=(1.5F);
    //30mm dimensions for testing.
    //public float caliber = (1.1811F);
    //public float bodyLength=(4F);
    //public float neckLength=(.7625F+4F);
    //public float tipLength=(.7625F+.7625F+4F);
    protected float x;
    protected float y;
    protected float z;
    private EntityLivingBase thrower;
    //private WorldServer world;
    private boolean isServer = false;
    private Entity entityThrower;
    private boolean firstUpdate = true;
    private boolean firstUpdateEnabled=true;
    private boolean isRemote;
    private double seed;


    public EntityArsenalProjectile(World worldIn, EntityLivingBase throwerIn, float yaw, float velocityPrivate, float inaccuracy, double seed) {
        super(worldIn, throwerIn);
        System.out.println("Projectile created. Is remote? "+this.world.isRemote);
        isRemote=worldIn.isRemote;
        this.seed=seed;
        //if(!isRemote){ //TEMP
        //    this.visible=false;
        //}
        //else{
        //    this.visible=true;
        //}
        if(checkProjectiles) {logger.info("EntityArsenalProjectile event triggered.");}
        thrower = throwerIn;

        motionVector = new Vector3d(velocityPrivate*Math.sin(throwerIn.rotationPitch)*Math.cos(yaw), velocityPrivate*Math.sin(throwerIn.rotationPitch), velocityPrivate*Math.cos(throwerIn.rotationPitch)*Math.sin(yaw));
        //motionVector = new Vector3d(0,0,0);

        //this.world=worldIn.;
        if (throwerIn.posX != 0 && throwerIn.posY!=0 && throwerIn.posZ!=0) {
            xInit = (float) throwerIn.posX;
            yInit = (float) throwerIn.posY;
            zInit = (float) throwerIn.posZ;
            xPosQuiet=xInit;
            yPosQuiet=yInit;
            zPosQuiet=zInit;
            this.pitch=throwerIn.rotationPitch;
            this.yaw=yaw;
        }
        this.motionX=motionVector.getX();
        this.motionY=motionVector.getY();
        this.motionZ=motionVector.getZ();
        //SimpleNetworkWrapper.INSTANCE.sendToServer(new VelocityPacket(this, this.getEntityId(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        if(checkProjectiles){logger.info("SHOOTING AT: X: {} Y: {} Z: {}", xInit, yInit, zInit);}

        this.velocityPrivate = velocityPrivate;

        shoot(throwerIn, throwerIn.rotationPitch, yaw, 0, velocityPrivate, inaccuracy);
    }

    public EntityArsenalProjectile(World worldIn, EntityPlayerMP throwerIn, float yaw, float velocityPrivate, float inaccuracy, double seed) {
        super(worldIn, throwerIn);
        isRemote=worldIn.isRemote;
        this.seed=seed;
        //if(!isRemote){//TEMP
        //    this.visible=false;
        //}
        //else{
        //    this.visible=true;
        //}
        if(checkProjectiles) {logger.info("EntityArsenalProjectile event triggered.");}
        thrower = throwerIn;

        motionVector = new Vector3d(velocityPrivate*Math.sin(throwerIn.rotationPitch)*Math.cos(yaw), velocityPrivate*Math.sin(throwerIn.rotationPitch), velocityPrivate*Math.cos(throwerIn.rotationPitch)*Math.sin(yaw));
        //motionVector = new Vector3d(0,0,0);

        //this.world=worldIn.;
        if (throwerIn.posX != 0 && throwerIn.posY!=0 && throwerIn.posZ!=0) {
            xInit = (float) throwerIn.posX;
            yInit = (float) throwerIn.posY;
            zInit = (float) throwerIn.posZ;
            xPosQuiet=xInit;
            yPosQuiet=yInit;
            zPosQuiet=zInit;
            this.pitch=throwerIn.rotationPitch;
            this.yaw=yaw;
        }
        this.motionX=motionVector.getX();
        this.motionY=motionVector.getY();
        this.motionZ=motionVector.getZ();
        //SimpleNetworkWrapper.INSTANCE.sendToServer(new VelocityPacket(this, this.getEntityId(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        if(checkProjectiles){logger.info("SHOOTING AT: X: {} Y: {} Z: {}", xInit, yInit, zInit);}

        this.velocityPrivate = velocityPrivate;

        shoot(throwerIn, throwerIn.rotationPitch, yaw, 0, velocityPrivate, inaccuracy);
    }

    public EntityArsenalProjectile(World worldIn) {
        super(worldIn);
        if(checkProjectiles) {
            logger.info("Secondary EntityArsenalProjectile event triggered.");
            if (worldIn.getEntityByID(this.getEntityId())!=null) {
                logger.info("ENTITY: ", worldIn.getEntityByID(this.getEntityId()));
                logger.info("Velocity X: {}\nVelocity Y: {}\nVelocity Z: {}", worldIn.getEntityByID(this.getEntityId()).motionX, worldIn.getEntityByID(this.getEntityId()).motionY, worldIn.getEntityByID(this.getEntityId()).motionZ);
            }
        }
    }

    @SideOnly(Side.SERVER)
    private void forceVelocityUpdate() {

        //Minecraft.getMinecraft().getIntegratedServer()
    }

    protected Vector3d getMotionVector(){
        return motionVector;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        System.out.println("First Update: "+firstUpdate+"\nHit Result: "+result+"\nEntity Thrower: "+entityThrower);
        if (result.entityHit != null&&!isRemote&&(!firstUpdate||result.entityHit.getEntityId()!=entityThrower.getEntityId())) {
            int temp = result.entityHit.hurtResistantTime;
            result.entityHit.hurtResistantTime=0;
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 3 * (float) velocityPrivate);
            result.entityHit.hurtResistantTime=temp;
        }
        else if (result.typeOfHit== RayTraceResult.Type.BLOCK) {
            System.out.println("Progress marker 0");
            BlockPos blockpos = result.getBlockPos();
                   this.blockX = blockpos.getX();
                   this.blockY = blockpos.getY();
                   this.blockZ = blockpos.getZ();
                   IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR) {
                System.out.println("Progress marker 1");
                //this.inBlock.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
                playStepSound(blockpos, iblockstate.getBlock());
                iblockstate.getBlock().addDestroyEffects(world, blockpos, Minecraft.getMinecraft().effectRenderer);
                String harvest = iblockstate.getBlock().getHarvestTool(iblockstate);
                float resistance = 9999;

                if(harvest!=null) {
                    if (harvest.equals("shovel")) {
                        //System.out.println("BLOCK: "+iblockstate.getBlock().getLocalizedName()+"\nHARVESTED BY SHOVEL."+"\nEXPLOSION RESISTANCE: "+iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null)+"\nHARDNESS: " + iblockstate.getBlockHardness(world, blockpos));
                        resistance = 10 * iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null)/iblockstate.getBlockHardness(world, blockpos);

                    }
                    else if (harvest.equals("pickaxe")) {
                        resistance = 2*iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null)*iblockstate.getBlockHardness(world, blockpos);
                    }
                    else if (harvest.equals("axe")) {
                        resistance = 5* iblockstate.getBlockHardness(world, blockpos)/iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null);
                    }
                }

                else if (iblockstate.getBlockHardness(world, blockpos) != -1){
                    resistance = (float)Math.pow((iblockstate.getBlockHardness(world, blockpos)+1), 3);
                }
                float destruction=(float)(((velocityPrivate*caliber))*(Math.pow(Math.random(), 9)));
                System.out.println("Destruction: "+destruction+"\nResistance: "+resistance+"\nBlock: "+iblockstate.getBlock().getLocalizedName());
                System.out.println("Progress marker 2");
                if(resistance-destruction<0) {
                    if(!world.isRemote) {
                        world.destroyBlock(blockpos, false);
                    }
                    System.out.println("Block broken.");
                }
                else if (world.isRemote){
                    double xSpeed=0;
                    double ySpeed=0;
                    double zSpeed=0;
                    float xDisplacement=0;
                    float yDisplacement=0;
                    float zDisplacement=0;
                    if(result.sideHit==EnumFacing.NORTH||result.sideHit==EnumFacing.SOUTH) {
                        zSpeed=-this.motionZ* .15;
                        xSpeed=(Math.random()-.5);
                        ySpeed=(Math.random()-.5);
                        xDisplacement=(float)Math.random()-.5F;
                        yDisplacement=(float)Math.random()-.5F;
                        zDisplacement=.5F;
                    }
                    else if (result.sideHit==EnumFacing.EAST||result.sideHit==EnumFacing.WEST) {
                        xSpeed=-this.motionX* .15;
                        ySpeed=(Math.random()-.5);
                        zSpeed=(Math.random()-.5);
                        xDisplacement=.5F;
                        yDisplacement=(float)Math.random()-.5F;
                        zDisplacement=(float)Math.random()-.5F;
                    }
                    else if (result.sideHit==EnumFacing.UP||result.sideHit==EnumFacing.DOWN) {
                        ySpeed=-this.motionY*.15;
                        xSpeed=(Math.random()-.5);
                        zSpeed=(Math.random()-.5);
                        xDisplacement=(float)Math.random()-.5F;
                        yDisplacement=.5F;
                        zDisplacement=(float)Math.random()-.5F;
                    }
                    for (int j = 0; j < Math.round(velocityPrivate*caliber); ++j)
                    {
                        xSpeed=xSpeed*(Math.random()+1);
                        ySpeed=ySpeed*(Math.random()+1);
                        zSpeed=zSpeed*(Math.random()+1);
                        System.out.println("Progress marker 3");
                        if(world.isRemote) {
                            System.out.println("Playing on client, should be working?");
                            float f3 = 0.25F;
                            this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, result.getBlockPos().getX()+xDisplacement, result.getBlockPos().getY()+yDisplacement, result.getBlockPos().getZ()+zDisplacement, xSpeed, ySpeed, zSpeed, world.getBlockState(result.getBlockPos()).getBlock().getStateId(world.getBlockState(result.getBlockPos())));
                        }
                        else {
                            System.out.println("Played on server, no effect.");
                        }
                    }
                    //Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleImpact(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), xSpeed, ySpeed, zSpeed, iblockstate));
                }
            }
        }
        else {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        //return (float)((ticksInAir/20)*9.8);
        return .15F;
    }

    public double getSeed() {
        return seed;
    }

    @Override
    public void onUpdate() {
        this.setInvisible(false);
        if(!world.isRemote) {
            world.getMinecraftServer().getWorld(this.dimension).getEntityTracker().sendToTracking(this, new SPacketEntityTeleport(this));
        }
        //SimpleNetworkWrapper.INSTANCE.sendToServer(new VelocityPacket(this.getUniqueID(), this.seed, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        //if(motionVector==null||motionVector.length()<.000001) {
        //    System.out.println("Incomplete client-side mirror detected, containing");
        //    this.setDead();
        //    return;
        //    //if(world.getMinecraftServer().getEntityFromUuid(this.entityUniqueID) instanceof EntityArsenalProjectile) {
        //    //    motionVector = new Vector3d(((EntityArsenalProjectile) world.getMinecraftServer().getEntityFromUuid(this.entityUniqueID)).getMotionVector());
        //    //}
        //}
        System.out.println("\nMotion vector: "+motionVector+"\nEntity ID: "+this.getEntityId()+"\nIs invisible? "+this.isInvisible());
        velocityPrivate=motionVector.length();
        //This is where the value of n is set, based on, again, the values Pejsa defines. The odd decimals are simply a result of converting feet per second to blocks per tick.
        if(velocityPrivate<13.716||(velocityPrivate<21.336&&velocityPrivate>=18.288)) {
            n=0;
        }
        else if (velocityPrivate>=13.716&&velocityPrivate<18.288) {
            n=-3.0F;
        }
        else if (velocityPrivate>=21.336) {
            n=.5F;
        }
        if(/*!world.isRemote*/true) {
            //THIS NEEDS THE ADDITION OF MASS AND AN ACTUAL DRAG COEFFICIENT
            System.out.println("N: " + n);
            System.out.println("BC: " + ballisticCoefficient);
            dragAcceleration = .1 * Math.pow(velocityPrivate, 2 - n) / ballisticCoefficient;
            double velocityTestMatch = (velocityPrivate-(Math.pow(velocityPrivate, 2 - n)/(30*ballisticCoefficient)));
            /*int velocityIterator = 0;
            int velocityFactor = (int) Math.round(velocityPrivate * 25);
            while (velocityIterator < velocityFactor) {
                if(dragAcceleration>0) {
                    velocityPrivate = velocityPrivate - dragAcceleration / velocityFactor;
                    dragAcceleration = .1 * Math.pow(velocityPrivate, 2 - n) / ballisticCoefficient;
                    //System.out.println("Drag: "+dragAcceleration+"\nVelocity: "+velocityPrivate);
                }
                velocityIterator += 1;
            }*/
            velocityPrivate=velocityTestMatch;
            System.out.println("\nVelocity Single Op Test: " + velocityTestMatch +"\nVelocity Iterative Result: "+velocityPrivate);
        }
        if(!firstUpdateEnabled) {
            firstUpdate=false;
        }
        {
            Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
            vec3d = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if(checkProjectiles){logger.info("RAYTRACING BEGINNING: \nPOSITIONVECTOR:\n   X:{}\n   Y:{}\n   Z:{}\nMOVEMENTVECTOR:\n   X:{}\n   Y:{}\n   Z:{}\nRESULT: {}",vec3d.x,vec3d.y,vec3d.z, vec3d1.x, vec3d1.y,vec3d1.z,raytraceresult);}

            if (raytraceresult != null)
            {
                vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }
            Entity entity = null;
            if(!isRemote) {
                List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
                double d0 = 0.0D;
                boolean flag = false;
                if (checkProjectiles) {
                    logger.info("{}", list);
                }

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = list.get(i);

                    if (entity1.canBeCollidedWith()) {
                        if (entity1 == this.ignoreEntity) {
                            flag = true;
                        }
                        //else if (entity1 == this.thrower) {
                        //    this.ignoreEntity = entity1;
                        //    flag = true;
                        //}
                        else if (this.thrower != null && this.ticksExisted < -1 && this.ignoreEntity == null) {
                            this.ignoreEntity = entity1;
                            flag = true;
                        } else {
                            flag = false;
                            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
                            if (checkProjectiles) {
                                logger.info("RAYTRACERESULT1: {}", raytraceresult1);
                            }
                            if (raytraceresult1 != null) {
                                double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                                if ((d0 > d1 || d0 == 0.0D) && entity1.getEntityId() != this.thrower.getEntityId() && !entity1.isDead && !(entity1.hurtResistantTime > 0) && (!firstUpdate || entity1 != entityThrower)) {
                                    if (checkProjectiles) {
                                        logger.info("Entity: {}\nDistance: {}\nPrevDistance: {}\nThrower: {}", entity1, d1, d0, this.thrower);
                                    }
                                    entity = entity1;
                                    d0 = d1;
                                }
                            }
                        }
                        if (checkProjectiles) {
                            logger.info("IGNORE ENTITY: {}", this.ignoreEntity);
                        }
                    }
                }
            }


            //if (this.ignoreEntity != null)
            //{
            //    if (flag)
            //    {
            //        this.ignoreTime = 2;
            //    }
            //    else if (this.ignoreTime-- <= 0)
            //    {
            //        this.ignoreEntity = null;
            //    }
            //}

            if (entity != null &&(!firstUpdate||entity!=entityThrower))
            {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null)
            {
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL)
                {
                    this.setPortal(raytraceresult.getBlockPos());
                }
                else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
                {
                    this.onImpact(raytraceresult);
                }
            }
            //firstUpdate = false;
        }

        //if (isServer) {
        //    if(checkProjectiles) {logger.info("SENDING PROPER VELOCITY UPDATES");}
        //    SimpleNetworkWrapper.INSTANCE.sendToAll(new VelocityPacket(this, this.getEntityId(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        //}
        if(checkProjectiles){logger.info("VELOCITY VECTOR {}", motionVector);}
        motionVectorOld = new Vector3d(prevMotionX,prevMotionY,prevMotionZ);
        //if (isServer) {
        //    if(checkProjectiles) {logger.info("SENDING PROPER VELOCITY UPDATES");}
        //    SimpleNetworkWrapper.INSTANCE.sendToAll(new VelocityPacket(this, this.getEntityId(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        //}

        //velocityPrivate = this.

        this.posX=this.prevPosX;
        this.posY=this.prevPosY;
        this.posZ=this.prevPosZ;
        this.posX += motionVector.getX();
        this.posY += motionVector.getY();
        this.posZ += motionVector.getZ();
        motionVector = new Vector3d(velocityPrivate*Math.sin(this.pitch)*Math.cos(this.yaw), velocityPrivate*Math.sin(this.pitch), velocityPrivate*Math.cos(this.pitch)*Math.sin(this.yaw));
        //motionVector = new Vector3d(velocityPrivate*Math.sin(this.rotationPitch)*Math.cos(this.rotationYaw), velocityPrivate*Math.sin(this.rotationPitch), velocityPrivate*Math.cos(this.rotationPitch)*Math.sin(this.rotationYaw));
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {

        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = this.getGravityVelocity();

        if (this.isInWater())
        {
            for (int j = 0; j < 4; ++j)
            {
                float f3 = 0.25F;
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }

            f1 = 0.8F;
        }

        this.motionX *= (double)f1;
        this.motionY *= (double)f1;
        this.motionZ *= (double)f1;

        if (!this.hasNoGravity())
        {
            this.motionY -= (double)f2;
        }

        this.setPosition(this.posX, this.posY, this.posZ);

        if(checkProjectiles){logger.info("Remote?: {}", world.isRemote);}
        if(!world.isRemote) {
            if(checkProjectiles){logger.info("Entity ID: {}", this.getEntityId());}
            if (checkProjectiles) {
                logger.info("SENDING PROPER VELOCITY UPDATES FOR ENTITY {}", this.getEntityId());
            }
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            world.getMinecraftServer().getWorld(this.dimension).getEntityTracker().sendToTracking(this, new SPacketEntityTeleport(this));
            //SimpleNetworkWrapper.INSTANCE.sendToServer(new VelocityPacket(this.getUniqueID(), this.seed, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
            xPosPrevQuiet=xPosQuiet;
            yPosPrevQuiet=yPosQuiet;
            zPosPrevQuiet=zPosQuiet;
            xPosQuiet=posX;
            yPosQuiet=posY;
            zPosQuiet=posZ;
            motionVector = new Vector3d(posX-prevPosX,posY-prevPosY,posZ-prevPosZ);
        }


        if(checkProjectiles){logger.info("xvel: {} yvel: {} zvel:{}", motionX, motionY, motionZ);}
        //this.velocityPrivate = Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ);
        //velocityX = velocityPrivate*Math.sin(this.rotationPitch)*Math.cos(this.rotationYaw);
        //velocityY = velocityPrivate*Math.sin(this.rotationPitch);
        //velocityZ = velocityPrivate*Math.cos(this.rotationPitch)*Math.sin(this.rotationYaw);
        //motionY = tempMotionY;
        //motionZ = tempMotionZ;
        //motionX = tempMotionX;
        //this.world.entity
        //EntityTracker.updateServerPosition(this, posX, posY, posZ);
        //if(checkProjectiles) {logger.info("Projectile Init Values:\nX: {} Y: {} Z: {}", motionX, motionY, motionZ);}
        //motionY = velocityPrivate*Math.sin(pitch);
        //motionX = velocityPrivate*Math.cos(pitch)*Math.sin(yaw);
        //motionZ = velocityPrivate*Math.sin(pitch)*Math.cos(yaw);
        //System.out.println ("X: " + motionX + " Y: " + motionY + " Z: " + motionZ);

        previousRoll = roll;
        roll += rollSpeed;
        Minecraft.getMinecraft().entityRenderer.updateRenderer();

    }



    @Override
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        if(checkProjectiles) {logger.info("Shoot event triggered.");}
        yaw = rotationYawIn;
        pitch = rotationPitchIn;
        isServer = true;
        this.entityThrower=entityThrower;
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;


        if (!entityThrower.onGround)
        {
            this.motionY += entityThrower.motionY;
        }
        this.velocityPrivate = Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ);
    }

    //DISABLED 'CAUSE IT'S OBSOLETE, BUT CAN PROBABLY BE REFERENCED
    //Override
    //rotected void onImpact(RayTraceResult result) {
    //   Entity entity = result.entityHit;

    //   if (entity != null)
    //   {
    //       float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
    //       int i = MathHelper.ceil((double)f * this.damage);
    //       if (checkProjectiles) { TinkersArsenal.logger.info("BASE DAMAGE: "+this.damage);}
    //       if (checkProjectiles) {TinkersArsenal.logger.info("VELOCITY MULTIPLIER: "+f);}
    //       if (checkProjectiles) {TinkersArsenal.logger.info("PROJECTILE DAMAGE: "+i);}


    //       DamageSource damagesource;

    //       if (this.entityThrower == null)
    //       {
    //           damagesource = DamageSource.causeArrowDamage(this, this);
    //       }
    //       else
    //       {
    //           damagesource = DamageSource.causeArrowDamage(this, this.entityThrower);
    //       }

    //       if (this.isBurning() && !(entity instanceof EntityEnderman))
    //       {
    //           entity.setFire(5);
    //       }

    //       if (entity.attackEntityFrom(damagesource, (float)i))
    //       {
    //           if (entity instanceof EntityLivingBase)
    //           {
    //               EntityLivingBase entitylivingbase = (EntityLivingBase)entity;

    //               if (!this.world.isRemote)
    //               {
    //                   entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
    //               }

    //               //if (this.knockbackStrength > 0)
    //               //{
    //               //    float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
//DI//ABLED AS I DON'T KNOW WHAT DO DO WITH IT
    //               //    if (f1 > 0.0F)
    //               //    {
    //               //        entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
    //               //    }
    //               //}

    //               if (this.entityThrower instanceof EntityLivingBase)
    //               {
    //                   EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.entityThrower);
    //                   EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.entityThrower, entitylivingbase);
    //               }

    //               //this.arrowHit(entitylivingbase);
    //               //DISABLED 'CAUSE THIS DOESN'T HAVE arrowHit ANYMORE, NOT SURE WHAT I'D NEED TO REPLACE JUST YET

    //               if (this.entityThrower != null && entitylivingbase != this.entityThrower && entitylivingbase instanceof EntityPlayer && this.entityThrower instanceof EntityPlayerMP)
    //               {
    //                   ((EntityPlayerMP)this.entityThrower).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
    //               }
    //           }

    //           this.playSound(SoundEvents.BLOCK_ANVIL_HIT, .5F, 1.3F / (this.rand.nextFloat() * 0.2F + 0.9F));
    //           this.playSound(SoundEvents.E_PARROT_IM_MAGMACUBE, 3.5F, 1.3F / (this.rand.nextFloat()*0.2F+0.9F));

    //           //if (!(entity instanceof EntityEnderman))
    //           //{
    //           //    this.setDead();
    //           //}
    //       }
    //       else
    //       {
    //           this.motionX *= -0.10000000149011612D;
    //           this.motionY *= -0.10000000149011612D;
    //           this.motionZ *= -0.10000000149011612D;
    //           this.rotationYaw += 180.0F;
    //           this.prevRotationYaw += 180.0F;
    //           this.ticksInAir = 0;

    //           if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D)
    //           {

    //               this.setDead();
    //           }
    //       }
    //   }
    //   else
    //   {
    //       BlockPos blockpos = result.getBlockPos();
    //       this.blockX = blockpos.getX();
    //       this.blockY = blockpos.getY();
    //       this.blockZ = blockpos.getZ();
    //       IBlockState iblockstate = this.world.getBlockState(blockpos);
    //       this.inBlock = iblockstate.getBlock();
    //       this.inMeta = this.inBlock.getMetaFromState(iblockstate);
    //       this.motionX = (double)((float)(result.hitVec.x - this.posX));
    //       this.motionY = (double)((float)(result.hitVec.y - this.posY));
    //       this.motionZ = (double)((float)(result.hitVec.z - this.posZ));
    //       float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
    //       this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
    //       this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
    //       this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
    //       //this.playSound(SoundEvents.BLOCK_ANVIL_HIT, 4F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    //       this.inGround = true;
    //
    //       if (iblockstate.getMaterial() != Material.AIR)
    //       {
    //           this.inBlock.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
    //           playStepSound(blockpos, iblockstate.getBlock());
    //       }
    //   }
    //


}


//    public EntityArsenalProjectile(World worldIn, double x, double y, double z) {
//        super(worldIn);
//        shootingEntity.setPosition(x, y, z);
//    }
//
//    public EntityArsenalProjectile(World worldIn, EntityLivingBase shooter, double velocityPrivate) {
//        super(worldIn, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
//        super.shootingEntity = shooter;
//        double pitch = shooter.rotationPitch;
//        double yaw = shooter.rotationYaw;
//        System.out.println("PITCH: " + pitch);
//        System.out.println("YAW: " + yaw);
//        motionZ = velocityPrivate*Math.sin(pitch)*Math.cos(yaw);
//        motionY = velocityPrivate*Math.sin(pitch);
//        motionX = velocityPrivate*Math.cos(pitch)*Math.sin(yaw);
//        System.out.println ("X: " + motionX + " Y: " + motionY + " Z: " + motionZ);
//    }
//
//    public EntityArsenalProjectile(World worldIn, EntityLivingBase shooter) {
//        super(worldIn, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
//        super.shootingEntity = shooter;
//        double pitch = shooter.rotationPitch;
//        double yaw = shooter.rotationYaw;
//        System.out.println("PITCH: " + pitch);
//        System.out.println("YAW: " + yaw);
//        motionZ = velocityPrivate*Math.sin(pitch)*Math.cos(yaw);
//        motionY = velocityPrivate*Math.sin(pitch);
//        motionX = velocityPrivate*Math.cos(pitch)*Math.sin(yaw);
//        System.out.println ("X: " + motionX + " Y: " + motionY + " Z: " + motionZ);
//    }
//
//    /**
//     * Checks if the entity is in range to render.
//     */
//    @SideOnly(Side.CLIENT)
//    public boolean isInRangeToRenderDist(double distance) {
//        return super.isInRangeToRenderDist(distance);
//    }
//
//    protected void entityInit() {
//        super.entityInit();
//    }
//
//
//    /**
//     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
//     */
//
//    /**
//     * Set the position and rotation values directly without any clamping.
//     */
//    @SideOnly(Side.CLIENT)
//    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
//        //Anyways, it's -Z for north, but is y up and x west/east
//        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
//
//    }
//
//    /**
//     * Updates the entity motion clientside, called by packets from the server
//     */
//    @SideOnly(Side.CLIENT)
//    public void setVelocity(double x, double y, double z) {
//        super.setVelocity(x, y, z);
//    }
//
//    /**
//     * Called to update the entity's position/logic.
//     */
//    public void onUpdate() {
//        super.onUpdate();
//        //motionX = velocityPrivate*Math.sin(pitch)*Math.cos(yaw);
//        //motionY = velocityPrivate*Math.sin(pitch);
//        //motionZ = velocityPrivate*Math.cos(pitch)*Math.sin(yaw);
//
//        if (super.collidedHorizontally || super.collidedVertically || this.inGround || this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
//            moving = false;
//        }
//
//        if (!world.isRemote && moving) {
//            //if (soundCounter == 2) {            keep in mind: BLOCK_ANVIL_FALL,
//
//            //new TASoundEvent(this.world, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.AMBIENT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F), true);
//            //this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_ANVIL_FALL, this.getSoundCategory(), 10F, 2F / (this.rand.nextFloat() * 0.3F + 0.9F));
//            //System.out.println("X: "+ this.posX + " Y: " + this.posY + " Z: " + this.posZ);
//            //System.out.println("X Velocity: "+ this.motionX + " Y Velocity: " + this.motionY + " Z Velocity: " + this.motionZ);
//            //this.world.spawnParticle(EnumParticleTypes.SPELL, true, this.posX, this.posY, this.posZ, 0, 0, 0);
//            this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT,2F * (float)Math.sqrt(this.motionX*this.motionX+this.motionZ*this.motionZ+this.motionY*this.motionY)/8, .2F / (this.rand.nextFloat() * 0.3F + 0.9F)*this.velocityPrivate);
//                /*soundCounter = 0;
//            }
//            else if (soundCounter >= 2) {
//                soundCounter = 2;
//            }
//            else {
//                soundCounter += 1;
//            }*/
//
//        }
//    }
//
//
//
//    public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocityPrivate, float inaccuracy)
//    {
//        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) * velocityPrivate;
//        float f1 = -MathHelper.sin(pitch * 0.017453292F) * velocityPrivate;
//        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) * velocityPrivate;
//        this.shoot((double)f, (double)f1, (double)f2, velocityPrivate, inaccuracy);
//        this.motionX += shooter.motionX;
//        this.motionY += shooter.motionY;
//        this.motionZ += shooter.motionZ;
//
//        if (!shooter.onGround)
//        {
//            this.motionY += shooter.motionY;
//        }
//        this.onUpdate();
//    }
//
//    @Override
//    protected ItemStack getArrowStack() {
//        return new ItemStack(CommonProxy.SUPERSONIC_ARROW);
//    }
//}
//
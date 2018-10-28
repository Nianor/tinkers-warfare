package com.nianor.tinkersarsenal.common.entities;

import com.nianor.tinkersarsenal.client.particles.TracerParticle;
import com.nianor.tinkersarsenal.common.tileentities.DamageCounterEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3d;

import static com.nianor.tinkersarsenal.TinkersArsenal.*;
import static java.lang.Double.NaN;

//Words of Don:
//Yeah, you should NEVER have seperate ents on client and server.  Das bad fer inter-mod support.
//As to runnables, check the forge packet info.  It's rather hard for me to explain.
//In a nutshell, however, a runnable makes the code in the packet wait to execute until the game thread is done processing to prevent a CME.

public class EntityArsenalProjectile extends EntityThrowable {


    public int timeMultiplier = 1;//Used for testing, allows bullets to move in slow motion.


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
    private double prevX;
    private double prevY;
    private double prevZ;
    private double prevOldX;
    private double prevOldY;
    private double prevOldZ;
    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;
    public static double xPosQuiet;
    public static double yPosQuiet;
    public static double zPosQuiet;
    public static double xPosPrevQuiet;
    public static double yPosPrevQuiet;
    public static double zPosPrevQuiet;
    //REMOVED THIS, WE'LL SEE HOW MUCH STUFF IT BREAKS. MIGHT BE BETTER TO DO WITHOUT.
    public Vector3d motionVector;
    private double dragAcceleration;
    private Vector3d motionVectorOld;
    private static float n;//This is n as used in Pejsa's ballistic formulas. While this is not a 100% accurate representation of real-life ballistics, it makes a decent effort.
    private static double ballisticCoefficient = .6;//THIS SHOULD BE BETWEEN .35 and .63
    //Mass is in grams
    //7.62x54R dimensions for testing.
    public float caliber = (.312F);
    public float bodyLength = (.25F);
    public float neckLength = (.25F + .125F);
    public float tipLength = (.5F);
    public double mass = 11.7;
    //.50 cal dimensions for testing.
    //public float caliber = (.5F);
    //public float bodyLength=(.75F);
    //public float neckLength=(1.125F);
    //public float tipLength=(1.5F);
    //public float mass = ;
    //30mm dimensions for testing.
    //public float caliber = (1.1811F);
    //public float bodyLength=(4F);
    //public float neckLength=(.7625F+4F);
    //public float tipLength=(.7625F+.7625F+4F);
    //public float mass = ;
    protected float x;
    protected float y;
    protected float z;
    private EntityLivingBase thrower;
    //private WorldServer world;
    private boolean isServer = false;
    private Entity entityThrower;
    private boolean firstUpdate = true;
    private boolean firstUpdateEnabled = true;
    private boolean isRemote;
    private double seed;


    public EntityArsenalProjectile(World worldIn, EntityLivingBase throwerIn, float yaw, float velocityPrivate, float inaccuracy, double seed) {
        super(worldIn, throwerIn);
        System.out.println("Projectile created. Is remote? " + this.world.isRemote);
        isRemote = worldIn.isRemote;
        this.seed = seed;
        //if(!isRemote){ //TEMP
        //    this.visible=false;
        //}
        //else{
        //    this.visible=true;
        //}
        if (checkProjectiles) {
            logger.info("EntityArsenalProjectile event triggered.");
        }
        thrower = throwerIn;

        motionVector = new Vector3d(velocityPrivate * Math.sin(throwerIn.rotationPitch) * Math.cos(yaw), velocityPrivate * Math.sin(throwerIn.rotationPitch), velocityPrivate * Math.cos(throwerIn.rotationPitch) * Math.sin(yaw));
        //motionVector = new Vector3d(0,0,0);

        //this.world=worldIn.;
        if (throwerIn.posX != 0 && throwerIn.posY != 0 && throwerIn.posZ != 0) {
            xInit = (float) throwerIn.posX;
            yInit = (float) throwerIn.posY;
            zInit = (float) throwerIn.posZ;
            xPosQuiet = xInit;
            yPosQuiet = yInit;
            zPosQuiet = zInit;
            this.pitch = throwerIn.rotationPitch;
            this.yaw = yaw;
        }
        this.motionX = motionVector.getX();
        this.motionY = motionVector.getY();
        this.motionZ = motionVector.getZ();
        rotationPitch=throwerIn.rotationPitch;
        rotationYaw=yaw;
        //SimpleNetworkWrapper.INSTANCE.sendToServer(new VelocityPacket(this, this.getEntityId(), this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ));
        if (checkProjectiles) {
            logger.info("SHOOTING AT: X: {} Y: {} Z: {}", xInit, yInit, zInit);
        }

        this.velocityPrivate = velocityPrivate;

        shoot(throwerIn, throwerIn.rotationPitch, yaw, 0, velocityPrivate, inaccuracy);
    }

    public EntityArsenalProjectile(World worldIn, EntityPlayerMP throwerIn, float yaw, float velocityPrivate, float inaccuracy, double seed) {
        super(worldIn, throwerIn);
        isRemote = worldIn.isRemote;
        this.seed = seed;
        if (checkProjectiles) {
            logger.info("EntityArsenalProjectile event triggered.");
        }
        thrower = throwerIn;

        motionVector = new Vector3d(velocityPrivate * Math.sin(throwerIn.rotationPitch) * Math.cos(yaw), velocityPrivate * Math.sin(throwerIn.rotationPitch), velocityPrivate * Math.cos(throwerIn.rotationPitch) * Math.sin(yaw));
        if (throwerIn.posX != 0 && throwerIn.posY != 0 && throwerIn.posZ != 0) {
            xInit = (float) throwerIn.posX;
            yInit = (float) throwerIn.posY;
            zInit = (float) throwerIn.posZ;
            xPosQuiet = xInit;
            yPosQuiet = yInit;
            zPosQuiet = zInit;
            this.pitch = throwerIn.rotationPitch;
            this.yaw = yaw;
        }
        this.motionX = motionVector.getX();
        this.motionY = motionVector.getY();
        this.motionZ = motionVector.getZ();
        if (checkProjectiles) {
            logger.info("SHOOTING AT: X: {} Y: {} Z: {}", xInit, yInit, zInit);
        }

        this.velocityPrivate = velocityPrivate;

        shoot(throwerIn, throwerIn.rotationPitch, yaw, 0, velocityPrivate, inaccuracy);
    }

    public EntityArsenalProjectile(World worldIn) {
        super(worldIn);
        if (checkProjectiles) {
            logger.info("Secondary EntityArsenalProjectile event triggered.");
            if (worldIn.getEntityByID(this.getEntityId()) != null) {
                logger.info("ENTITY: ", worldIn.getEntityByID(this.getEntityId()));
                logger.info("Velocity X: {}\nVelocity Y: {}\nVelocity Z: {}", worldIn.getEntityByID(this.getEntityId()).motionX, worldIn.getEntityByID(this.getEntityId()).motionY, worldIn.getEntityByID(this.getEntityId()).motionZ);
            }
        }
    }

    @SideOnly(Side.SERVER)
    private void forceVelocityUpdate() {

        //Minecraft.getMinecraft().getIntegratedServer()
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        System.out.println("First Update: " + firstUpdate + "\nHit Result: " + result + "\nEntity Thrower: " + entityThrower);
        if (result.entityHit != null && !isRemote && (!firstUpdate || result.entityHit.getEntityId() != entityThrower.getEntityId())) {
            int temp = result.entityHit.hurtResistantTime;
            result.entityHit.hurtResistantTime = 0;
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 3 * (float) velocityPrivate);
            result.entityHit.hurtResistantTime = temp;
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
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

                if (harvest != null) {
                    if (harvest.equals("shovel")) {
                        //System.out.println("BLOCK: "+iblockstate.getBlock().getLocalizedName()+"\nHARVESTED BY SHOVEL."+"\nEXPLOSION RESISTANCE: "+iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null)+"\nHARDNESS: " + iblockstate.getBlockHardness(world, blockpos));
                        resistance = 10 * iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null) / iblockstate.getBlockHardness(world, blockpos);

                    } else if (harvest.equals("pickaxe")) {
                        resistance = 2 * iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null) * iblockstate.getBlockHardness(world, blockpos);
                    } else if (harvest.equals("axe")) {
                        resistance = 5 * iblockstate.getBlockHardness(world, blockpos) / iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null);
                    }
                } else if (iblockstate.getBlockHardness(world, blockpos) != -1) {
                    resistance = (float) Math.pow((iblockstate.getBlockHardness(world, blockpos) + 1), 3);
                }
                float destruction = (float) (((velocityPrivate * caliber)) * (Math.pow(Math.random(), 5)));
                System.out.println("Destruction: " + destruction + "\nResistance: " + resistance + "\nBlock: " + iblockstate.getBlock().getLocalizedName());
                System.out.println("Progress marker 2");
                if (world.getTileEntity(result.getBlockPos()) instanceof DamageCounterEntity) {
                    ((DamageCounterEntity) world.getTileEntity(result.getBlockPos())).damage(Math.round(destruction));
                } else {
                    new DamageCounterEntity();
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setInteger("health", 100);
                    compound.setInteger("damage", Math.round(destruction));
                    compound.setInteger("x", result.getBlockPos().getX());
                    compound.setInteger("y", result.getBlockPos().getY());
                    compound.setInteger("z", result.getBlockPos().getZ());
                    DamageCounterEntity.create(world, compound);
                }
                /*if(resistance-destruction<0) {
                    if(!world.isRemote) {
                        world.destroyBlock(blockpos, false);
                    }
                    System.out.println("Block broken.");
                }*/
                if (world.isRemote) {
                    double xSpeed = 0;
                    double ySpeed = 0;
                    double zSpeed = 0;
                    float xDisplacement = 0;
                    float yDisplacement = 0;
                    float zDisplacement = 0;
                    if (result.sideHit == EnumFacing.NORTH || result.sideHit == EnumFacing.SOUTH) {
                        zSpeed = -this.motionZ * .15;
                        xSpeed = (Math.random() - .5);
                        ySpeed = (Math.random() - .5);
                        xDisplacement = (float) Math.random() - .5F;
                        yDisplacement = (float) Math.random() - .5F;
                        zDisplacement = .5F;
                    } else if (result.sideHit == EnumFacing.EAST || result.sideHit == EnumFacing.WEST) {
                        xSpeed = -this.motionX * .15;
                        ySpeed = (Math.random() - .5);
                        zSpeed = (Math.random() - .5);
                        xDisplacement = .5F;
                        yDisplacement = (float) Math.random() - .5F;
                        zDisplacement = (float) Math.random() - .5F;
                    } else if (result.sideHit == EnumFacing.UP || result.sideHit == EnumFacing.DOWN) {
                        ySpeed = -this.motionY * .15;
                        xSpeed = (Math.random() - .5);
                        zSpeed = (Math.random() - .5);
                        xDisplacement = (float) Math.random() - .5F;
                        yDisplacement = .5F;
                        zDisplacement = (float) Math.random() - .5F;
                    }
                    /*
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
                    */
                    //Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleImpact(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), xSpeed, ySpeed, zSpeed, iblockstate));
                }
            }
        } else {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        //return (float)((ticksInAir/20)*9.8);
        return .03F;
    }

    public double getSeed() {
        return seed;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        if(teleport) {
            prevOldX=prevX;
            prevOldY=prevY;
            prevOldZ=prevZ;
            prevX=x;
            prevY=y;
            prevZ=z;

            motionX = prevOldX - prevX;
            motionY = prevOldY - prevY;
            motionZ = prevOldZ - prevZ;

        }
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);

        double velocity=Math.pow(motionX*motionX+motionY*motionY+motionZ*motionZ, .5);

        float i=0;
        while (i<=velocity&&teleport) {
            float posFraction = (float)(i/velocity);
            Minecraft.getMinecraft().effectRenderer.addEffect(new TracerParticle(this.world, prevOldX+this.motionX*posFraction, prevOldY+this.motionY*posFraction, prevOldZ+this.motionZ*posFraction, this.caliber, 1F, 0F, 0F, (float)((Math.random()-.5)*.075), (float)((Math.random()-.5)*.075), (float)((Math.random()-.5)*.075)));
            //Disabled and possibly broken "conventional" particle spawn method
            //world.spawnParticle(WarfareParticles.TRACER_PARTICLE, prevOldX+this.motionX*posFraction, prevOldY+this.motionY*posFraction, prevOldZ+this.motionZ*posFraction, 1, 0, .5);
            //This one works, disabled for custom particle testing.
            //world.spawnParticle(EnumParticleTypes.REDSTONE, prevOldX+this.motionX*posFraction, prevOldY+this.motionY*posFraction, prevOldZ+this.motionZ*posFraction, 0, 0, .5);
            //This one works correctly, but doesn't do custom colors. Leaving it in so that when Mojang de-janks redstone color, I have a backup.
            //world.spawnParticle(EnumParticleTypes.REDSTONE, posX+this.motionX*posFraction, posY+this.motionY*posFraction, posZ+this.motionZ*posFraction, 0, 0, 0, 1,1,1);
            i+=1;
        }

    }

    @Override
    public void onUpdate() {
        if (this.posY < 0 && !Loader.isModLoaded("cubicchunks") && (dimension != 1 || dimension != 0 || dimension != -1)) {
            System.out.println("Projectile below bedrock, setting to dead.\nX position: " + this.posX + "\nY position: " + this.posY + "\nZ position: " + this.posZ);
            this.setDead();
        }
        //<editor-fold desc="Server-side movement procedures.">
        if(!world.isRemote) {
            if(checkProjectiles) {
                System.out.println("VELOCITY PRE-DRAG FOR ENTITY " + this.getEntityId() + "\nMOTION X: " + motionX + "\nMOTION Y: " + motionY + "\nMOTION Z: " + motionZ);
            }
            double velocity=Math.pow(motionX*motionX+motionY*motionY+motionZ*motionZ, .5);

            pitch =rotationPitch;
            yaw=rotationYaw;

            double dragCoefficient = 1/(10*ballisticCoefficient)+((Math.sqrt(1.38*velocity)+1)/(7*Math.sqrt(Math.sqrt(ballisticCoefficient))*Math.abs(Math.sqrt(1.38*velocity)+ballisticCoefficient-1.8)+.75));
            double a=1; //Air density
            double dragForce =a*velocity*velocity*(3.1415/(4*39.3701))*dragCoefficient*(mass*0.00220462)*.4167/ballisticCoefficient/2;// //FROM http://www.kevinboone.net/zom2.html
            if(checkProjectiles) {
                System.out.println("DRAG STUFFS: \nDrag Coefficient: " + dragCoefficient + "\nDrag Force: " + dragForce + "\nOld Velocity: " + velocity + "\nNew Velocity: " + (velocity - dragForce / (mass / 1000) / 20) + "\nChange In Velocity: " + (dragForce / (mass / 1000) / 20));
            }
            if(dragForce!=NaN&&dragForce!=0) {
                velocity = velocity - dragForce / (mass / 453.592) / 20 / timeMultiplier;
            }
            if(checkProjectiles) {
                System.out.println("Velocity: " + velocity);
            }
            if(velocity<0) {
                velocity=0;
            }

            Vector3d tempVector = new Vector3d(motionX, motionY, motionZ);
            tempVector.scale(tempVector.length()/velocity);
            if(checkProjectiles) {
                System.out.println("Temp Vector: " + tempVector);
            }
            motionX=tempVector.getX();
            motionY=tempVector.getY()-this.getGravityVelocity();
            motionZ=tempVector.getZ();

            this.posX += motionX/timeMultiplier;
            this.posY += motionY/timeMultiplier;
            this.posZ += motionZ/timeMultiplier;
            if(checkProjectiles) {
                System.out.println("VELOCITY POST-DRAG: \nMOTION X: " + motionX + "\nMOTION Y: " + motionY + "\nMOTION Z: " + motionZ);
            }
            world.getMinecraftServer().getWorld(this.dimension).getEntityTracker().sendToTrackingAndSelf(this, new SPacketEntityTeleport(this));
        }
        //</editor-fold>
        //<editor-fold desc="Client-side movement procedures.">
        else {



            this.posX += motionX;
            this.posY += motionY;
            this.posZ += motionZ;
            if(checkProjectiles) {
                System.out.println("VELOCITY UPDATE: \nMOTION X: " + motionX + "\nMOTION Y: " + motionY + "\nMOTION Z: " + motionZ);
            }
        }
        //</editor-fold>

        //System.out.println("\nMotion vector: " + motionVector + "\nEntity ID: " + this.getEntityId() + "\nIs invisible? " + this.isInvisible());
        //if (motionVector != null) {
        //    velocityPrivate = motionVector.length();
        //}
        //else {
        //    motionVector=new Vector3d(velocityPrivate * Math.sin(this.rotationPitch) * Math.cos(this.rotationYaw), velocityPrivate * Math.sin(this.rotationPitch), velocityPrivate * Math.cos(this.rotationPitch) * Math.sin(this.rotationYaw));
        //}
        ////This is where the value of n is set, based on, again, the values Pejsa defines. The odd decimals are simply a result of converting feet per second to blocks per tick.
        ///*if(velocityPrivate<13.716||(velocityPrivate<21.336&&velocityPrivate>=18.288)) {
        //    n=0;
        //}
        //else if (velocityPrivate>=13.716&&velocityPrivate<18.288) {
        //    n=-3.0F;
        //}
        //else if (velocityPrivate>=21.336) {
        //    n=.5F;
        //}*/
        //if (false) {//!world.isRemote) {
        //    //THIS NEEDS THE ADDITION OF MASS AND AN ACTUAL DRAG COEFFICIENT
        //    System.out.println("N: " + n);
        //    System.out.println("BC: " + ballisticCoefficient);
        //    //double velocityTestMatch = (velocityPrivate-(Math.pow(velocityPrivate, 2 - n)/(30*ballisticCoefficient)));
        //    double velocityTestMatch = (velocityPrivate - (Math.pow(velocityPrivate, 2 - ((9 / (Math.pow(velocityPrivate - 15, 2) + 3)) + Math.pow(velocityPrivate / 20, .25) / 2)) / (30 * ballisticCoefficient)) / 100);//This is an experimental drag calculation to bypass the need for an "n" value by substituting a simplified calculation
        //    /*if(((velocityTestMatch>=13.716&&velocityTestMatch<18.288)&&n!=-3.0f)||(velocityTestMatch>=21.336&&n!=.5)||(n!=0&&(velocityTestMatch<13.716||(velocityTestMatch<21.336&&velocityTestMatch>=18.288)))){
        //        System.out.println("AERODYNAMIC SHIFT DETECTED");
        //        double targetVelocity=0;
        //        float nextN=0;
        //        if(velocityPrivate>=21.336) {
        //            targetVelocity=21.336;
        //            nextN=0F;
        //        }
        //        else if (velocityPrivate>=18.288) {
        //            targetVelocity=18.288;
        //            nextN=-3.0F;
        //        }
        //        else if (velocityPrivate>=13.716) {
        //            targetVelocity=13.716;
        //            nextN=0F;
        //        }
        //            double timeMultiplier = (velocityPrivate-targetVelocity)/((Math.pow(velocityPrivate, 2-n)/(30*ballisticCoefficient)));
        //            System.out.println("Time Multiplier: "+timeMultiplier);
        //            velocityTestMatch=velocityPrivate-timeMultiplier*((Math.pow(velocityPrivate, 2-n)/(30*ballisticCoefficient)));
        //            velocityTestMatch=velocityTestMatch-(1-timeMultiplier)*((Math.pow(velocityTestMatch, 2-nextN)/(30*ballisticCoefficient)));
        //        //velocityTestMatch=(velocityPrivate-(Math.pow(velocityPrivate, 2-n)/(30*ballisticCoefficient)));//TRY SOLVING FOR THE TIME THAT n SWITCHES, THEN CALCULATE IT AS TWO INTEGRALS. PROBLEM SOLVED.
//
        //        System.out.println("Final Velocity: " + velocityTestMatch);
        //    }
//
        //    /*int velocityIterator = 0;
        //    int velocityFactor = (int) Math.round(velocityPrivate * 25);
        //    while (velocityIterator < velocityFactor) {
        //        if(dragAcceleration>0) {
        //            velocityPrivate = velocityPrivate - dragAcceleration / velocityFactor;
        //            dragAcceleration = .1 * Math.pow(velocityPrivate, 2 - n) / ballisticCoefficient;
        //            //System.out.println("Drag: "+dragAcceleration+"\nVelocity: "+velocityPrivate);
        //        }
        //        velocityIterator += 1;
        //    }*/
        //    velocityPrivate = velocityTestMatch;
        //    System.out.println("\nVelocity Single Op Test: " + velocityTestMatch + "\nVelocity Iterative Result: " + velocityPrivate);
        //}
        //if (!firstUpdateEnabled) {
        //    firstUpdate = false;
        //}
//
        //motionVector = new Vector3d(velocityPrivate * Math.sin(this.rotationPitch) * Math.cos(this.rotationYaw), velocityPrivate * Math.sin(this.rotationPitch), velocityPrivate * Math.cos(this.rotationPitch) * Math.sin(this.rotationYaw));
//
        //this.motionX = motionVector.getX();
        //this.motionY = motionVector.getY();
        //this.motionZ = motionVector.getZ();
//
        //float f1 = 1F;
        //float f2 = this.getGravityVelocity();
//
        //if (this.isInWater()) {
        //    for (int j = 0; j < 4; ++j) {
        //        float f3 = 0.25F;
        //        this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
        //    }
//
        //    f1 = 0.8F;
        //}
//
        //this.motionX *= (double) f1;
        //this.motionY *= (double) f1;
        //this.motionZ *= (double) f1;
//
        //if (!this.hasNoGravity()) {
        //    this.motionY -= (double) f2;
        //}
//
//
        //this.motionVector = new Vector3d(this.motionX, this.motionY, this.motionZ);
//
        //float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        //this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
//
        //for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
//
        //}
//
        //while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
        //    this.prevRotationPitch += 360.0F;
        //}
//
        //while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
        //    this.prevRotationYaw -= 360.0F;
        //}
//
        //while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
        //    this.prevRotationYaw += 360.0F;
        //}
//
        //this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        //this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
//
        //motionVector = new Vector3d(velocityPrivate * Math.sin(this.pitch) * Math.cos(this.yaw), velocityPrivate * Math.sin(this.pitch), velocityPrivate * Math.cos(this.pitch) * Math.sin(this.yaw));
        //this.motionX = motionVector.getX();
        //this.motionY = motionVector.getY();
        //this.motionZ = motionVector.getZ();
        //{
        //    Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        //    Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        //    RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
        //    vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        //    vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        //    if (checkProjectiles) {
        //        logger.info("RAYTRACING BEGINNING: \nPOSITIONVECTOR:\n   X:{}\n   Y:{}\n   Z:{}\nMOVEMENTVECTOR:\n   X:{}\n   Y:{}\n   Z:{}\nRESULT: {}", vec3d.x, vec3d.y, vec3d.z, vec3d1.x, vec3d1.y, vec3d1.z, raytraceresult);
        //    }
        //    System.out.println("Raytrace results: " + raytraceresult);
        //    if (raytraceresult != null) {
        //        vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        //    }
        //    Entity entity = null;
        //    if (!isRemote) {
        //        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        //        double d0 = 0.0D;
        //        boolean flag = false;
        //        if (checkProjectiles) {
        //            logger.info("{}", list);
        //        }
//
        //        for (int i = 0; i < list.size(); ++i) {
        //            System.out.println("Length of collision list: " + list.size());
        //            Entity entity1 = list.get(i);
//
        //            if (entity1.canBeCollidedWith()) {
        //                if (entity1 == this.ignoreEntity) {
        //                    flag = true;
        //                } else if (this.thrower != null && this.ticksExisted < -1 && this.ignoreEntity == null) {
        //                    this.ignoreEntity = entity1;
        //                    System.out.println("Thrower not null, ticks existed less than negative 1(?), and no entity set to be ignored.");
        //                    flag = true;
        //                } else {
        //                    System.out.println("Thrower: " + thrower + "\nTicks existed: " + ticksExisted + "\nIgnore Entity: " + ignoreEntity);
        //                    flag = false;
        //                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
        //                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
        //                    if (checkProjectiles) {
        //                        logger.info("RAYTRACERESULT1: {}", raytraceresult1);
        //                    }
        //                    if (raytraceresult1 != null) {
        //                        double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
//
        //                        if ((d0 > d1 || d0 == 0.0D) && entity1.getEntityId() != this.thrower.getEntityId() && !entity1.isDead && !(entity1.hurtResistantTime > 0) && (!firstUpdate || entity1 != entityThrower)) {
        //                            if (checkProjectiles) {
        //                                logger.info("Entity: {}\nDistance: {}\nPrevDistance: {}\nThrower: {}", entity1, d1, d0, this.thrower);
        //                            }
        //                            entity = entity1;
        //                            d0 = d1;
        //                        }
        //                    }
        //                }
        //                if (checkProjectiles) {
        //                    logger.info("IGNORE ENTITY: {}", this.ignoreEntity);
        //                }
        //            }
        //        }
        //    }
//
        //    if (entity != null && (!firstUpdate || entity != entityThrower)) {
        //        raytraceresult = new RayTraceResult(entity);
        //    }
//
        //    if (raytraceresult != null) {
        //        if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
        //            this.setPortal(raytraceresult.getBlockPos());
        //        } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
        //            this.onImpact(raytraceresult);
        //        }
        //    }
        //}
//
        //this.posX += this.motionX;
        //this.posY += this.motionY;
        //this.posZ += this.motionZ;
//
//
        //if (checkProjectiles) {
        //    logger.info("VELOCITY VECTOR {}", motionVector);
        //}
        //motionVectorOld = new Vector3d(prevMotionX, prevMotionY, prevMotionZ);
//
//
        //this.setPosition(this.posX, this.posY, this.posZ);
//
        //if (checkProjectiles) {
        //    logger.info("Remote?: {}", world.isRemote);
        //}
        if (!world.isRemote) {
        //    if (checkProjectiles) {
        //        logger.info("Entity ID: {}", this.getEntityId());
        //    }
        //    if (checkProjectiles) {
        //        logger.info("SENDING PROPER VELOCITY UPDATES FOR ENTITY {}", this.getEntityId());
        //    }
        //    prevPosX = posX;
        //    prevPosY = posY;
        //    prevPosZ = posZ;
            world.getMinecraftServer().getWorld(this.dimension).getEntityTracker().sendToTracking(this, new SPacketEntityTeleport(this));
        //    xPosPrevQuiet = xPosQuiet;
        //    yPosPrevQuiet = yPosQuiet;
        //    zPosPrevQuiet = zPosQuiet;
        //    xPosQuiet = posX;
        //    yPosQuiet = posY;
        //    zPosQuiet = posZ;
        //    motionVector = new Vector3d(posX - prevPosX, posY - prevPosY, posZ - prevPosZ);
        }
//
//
        //if (checkProjectiles) {
        //    logger.info("xvel: {} yvel: {} zvel:{}", motionX, motionY, motionZ);
        //}

        previousRoll = roll;
        roll += rollSpeed;

    }


    @Override
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        if (checkProjectiles) {
            logger.info("Shoot event triggered.");
        }
        yaw = rotationYawIn;
        pitch = rotationPitchIn;
        isServer = true;
        this.entityThrower = entityThrower;
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;


        if (!entityThrower.onGround) {
            this.motionY += entityThrower.motionY;
        }
        this.velocityPrivate = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
    }
}
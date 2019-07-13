package com.teamabnormals.upgrade_aquatic.client.particle;

import com.teamabnormals.upgrade_aquatic.common.blocks.BlockJellyTorch.JellyTorchType;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleJellyTorch extends UAParticle {
	private final float rotSpeed;
	private final float scale;
	private final JellyTorchType torchType;
    private final int MAX_FRAME_ID = 2;
    protected int currentFrame = 0;
    private boolean directionRight = true;
    private int lastTick = 0;

	public ParticleJellyTorch(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int torchType) {
        super(world, posX, posY, posZ, motionX, motionY, motionZ);
        this.motionX = this.motionX * 0.009999999776482582d + motionX;
        this.motionY = this.motionY * 0.009999999776482582d + motionY;
        this.motionZ = this.motionZ * 0.009999999776482582d + motionZ;
        this.scale = this.particleScale = 0.25F;
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.particleAngle = (float)Math.random() * ((float)Math.PI * 2F);
        this.particleGravity = 0.0F;
        this.particleRed = 1f;
        this.particleGreen = 1f;
        this.particleBlue = 1f;
        this.maxAge = (int) (12d / (Math.random() * 0.8d + 0.2d)) + 4;
        this.torchType = JellyTorchType.values()[torchType];
    }
	
	@Override
    public void move(double x, double y, double z) {
    	super.move(x, y, z);
    }

    @Override
    public void onPreRender(BufferBuilder buffer, ActiveRenderInfo activeInfo, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.onPreRender(buffer, activeInfo, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    	Entity entity = activeInfo.getRenderViewEntity();
        if (entity.ticksExisted >= this.lastTick + 5) {
            if (this.currentFrame == MAX_FRAME_ID) {
                this.directionRight = false;
            } else if (currentFrame == 0) {
                this.directionRight = true;
            }
            this.currentFrame = this.currentFrame + (directionRight ? 1 : -1);
            this.lastTick = entity.ticksExisted;
        }
        float f = ((float) this.age + partialTicks) / (float) this.maxAge;
        this.particleScale = this.scale * (1f - f * f * 0.5f);
    }
    
    @Override
    public void tick() {
    	super.tick();
    	this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;
        if (this.onGround) {
           this.prevParticleAngle = this.particleAngle = 0.0F;
        }
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        float f = ((float) this.age + partialTick) / (float) this.maxAge;
        f = MathHelper.clamp(f, 0f, 1f);
        int i = super.getBrightnessForRender(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15f * 16f);
        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }
    
    @Override
    ResourceLocation getTexture() {
        return UAParticleSprites.JELLY_TORCHES.get(torchType).get(currentFrame);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticle {
    	
        @Override
        public Particle makeParticle(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... params) {
            return new ParticleJellyTorch(world, x, y, z, xSpeed, ySpeed, zSpeed, params.length > 0 ? params[0] : world.rand.nextInt(JellyTorchType.values().length));
        }
        
    }
}

package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleItemPickup extends Particle {
   private final Entity item;
   private final Entity target;
   private int age;
   private final int maxAge;
   private final float yOffset;
   private final RenderManager renderManager = Minecraft.getInstance().getRenderManager();

   public ParticleItemPickup(World p_i1233_1_, Entity p_i1233_2_, Entity p_i1233_3_, float p_i1233_4_) {
      super(p_i1233_1_, p_i1233_2_.posX, p_i1233_2_.posY, p_i1233_2_.posZ, p_i1233_2_.motionX, p_i1233_2_.motionY, p_i1233_2_.motionZ);
      this.item = p_i1233_2_;
      this.target = p_i1233_3_;
      this.maxAge = 3;
      this.yOffset = p_i1233_4_;
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = ((float)this.age + p_180434_3_) / (float)this.maxAge;
      f = f * f;
      double d0 = this.item.posX;
      double d1 = this.item.posY;
      double d2 = this.item.posZ;
      double d3 = this.target.lastTickPosX + (this.target.posX - this.target.lastTickPosX) * (double)p_180434_3_;
      double d4 = this.target.lastTickPosY + (this.target.posY - this.target.lastTickPosY) * (double)p_180434_3_ + (double)this.yOffset;
      double d5 = this.target.lastTickPosZ + (this.target.posZ - this.target.lastTickPosZ) * (double)p_180434_3_;
      double d6 = d0 + (d3 - d0) * (double)f;
      double d7 = d1 + (d4 - d1) * (double)f;
      double d8 = d2 + (d5 - d2) * (double)f;
      int i = this.getBrightnessForRender(p_180434_3_);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      d6 = d6 - interpPosX;
      d7 = d7 - interpPosY;
      d8 = d8 - interpPosZ;
      GlStateManager.enableLighting();
      this.renderManager.renderEntity(this.item, d6, d7, d8, this.item.rotationYaw, p_180434_3_, false);
   }

   public void tick() {
      ++this.age;
      if (this.age == this.maxAge) {
         this.setExpired();
      }

   }

   public int getFXLayer() {
      return 3;
   }
}

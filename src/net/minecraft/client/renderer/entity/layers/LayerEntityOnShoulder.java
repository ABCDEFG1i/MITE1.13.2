package net.minecraft.client.renderer.entity.layers;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelParrot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerEntityOnShoulder implements LayerRenderer<EntityPlayer> {
   private final RenderManager renderManager;
   protected RenderLivingBase<? extends EntityLivingBase> leftRenderer;
   private ModelBase leftModel;
   private ResourceLocation leftResource;
   private UUID leftUniqueId;
   private EntityType<?> leftEntityClass;
   protected RenderLivingBase<? extends EntityLivingBase> rightRenderer;
   private ModelBase rightModel;
   private ResourceLocation rightResource;
   private UUID rightUniqueId;
   private EntityType<?> rightEntityClass;

   public LayerEntityOnShoulder(RenderManager p_i47370_1_) {
      this.renderManager = p_i47370_1_;
   }

   public void doRenderLayer(EntityPlayer p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (p_177141_1_.getLeftShoulderEntity() != null || p_177141_1_.getRightShoulderEntity() != null) {
         GlStateManager.enableRescaleNormal();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         NBTTagCompound nbttagcompound = p_177141_1_.getLeftShoulderEntity();
         if (!nbttagcompound.isEmpty()) {
            LayerEntityOnShoulder.DataHolder layerentityonshoulder$dataholder = this.func_200695_a(p_177141_1_, this.leftUniqueId, nbttagcompound, this.leftRenderer, this.leftModel, this.leftResource, this.leftEntityClass, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, true);
            this.leftUniqueId = layerentityonshoulder$dataholder.entityId;
            this.leftRenderer = layerentityonshoulder$dataholder.renderer;
            this.leftResource = layerentityonshoulder$dataholder.textureLocation;
            this.leftModel = layerentityonshoulder$dataholder.model;
            this.leftEntityClass = layerentityonshoulder$dataholder.field_200698_e;
         }

         NBTTagCompound nbttagcompound1 = p_177141_1_.getRightShoulderEntity();
         if (!nbttagcompound1.isEmpty()) {
            LayerEntityOnShoulder.DataHolder layerentityonshoulder$dataholder1 = this.func_200695_a(p_177141_1_, this.rightUniqueId, nbttagcompound1, this.rightRenderer, this.rightModel, this.rightResource, this.rightEntityClass, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, false);
            this.rightUniqueId = layerentityonshoulder$dataholder1.entityId;
            this.rightRenderer = layerentityonshoulder$dataholder1.renderer;
            this.rightResource = layerentityonshoulder$dataholder1.textureLocation;
            this.rightModel = layerentityonshoulder$dataholder1.model;
            this.rightEntityClass = layerentityonshoulder$dataholder1.field_200698_e;
         }

         GlStateManager.disableRescaleNormal();
      }
   }

   private LayerEntityOnShoulder.DataHolder func_200695_a(EntityPlayer p_200695_1_, @Nullable UUID p_200695_2_, NBTTagCompound p_200695_3_, RenderLivingBase<? extends EntityLivingBase> p_200695_4_, ModelBase p_200695_5_, ResourceLocation p_200695_6_, EntityType<?> p_200695_7_, float p_200695_8_, float p_200695_9_, float p_200695_10_, float p_200695_11_, float p_200695_12_, float p_200695_13_, float p_200695_14_, boolean p_200695_15_) {
      if (p_200695_2_ == null || !p_200695_2_.equals(p_200695_3_.getUniqueId("UUID"))) {
         p_200695_2_ = p_200695_3_.getUniqueId("UUID");
         p_200695_7_ = EntityType.getById(p_200695_3_.getString("id"));
         if (p_200695_7_ == EntityType.PARROT) {
            p_200695_4_ = new RenderParrot(this.renderManager);
            p_200695_5_ = new ModelParrot();
            p_200695_6_ = RenderParrot.PARROT_TEXTURES[p_200695_3_.getInteger("Variant")];
         }
      }

      p_200695_4_.bindTexture(p_200695_6_);
      GlStateManager.pushMatrix();
      float f = p_200695_1_.isSneaking() ? -1.3F : -1.5F;
      float f1 = p_200695_15_ ? 0.4F : -0.4F;
      GlStateManager.translatef(f1, f, 0.0F);
      if (p_200695_7_ == EntityType.PARROT) {
         p_200695_11_ = 0.0F;
      }

      p_200695_5_.setLivingAnimations(p_200695_1_, p_200695_8_, p_200695_9_, p_200695_10_);
      p_200695_5_.setRotationAngles(p_200695_8_, p_200695_9_, p_200695_11_, p_200695_12_, p_200695_13_, p_200695_14_, p_200695_1_);
      p_200695_5_.render(p_200695_1_, p_200695_8_, p_200695_9_, p_200695_11_, p_200695_12_, p_200695_13_, p_200695_14_);
      GlStateManager.popMatrix();
      return new LayerEntityOnShoulder.DataHolder(p_200695_2_, p_200695_4_, p_200695_5_, p_200695_6_, p_200695_7_);
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   class DataHolder {
      public UUID entityId;
      public RenderLivingBase<? extends EntityLivingBase> renderer;
      public ModelBase model;
      public ResourceLocation textureLocation;
      public EntityType<?> field_200698_e;

      public DataHolder(UUID p_i48600_2_, RenderLivingBase<? extends EntityLivingBase> p_i48600_3_, ModelBase p_i48600_4_, ResourceLocation p_i48600_5_, EntityType<?> p_i48600_6_) {
         this.entityId = p_i48600_2_;
         this.renderer = p_i48600_3_;
         this.model = p_i48600_4_;
         this.textureLocation = p_i48600_5_;
         this.field_200698_e = p_i48600_6_;
      }
   }
}

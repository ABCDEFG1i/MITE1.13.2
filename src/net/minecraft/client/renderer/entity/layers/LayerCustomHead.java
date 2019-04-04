package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
   private final ModelRenderer modelRenderer;

   public LayerCustomHead(ModelRenderer p_i46120_1_) {
      this.modelRenderer = p_i46120_1_;
   }

   public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      ItemStack itemstack = p_177141_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
      if (!itemstack.isEmpty()) {
         Item item = itemstack.getItem();
         Minecraft minecraft = Minecraft.getInstance();
         GlStateManager.pushMatrix();
         if (p_177141_1_.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         boolean flag = p_177141_1_ instanceof EntityVillager || p_177141_1_ instanceof EntityZombieVillager;
         if (p_177141_1_.isChild() && !(p_177141_1_ instanceof EntityVillager)) {
            float f = 2.0F;
            float f1 = 1.4F;
            GlStateManager.translatef(0.0F, 0.5F * p_177141_8_, 0.0F);
            GlStateManager.scalef(0.7F, 0.7F, 0.7F);
            GlStateManager.translatef(0.0F, 16.0F * p_177141_8_, 0.0F);
         }

         this.modelRenderer.postRender(0.0625F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockAbstractSkull) {
            float f3 = 1.1875F;
            GlStateManager.scalef(1.1875F, -1.1875F, -1.1875F);
            if (flag) {
               GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameprofile = null;
            if (itemstack.hasTag()) {
               NBTTagCompound nbttagcompound = itemstack.getTag();
               if (nbttagcompound.hasKey("SkullOwner", 10)) {
                  gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
               } else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                  String s = nbttagcompound.getString("SkullOwner");
                  if (!StringUtils.isBlank(s)) {
                     gameprofile = TileEntitySkull.updateGameProfile(new GameProfile(null, s));
                     nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                  }
               }
            }

            TileEntitySkullRenderer.instance.render(-0.5F, 0.0F, -0.5F, null, 180.0F, ((BlockAbstractSkull)((ItemBlock)item).getBlock()).getSkullType(), gameprofile, -1, p_177141_2_);
         } else if (!(item instanceof ItemArmor) || ((ItemArmor)item).getEquipmentSlot() != EntityEquipmentSlot.HEAD) {
            float f2 = 0.625F;
            GlStateManager.translatef(0.0F, -0.25F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scalef(0.625F, -0.625F, -0.625F);
            if (flag) {
               GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
            }

            minecraft.getFirstPersonRenderer().func_178099_a(p_177141_1_, itemstack, ItemCameraTransforms.TransformType.HEAD);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}

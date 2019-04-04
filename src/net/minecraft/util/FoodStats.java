package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoodStats {
    private float foodExhaustionLevel;
    private int foodLevel = 20;
    private float foodSaturationLevel;
    private int foodTimer;
    private int maxFoodLevel = 6;
    private int prevFoodLevel = 20;

    public FoodStats() {
        this.foodSaturationLevel = 5.0F;
    }

    public void addExhaustion(float p_75113_1_) {
        //MITEMODDED Easier to be hungry
        p_75113_1_ *= 1.5;
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + p_75113_1_, 40.0F);
    }

    public void addStats(int foodLevel, float foodSaturationLevel) {
        this.foodLevel = Math.min(foodLevel + this.foodLevel, this.maxFoodLevel);
        //MITEMODDED changed to make the food saturation can big than the food level
        // Origin:
        // this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float) p_75122_1_ * p_75122_2_ * 2.0F,
        //                                            (float) this.foodLevel);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + foodSaturationLevel, this.maxFoodLevel);
    }

    public void addStats(ItemFood p_151686_1_, ItemStack p_151686_2_) {
        this.addStats(p_151686_1_.getHealAmount(p_151686_2_), p_151686_1_.getSaturationModifier(p_151686_2_));
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(int p_75114_1_) {
        this.foodLevel = p_75114_1_;
    }

    public int getMaxFoodLevel() {
        return this.maxFoodLevel;
    }

    public void setMaxFoodLevel(int maxFoodLevel) {
        this.maxFoodLevel = maxFoodLevel;
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    //MITEMODDED Changed to make always can eat food
    public boolean needFood() {
        return true;
    }

    public void readNBT(NBTTagCompound p_75112_1_) {
        if (p_75112_1_.hasKey("foodLevel", 99)) {
            this.foodLevel = p_75112_1_.getInteger("foodLevel");
            this.foodTimer = p_75112_1_.getInteger("foodTickTimer");
            this.foodSaturationLevel = p_75112_1_.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = p_75112_1_.getFloat("foodExhaustionLevel");
            this.maxFoodLevel = p_75112_1_.getInteger("maxFoodLevel");
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void setFoodSaturationLevel(float p_75119_1_) {
        this.foodSaturationLevel = p_75119_1_;
    }

    public void tick(EntityPlayer player) {
        EnumDifficulty enumdifficulty = player.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.foodExhaustionLevel > 4.0F) {
            this.foodExhaustionLevel -= 4.0F;
            if (this.foodSaturationLevel > 0.0F) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            } else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean isNaturalRegeneration = player.world.getGameRules().getBoolean("naturalRegeneration");
        //MITEMODDED Remove to make the health regeneration slowly
        //      if (isNaturalRegeneration && this.foodSaturationLevel > 0.0F && player.shouldHeal() && this.foodLevel >= maxFoodLevel) {
        //         ++this.foodTimer;
        //         if (this.foodTimer >= 10) {
        //            float f = Math.min(this.foodSaturationLevel, 6.0F);
        //            player.heal(f / 6.0F);
        //            this.addExhaustion(f);
        //            this.foodTimer = 0;
        //         }
        //      } else

        ++this.foodTimer;
        //MITEMODDED Remove the health's food level requirement
        // Origin:
        //    if (isNaturalRegeneration && this.foodLevel >= 18 && player.shouldHeal()) {
        if (isNaturalRegeneration && player.shouldHeal()) {
            if (this.foodTimer >= player.getNaturalHealSpeed()) {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTimer = 0;
            }
        }
        if (this.foodLevel <= 0 && this.foodSaturationLevel <= 0) {
            if (this.foodTimer >= 120) {
                //MITEMODDED Changed to make you always can hunger to die
                // Origin:
                // if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player
                //                        .getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
                //                    player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                //                }
                player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                this.foodTimer = 0;
            }
        } else {
            this.foodTimer = 0;
        }

    }

    public void writeNBT(NBTTagCompound p_75117_1_) {
        p_75117_1_.setInteger("foodLevel", this.foodLevel);
        p_75117_1_.setInteger("foodTickTimer", this.foodTimer);
        p_75117_1_.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        p_75117_1_.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
        p_75117_1_.setInteger("maxFoodLevel", this.maxFoodLevel);
    }
}

package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class DamageSourceNetherBed extends DamageSource {
   protected DamageSourceNetherBed() {
      super("netherBed");
      this.setDifficultyScaled();
      this.setExplosion();
   }

   public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_) {
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TextComponentTranslation("death.attack.netherBed.link")).applyTextStyle((p_211694_0_) -> {
         p_211694_0_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("MCPE-28723")));
      });
      return new TextComponentTranslation("death.attack.netherBed.message", p_151519_1_.getDisplayName(), itextcomponent);
   }
}

package net.minecraft.world.border;

public interface IBorderListener {
   void onSizeChanged(WorldBorder p_177694_1_, double p_177694_2_);

   void onTransitionStarted(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_);

   void onCenterChanged(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_);

   void onWarningTimeChanged(WorldBorder p_177691_1_, int p_177691_2_);

   void onWarningDistanceChanged(WorldBorder p_177690_1_, int p_177690_2_);

   void onDamageAmountChanged(WorldBorder p_177696_1_, double p_177696_2_);

   void onDamageBufferChanged(WorldBorder p_177695_1_, double p_177695_2_);
}

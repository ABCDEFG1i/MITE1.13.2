package net.minecraft.client.tutorial;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITutorialStep {
   default void onStop() {
   }

   default void tick() {
   }

   default void handleMovement(MovementInput p_193247_1_) {
   }

   default void onMouseMove(double p_195870_1_, double p_195870_3_) {
   }

   default void onMouseHover(WorldClient p_193246_1_, RayTraceResult p_193246_2_) {
   }

   default void onHitBlock(WorldClient p_193250_1_, BlockPos p_193250_2_, IBlockState p_193250_3_, float p_193250_4_) {
   }

   default void openInventory() {
   }

   default void handleSetSlot(ItemStack p_193252_1_) {
   }
}

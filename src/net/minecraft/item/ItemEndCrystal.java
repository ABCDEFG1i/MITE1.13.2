package net.minecraft.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEndCrystal extends Item {
   public ItemEndCrystal(Item.Properties p_i48503_1_) {
      super(p_i48503_1_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (iblockstate.getBlock() != Blocks.OBSIDIAN && iblockstate.getBlock() != Blocks.BEDROCK) {
         return EnumActionResult.FAIL;
      } else {
         BlockPos blockpos1 = blockpos.up();
         if (!world.isAirBlock(blockpos1)) {
            return EnumActionResult.FAIL;
         } else {
            double d0 = (double)blockpos1.getX();
            double d1 = (double)blockpos1.getY();
            double d2 = (double)blockpos1.getZ();
            List<Entity> list = world.func_72839_b(null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return EnumActionResult.FAIL;
            } else {
               if (!world.isRemote) {
                  EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world, d0 + 0.5D, d1, d2 + 0.5D);
                  entityendercrystal.setShowBottom(false);
                  world.spawnEntity(entityendercrystal);
                  if (world.dimension instanceof EndDimension) {
                     DragonFightManager dragonfightmanager = ((EndDimension)world.dimension).getDragonFightManager();
                     dragonfightmanager.tryRespawnDragon();
                  }
               }

               p_195939_1_.getItem().shrink(1);
               return EnumActionResult.SUCCESS;
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }
}

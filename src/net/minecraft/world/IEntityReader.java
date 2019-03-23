package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader {
   List<Entity> func_175674_a(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_);

   default List<Entity> func_72839_b(@Nullable Entity p_72839_1_, AxisAlignedBB p_72839_2_) {
      return this.func_175674_a(p_72839_1_, p_72839_2_, EntitySelectors.NOT_SPECTATING);
   }

   default Stream<VoxelShape> func_211155_a(@Nullable Entity p_211155_1_, VoxelShape p_211155_2_, Set<Entity> p_211155_3_) {
      if (p_211155_2_.isEmpty()) {
         return Stream.empty();
      } else {
         AxisAlignedBB axisalignedbb = p_211155_2_.getBoundingBox();
         return this.func_72839_b(p_211155_1_, axisalignedbb.grow(0.25D)).stream().filter((p_212382_2_) -> {
            return !p_211155_3_.contains(p_212382_2_) && (p_211155_1_ == null || !p_211155_1_.isRidingSameEntity(p_212382_2_));
         }).flatMap((p_212380_2_) -> {
            return Stream.of(p_212380_2_.getCollisionBoundingBox(), p_211155_1_ == null ? null : p_211155_1_.getCollisionBox(p_212380_2_)).filter(Objects::nonNull).filter((p_212381_1_) -> {
               return p_212381_1_.intersects(axisalignedbb);
            }).map(VoxelShapes::func_197881_a);
         });
      }
   }
}

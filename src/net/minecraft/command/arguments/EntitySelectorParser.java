package net.minecraft.command.arguments;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class EntitySelectorParser {
   public static final SimpleCommandExceptionType INVALID_ENTITY_NAME_OR_UUID = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.invalid"));
   public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((p_208703_0_) -> {
      return new TextComponentTranslation("argument.entity.selector.unknown", p_208703_0_);
   });
   public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.not_allowed"));
   public static final SimpleCommandExceptionType SELECTOR_TYPE_MISSING = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.missing"));
   public static final SimpleCommandExceptionType EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.options.unterminated"));
   public static final DynamicCommandExceptionType EXPECTED_VALUE_FOR_OPTION = new DynamicCommandExceptionType((p_208711_0_) -> {
      return new TextComponentTranslation("argument.entity.options.valueless", p_208711_0_);
   });
   public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (p_197402_0_, p_197402_1_) -> {
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST = (p_197392_0_, p_197392_1_) -> {
      p_197392_1_.sort((p_197393_1_, p_197393_2_) -> {
         return Doubles.compare(p_197393_1_.getDistanceSq(p_197392_0_), p_197393_2_.getDistanceSq(p_197392_0_));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST = (p_197383_0_, p_197383_1_) -> {
      p_197383_1_.sort((p_197369_1_, p_197369_2_) -> {
         return Doubles.compare(p_197369_2_.getDistanceSq(p_197383_0_), p_197369_1_.getDistanceSq(p_197383_0_));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM = (p_197368_0_, p_197368_1_) -> {
      Collections.shuffle(p_197368_1_);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NONE = (p_201342_0_, p_201342_1_) -> {
      return p_201342_0_.buildFuture();
   };
   private final StringReader reader;
   private final boolean hasPermission;
   private int limit;
   private boolean includeNonPlayers;
   private boolean currentWorldOnly;
   private MinMaxBounds.FloatBound distance = MinMaxBounds.FloatBound.UNBOUNDED;
   private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private Double z;
   @Nullable
   private Double dx;
   @Nullable
   private Double dy;
   @Nullable
   private Double dz;
   private MinMaxBoundsWrapped xRotation = MinMaxBoundsWrapped.UNBOUNDED;
   private MinMaxBoundsWrapped yRotation = MinMaxBoundsWrapped.UNBOUNDED;
   private Predicate<Entity> filter = (p_197375_0_) -> {
      return true;
   };
   private BiConsumer<Vec3d, List<? extends Entity>> sorter = ARBITRARY;
   private boolean self;
   @Nullable
   private String username;
   private int cursorStart;
   @Nullable
   private UUID uuid;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandler = SUGGEST_NONE;
   private boolean field_202000_F;
   private boolean field_202001_G;
   private boolean field_202002_H;
   private boolean field_202003_I;
   private boolean field_202004_J;
   private boolean field_202005_K;
   private boolean field_202006_L;
   private boolean field_202007_M;
   private Class<? extends Entity> type;
   private boolean field_202009_O;
   private boolean field_202010_P;
   private boolean field_202011_Q;
   private boolean checkPermission;

   public EntitySelectorParser(StringReader p_i47958_1_) {
      this(p_i47958_1_, true);
   }

   public EntitySelectorParser(StringReader p_i49550_1_, boolean p_i49550_2_) {
      this.reader = p_i49550_1_;
      this.hasPermission = p_i49550_2_;
   }

   public EntitySelector build() {
      AxisAlignedBB axisalignedbb;
      if (this.dx == null && this.dy == null && this.dz == null) {
         if (this.distance.func_196977_b() != null) {
            float f = this.distance.func_196977_b();
            axisalignedbb = new AxisAlignedBB((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
         } else {
            axisalignedbb = null;
         }
      } else {
         axisalignedbb = this.func_197390_a(this.dx == null ? 0.0D : this.dx, this.dy == null ? 0.0D : this.dy, this.dz == null ? 0.0D : this.dz);
      }

      Function<Vec3d, Vec3d> function;
      if (this.x == null && this.y == null && this.z == null) {
         function = (p_197379_0_) -> {
            return p_197379_0_;
         };
      } else {
         function = (p_197367_1_) -> {
            return new Vec3d(this.x == null ? p_197367_1_.x : this.x, this.y == null ? p_197367_1_.y : this.y, this.z == null ? p_197367_1_.z : this.z);
         };
      }

      return new EntitySelector(this.limit, this.includeNonPlayers, this.currentWorldOnly, this.filter, this.distance, function, axisalignedbb, this.sorter, this.self, this.username, this.uuid, this.type == null ? Entity.class : this.type, this.checkPermission);
   }

   private AxisAlignedBB func_197390_a(double p_197390_1_, double p_197390_3_, double p_197390_5_) {
      boolean flag = p_197390_1_ < 0.0D;
      boolean flag1 = p_197390_3_ < 0.0D;
      boolean flag2 = p_197390_5_ < 0.0D;
      double d0 = flag ? p_197390_1_ : 0.0D;
      double d1 = flag1 ? p_197390_3_ : 0.0D;
      double d2 = flag2 ? p_197390_5_ : 0.0D;
      double d3 = (flag ? 0.0D : p_197390_1_) + 1.0D;
      double d4 = (flag1 ? 0.0D : p_197390_3_) + 1.0D;
      double d5 = (flag2 ? 0.0D : p_197390_5_) + 1.0D;
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public void updateFilter() {
      if (this.xRotation != MinMaxBoundsWrapped.UNBOUNDED) {
         this.filter = this.filter.and(this.boundsToPredicate(this.xRotation, (p_197386_0_) -> {
            return (double)p_197386_0_.rotationPitch;
         }));
      }

      if (this.yRotation != MinMaxBoundsWrapped.UNBOUNDED) {
         this.filter = this.filter.and(this.boundsToPredicate(this.yRotation, (p_197385_0_) -> {
            return (double)p_197385_0_.rotationYaw;
         }));
      }

      if (!this.level.isUnbounded()) {
         this.filter = this.filter.and((p_197371_1_) -> {
            return !(p_197371_1_ instanceof EntityPlayerMP) ? false : this.level.test(((EntityPlayerMP)p_197371_1_).experienceLevel);
         });
      }

   }

   private Predicate<Entity> boundsToPredicate(MinMaxBoundsWrapped p_197366_1_, ToDoubleFunction<Entity> p_197366_2_) {
      double d0 = (double)MathHelper.wrapDegrees(p_197366_1_.getMin() == null ? 0.0F : p_197366_1_.getMin());
      double d1 = (double)MathHelper.wrapDegrees(p_197366_1_.getMax() == null ? 359.0F : p_197366_1_.getMax());
      return (p_197374_5_) -> {
         double d2 = MathHelper.wrapDegrees(p_197366_2_.applyAsDouble(p_197374_5_));
         if (d0 > d1) {
            return d2 >= d0 || d2 <= d1;
         } else {
            return d2 >= d0 && d2 <= d1;
         }
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.checkPermission = true;
      this.suggestionHandler = this::func_201959_d;
      if (!this.reader.canRead()) {
         throw SELECTOR_TYPE_MISSING.createWithContext(this.reader);
      } else {
         int i = this.reader.getCursor();
         char c0 = this.reader.read();
         if (c0 == 'p') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = NEAREST;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (c0 == 'a') {
            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = false;
            this.sorter = ARBITRARY;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (c0 == 'r') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = RANDOM;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (c0 == 's') {
            this.limit = 1;
            this.includeNonPlayers = true;
            this.self = true;
         } else {
            if (c0 != 'e') {
               this.reader.setCursor(i);
               throw UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(c0));
            }

            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = true;
            this.sorter = ARBITRARY;
            this.filter = Entity::isEntityAlive;
         }

         this.suggestionHandler = this::suggestOpenBracket;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestionHandler = this::suggestOptionsOrEnd;
            this.parseArguments();
         }

      }
   }

   protected void parseSingleEntity() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestionHandler = this::func_201974_c;
      }

      int i = this.reader.getCursor();
      String s = this.reader.readString();

      try {
         this.uuid = UUID.fromString(s);
         this.includeNonPlayers = true;
      } catch (IllegalArgumentException var4) {
         if (s.isEmpty() || s.length() > 16) {
            this.reader.setCursor(i);
            throw INVALID_ENTITY_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includeNonPlayers = false;
         this.username = s;
      }

      this.limit = 1;
   }

   public void parseArguments() throws CommandSyntaxException {
      this.suggestionHandler = this::suggestOptions;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            EntityOptions.Filter entityoptions$filter = EntityOptions.func_202017_a(this, s, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(i);
               throw EXPECTED_VALUE_FOR_OPTION.createWithContext(this.reader, s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestionHandler = SUGGEST_NONE;
            entityoptions$filter.handle(this);
            this.reader.skipWhitespace();
            this.suggestionHandler = this::suggestCommaOrEnd;
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestionHandler = this::suggestOptions;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestionHandler = SUGGEST_NONE;
            return;
         }

         throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean func_197378_e() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addFilter(Predicate<Entity> p_197401_1_) {
      this.filter = this.filter.and(p_197401_1_);
   }

   public void setCurrentWorldOnly() {
      this.currentWorldOnly = true;
   }

   public MinMaxBounds.FloatBound getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.FloatBound p_197397_1_) {
      this.distance = p_197397_1_;
   }

   public MinMaxBounds.IntBound getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.IntBound p_197399_1_) {
      this.level = p_197399_1_;
   }

   public MinMaxBoundsWrapped getXRotation() {
      return this.xRotation;
   }

   public void setXRotation(MinMaxBoundsWrapped p_197389_1_) {
      this.xRotation = p_197389_1_;
   }

   public MinMaxBoundsWrapped getYRotation() {
      return this.yRotation;
   }

   public void setYRotation(MinMaxBoundsWrapped p_197387_1_) {
      this.yRotation = p_197387_1_;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public Double getZ() {
      return this.z;
   }

   public void setX(double p_197384_1_) {
      this.x = p_197384_1_;
   }

   public void setY(double p_197395_1_) {
      this.y = p_197395_1_;
   }

   public void setZ(double p_197372_1_) {
      this.z = p_197372_1_;
   }

   public void setDx(double p_197377_1_) {
      this.dx = p_197377_1_;
   }

   public void setDy(double p_197391_1_) {
      this.dy = p_197391_1_;
   }

   public void setDz(double p_197405_1_) {
      this.dz = p_197405_1_;
   }

   @Nullable
   public Double getDx() {
      return this.dx;
   }

   @Nullable
   public Double getDy() {
      return this.dy;
   }

   @Nullable
   public Double getDz() {
      return this.dz;
   }

   public void setLimit(int p_197388_1_) {
      this.limit = p_197388_1_;
   }

   public void setIncludeNonPlayers(boolean p_197373_1_) {
      this.includeNonPlayers = p_197373_1_;
   }

   public void setSorter(BiConsumer<Vec3d, List<? extends Entity>> p_197376_1_) {
      this.sorter = p_197376_1_;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.cursorStart = this.reader.getCursor();
      this.suggestionHandler = this::suggestSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.hasPermission) {
            throw SELECTOR_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         this.parseSelector();
      } else {
         this.parseSingleEntity();
      }

      this.updateFilter();
      return this.build();
   }

   private static void suggestSelector(SuggestionsBuilder p_210326_0_) {
      p_210326_0_.suggest("@p", new TextComponentTranslation("argument.entity.selector.nearestPlayer"));
      p_210326_0_.suggest("@a", new TextComponentTranslation("argument.entity.selector.allPlayers"));
      p_210326_0_.suggest("@r", new TextComponentTranslation("argument.entity.selector.randomPlayer"));
      p_210326_0_.suggest("@s", new TextComponentTranslation("argument.entity.selector.self"));
      p_210326_0_.suggest("@e", new TextComponentTranslation("argument.entity.selector.allEntities"));
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder p_201981_1_, Consumer<SuggestionsBuilder> p_201981_2_) {
      p_201981_2_.accept(p_201981_1_);
      if (this.hasPermission) {
         suggestSelector(p_201981_1_);
      }

      return p_201981_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201974_c(SuggestionsBuilder p_201974_1_, Consumer<SuggestionsBuilder> p_201974_2_) {
      SuggestionsBuilder suggestionsbuilder = p_201974_1_.createOffset(this.cursorStart);
      p_201974_2_.accept(suggestionsbuilder);
      return p_201974_1_.add(suggestionsbuilder).buildFuture();
   }

   private CompletableFuture<Suggestions> func_201959_d(SuggestionsBuilder p_201959_1_, Consumer<SuggestionsBuilder> p_201959_2_) {
      SuggestionsBuilder suggestionsbuilder = p_201959_1_.createOffset(p_201959_1_.getStart() - 1);
      suggestSelector(suggestionsbuilder);
      p_201959_1_.add(suggestionsbuilder);
      return p_201959_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenBracket(SuggestionsBuilder p_201989_1_, Consumer<SuggestionsBuilder> p_201989_2_) {
      p_201989_1_.suggest(String.valueOf('['));
      return p_201989_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsOrEnd(SuggestionsBuilder p_201996_1_, Consumer<SuggestionsBuilder> p_201996_2_) {
      p_201996_1_.suggest(String.valueOf(']'));
      EntityOptions.suggestOptions(this, p_201996_1_);
      return p_201996_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptions(SuggestionsBuilder p_201994_1_, Consumer<SuggestionsBuilder> p_201994_2_) {
      EntityOptions.suggestOptions(this, p_201994_1_);
      return p_201994_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder p_201969_1_, Consumer<SuggestionsBuilder> p_201969_2_) {
      p_201969_1_.suggest(String.valueOf(','));
      p_201969_1_.suggest(String.valueOf(']'));
      return p_201969_1_.buildFuture();
   }

   public boolean func_197381_m() {
      return this.self;
   }

   public void setSuggestionHandler(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> p_201978_1_) {
      this.suggestionHandler = p_201978_1_;
   }

   public CompletableFuture<Suggestions> func_201993_a(SuggestionsBuilder p_201993_1_, Consumer<SuggestionsBuilder> p_201993_2_) {
      return this.suggestionHandler.apply(p_201993_1_.createOffset(this.reader.getCursor()), p_201993_2_);
   }

   public boolean func_201984_u() {
      return this.field_202000_F;
   }

   public void func_201990_c(boolean p_201990_1_) {
      this.field_202000_F = p_201990_1_;
   }

   public boolean func_201997_v() {
      return this.field_202001_G;
   }

   public void func_201998_d(boolean p_201998_1_) {
      this.field_202001_G = p_201998_1_;
   }

   public boolean func_201967_w() {
      return this.field_202002_H;
   }

   public void func_201979_e(boolean p_201979_1_) {
      this.field_202002_H = p_201979_1_;
   }

   public boolean func_201976_x() {
      return this.field_202003_I;
   }

   public void func_201986_f(boolean p_201986_1_) {
      this.field_202003_I = p_201986_1_;
   }

   public boolean func_201987_y() {
      return this.field_202004_J;
   }

   public void func_201988_g(boolean p_201988_1_) {
      this.field_202004_J = p_201988_1_;
   }

   public boolean func_201961_z() {
      return this.field_202005_K;
   }

   public void func_201973_h(boolean p_201973_1_) {
      this.field_202005_K = p_201973_1_;
   }

   public boolean func_201960_A() {
      return this.field_202006_L;
   }

   public void func_201975_i(boolean p_201975_1_) {
      this.field_202006_L = p_201975_1_;
   }

   public void func_201958_j(boolean p_201958_1_) {
      this.field_202007_M = p_201958_1_;
   }

   public void func_201964_a(Class<? extends Entity> p_201964_1_) {
      this.type = p_201964_1_;
   }

   public void func_201982_C() {
      this.field_202009_O = true;
   }

   public boolean func_201963_E() {
      return this.type != null;
   }

   public boolean func_201985_F() {
      return this.field_202009_O;
   }

   public boolean func_201995_G() {
      return this.field_202010_P;
   }

   public void func_201970_k(boolean p_201970_1_) {
      this.field_202010_P = p_201970_1_;
   }

   public boolean func_201966_H() {
      return this.field_202011_Q;
   }

   public void func_201992_l(boolean p_201992_1_) {
      this.field_202011_Q = p_201992_1_;
   }
}

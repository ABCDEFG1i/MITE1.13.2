package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
   public static final ResourceLocation field_207763_a = new ResourceLocation("block/fire_0");
   public static final ResourceLocation field_207764_b = new ResourceLocation("block/fire_1");
   public static final ResourceLocation field_207766_d = new ResourceLocation("block/lava_flow");
   public static final ResourceLocation field_207768_f = new ResourceLocation("block/water_flow");
   public static final ResourceLocation field_207769_g = new ResourceLocation("block/water_overlay");
   public static final ResourceLocation field_207770_h = new ResourceLocation("block/destroy_stage_0");
   public static final ResourceLocation field_207771_i = new ResourceLocation("block/destroy_stage_1");
   public static final ResourceLocation field_207772_j = new ResourceLocation("block/destroy_stage_2");
   public static final ResourceLocation field_207773_k = new ResourceLocation("block/destroy_stage_3");
   public static final ResourceLocation field_207774_l = new ResourceLocation("block/destroy_stage_4");
   public static final ResourceLocation field_207775_m = new ResourceLocation("block/destroy_stage_5");
   public static final ResourceLocation field_207776_n = new ResourceLocation("block/destroy_stage_6");
   public static final ResourceLocation field_207777_o = new ResourceLocation("block/destroy_stage_7");
   public static final ResourceLocation field_207778_p = new ResourceLocation("block/destroy_stage_8");
   public static final ResourceLocation field_207779_q = new ResourceLocation("block/destroy_stage_9");
   protected static final Set<ResourceLocation> field_177602_b = Sets.newHashSet(field_207768_f, field_207766_d, field_207769_g, field_207763_a, field_207764_b, field_207770_h, field_207771_i, field_207772_j, field_207773_k, field_207774_l, field_207775_m, field_207776_n, field_207777_o, field_207778_p, field_207779_q, new ResourceLocation("item/empty_armor_slot_helmet"), new ResourceLocation("item/empty_armor_slot_chestplate"), new ResourceLocation("item/empty_armor_slot_leggings"), new ResourceLocation("item/empty_armor_slot_boots"), new ResourceLocation("item/empty_armor_slot_shield"));
   private static final Logger field_177603_c = LogManager.getLogger();
   public static final ModelResourceLocation field_177604_a = new ModelResourceLocation("builtin/missing", "missing");
   @VisibleForTesting
   public static final String field_188641_d = ("{    'textures': {       'particle': '" + MissingTextureSprite.getSprite().getName().getPath() + "',       'missingno': '" + MissingTextureSprite.getSprite().getName().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
   private static final Map<String, String> field_177600_d = Maps.newHashMap(ImmutableMap.of("missing", field_188641_d));
   private static final Splitter field_209611_w = Splitter.on(',');
   private static final Splitter field_209612_x = Splitter.on('=').limit(2);
   protected static final ModelBlock field_177606_o = Util.make(ModelBlock.func_178294_a("{}"), (p_209273_0_) -> {
      p_209273_0_.field_178317_b = "generation marker";
   });
   protected static final ModelBlock field_177616_r = Util.make(ModelBlock.func_178294_a("{}"), (p_209274_0_) -> {
      p_209274_0_.field_178317_b = "block entity marker";
   });
   private static final StateContainer<Block, IBlockState> field_209613_y = (new StateContainer.Builder<Block, IBlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).create(BlockState::new);
   protected final IResourceManager field_177598_f;
   protected final TextureMap field_177609_j;
   protected final Map<ModelResourceLocation, IBakedModel> field_177605_n = Maps.newHashMap();
   private static final Map<ResourceLocation, StateContainer<Block, IBlockState>> field_209607_C = ImmutableMap.of(new ResourceLocation("item_frame"), field_209613_y);
   private final Map<ResourceLocation, IUnbakedModel> field_209608_D = Maps.newHashMap();
   private final Set<ResourceLocation> field_209609_E = Sets.newHashSet();
   private final ModelBlockDefinition.ContainerHolder field_209610_F = new ModelBlockDefinition.ContainerHolder();

   public ModelBakery(IResourceManager p_i49521_1_, TextureMap p_i49521_2_) {
      this.field_177598_f = p_i49521_1_;
      this.field_177609_j = p_i49521_2_;
   }

   private static Predicate<IBlockState> func_209605_a(StateContainer<Block, IBlockState> p_209605_0_, String p_209605_1_) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap();

      for(String s : field_209611_w.split(p_209605_1_)) {
         Iterator<String> iterator = field_209612_x.split(s).iterator();
         if (iterator.hasNext()) {
            String s1 = iterator.next();
            IProperty<?> iproperty = p_209605_0_.getProperty(s1);
            if (iproperty != null && iterator.hasNext()) {
               String s2 = iterator.next();
               Comparable<?> comparable = func_209592_a(iproperty, s2);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + iproperty.getAllowedValues());
               }

               map.put(iproperty, comparable);
            } else if (!s1.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
            }
         }
      }

      Block block = p_209605_0_.getOwner();
      return (p_209606_2_) -> {
         if (p_209606_2_ != null && block == p_209606_2_.getBlock()) {
            for(Entry<IProperty<?>, Comparable<?>> entry : map.entrySet()) {
               if (!Objects.equals(p_209606_2_.get(entry.getKey()), entry.getValue())) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      };
   }

   @Nullable
   static <T extends Comparable<T>> T func_209592_a(IProperty<T> p_209592_0_, String p_209592_1_) {
      return p_209592_0_.parseValue(p_209592_1_).orElse(null);
   }

   public IUnbakedModel func_209597_a(ResourceLocation p_209597_1_) {
      if (this.field_209608_D.containsKey(p_209597_1_)) {
         return this.field_209608_D.get(p_209597_1_);
      } else if (this.field_209609_E.contains(p_209597_1_)) {
         throw new IllegalStateException("Circular reference while loading " + p_209597_1_);
      } else {
         this.field_209609_E.add(p_209597_1_);
         IUnbakedModel iunbakedmodel = this.field_209608_D.get(field_177604_a);

         while(!this.field_209609_E.isEmpty()) {
            ResourceLocation resourcelocation = this.field_209609_E.iterator().next();

            try {
               if (!this.field_209608_D.containsKey(resourcelocation)) {
                  this.func_209598_b(resourcelocation);
               }
            } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
               field_177603_c.warn(modelbakery$blockstatedefinitionexception.getMessage());
               this.field_209608_D.put(resourcelocation, iunbakedmodel);
            } catch (Exception exception) {
               field_177603_c.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, p_209597_1_, exception);
               this.field_209608_D.put(resourcelocation, iunbakedmodel);
            } finally {
               this.field_209609_E.remove(resourcelocation);
            }
         }

         return this.field_209608_D.getOrDefault(p_209597_1_, iunbakedmodel);
      }
   }

   private void func_209598_b(ResourceLocation p_209598_1_) throws Exception {
      if (!(p_209598_1_ instanceof ModelResourceLocation)) {
         this.func_209593_a(p_209598_1_, this.func_177594_c(p_209598_1_));
      } else {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_209598_1_;
         if (Objects.equals(modelresourcelocation.func_177518_c(), "inventory")) {
            ResourceLocation resourcelocation2 = new ResourceLocation(p_209598_1_.getNamespace(), "item/" + p_209598_1_.getPath());
            ModelBlock modelblock = this.func_177594_c(resourcelocation2);
            this.func_209593_a(modelresourcelocation, modelblock);
            this.field_209608_D.put(resourcelocation2, modelblock);
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(p_209598_1_.getNamespace(), p_209598_1_.getPath());
            StateContainer<Block, IBlockState> statecontainer = Optional.ofNullable(field_209607_C.get(resourcelocation)).orElseGet(() -> {
               return IRegistry.field_212618_g.func_82594_a(resourcelocation).getStateContainer();
            });
            this.field_209610_F.func_209573_a(statecontainer);
            ImmutableList<IBlockState> immutablelist = statecontainer.getValidStates();
            Map<ModelResourceLocation, IBlockState> map = Maps.newHashMap();
            immutablelist.forEach((p_209587_2_) -> {
               IBlockState iblockstate = map.put(BlockModelShapes.func_209553_a(resourcelocation, p_209587_2_), p_209587_2_);
            });
            Map<IBlockState, IUnbakedModel> map1 = Maps.newHashMap();
            ResourceLocation resourcelocation1 = new ResourceLocation(p_209598_1_.getNamespace(), "blockstates/" + p_209598_1_.getPath() + ".json");
            boolean flag = false;

            label160: {
               try {
                  label161: {
                     List<Pair<String, ModelBlockDefinition>> lvt_9_4_;
                     try {
                        flag = true;
                        lvt_9_4_ = this.field_177598_f.getAllResources(resourcelocation1).stream().map((p_209591_1_) -> {
                           try (InputStream inputstream = p_209591_1_.getInputStream()) {
                              return Pair.of(p_209591_1_.getPackName(), ModelBlockDefinition.func_209577_a(this.field_209610_F, new InputStreamReader(inputstream, StandardCharsets.UTF_8)));
                           } catch (Exception exception1) {
                              throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", p_209591_1_.getLocation(), p_209591_1_.getPackName(), exception1.getMessage()));
                           }
                        }).collect(Collectors.toList());
                     } catch (IOException ioexception) {
                        field_177603_c.warn("Exception loading blockstate definition: {}: {}", resourcelocation1, ioexception);
                        flag = false;
                        break label161;
                     }

                     for(Pair<String, ModelBlockDefinition> pair : lvt_9_4_) {
                        ModelBlockDefinition modelblockdefinition = pair.getSecond();
                        Map<IBlockState, IUnbakedModel> map2 = Maps.newIdentityHashMap();
                        IUnbakedModel iunbakedmodel;
                        if (modelblockdefinition.func_188002_b()) {
                           iunbakedmodel = modelblockdefinition.func_188001_c();
                           immutablelist.forEach((p_209603_2_) -> {
                              IUnbakedModel iunbakedmodel1 = map2.put(p_209603_2_, iunbakedmodel);
                           });
                        } else {
                           iunbakedmodel = null;
                        }

                        modelblockdefinition.func_209578_a().forEach((p_209589_8_, p_209589_9_) -> {
                           try {
                              immutablelist.stream().filter(func_209605_a(statecontainer, p_209589_8_)).forEach((p_209590_5_) -> {
                                 IUnbakedModel iunbakedmodel1 = map2.put(p_209590_5_, p_209589_9_);
                                 if (iunbakedmodel1 != null && iunbakedmodel1 != iunbakedmodel) {
                                    map2.put(p_209590_5_, this.field_209608_D.get(field_177604_a));
                                    throw new RuntimeException("Overlapping definition with: " + modelblockdefinition.func_209578_a().entrySet().stream().filter((p_209604_1_) -> {
                                       return p_209604_1_.getValue() == iunbakedmodel1;
                                    }).findFirst().get().getKey());
                                 }
                              });
                           } catch (Exception exception1) {
                              field_177603_c.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair.getFirst(), p_209589_8_, exception1.getMessage());
                           }

                        });
                        map1.putAll(map2);
                     }

                     flag = false;
                     break label160;
                  }
               } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
                  throw modelbakery$blockstatedefinitionexception;
               } catch (Exception exception) {
                  throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourcelocation1, exception));
               } finally {
                  if (flag) {
                     for(Entry<ModelResourceLocation, IBlockState> entry : map.entrySet()) {
                        this.func_209593_a(entry.getKey(), map1.getOrDefault(entry.getValue(), this.field_209608_D.get(field_177604_a)));
                     }

                  }
               }

               for(Entry<ModelResourceLocation, IBlockState> entry2 : map.entrySet()) {
                  this.func_209593_a(entry2.getKey(), map1.getOrDefault(entry2.getValue(), this.field_209608_D.get(field_177604_a)));
               }

               return;
            }

            for(Entry<ModelResourceLocation, IBlockState> entry1 : map.entrySet()) {
               this.func_209593_a(entry1.getKey(), map1.getOrDefault(entry1.getValue(), this.field_209608_D.get(field_177604_a)));
            }
         }

      }
   }

   private void func_209593_a(ResourceLocation p_209593_1_, IUnbakedModel p_209593_2_) {
      this.field_209608_D.put(p_209593_1_, p_209593_2_);
      this.field_209609_E.addAll(p_209593_2_.func_187965_e());
   }

   private void func_209594_a(Map<ModelResourceLocation, IUnbakedModel> p_209594_1_, ModelResourceLocation p_209594_2_) {
      p_209594_1_.put(p_209594_2_, this.func_209597_a(p_209594_2_));
   }

   public Map<ModelResourceLocation, IBakedModel> func_177570_a() {
      Map<ModelResourceLocation, IUnbakedModel> map = Maps.newHashMap();

      try {
         this.field_209608_D.put(field_177604_a, this.func_177594_c(field_177604_a));
         this.func_209594_a(map, field_177604_a);
      } catch (IOException ioexception) {
         field_177603_c.error("Error loading missing model, should never happen :(", ioexception);
         throw new RuntimeException(ioexception);
      }

      field_209607_C.forEach((p_209602_2_, p_209602_3_) -> {
         p_209602_3_.getValidStates().forEach((p_209601_3_) -> {
            this.func_209594_a(map, BlockModelShapes.func_209553_a(p_209602_2_, p_209601_3_));
         });
      });

      for(Block block : IRegistry.field_212618_g) {
         block.getStateContainer().getValidStates().forEach((p_209600_2_) -> {
            this.func_209594_a(map, BlockModelShapes.func_209554_c(p_209600_2_));
         });
      }

      for(ResourceLocation resourcelocation : IRegistry.field_212630_s.func_148742_b()) {
         this.func_209594_a(map, new ModelResourceLocation(resourcelocation, "inventory"));
      }

      this.func_209594_a(map, new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      Set<String> set = Sets.newLinkedHashSet();
      Set<ResourceLocation> set1 = map.values().stream().flatMap((p_209595_2_) -> {
         return p_209595_2_.func_209559_a(this::func_209597_a, set).stream();
      }).collect(Collectors.toSet());
      set1.addAll(field_177602_b);
      set.forEach((p_209588_0_) -> {
         field_177603_c.warn("Unable to resolve texture reference: {}", p_209588_0_);
      });
      this.field_177609_j.stitch(this.field_177598_f, set1);
      map.forEach((p_209599_1_, p_209599_2_) -> {
         IBakedModel ibakedmodel = null;

         try {
            ibakedmodel = p_209599_2_.func_209558_a(this::func_209597_a, this.field_177609_j::getSprite, ModelRotation.X0_Y0, false);
         } catch (Exception exception) {
            field_177603_c.warn("Unable to bake model: '{}': {}", p_209599_1_, exception);
         }

         if (ibakedmodel != null) {
            this.field_177605_n.put(p_209599_1_, ibakedmodel);
         }

      });
      return this.field_177605_n;
   }

   protected ModelBlock func_177594_c(ResourceLocation p_177594_1_) throws IOException {
      Reader reader = null;
      IResource iresource = null;

      ModelBlock lvt_5_2_;
      try {
         String s = p_177594_1_.getPath();
         if (!"builtin/generated".equals(s)) {
            if ("builtin/entity".equals(s)) {
               lvt_5_2_ = field_177616_r;
               return lvt_5_2_;
            }

            if (s.startsWith("builtin/")) {
               String s2 = s.substring("builtin/".length());
               String s1 = field_177600_d.get(s2);
               if (s1 == null) {
                  throw new FileNotFoundException(p_177594_1_.toString());
               }

               reader = new StringReader(s1);
            } else {
               iresource = this.field_177598_f.getResource(new ResourceLocation(p_177594_1_.getNamespace(), "models/" + p_177594_1_.getPath() + ".json"));
               reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
            }

            lvt_5_2_ = ModelBlock.func_178307_a(reader);
            lvt_5_2_.field_178317_b = p_177594_1_.toString();
            ModelBlock modelblock1 = lvt_5_2_;
            return modelblock1;
         }

         lvt_5_2_ = field_177606_o;
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly(iresource);
      }

      return lvt_5_2_;
   }

   @OnlyIn(Dist.CLIENT)
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String p_i49526_1_) {
         super(p_i49526_1_);
      }
   }
}

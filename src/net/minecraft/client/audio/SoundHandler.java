package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundHandler implements ITickable, IResourceManagerReloadListener {
   public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
   private final SoundManager sndManager;
   private final IResourceManager resourceManager;

   public SoundHandler(IResourceManager p_i45122_1_, GameSettings p_i45122_2_) {
      this.resourceManager = p_i45122_1_;
      this.sndManager = new SoundManager(this, p_i45122_2_);
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.soundRegistry.clear();

      for(String s : p_195410_1_.getResourceNamespaces()) {
         try {
            for(IResource iresource : p_195410_1_.getAllResources(new ResourceLocation(s, "sounds.json"))) {
               try {
                  Map<String, SoundList> map = this.getSoundMap(iresource.getInputStream());

                  for(Entry<String, SoundList> entry : map.entrySet()) {
                     this.loadSoundResource(new ResourceLocation(s, entry.getKey()), entry.getValue());
                  }
               } catch (RuntimeException runtimeexception) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getPackName(), runtimeexception);
               }
            }
         } catch (IOException var11) {
         }
      }

      for(ResourceLocation resourcelocation : this.soundRegistry.keySet()) {
         SoundEventAccessor soundeventaccessor = this.soundRegistry.get(resourcelocation);
         if (soundeventaccessor.getSubtitle() instanceof TextComponentTranslation) {
            String s1 = ((TextComponentTranslation)soundeventaccessor.getSubtitle()).getKey();
            if (!I18n.hasKey(s1)) {
               LOGGER.debug("Missing subtitle {} for event: {}", s1, resourcelocation);
            }
         }
      }

      for(ResourceLocation resourcelocation1 : this.soundRegistry.keySet()) {
         if (IRegistry.field_212633_v.func_212608_b(resourcelocation1) == null) {
            LOGGER.debug("Not having sound event for: {}", resourcelocation1);
         }
      }

      this.sndManager.reloadSoundSystem();
   }

   @Nullable
   protected Map<String, SoundList> getSoundMap(InputStream p_175085_1_) {
      Map map;
      try {
         map = JsonUtils.fromJson(GSON, new InputStreamReader(p_175085_1_, StandardCharsets.UTF_8), TYPE);
      } finally {
         IOUtils.closeQuietly(p_175085_1_);
      }

      return map;
   }

   private void loadSoundResource(ResourceLocation p_147693_1_, SoundList p_147693_2_) {
      SoundEventAccessor soundeventaccessor = this.soundRegistry.get(p_147693_1_);
      boolean flag = soundeventaccessor == null;
      if (flag || p_147693_2_.canReplaceExisting()) {
         if (!flag) {
            LOGGER.debug("Replaced sound event location {}", p_147693_1_);
         }

         soundeventaccessor = new SoundEventAccessor(p_147693_1_, p_147693_2_.getSubtitle());
         this.soundRegistry.put(p_147693_1_, soundeventaccessor);
      }

      for(final Sound sound : p_147693_2_.getSounds()) {
         final ResourceLocation resourcelocation = sound.getSoundLocation();
         ISoundEventAccessor<Sound> isoundeventaccessor;
         switch(sound.getType()) {
         case FILE:
            if (!this.validateSoundResource(sound, p_147693_1_)) {
               continue;
            }

            isoundeventaccessor = sound;
            break;
         case SOUND_EVENT:
            isoundeventaccessor = new ISoundEventAccessor<Sound>() {
               public int getWeight() {
                  SoundEventAccessor soundeventaccessor1 = SoundHandler.this.soundRegistry.get(resourcelocation);
                  return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
               }

               public Sound cloneEntry() {
                  SoundEventAccessor soundeventaccessor1 = SoundHandler.this.soundRegistry.get(resourcelocation);
                  if (soundeventaccessor1 == null) {
                     return SoundHandler.MISSING_SOUND;
                  } else {
                     Sound sound1 = soundeventaccessor1.cloneEntry();
                     return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                  }
               }
            };
            break;
         default:
            throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
         }

         if (isoundeventaccessor.cloneEntry().shouldPreload()) {
            this.sndManager.enqueuePreload(isoundeventaccessor.cloneEntry());
         }

         soundeventaccessor.addSound(isoundeventaccessor);
      }

   }

   private boolean validateSoundResource(Sound p_184401_1_, ResourceLocation p_184401_2_) {
      ResourceLocation resourcelocation = p_184401_1_.getSoundAsOggLocation();
      IResource iresource = null;

      boolean flag;
      try {
         iresource = this.resourceManager.getResource(resourcelocation);
         iresource.getInputStream();
         return true;
      } catch (FileNotFoundException var11) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_184401_2_);
         flag = false;
      } catch (IOException ioexception) {
         LOGGER.warn("Could not load sound file {}, cannot add it to event {}", resourcelocation, p_184401_2_, ioexception);
         flag = false;
         return flag;
      } finally {
         IOUtils.closeQuietly(iresource);
      }

      return flag;
   }

   @Nullable
   public SoundEventAccessor getAccessor(ResourceLocation p_184398_1_) {
      return this.soundRegistry.get(p_184398_1_);
   }

   public Collection<ResourceLocation> func_195477_a() {
      return this.soundRegistry.keySet();
   }

   public void play(ISound p_147682_1_) {
      this.sndManager.play(p_147682_1_);
   }

   public void playDelayed(ISound p_147681_1_, int p_147681_2_) {
      this.sndManager.playDelayedSound(p_147681_1_, p_147681_2_);
   }

   public void setListener(EntityPlayer p_147691_1_, float p_147691_2_) {
      this.sndManager.setListener(p_147691_1_, p_147691_2_);
   }

   public void pause() {
      this.sndManager.pause();
   }

   public void stop() {
      this.sndManager.stopAllSounds();
   }

   public void unloadSounds() {
      this.sndManager.unloadSoundSystem();
   }

   public void tick() {
      this.sndManager.tick();
   }

   public void resume() {
      this.sndManager.resume();
   }

   public void setSoundLevel(SoundCategory p_184399_1_, float p_184399_2_) {
      if (p_184399_1_ == SoundCategory.MASTER && p_184399_2_ <= 0.0F) {
         this.stop();
      }

      this.sndManager.setVolume(p_184399_1_, p_184399_2_);
   }

   public void stop(ISound p_147683_1_) {
      this.sndManager.stop(p_147683_1_);
   }

   public boolean isPlaying(ISound p_147692_1_) {
      return this.sndManager.isPlaying(p_147692_1_);
   }

   public void addListener(ISoundEventListener p_184402_1_) {
      this.sndManager.addListener(p_184402_1_);
   }

   public void removeListener(ISoundEventListener p_184400_1_) {
      this.sndManager.removeListener(p_184400_1_);
   }

   public void stop(@Nullable ResourceLocation p_195478_1_, @Nullable SoundCategory p_195478_2_) {
      this.sndManager.stop(p_195478_1_, p_195478_2_);
   }
}

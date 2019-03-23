package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.gen.area.IArea;

public abstract class LayerContext<R extends IArea> implements IContextExtended<R> {
   private long seed;
   private long positionHash;
   protected long seedModifier;
   protected NoiseGeneratorImproved noiseGenerator;

   public LayerContext(long p_i48648_1_) {
      this.seedModifier = p_i48648_1_;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += p_i48648_1_;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += p_i48648_1_;
      this.seedModifier *= this.seedModifier * 6364136223846793005L + 1442695040888963407L;
      this.seedModifier += p_i48648_1_;
   }

   public void setSeed(long p_202699_1_) {
      this.seed = p_202699_1_;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedModifier;
      this.noiseGenerator = new NoiseGeneratorImproved(new Random(p_202699_1_));
   }

   public void setPosition(long p_202698_1_, long p_202698_3_) {
      this.positionHash = this.seed;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += p_202698_1_;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += p_202698_3_;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += p_202698_1_;
      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += p_202698_3_;
   }

   public int random(int p_202696_1_) {
      int i = (int)((this.positionHash >> 24) % (long)p_202696_1_);
      if (i < 0) {
         i += p_202696_1_;
      }

      this.positionHash *= this.positionHash * 6364136223846793005L + 1442695040888963407L;
      this.positionHash += this.seed;
      return i;
   }

   public int selectRandomly(int... p_202697_1_) {
      return p_202697_1_[this.random(p_202697_1_.length)];
   }

   public NoiseGeneratorImproved getNoiseGenerator() {
      return this.noiseGenerator;
   }
}

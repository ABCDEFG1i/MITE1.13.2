package net.minecraft.block;

public abstract class BlockStemGrown extends Block {
   public BlockStemGrown(Block.Properties p_i48317_1_) {
      super(p_i48317_1_);
   }

   public abstract BlockStem getStem();

   public abstract BlockAttachedStem getAttachedStem();
}

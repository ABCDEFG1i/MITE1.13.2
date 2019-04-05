package net.minecraft.block;

public class BlockMaterial extends Block implements IMineLevel {
    private int mineLevel;
    public BlockMaterial(Properties p_i48440_1_,int mineLevel) {
        super(p_i48440_1_);
        this.mineLevel = mineLevel;
    }

    @Override
    public int getMineLevel() {
        return mineLevel;
    }
}

package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityAnvil extends TileEntity {

    private int damage;


    public TileEntityAnvil() {
        super(TileEntityType.ANVIL);
    }

    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        if (p_145839_1_.hasKey("Damage")) {
            this.damage = p_145839_1_.getInteger("Damage");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
        super.writeToNBT(p_189515_1_);
        p_189515_1_.setInteger("Damage", damage);
        return p_189515_1_;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}

package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SPacketUpdateHealth implements Packet<INetHandlerPlayClient> {
    private int foodLevel;
    private float health;
    private int maxFoodLevel;
    private float saturationLevel;

    public SPacketUpdateHealth() {
    }

    public SPacketUpdateHealth(float health, int foodLevel, float saturationLevel, int maxFoodLevel) {
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturationLevel = saturationLevel;
        this.maxFoodLevel = maxFoodLevel;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFoodLevel() {
        return this.foodLevel;
    }

    @OnlyIn(Dist.CLIENT)
    public float getHealth() {
        return this.health;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxFoodLevel() {
        return maxFoodLevel;
    }

    @OnlyIn(Dist.CLIENT)
    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
        this.health = p_148837_1_.readFloat();
        this.foodLevel = p_148837_1_.readVarInt();
        this.saturationLevel = p_148837_1_.readFloat();
        this.maxFoodLevel = p_148837_1_.readVarInt();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeFloat(this.health);
        p_148840_1_.writeVarInt(this.foodLevel);
        p_148840_1_.writeFloat(this.saturationLevel);
        p_148840_1_.writeInt(this.maxFoodLevel);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleUpdateHealth(this);
    }
}

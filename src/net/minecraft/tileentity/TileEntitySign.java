package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySign extends TileEntity implements ICommandSource {
   public final ITextComponent[] signText = new ITextComponent[]{new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
   public int lineBeingEdited = -1;
   private boolean isEditable = true;
   private EntityPlayer player;
   private final String[] field_212367_h = new String[4];

   public TileEntitySign() {
      super(TileEntityType.SIGN);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);

      for(int i = 0; i < 4; ++i) {
         String s = ITextComponent.Serializer.toJson(this.signText[i]);
         p_189515_1_.setString("Text" + (i + 1), s);
      }

      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      this.isEditable = false;
      super.readFromNBT(p_145839_1_);

      for(int i = 0; i < 4; ++i) {
         String s = p_145839_1_.getString("Text" + (i + 1));
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
         if (this.world instanceof WorldServer) {
            try {
               this.signText[i] = TextComponentUtils.func_197680_a(this.getCommandSource((EntityPlayerMP)null), itextcomponent, (Entity)null);
            } catch (CommandSyntaxException var6) {
               this.signText[i] = itextcomponent;
            }
         } else {
            this.signText[i] = itextcomponent;
         }

         this.field_212367_h[i] = null;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_212366_a(int p_212366_1_) {
      return this.signText[p_212366_1_];
   }

   public void func_212365_a(int p_212365_1_, ITextComponent p_212365_2_) {
      this.signText[p_212365_1_] = p_212365_2_;
      this.field_212367_h[p_212365_1_] = null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String func_212364_a(int p_212364_1_, Function<ITextComponent, String> p_212364_2_) {
      if (this.field_212367_h[p_212364_1_] == null && this.signText[p_212364_1_] != null) {
         this.field_212367_h[p_212364_1_] = p_212364_2_.apply(this.signText[p_212364_1_]);
      }

      return this.field_212367_h[p_212364_1_];
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   @OnlyIn(Dist.CLIENT)
   public void setEditable(boolean p_145913_1_) {
      this.isEditable = p_145913_1_;
      if (!p_145913_1_) {
         this.player = null;
      }

   }

   public void setPlayer(EntityPlayer p_145912_1_) {
      this.player = p_145912_1_;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(EntityPlayer p_174882_1_) {
      for(ITextComponent itextcomponent : this.signText) {
         Style style = itextcomponent == null ? null : itextcomponent.getStyle();
         if (style != null && style.getClickEvent() != null) {
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               p_174882_1_.getServer().getCommandManager().handleCommand(this.getCommandSource((EntityPlayerMP)p_174882_1_), clickevent.getValue());
            }
         }
      }

      return true;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
   }

   public CommandSource getCommandSource(@Nullable EntityPlayerMP p_195539_1_) {
      String s = p_195539_1_ == null ? "Sign" : p_195539_1_.getName().getString();
      ITextComponent itextcomponent = (ITextComponent)(p_195539_1_ == null ? new TextComponentString("Sign") : p_195539_1_.getDisplayName());
      return new CommandSource(this, new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D), Vec2f.ZERO, (WorldServer)this.world, 2, s, itextcomponent, this.world.getServer(), p_195539_1_);
   }

   public boolean shouldReceiveFeedback() {
      return false;
   }

   public boolean shouldReceiveErrors() {
      return false;
   }

   public boolean allowLogging() {
      return false;
   }
}

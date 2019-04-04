package net.minecraft.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CommandBlockBaseLogic implements ICommandSource {
   private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   private ITextComponent lastOutput;
   private String commandStored = "";
   private ITextComponent customName = new TextComponentString("@");

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int p_184167_1_) {
      this.successCount = p_184167_1_;
   }

   public ITextComponent getLastOutput() {
      return this.lastOutput == null ? new TextComponentString("") : this.lastOutput;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189510_1_) {
      p_189510_1_.setString("Command", this.commandStored);
      p_189510_1_.setInteger("SuccessCount", this.successCount);
      p_189510_1_.setString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      p_189510_1_.setBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         p_189510_1_.setString("LastOutput", ITextComponent.Serializer.toJson(this.lastOutput));
      }

      p_189510_1_.setBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         p_189510_1_.setLong("LastExecution", this.lastExecution);
      }

      return p_189510_1_;
   }

   public void readDataFromNBT(NBTTagCompound p_145759_1_) {
      this.commandStored = p_145759_1_.getString("Command");
      this.successCount = p_145759_1_.getInteger("SuccessCount");
      if (p_145759_1_.hasKey("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145759_1_.getString("CustomName"));
      }

      if (p_145759_1_.hasKey("TrackOutput", 1)) {
         this.trackOutput = p_145759_1_.getBoolean("TrackOutput");
      }

      if (p_145759_1_.hasKey("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = ITextComponent.Serializer.fromJson(p_145759_1_.getString("LastOutput"));
         } catch (Throwable throwable) {
            this.lastOutput = new TextComponentString(throwable.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if (p_145759_1_.hasKey("UpdateLastExecution")) {
         this.updateLastExecution = p_145759_1_.getBoolean("UpdateLastExecution");
      }

      if (this.updateLastExecution && p_145759_1_.hasKey("LastExecution")) {
         this.lastExecution = p_145759_1_.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }

   }

   public void setCommand(String p_145752_1_) {
      this.commandStored = p_145752_1_;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.commandStored;
   }

   public boolean trigger(World p_145755_1_) {
      if (!p_145755_1_.isRemote && p_145755_1_.getTotalWorldTime() != this.lastExecution) {
         if ("Searge".equalsIgnoreCase(this.commandStored)) {
            this.lastOutput = new TextComponentString("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer minecraftserver = this.getWorld().getServer();
            if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled() && !StringUtils.isNullOrEmpty(this.commandStored)) {
               try {
                  this.lastOutput = null;
                  CommandSource commandsource = this.getCommandSource().withResultConsumer((p_209527_1_, p_209527_2_, p_209527_3_) -> {
                     if (p_209527_2_) {
                        ++this.successCount;
                     }

                  });
                  minecraftserver.getCommandManager().handleCommand(commandsource, this.commandStored);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Executing command block");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
                  crashreportcategory.addDetail("Command", this::getCommand);
                  crashreportcategory.addDetail("Name", () -> {
                     return this.getName().getString();
                  });
                  throw new ReportedException(crashreport);
               }
            }

            if (this.updateLastExecution) {
               this.lastExecution = p_145755_1_.getTotalWorldTime();
            } else {
               this.lastExecution = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ITextComponent getName() {
      return this.customName;
   }

   public void setName(ITextComponent p_207405_1_) {
      this.customName = p_207405_1_;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      if (this.trackOutput) {
         this.lastOutput = (new TextComponentString("[" + TIMESTAMP_FORMAT.format(new Date()) + "] ")).appendSibling(p_145747_1_);
         this.updateCommand();
      }

   }

   public abstract WorldServer getWorld();

   public abstract void updateCommand();

   public void setLastOutput(@Nullable ITextComponent p_145750_1_) {
      this.lastOutput = p_145750_1_;
   }

   public void setTrackOutput(boolean p_175573_1_) {
      this.trackOutput = p_175573_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean tryOpenEditCommandBlock(EntityPlayer p_175574_1_) {
      if (!p_175574_1_.canUseCommandBlock()) {
         return false;
      } else {
         if (p_175574_1_.getEntityWorld().isRemote) {
            p_175574_1_.displayGuiEditCommandCart(this);
         }

         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getPositionVector();

   public abstract CommandSource getCommandSource();

   public boolean shouldReceiveFeedback() {
      return this.getWorld().getGameRules().getBoolean("sendCommandFeedback") && this.trackOutput;
   }

   public boolean shouldReceiveErrors() {
      return this.trackOutput;
   }

   public boolean allowLogging() {
      return this.getWorld().getGameRules().getBoolean("commandBlockOutput");
   }
}

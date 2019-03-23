package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.SlotArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class ReplaceItemCommand {
   private static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.replaceitem.block.failed"));
   private static final DynamicCommandExceptionType INAPPLICABLE_SLOT_EXCEPTION = new DynamicCommandExceptionType((p_211409_0_) -> {
      return new TextComponentTranslation("commands.replaceitem.slot.inapplicable", p_211409_0_);
   });
   private static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((p_211411_0_, p_211411_1_) -> {
      return new TextComponentTranslation("commands.replaceitem.entity.failed", p_211411_0_, p_211411_1_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198602_0_) {
      p_198602_0_.register(Commands.literal("replaceitem").requires((p_198607_0_) -> {
         return p_198607_0_.hasPermissionLevel(2);
      }).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.itemSlot()).then(Commands.argument("item", ItemArgument.itemStack()).executes((p_198601_0_) -> {
         return replaceItemBlock(p_198601_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198601_0_, "pos"), SlotArgument.getSlot(p_198601_0_, "slot"), ItemArgument.getItemStack(p_198601_0_, "item").createStack(1, false));
      }).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198605_0_) -> {
         return replaceItemBlock(p_198605_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198605_0_, "pos"), SlotArgument.getSlot(p_198605_0_, "slot"), ItemArgument.getItemStack(p_198605_0_, "item").createStack(IntegerArgumentType.getInteger(p_198605_0_, "count"), true));
      })))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.argument("slot", SlotArgument.itemSlot()).then(Commands.argument("item", ItemArgument.itemStack()).executes((p_198600_0_) -> {
         return replaceItemEntities(p_198600_0_.getSource(), EntityArgument.getEntities(p_198600_0_, "targets"), SlotArgument.getSlot(p_198600_0_, "slot"), ItemArgument.getItemStack(p_198600_0_, "item").createStack(1, false));
      }).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((p_198606_0_) -> {
         return replaceItemEntities(p_198606_0_.getSource(), EntityArgument.getEntities(p_198606_0_, "targets"), SlotArgument.getSlot(p_198606_0_, "slot"), ItemArgument.getItemStack(p_198606_0_, "item").createStack(IntegerArgumentType.getInteger(p_198606_0_, "count"), true));
      })))))));
   }

   private static int replaceItemBlock(CommandSource p_198603_0_, BlockPos p_198603_1_, int p_198603_2_, ItemStack p_198603_3_) throws CommandSyntaxException {
      TileEntity tileentity = p_198603_0_.getWorld().getTileEntity(p_198603_1_);
      if (!(tileentity instanceof IInventory)) {
         throw BLOCK_FAILED_EXCEPTION.create();
      } else {
         IInventory iinventory = (IInventory)tileentity;
         if (p_198603_2_ >= 0 && p_198603_2_ < iinventory.getSizeInventory()) {
            iinventory.setInventorySlotContents(p_198603_2_, p_198603_3_);
            p_198603_0_.sendFeedback(new TextComponentTranslation("commands.replaceitem.block.success", p_198603_1_.getX(), p_198603_1_.getY(), p_198603_1_.getZ(), p_198603_3_.getTextComponent()), true);
            return 1;
         } else {
            throw INAPPLICABLE_SLOT_EXCEPTION.create(p_198603_2_);
         }
      }
   }

   private static int replaceItemEntities(CommandSource p_198604_0_, Collection<? extends Entity> p_198604_1_, int p_198604_2_, ItemStack p_198604_3_) throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(p_198604_1_.size());

      for(Entity entity : p_198604_1_) {
         if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)entity).inventoryContainer.detectAndSendChanges();
         }

         if (entity.replaceItemInInventory(p_198604_2_, p_198604_3_.copy())) {
            list.add(entity);
            if (entity instanceof EntityPlayerMP) {
               ((EntityPlayerMP)entity).inventoryContainer.detectAndSendChanges();
            }
         }
      }

      if (list.isEmpty()) {
         throw ENTITY_FAILED_EXCEPTION.create(p_198604_3_.getTextComponent(), p_198604_2_);
      } else {
         if (list.size() == 1) {
            p_198604_0_.sendFeedback(new TextComponentTranslation("commands.replaceitem.entity.success.single", list.iterator().next().getDisplayName(), p_198604_3_.getTextComponent()), true);
         } else {
            p_198604_0_.sendFeedback(new TextComponentTranslation("commands.replaceitem.entity.success.multiple", list.size(), p_198604_3_.getTextComponent()), true);
         }

         return list.size();
      }
   }
}

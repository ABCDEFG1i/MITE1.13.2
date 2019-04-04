package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType SET_POINTS_INVALID_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.experience.set.points.invalid"));

    private static int addExperience(CommandSource p_198448_0_, Collection<? extends EntityPlayerMP> p_198448_1_, int p_198448_2_, ExperienceCommand.Type p_198448_3_) {
        for (EntityPlayerMP entityplayermp : p_198448_1_) {
            p_198448_3_.xpAdder.accept(entityplayermp, p_198448_2_);
        }

        if (p_198448_1_.size() == 1) {
            p_198448_0_.sendFeedback(
                    new TextComponentTranslation("commands.experience.add." + p_198448_3_.name + ".success.single",
                            p_198448_2_, p_198448_1_.iterator().next().getDisplayName()), true);
        } else {
            p_198448_0_.sendFeedback(
                    new TextComponentTranslation("commands.experience.add." + p_198448_3_.name + ".success.multiple",
                            p_198448_2_, p_198448_1_.size()), true);
        }

        return p_198448_1_.size();
    }

    private static int queryExperience(CommandSource p_198443_0_, EntityPlayerMP p_198443_1_, ExperienceCommand.Type p_198443_2_) {
        int i = p_198443_2_.xpGetter.applyAsInt(p_198443_1_);
        p_198443_0_.sendFeedback(new TextComponentTranslation("commands.experience.query." + p_198443_2_.name,
                p_198443_1_.getDisplayName(), i), false);
        return i;
    }

    public static void register(CommandDispatcher<CommandSource> p_198437_0_) {
        LiteralCommandNode<CommandSource> literalcommandnode = p_198437_0_.register(
                Commands.literal("experience").requires((p_198442_0_) -> {
                    return p_198442_0_.hasPermissionLevel(2);
                }).then(Commands.literal("add").then(
                        Commands.argument("targets", EntityArgument.multiplePlayers()).then(
                                Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                                    return addExperience(p_198445_0_.getSource(),
                                            EntityArgument.getPlayers(p_198445_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198445_0_, "amount"),
                                            ExperienceCommand.Type.POINTS);
                                }).then(Commands.literal("points").executes((p_198447_0_) -> {
                                    return addExperience(p_198447_0_.getSource(),
                                            EntityArgument.getPlayers(p_198447_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198447_0_, "amount"),
                                            ExperienceCommand.Type.POINTS);
                                })).then(Commands.literal("levels").executes((p_198436_0_) -> {
                                    return addExperience(p_198436_0_.getSource(),
                                            EntityArgument.getPlayers(p_198436_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198436_0_, "amount"),
                                            ExperienceCommand.Type.LEVELS);
                                }))))).then(Commands.literal("set").then(
                        Commands.argument("targets", EntityArgument.multiplePlayers()).then(
                                Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198439_0_) -> {
                                    return setExperience(p_198439_0_.getSource(),
                                            EntityArgument.getPlayers(p_198439_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198439_0_, "amount"),
                                            ExperienceCommand.Type.POINTS);
                                }).then(Commands.literal("points").executes((p_198444_0_) -> {
                                    return setExperience(p_198444_0_.getSource(),
                                            EntityArgument.getPlayers(p_198444_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198444_0_, "amount"),
                                            ExperienceCommand.Type.POINTS);
                                })).then(Commands.literal("levels").executes((p_198440_0_) -> {
                                    return setExperience(p_198440_0_.getSource(),
                                            EntityArgument.getPlayers(p_198440_0_, "targets"),
                                            IntegerArgumentType.getInteger(p_198440_0_, "amount"),
                                            ExperienceCommand.Type.LEVELS);
                                }))))).then(Commands.literal("query").then(
                        Commands.argument("targets", EntityArgument.singlePlayer()).then(
                                Commands.literal("points").executes((p_198435_0_) -> {
                                    return queryExperience(p_198435_0_.getSource(),
                                            EntityArgument.getOnePlayer(p_198435_0_, "targets"),
                                            ExperienceCommand.Type.POINTS);
                                })).then(Commands.literal("levels").executes((p_198446_0_) -> {
                            return queryExperience(p_198446_0_.getSource(),
                                    EntityArgument.getOnePlayer(p_198446_0_, "targets"), ExperienceCommand.Type.LEVELS);
                        })))));
        p_198437_0_.register(Commands.literal("xp").requires((p_198441_0_) -> {
            return p_198441_0_.hasPermissionLevel(2);
        }).redirect(literalcommandnode));
    }

    private static int setExperience(CommandSource p_198438_0_, Collection<? extends EntityPlayerMP> p_198438_1_, int p_198438_2_, ExperienceCommand.Type p_198438_3_) throws
            CommandSyntaxException {
        int i = 0;

        for (EntityPlayerMP entityplayermp : p_198438_1_) {
            if (p_198438_3_.xpSetter.test(entityplayermp, p_198438_2_)) {
                ++i;
            }
        }

        if (i == 0) {
            throw SET_POINTS_INVALID_EXCEPTION.create();
        } else {
            if (p_198438_1_.size() == 1) {
                p_198438_0_.sendFeedback(
                        new TextComponentTranslation("commands.experience.set." + p_198438_3_.name + ".success.single",
                                p_198438_2_, p_198438_1_.iterator().next().getDisplayName()), true);
            } else {
                p_198438_0_.sendFeedback(new TextComponentTranslation(
                        "commands.experience.set." + p_198438_3_.name + ".success.multiple", p_198438_2_,
                        p_198438_1_.size()), true);
            }

            return p_198438_1_.size();
        }
    }

    enum Type {
        POINTS("points", EntityPlayer::addXpValue, (p_198424_0_, p_198424_1_) -> {
            if (p_198424_1_ >= p_198424_0_.xpBarCap()) {
                return false;
            } else {
                p_198424_0_.func_195394_a(p_198424_1_);
                return true;
            }
        }, (p_198422_0_) -> {
            return MathHelper.floor(p_198422_0_.experience * (float) p_198422_0_.xpBarCap());
        }), LEVELS("levels", EntityPlayerMP::addExperienceLevel, (p_198425_0_, p_198425_1_) -> {
            p_198425_0_.func_195399_b(p_198425_1_);
            return true;
        }, (p_198427_0_) -> {
            return p_198427_0_.experienceLevel;
        });

        public final String name;
        public final BiConsumer<EntityPlayerMP, Integer> xpAdder;
        public final BiPredicate<EntityPlayerMP, Integer> xpSetter;
        private final ToIntFunction<EntityPlayerMP> xpGetter;

        Type(String p_i48027_3_, BiConsumer<EntityPlayerMP, Integer> p_i48027_4_, BiPredicate<EntityPlayerMP, Integer> p_i48027_5_, ToIntFunction<EntityPlayerMP> p_i48027_6_) {
            this.xpAdder = p_i48027_4_;
            this.name = p_i48027_3_;
            this.xpSetter = p_i48027_5_;
            this.xpGetter = p_i48027_6_;
        }
    }
}

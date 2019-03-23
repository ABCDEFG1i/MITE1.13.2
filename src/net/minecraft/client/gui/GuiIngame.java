package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiIngame extends Gui {
    protected static final ResourceLocation PUMPKIN_BLUR_TEX_PATH =
            new ResourceLocation("textures/misc/pumpkinblur.png");
    protected static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
    protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
    protected final Map<ChatType, List<IChatListener>> chatListeners = Maps.newHashMap();
    protected final ItemRenderer itemRenderer;
    protected final Minecraft mc;
    protected final GuiBossOverlay overlayBoss;
    protected final GuiOverlayDebug overlayDebug;
    protected final GuiPlayerTabOverlay overlayPlayerList;
    protected final GuiSubtitleOverlay overlaySubtitle;
    protected final GuiNewChat persistantChatGUI;
    protected final Random rand = new Random();
    protected final GuiSpectator spectatorGui;
    protected boolean animateOverlayMessageColor;
    protected String displayedSubTitle = "";
    protected String displayedTitle = "";
    protected long healthUpdateCounter;
    protected ItemStack highlightingItemStack = ItemStack.EMPTY;
    protected int lastPlayerHealth;
    protected long lastSystemTime;
    protected String overlayMessage = "";
    protected int overlayMessageTime;
    protected int playerHealth;
    protected float prevVignetteBrightness = 1.0F;
    protected int remainingHighlightTicks;
    protected int scaledHeight;
    protected int scaledWidth;
    protected int ticks;
    protected int titleDisplayTime;
    protected int titleFadeIn;
    protected int titleFadeOut;
    protected int titlesTimer;

    public GuiIngame(Minecraft p_i46325_1_) {
        this.mc = p_i46325_1_;
        this.itemRenderer = p_i46325_1_.getItemRenderer();
        this.overlayDebug = new GuiOverlayDebug(p_i46325_1_);
        this.spectatorGui = new GuiSpectator(p_i46325_1_);
        this.persistantChatGUI = new GuiNewChat(p_i46325_1_);
        this.overlayPlayerList = new GuiPlayerTabOverlay(p_i46325_1_, this);
        this.overlayBoss = new GuiBossOverlay(p_i46325_1_);
        this.overlaySubtitle = new GuiSubtitleOverlay(p_i46325_1_);

        for (ChatType chattype : ChatType.values()) {
            this.chatListeners.put(chattype, Lists.newArrayList());
        }

        IChatListener ichatlistener = NarratorChatListener.INSTANCE;
        this.chatListeners.get(ChatType.CHAT).add(new NormalChatListener(p_i46325_1_));
        this.chatListeners.get(ChatType.CHAT).add(ichatlistener);
        this.chatListeners.get(ChatType.SYSTEM).add(new NormalChatListener(p_i46325_1_));
        this.chatListeners.get(ChatType.SYSTEM).add(ichatlistener);
        this.chatListeners.get(ChatType.GAME_INFO).add(new OverlayChatListener(p_i46325_1_));
        this.setDefaultTitlesTimes();
    }

    public void addChatMessage(ChatType p_191742_1_, ITextComponent p_191742_2_) {
        for (IChatListener ichatlistener : this.chatListeners.get(p_191742_1_)) {
            ichatlistener.say(p_191742_1_, p_191742_2_);
        }

    }

    public void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_) {
        if (p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (p_175178_1_ != null) {
            this.displayedTitle = p_175178_1_;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (p_175178_2_ != null) {
            this.displayedSubTitle = p_175178_2_;
        } else {
            if (p_175178_3_ >= 0) {
                this.titleFadeIn = p_175178_3_;
            }

            if (p_175178_4_ >= 0) {
                this.titleDisplayTime = p_175178_4_;
            }

            if (p_175178_5_ >= 0) {
                this.titleFadeOut = p_175178_5_;
            }

            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }

        }
    }

    private int func_212302_c(int p_212302_1_) {
        return (int) Math.ceil((double) p_212302_1_ / 10.0D);
    }

    protected void func_212303_b(Entity p_212303_1_) {
        WorldBorder worldborder = this.mc.world.getWorldBorder();
        float f = (float) worldborder.getClosestDistance(p_212303_1_);
        double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D,
                             Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
        double d1 = Math.max((double) worldborder.getWarningDistance(), d0);
        if ((double) f < d1) {
            f = 1.0F - (float) ((double) f / d1);
        } else {
            f = 0.0F;
        }

        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ZERO,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        if (f > 0.0F) {
            GlStateManager.color4f(0.0F, f, f, 1.0F);
        } else {
            GlStateManager.color4f(this.prevVignetteBrightness,
                                   this.prevVignetteBrightness,
                                   this.prevVignetteBrightness,
                                   1.0F);
        }

        this.mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) this.scaledHeight, -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) this.scaledWidth, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
    }

    private EntityPlayer getCurrentPlayer() {
        return !(this.mc.getRenderViewEntity() instanceof EntityPlayer) ? null : (EntityPlayer) this.mc
                .getRenderViewEntity();
    }

    private EntityLivingBase func_212305_n() {
        EntityPlayer entityplayer = this.getCurrentPlayer();
        if (entityplayer != null) {
            Entity entity = entityplayer.getRidingEntity();
            if (entity == null) {
                return null;
            }

            if (entity instanceof EntityLivingBase) {
                return (EntityLivingBase) entity;
            }
        }

        return null;
    }

    private int func_212306_a(EntityLivingBase p_212306_1_) {
        if (p_212306_1_ != null && p_212306_1_.isLiving()) {
            float f = p_212306_1_.getMaxHealth();
            int i = (int) (f + 0.5F) / 2;
            if (i > 30) {
                i = 30;
            }

            return i;
        } else {
            return 0;
        }
    }

    private void func_212307_a(Entity p_212307_1_) {
        if (p_212307_1_ != null) {
            float f = MathHelper.clamp(1.0F - p_212307_1_.getBrightness(), 0.0F, 1.0F);
            this.prevVignetteBrightness =
                    (float) ((double) this.prevVignetteBrightness + (double) (f - this.prevVignetteBrightness) * 0.01D);
        }
    }

    public GuiBossOverlay getBossOverlay() {
        return this.overlayBoss;
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRenderer;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public int getTicks() {
        return this.ticks;
    }

    protected void renderAttackIndicator(float p_194798_1_) {
        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.thirdPersonView == 0) {
            if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR && this.mc.pointedEntity == null) {
                RayTraceResult raytraceresult = this.mc.objectMouseOver;
                if (raytraceresult == null || raytraceresult.type != RayTraceResult.Type.BLOCK) {
                    return;
                }

                BlockPos blockpos = raytraceresult.getBlockPos();
                if (!this.mc.world.getBlockState(blockpos).getBlock().hasTileEntity() || !(this.mc.world.getTileEntity(
                        blockpos) instanceof IInventory)) {
                    return;
                }
            }

            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.player.hasReducedDebug() &&
                !gamesettings.reducedDebugInfo) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef((float) (this.scaledWidth / 2), (float) (this.scaledHeight / 2), this.zLevel);
                Entity entity = this.mc.getRenderViewEntity();
                GlStateManager.rotatef(
                        entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_194798_1_,
                        -1.0F,
                        0.0F,
                        0.0F);
                GlStateManager.rotatef(
                        entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_194798_1_,
                        0.0F,
                        1.0F,
                        0.0F);
                GlStateManager.scalef(-1.0F, -1.0F, -1.0F);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                                                 GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                                                 GlStateManager.SourceFactor.ONE,
                                                 GlStateManager.DestFactor.ZERO);
                int l = 15;
                this.drawTexturedModalRect((float) this.scaledWidth / 2.0F - 7.5F,
                                           (float) this.scaledHeight / 2.0F - 7.5F,
                                           0,
                                           0,
                                           15,
                                           15);
                if (this.mc.gameSettings.attackIndicator == 1) {
                    float f = this.mc.player.getCooledAttackStrength(0.0F);
                    boolean flag = false;
                    if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof EntityLivingBase &&
                        f >= 1.0F) {
                        flag = this.mc.player.getCooldownPeriod() > 5.0F;
                        flag = flag & this.mc.pointedEntity.isEntityAlive();
                    }

                    int i = this.scaledHeight / 2 - 7 + 16;
                    int j = this.scaledWidth / 2 - 8;
                    if (flag) {
                        this.drawTexturedModalRect(j, i, 68, 94, 16, 16);
                    } else if (f < 1.0F) {
                        int k = (int) (f * 17.0F);
                        this.drawTexturedModalRect(j, i, 36, 94, 16, 4);
                        this.drawTexturedModalRect(j, i, 52, 94, k, 4);
                    }
                }
            }

        }
    }

    public void renderDemoOverlay() {
        this.mc.profiler.startSection("demo");
        String s;
        if (this.mc.world.getTotalWorldTime() >= 120500L) {
            s = I18n.format("demo.demoExpired");
        } else {
            s = I18n.format("demo.remainingTime",
                            StringUtils.ticksToElapsedTime((int) (120500L - this.mc.world.getTotalWorldTime())));
        }

        int i = this.getFontRenderer().getStringWidth(s);
        this.getFontRenderer().drawStringWithShadow(s, (float) (this.scaledWidth - i - 10), 5.0F, 16777215);
        this.mc.profiler.endSection();
    }

    public void renderExpBar(int p_194804_1_) {
        this.mc.profiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int i = this.mc.player.xpBarCap();
        if (i > 0) {
            int j = 182;
            int k = (int) (this.mc.player.experience * 183.0F);
            int l = this.scaledHeight - 32 + 3;
            this.drawTexturedModalRect(p_194804_1_, l, 0, 64, 182, 5);
            if (k > 0) {
                this.drawTexturedModalRect(p_194804_1_, l, 0, 69, k, 5);
            }
        }

        this.mc.profiler.endSection();
        if (this.mc.player.experienceLevel > 0) {
            this.mc.profiler.startSection("expLevel");
            String s = "" + this.mc.player.experienceLevel;
            int i1 = (this.scaledWidth - this.getFontRenderer().getStringWidth(s)) / 2;
            int j1 = this.scaledHeight - 31 - 4;
            this.getFontRenderer().drawString(s, (float) (i1 + 1), (float) j1, 0);
            this.getFontRenderer().drawString(s, (float) (i1 - 1), (float) j1, 0);
            this.getFontRenderer().drawString(s, (float) i1, (float) (j1 + 1), 0);
            this.getFontRenderer().drawString(s, (float) i1, (float) (j1 - 1), 0);
            this.getFontRenderer().drawString(s, (float) i1, (float) j1, 8453920);
            this.mc.profiler.endSection();
        }

    }

    public void renderGameOverlay(float p_175180_1_) {
        this.scaledWidth = this.mc.mainWindow.getScaledWidth();
        this.scaledHeight = this.mc.mainWindow.getScaledHeight();
        FontRenderer fontrenderer = this.getFontRenderer();
        GlStateManager.enableBlend();
        if (Minecraft.isFancyGraphicsEnabled()) {
            this.func_212303_b(this.mc.getRenderViewEntity());
        } else {
            GlStateManager.enableDepthTest();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                             GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                             GlStateManager.SourceFactor.ONE,
                                             GlStateManager.DestFactor.ZERO);
        }

        ItemStack itemstack = this.mc.player.inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            this.renderPumpkinOverlay();
        }

        if (!this.mc.player.isPotionActive(MobEffects.NAUSEA)) {
            float f = this.mc.player.prevTimeInPortal +
                      (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * p_175180_1_;
            if (f > 0.0F) {
                this.renderPortal(f);
            }
        }

        if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
            this.spectatorGui.func_195622_a(p_175180_1_);
        } else if (!this.mc.gameSettings.hideGUI) {
            this.renderHotbar(p_175180_1_);
        }

        if (!this.mc.gameSettings.hideGUI) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(ICONS);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            this.renderAttackIndicator(p_175180_1_);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                             GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                             GlStateManager.SourceFactor.ONE,
                                             GlStateManager.DestFactor.ZERO);
            this.mc.profiler.startSection("bossHealth");
            this.overlayBoss.renderBossHealth();
            this.mc.profiler.endSection();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(ICONS);
            if (this.mc.playerController.shouldDrawHUD()) {
                this.renderPlayerStats();
            }

            this.renderVehicleHealth();
            GlStateManager.disableBlend();
            int k = this.scaledWidth / 2 - 91;
            if (this.mc.player.isRidingHorse()) {
                this.renderHorseJumpBar(k);
            } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
                this.renderExpBar(k);
            }

            if (this.mc.gameSettings.heldItemTooltips &&
                this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                this.renderSelectedItem();
            } else if (this.mc.player.isSpectator()) {
                this.spectatorGui.renderSelectedItem();
            }
        }

        if (this.mc.player.getSleepTimer() > 0) {
            this.mc.profiler.startSection("sleep");
            GlStateManager.disableDepthTest();
            GlStateManager.disableAlphaTest();
            float f2 = (float) this.mc.player.getSleepTimer();
            float f1 = f2 / 100.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F - (f2 - 100.0F) / 10.0F;
            }

            int i = (int) (220.0F * f1) << 24 | 1052704;
            drawRect(0, 0, this.scaledWidth, this.scaledHeight, i);
            GlStateManager.enableAlphaTest();
            GlStateManager.enableDepthTest();
            this.mc.profiler.endSection();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.mc.isDemo()) {
            this.renderDemoOverlay();
        }

        this.renderPotionEffects();
        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.render();
        }

        if (!this.mc.gameSettings.hideGUI) {
            if (this.overlayMessageTime > 0) {
                this.mc.profiler.startSection("overlayMessage");
                float f3 = (float) this.overlayMessageTime - p_175180_1_;
                int l = (int) (f3 * 255.0F / 20.0F);
                if (l > 255) {
                    l = 255;
                }

                if (l > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float) (this.scaledWidth / 2), (float) (this.scaledHeight - 68), 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                                     GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                     GlStateManager.SourceFactor.ONE,
                                                     GlStateManager.DestFactor.ZERO);
                    int j1 = 16777215;
                    if (this.animateOverlayMessageColor) {
                        j1 = MathHelper.hsvToRGB(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
                    }

                    fontrenderer.drawString(this.overlayMessage,
                                            (float) (-fontrenderer.getStringWidth(this.overlayMessage) / 2),
                                            -4.0F,
                                            j1 + (l << 24 & -16777216));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                this.mc.profiler.endSection();
            }

            if (this.titlesTimer > 0) {
                this.mc.profiler.startSection("titleAndSubtitle");
                float f4 = (float) this.titlesTimer - p_175180_1_;
                int i1 = 255;
                if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                    float f5 = (float) (this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f4;
                    i1 = (int) (f5 * 255.0F / (float) this.titleFadeIn);
                }

                if (this.titlesTimer <= this.titleFadeOut) {
                    i1 = (int) (f4 * 255.0F / (float) this.titleFadeOut);
                }

                i1 = MathHelper.clamp(i1, 0, 255);
                if (i1 > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float) (this.scaledWidth / 2), (float) (this.scaledHeight / 2), 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                                     GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                     GlStateManager.SourceFactor.ONE,
                                                     GlStateManager.DestFactor.ZERO);
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(4.0F, 4.0F, 4.0F);
                    int k1 = i1 << 24 & -16777216;
                    fontrenderer.drawStringWithShadow(this.displayedTitle,
                                                      (float) (-fontrenderer.getStringWidth(this.displayedTitle) / 2),
                                                      -10.0F,
                                                      16777215 | k1);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(2.0F, 2.0F, 2.0F);
                    fontrenderer.drawStringWithShadow(this.displayedSubTitle,
                                                      (float) (-fontrenderer.getStringWidth(this.displayedSubTitle) /
                                                               2),
                                                      5.0F,
                                                      16777215 | k1);
                    GlStateManager.popMatrix();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                this.mc.profiler.endSection();
            }

            this.overlaySubtitle.render();
            Scoreboard scoreboard = this.mc.world.getScoreboard();
            ScoreObjective scoreobjective = null;
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.player.getScoreboardName());
            if (scoreplayerteam != null) {
                int j = scoreplayerteam.getColor().getColorIndex();
                if (j >= 0) {
                    scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + j);
                }
            }

            ScoreObjective scoreobjective1 =
                    scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
            if (scoreobjective1 != null) {
                this.renderScoreboard(scoreobjective1);
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                             GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                             GlStateManager.SourceFactor.ONE,
                                             GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlphaTest();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, (float) (this.scaledHeight - 48), 0.0F);
            this.mc.profiler.startSection("chat");
            this.persistantChatGUI.drawChat(this.ticks);
            this.mc.profiler.endSection();
            GlStateManager.popMatrix();
            scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);
            if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown() ||
                this.mc.isIntegratedServerRunning() && this.mc.player.connection.getPlayerInfoMap().size() <= 1 &&
                scoreobjective1 == null) {
                this.overlayPlayerList.setVisible(false);
            } else {
                this.overlayPlayerList.setVisible(true);
                this.overlayPlayerList.renderPlayerlist(this.scaledWidth, scoreboard, scoreobjective1);
            }
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlphaTest();
    }

    public void renderHorseJumpBar(int p_194803_1_) {
        this.mc.profiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        float f = this.mc.player.getHorseJumpPower();
        int i = 182;
        int j = (int) (f * 183.0F);
        int k = this.scaledHeight - 32 + 3;
        this.drawTexturedModalRect(p_194803_1_, k, 0, 84, 182, 5);
        if (j > 0) {
            this.drawTexturedModalRect(p_194803_1_, k, 0, 89, j, 5);
        }

        this.mc.profiler.endSection();
    }

    protected void renderHotbar(float p_194806_1_) {
        EntityPlayer entityplayer = this.getCurrentPlayer();
        if (entityplayer != null) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
            ItemStack itemstack = entityplayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
            int i = this.scaledWidth / 2;
            float f = this.zLevel;
            int j = 182;
            int k = 91;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20,
                                       this.scaledHeight - 22 - 1,
                                       0,
                                       22,
                                       24,
                                       22);
            if (!itemstack.isEmpty()) {
                if (enumhandside == EnumHandSide.LEFT) {
                    this.drawTexturedModalRect(i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
                } else {
                    this.drawTexturedModalRect(i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
                }
            }

            this.zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                             GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                             GlStateManager.SourceFactor.ONE,
                                             GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (int l = 0; l < 9; ++l) {
                int i1 = i - 90 + l * 20 + 2;
                int j1 = this.scaledHeight - 16 - 3;
                this.renderHotbarItem(i1, j1, p_194806_1_, entityplayer, entityplayer.inventory.mainInventory.get(l));
            }

            if (!itemstack.isEmpty()) {
                int l1 = this.scaledHeight - 16 - 3;
                if (enumhandside == EnumHandSide.LEFT) {
                    this.renderHotbarItem(i - 91 - 26, l1, p_194806_1_, entityplayer, itemstack);
                } else {
                    this.renderHotbarItem(i + 91 + 10, l1, p_194806_1_, entityplayer, itemstack);
                }
            }

            if (this.mc.gameSettings.attackIndicator == 2) {
                float f1 = this.mc.player.getCooledAttackStrength(0.0F);
                if (f1 < 1.0F) {
                    int i2 = this.scaledHeight - 20;
                    int j2 = i + 91 + 6;
                    if (enumhandside == EnumHandSide.RIGHT) {
                        j2 = i - 91 - 22;
                    }

                    this.mc.getTextureManager().bindTexture(Gui.ICONS);
                    int k1 = (int) (f1 * 19.0F);
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
                    this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    private void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer p_184044_4_, ItemStack p_184044_5_) {
        if (!p_184044_5_.isEmpty()) {
            float f = (float) p_184044_5_.getAnimationsToGo() - p_184044_3_;
            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translatef((float) (p_184044_1_ + 8), (float) (p_184044_2_ + 12), 0.0F);
                GlStateManager.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translatef((float) (-(p_184044_1_ + 8)), (float) (-(p_184044_2_ + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(p_184044_4_, p_184044_5_, p_184044_1_, p_184044_2_);
            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(this.mc.fontRenderer, p_184044_5_, p_184044_1_, p_184044_2_);
        }
    }

    private void renderPlayerStats() {
        EntityPlayer entityplayer = this.getCurrentPlayer();
        int renderHealth = MathHelper.ceil(entityplayer.getHealth());
        boolean flag = this.healthUpdateCounter > (long) this.ticks &&
                       (this.healthUpdateCounter - (long) this.ticks) / 3L % 2L == 1L;
        long milliTime = Util.milliTime();
        if (renderHealth < this.playerHealth && entityplayer.hurtResistantTime > 0) {
            this.lastSystemTime = milliTime;
            this.healthUpdateCounter = (long) (this.ticks + 20);
        } else if (renderHealth > this.playerHealth && entityplayer.hurtResistantTime > 0) {
            this.lastSystemTime = milliTime;
            this.healthUpdateCounter = (long) (this.ticks + 10);
        }

        if (milliTime - this.lastSystemTime > 1000L) {
            this.playerHealth = renderHealth;
            this.lastPlayerHealth = renderHealth;
            this.lastSystemTime = milliTime;
        }

        this.playerHealth = renderHealth;
        int lastPlayerHealth = this.lastPlayerHealth;
        this.rand.setSeed((long) (this.ticks * 312871));
        FoodStats foodstats = entityplayer.getFoodStats();
        int foodLevel = foodstats.getFoodLevel();
        IAttributeInstance maxHealthAttribute = entityplayer.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int guiLeft = this.scaledWidth / 2 - 91;
        int guiRight = this.scaledWidth / 2 + 91;
        int guiUp = this.scaledHeight - 39;
        float maxHealth = (float) maxHealthAttribute.getAttributeValue();
        int renderAbsorptionAmount = MathHelper.ceil(entityplayer.getAbsorptionAmount());
        int i2 = MathHelper.ceil((maxHealth + (float) renderAbsorptionAmount) / 2.0F / 10.0F);
        int j2 = Math.max(10 - (i2 - 2), 3);
        int k2 = guiUp - (i2 - 1) * j2 - 10;
        int l2 = guiUp - 10;
        int i3 = renderAbsorptionAmount;
        int totalArmorValue = entityplayer.getTotalArmorValue();
        int k3 = -1;
        if (entityplayer.isPotionActive(MobEffects.REGENERATION)) {
            k3 = this.ticks % MathHelper.ceil(maxHealth + 5.0F);
        }

        this.mc.profiler.startSection("armor");

        for (int arromrIndex = 0; arromrIndex < 10; ++arromrIndex) {
            if (totalArmorValue > 0) {
                int i4 = guiLeft + arromrIndex * 8;
                if (arromrIndex * 2 + 1 < totalArmorValue) {
                    this.drawTexturedModalRect(i4, k2, 34, 9, 9, 9);
                }

                if (arromrIndex * 2 + 1 == totalArmorValue) {
                    this.drawTexturedModalRect(i4, k2, 25, 9, 9, 9);
                }

                if (arromrIndex * 2 + 1 > totalArmorValue) {
                    this.drawTexturedModalRect(i4, k2, 16, 9, 9, 9);
                }
            }
        }

        this.mc.profiler.endStartSection("health");

        for (int renderHearts = MathHelper.ceil((maxHealth + (float) renderAbsorptionAmount) / 2.0F) - 1; renderHearts >= 0; --renderHearts) {
            int heartTextureIndex = 16;
            if (entityplayer.isPotionActive(MobEffects.POISON)) {
                heartTextureIndex += 36;
            } else if (entityplayer.isPotionActive(MobEffects.WITHER)) {
                heartTextureIndex += 72;
            }

            int j4 = 0;
            if (flag) {
                j4 = 1;
            }

            int k4 = MathHelper.ceil((float) (renderHearts + 1) / 10.0F) - 1;
            int l4 = guiLeft + renderHearts % 10 * 8;
            int i5 = guiUp - k4 * j2;
            if (renderHealth <= 4) {
                i5 += this.rand.nextInt(2);
            }

            if (i3 <= 0 && renderHearts == k3) {
                i5 -= 2;
            }

            int j5 = 0;
            if (entityplayer.world.getWorldInfo().isHardcoreModeEnabled()) {
                j5 = 5;
            }

            this.drawTexturedModalRect(l4, i5, 16 + j4 * 9, 9 * j5, 9, 9);
            if (flag) {
                if (renderHearts * 2 + 1 < lastPlayerHealth) {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 54, 9 * j5, 9, 9);
                }

                if (renderHearts * 2 + 1 == lastPlayerHealth) {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 63, 9 * j5, 9, 9);
                }
            }

            if (i3 > 0) {
                if (i3 == renderAbsorptionAmount && renderAbsorptionAmount % 2 == 1) {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 153, 9 * j5, 9, 9);
                    --i3;
                } else {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 144, 9 * j5, 9, 9);
                    i3 -= 2;
                }
            } else {
                if (renderHearts * 2 + 1 < renderHealth) {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 36, 9 * j5, 9, 9);
                }

                if (renderHearts * 2 + 1 == renderHealth) {
                    this.drawTexturedModalRect(l4, i5, heartTextureIndex + 45, 9 * j5, 9, 9);
                }
            }
        }

        EntityLivingBase entitylivingbase = this.func_212305_n();
        int j6 = this.func_212306_a(entitylivingbase);
        if (j6 == 0) {
            this.mc.profiler.endStartSection("food");

            for (int k6 = 0; k6 < entityplayer.getFoodStats().getMaxFoodLevel() / 2; ++k6) {
                int i7 = guiUp;
                int k7 = 16;
                int i8 = 0;
                if (entityplayer.isPotionActive(MobEffects.HUNGER)) {
                    k7 += 36;
                    i8 = 13;
                }

                if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && this.ticks % (foodLevel * 3 + 1) == 0) {
                    i7 = guiUp + (this.rand.nextInt(3) - 1);
                }

                int k8 = guiRight - k6 * 8 - 9;
                this.drawTexturedModalRect(k8, i7, 16 + i8 * 9, 27, 9, 9);
                if (k6 * 2 + 1 < foodLevel) {
                    this.drawTexturedModalRect(k8, i7, k7 + 36, 27, 9, 9);
                }

                if (k6 * 2 + 1 == foodLevel) {
                    this.drawTexturedModalRect(k8, i7, k7 + 45, 27, 9, 9);
                }
            }

            l2 -= 10;
        }

        this.mc.profiler.endStartSection("air");
        int l6 = entityplayer.getAir();
        int j7 = entityplayer.getMaxAir();
        if (entityplayer.areEyesInFluid(FluidTags.WATER) || l6 < j7) {
            int l7 = this.func_212302_c(j6) - 1;
            l2 = l2 - l7 * 10;
            int j8 = MathHelper.ceil((double) (l6 - 2) * 10.0D / (double) j7);
            int l8 = MathHelper.ceil((double) l6 * 10.0D / (double) j7) - j8;

            for (int k5 = 0; k5 < j8 + l8; ++k5) {
                if (k5 < j8) {
                    this.drawTexturedModalRect(guiRight - k5 * 8 - 9, l2, 16, 18, 9, 9);
                } else {
                    this.drawTexturedModalRect(guiRight - k5 * 8 - 9, l2, 25, 18, 9, 9);
                }
            }
        }

        this.mc.profiler.endSection();
    }

    protected void renderPortal(float p_194805_1_) {
        if (p_194805_1_ < 1.0F) {
            p_194805_1_ = p_194805_1_ * p_194805_1_;
            p_194805_1_ = p_194805_1_ * p_194805_1_;
            p_194805_1_ = p_194805_1_ * 0.8F + 0.2F;
        }

        GlStateManager.disableAlphaTest();
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, p_194805_1_);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(
                Blocks.NETHER_PORTAL.getDefaultState());
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f2 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) this.scaledHeight, -90.0D).tex((double) f, (double) f3).endVertex();
        bufferbuilder.pos((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).tex((double) f2, (double) f3)
                     .endVertex();
        bufferbuilder.pos((double) this.scaledWidth, 0.0D, -90.0D).tex((double) f2, (double) f1).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex((double) f, (double) f1).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderPotionEffects() {
        Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
        if (!collection.isEmpty()) {
            this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            GlStateManager.enableBlend();
            int i = 0;
            int j = 0;

            for (PotionEffect potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
                Potion potion = potioneffect.getPotion();
                if (potion.hasStatusIcon() && potioneffect.func_205348_f()) {
                    int k = this.scaledWidth;
                    int l = 1;
                    if (this.mc.isDemo()) {
                        l += 15;
                    }

                    int i1 = potion.getStatusIconIndex();
                    if (potion.isBeneficial()) {
                        ++i;
                        k = k - 25 * i;
                    } else {
                        ++j;
                        k = k - 25 * j;
                        l += 26;
                    }

                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;
                    if (potioneffect.isAmbient()) {
                        this.drawTexturedModalRect(k, l, 165, 166, 24, 24);
                    } else {
                        this.drawTexturedModalRect(k, l, 141, 166, 24, 24);
                        if (potioneffect.getDuration() <= 200) {
                            int j1 = 10 - potioneffect.getDuration() / 20;
                            f = MathHelper.clamp((float) potioneffect.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) +
                                MathHelper.cos((float) potioneffect.getDuration() * (float) Math.PI / 5.0F) *
                                MathHelper.clamp((float) j1 / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
                    int l1 = i1 % 12;
                    int k1 = i1 / 12;
                    this.drawTexturedModalRect(k + 3, l + 3, l1 * 18, 198 + k1 * 18, 18, 18);
                }
            }

        }
    }

    protected void renderPumpkinOverlay() {
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlphaTest();
        this.mc.getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) this.scaledHeight, -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) this.scaledWidth, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderScoreboard(ScoreObjective p_194802_1_) {
        Scoreboard scoreboard = p_194802_1_.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(p_194802_1_);
        List<Score> list = collection.stream().filter((p_210100_0_) -> {
            return p_210100_0_.getPlayerName() != null && !p_210100_0_.getPlayerName().startsWith("#");
        }).collect(Collectors.toList());
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        String s = p_194802_1_.getDisplayName().getFormattedText();
        int i = this.getFontRenderer().getStringWidth(s);
        int j = i;

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s1 = ScorePlayerTeam.formatMemberName(scoreplayerteam,
                                                         new TextComponentString(score.getPlayerName()))
                                       .getFormattedText() + ": " + TextFormatting.RED + score.getScorePoints();
            j = Math.max(j, this.getFontRenderer().getStringWidth(s1));
        }

        int j1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int k1 = this.scaledHeight / 2 + j1 / 3;
        int l1 = 3;
        int i2 = this.scaledWidth - j - 3;
        int k = 0;

        for (Score score1 : collection) {
            ++k;
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s2 = ScorePlayerTeam.formatMemberName(scoreplayerteam1,
                                                         new TextComponentString(score1.getPlayerName()))
                                       .getFormattedText();
            String s3 = TextFormatting.RED + "" + score1.getScorePoints();
            int l = k1 - k * this.getFontRenderer().FONT_HEIGHT;
            int i1 = this.scaledWidth - 3 + 2;
            drawRect(i2 - 2, l, i1, l + this.getFontRenderer().FONT_HEIGHT, 1342177280);
            this.getFontRenderer().drawString(s2, (float) i2, (float) l, 553648127);
            this.getFontRenderer().drawString(s3,
                                              (float) (i1 - this.getFontRenderer().getStringWidth(s3)),
                                              (float) l,
                                              553648127);
            if (k == collection.size()) {
                drawRect(i2 - 2, l - this.getFontRenderer().FONT_HEIGHT - 1, i1, l - 1, 1610612736);
                drawRect(i2 - 2, l - 1, i1, l, 1342177280);
                this.getFontRenderer().drawString(s,
                                                  (float) (i2 + j / 2 - i / 2),
                                                  (float) (l - this.getFontRenderer().FONT_HEIGHT),
                                                  553648127);
            }
        }

    }

    public void renderSelectedItem() {
        this.mc.profiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty()) {
            ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.highlightingItemStack
                                                                                                .getDisplayName())
                                                                         .applyTextStyle(this.highlightingItemStack
                                                                                                 .getRarity().color);
            if (this.highlightingItemStack.hasDisplayName()) {
                itextcomponent.applyTextStyle(TextFormatting.ITALIC);
            }

            String s = itextcomponent.getFormattedText();
            int i = (this.scaledWidth - this.getFontRenderer().getStringWidth(s)) / 2;
            int j = this.scaledHeight - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
                j += 14;
            }

            int k = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);
            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                                 GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                 GlStateManager.SourceFactor.ONE,
                                                 GlStateManager.DestFactor.ZERO);
                this.getFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.profiler.endSection();
    }

    private void renderVehicleHealth() {
        EntityLivingBase entitylivingbase = this.func_212305_n();
        int i = this.func_212306_a(entitylivingbase);
        if (i != 0) {
            int j = (int) Math.ceil((double) entitylivingbase.getHealth());
            this.mc.profiler.endStartSection("mountHealth");
            int k = this.scaledHeight - 39;
            int l = this.scaledWidth / 2 + 91;
            int i1 = k;
            int j1 = 0;

            for (boolean flag = false; i > 0; j1 += 20) {
                int k1 = Math.min(i, 10);
                i -= k1;

                for (int l1 = 0; l1 < k1; ++l1) {
                    int i2 = 52;
                    int j2 = 0;
                    int k2 = l - l1 * 8 - 9;
                    this.drawTexturedModalRect(k2, i1, 52 + j2 * 9, 9, 9, 9);
                    if (l1 * 2 + 1 + j1 < j) {
                        this.drawTexturedModalRect(k2, i1, 88, 9, 9, 9);
                    }

                    if (l1 * 2 + 1 + j1 == j) {
                        this.drawTexturedModalRect(k2, i1, 97, 9, 9, 9);
                    }
                }

                i1 -= 10;
            }

        }
    }

    public void resetPlayersOverlayFooterHeader() {
        this.overlayPlayerList.resetFooterHeader();
        this.overlayBoss.clearBossInfos();
        this.mc.getToastGui().clear();
    }

    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void setOverlayMessage(String p_110326_1_, boolean p_110326_2_) {
        this.overlayMessage = p_110326_1_;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = p_110326_2_;
    }

    public void setOverlayMessage(ITextComponent p_175188_1_, boolean p_175188_2_) {
        this.setOverlayMessage(p_175188_1_.getString(), p_175188_2_);
    }

    public void setRecordPlayingMessage(String p_73833_1_) {
        this.setOverlayMessage(I18n.format("record.nowPlaying", p_73833_1_), true);
    }

    public void tick() {
        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }

        if (this.titlesTimer > 0) {
            --this.titlesTimer;
            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }

        ++this.ticks;
        Entity entity = this.mc.getRenderViewEntity();
        if (entity != null) {
            this.func_212307_a(entity);
        }

        if (this.mc.player != null) {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();
            if (itemstack.isEmpty()) {
                this.remainingHighlightTicks = 0;
            } else if (!this.highlightingItemStack.isEmpty() &&
                       itemstack.getItem() == this.highlightingItemStack.getItem() && itemstack.getDisplayName().equals(
                    this.highlightingItemStack.getDisplayName())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }

    }
}

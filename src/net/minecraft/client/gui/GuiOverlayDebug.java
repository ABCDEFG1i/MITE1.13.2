package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.state.IProperty;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

@OnlyIn(Dist.CLIENT)
public class GuiOverlayDebug extends Gui {
    private final FontRenderer fontRenderer;
    private final Minecraft mc;
    protected RayTraceResult field_211537_g;
    protected RayTraceResult field_211538_h;

    public GuiOverlayDebug(Minecraft p_i45543_1_) {
        this.mc = p_i45543_1_;
        this.fontRenderer = p_i45543_1_.fontRenderer;
    }

    private static long bytesToMb(long p_175240_0_) {
        return p_175240_0_ / 1024L / 1024L;
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = MathHelper.clamp((int) ((float) i + (float) (i1 - i) * p_181553_3_), 0, 255);
        int j2 = MathHelper.clamp((int) ((float) j + (float) (j1 - j) * p_181553_3_), 0, 255);
        int k2 = MathHelper.clamp((int) ((float) k + (float) (k1 - k) * p_181553_3_), 0, 255);
        int l2 = MathHelper.clamp((int) ((float) l + (float) (l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    protected void renderDebugInfoRight() {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i);
            if (!Strings.isNullOrEmpty(s)) {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = this.mc.mainWindow.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, (float) l, (float) i1, 14737632);
            }
        }

    }

    protected List<String> func_209011_c() {
        IntegratedServer integratedserver = this.mc.getIntegratedServer();
        NetworkManager networkmanager = this.mc.getConnection().getNetworkManager();
        float f = networkmanager.func_211390_n();
        float f1 = networkmanager.func_211393_m();
        String s;
        if (integratedserver != null) {
            s = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedserver.getTickTime(), f,
                    f1);
        } else {
            s = String.format("\"%s\" server, %.0f tx, %.0f rx", this.mc.player.getServerBrand(), f, f1);
        }

        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX,
                this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);
        if (this.mc.isReducedDebug()) {
            return Lists.newArrayList(
                    "Minecraft 1.13.2-MITE (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.mc.debug, s, this.mc.renderGlobal.func_72735_c(), this.mc.renderGlobal.func_72723_d(),
                    "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(),
                    this.mc.world.getProviderName(), "");
        } else {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s1 = "Invalid";
            switch (enumfacing) {
                case NORTH:
                    s1 = "Towards negative Z";
                    break;
                case SOUTH:
                    s1 = "Towards positive Z";
                    break;
                case WEST:
                    s1 = "Towards negative X";
                    break;
                case EAST:
                    s1 = "Towards positive X";
            }

            DimensionType dimensiontype = this.mc.world.dimension.getType();
            World world;
            if (integratedserver != null) {
                world = integratedserver.func_71218_a(dimensiontype);
            } else {
                world = this.mc.world;
            }

            ForcedChunksSaveData forcedchunkssavedata = world.func_212411_a(dimensiontype, ForcedChunksSaveData::new,
                    "chunks");
            ArrayList<String> list;
            if (SharedConstants.developmentMode) {
                list = Lists.newArrayList(
                        "Minecraft 1.13.2-MITE (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(
                                this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType()) + ")", this.mc.debug,
                        s, this.mc.renderGlobal.func_72735_c(), this.mc.renderGlobal.func_72723_d(),
                        "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(),
                        this.mc.world.getProviderName(), DimensionType.func_212678_a(
                                dimensiontype).toString() + " FC: " + (forcedchunkssavedata == null ? "n/a" : Integer.toString(
                                forcedchunkssavedata.func_212438_a().size())), "",
                        String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().posX,
                                this.mc.getRenderViewEntity().getEntityBoundingBox().minY,
                                this.mc.getRenderViewEntity().posZ),
                        String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()),
                        String.format("Chunk: %d %d %d in %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15,
                                blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4),
                        String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", enumfacing, s1,
                                MathHelper.wrapDegrees(entity.rotationYaw),
                                MathHelper.wrapDegrees(entity.rotationPitch)));
            } else {
                list = Lists.newArrayList(
                        "Minecraft 1.13.2-MITE (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(
                                this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType()) + ")", this.mc.debug,
                        s, this.mc.renderGlobal.func_72735_c(), this.mc.renderGlobal.func_72723_d(),
                        "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(),
                        this.mc.world.getProviderName(), DimensionType.func_212678_a(
                                dimensiontype).toString() + " FC: " + (forcedchunkssavedata == null ? "n/a" : Integer.toString(
                                forcedchunkssavedata.func_212438_a().size())), "");
            }

            if (this.mc.world != null) {
                Chunk chunk = this.mc.world.getChunk(blockpos);
                if (this.mc.world.isBlockLoaded(blockpos) && blockpos.getY() >= 0 && blockpos.getY() < 256) {
                    if (!chunk.isEmpty()) {
                        if (SharedConstants.developmentMode) {
                            list.add("Biome: " + IRegistry.field_212624_m.func_177774_c(chunk.getBiome(blockpos)));
                            list.add("Light: " + chunk.getLightSubtracted(blockpos, 0,
                                    chunk.getWorld().dimension.hasSkyLight()) + " (" + chunk.getLight(EnumLightType.SKY,
                                    blockpos, chunk.getWorld().dimension.hasSkyLight()) + " sky, " + chunk.getLight(
                                    EnumLightType.BLOCK, blockpos,
                                    chunk.getWorld().dimension.hasSkyLight()) + " block)");
                        }

                        DifficultyInstance difficultyinstance = this.mc.world.getDifficultyForLocation(blockpos);
                        if (this.mc.isIntegratedServerRunning() && integratedserver != null) {
                            EntityPlayerMP entityplayermp = integratedserver.getPlayerList().getPlayerByUUID(
                                    this.mc.player.getUniqueID());
                            if (entityplayermp != null) {
                                difficultyinstance = entityplayermp.world.getDifficultyForLocation(
                                        new BlockPos(entityplayermp));
                            }
                        }

                        list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)",
                                difficultyinstance.getAdditionalDifficulty(),
                                difficultyinstance.getClampedAdditionalDifficulty(),
                                this.mc.world.getWorldTime() / 24000L));
                    } else {
                        list.add("Waiting for chunk...");
                    }
                } else {
                    list.add("Outside of world...");
                }
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.func_147702_a()) {
                list.add("Shader: " + this.mc.entityRenderer.func_147706_e().getShaderGroupName());
            }

            BlockPos blockpos2;
            if (SharedConstants.developmentMode && this.field_211537_g != null && this.field_211537_g.type == RayTraceResult.Type.BLOCK) {
                blockpos2 = this.field_211537_g.getBlockPos();
                list.add(String.format("Looking at block: %d %d %d", blockpos2.getX(), blockpos2.getY(),
                        blockpos2.getZ()));
            }

            if (SharedConstants.developmentMode && this.field_211538_h != null && this.field_211538_h.type == RayTraceResult.Type.BLOCK) {
                blockpos2 = this.field_211538_h.getBlockPos();
                list.add(String.format("Looking at liquid: %d %d %d", blockpos2.getX(), blockpos2.getY(),
                        blockpos2.getZ()));
            }

            return list;
        }
    }

    private String func_211534_a(Entry p_211534_1_) {
        IProperty iproperty = (IProperty) p_211534_1_.getKey();
        Comparable comparable = (Comparable) p_211534_1_.getValue();
        String s = Util.getValueName(iproperty, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            s = TextFormatting.GREEN + s;
        } else if (Boolean.FALSE.equals(comparable)) {
            s = TextFormatting.RED + s;
        }

        return iproperty.getName() + ": " + s;
    }

    protected List<String> getDebugInfoRight() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(
                String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
                String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)),
                String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "",
                String.format("CPU: %s", OpenGlHelper.getCpu()), "",
                String.format("Display: %dx%d (%s)", Minecraft.getInstance().mainWindow.getFramebufferWidth(),
                        Minecraft.getInstance().mainWindow.getFramebufferHeight(), GlStateManager.getString(7936)),
                GlStateManager.getString(7937), GlStateManager.getString(7938));
        if (this.mc.isReducedDebug()) {
            return list;
        } else if (!SharedConstants.developmentMode) {
            return list;
        } else {
            BlockPos blockpos1;
            UnmodifiableIterator var12;
            Entry entry1;
            Iterator var16;
            ResourceLocation resourcelocation1;
            if (this.field_211537_g != null && this.field_211537_g.type == RayTraceResult.Type.BLOCK) {
                blockpos1 = this.field_211537_g.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos1);
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Block");
                list.add(String.valueOf(IRegistry.field_212618_g.func_177774_c(iblockstate.getBlock())));
                var12 = iblockstate.getValues().entrySet().iterator();

                while (var12.hasNext()) {
                    entry1 = (Entry) var12.next();
                    list.add(this.func_211534_a(entry1));
                }

                var16 = this.mc.getConnection().getTags().getBlocks().getOwningTags(iblockstate.getBlock()).iterator();

                while (var16.hasNext()) {
                    resourcelocation1 = (ResourceLocation) var16.next();
                    list.add("#" + resourcelocation1);
                }
            }

            if (this.field_211538_h != null && this.field_211538_h.type == RayTraceResult.Type.BLOCK) {
                blockpos1 = this.field_211538_h.getBlockPos();
                IFluidState ifluidstate = this.mc.world.getFluidState(blockpos1);
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Fluid");
                list.add(String.valueOf(IRegistry.field_212619_h.func_177774_c(ifluidstate.getFluid())));
                var12 = ifluidstate.getValues().entrySet().iterator();

                while (var12.hasNext()) {
                    entry1 = (Entry) var12.next();
                    list.add(this.func_211534_a(entry1));
                }

                var16 = this.mc.getConnection().getTags().getFluids().getOwningTags(ifluidstate.getFluid()).iterator();

                while (var16.hasNext()) {
                    resourcelocation1 = (ResourceLocation) var16.next();
                    list.add("#" + resourcelocation1);
                }
            }

            Entity entity = this.mc.pointedEntity;
            if (entity != null) {
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Entity");
                list.add(String.valueOf(IRegistry.field_212629_r.func_177774_c(entity.getType())));
            }

            return list;
        }
    }

    private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256,
                (float) p_181552_1_ / (float) p_181552_3_) : this.blendColors(-256, -65536,
                (float) (p_181552_1_ - p_181552_3_) / (float) (p_181552_4_ - p_181552_3_));
    }

    public void render() {
        this.mc.profiler.startSection("debug");
        GlStateManager.pushMatrix();
        Entity entity = this.mc.getRenderViewEntity();
        this.field_211537_g = entity.rayTrace(20.0D, 0.0F, RayTraceFluidMode.NEVER);
        this.field_211538_h = entity.rayTrace(20.0D, 0.0F, RayTraceFluidMode.ALWAYS);
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight();
        GlStateManager.popMatrix();
        if (this.mc.gameSettings.showLagometer) {
            this.renderLagometer();
        }

        this.mc.profiler.endSection();
    }

    protected void renderDebugInfoLeft() {
        List list = this.func_209011_c();
        list.add("");
        list.add(
                "Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");

        for (int i = 0; i < list.size(); ++i) {
            String s = (String) list.get(i);
            if (!Strings.isNullOrEmpty(s)) {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int i1 = 2 + j * i;
                drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2.0F, (float) i1, 14737632);
            }
        }

    }

    public void renderLagometer() {
        GlStateManager.disableDepthTest();
        FrameTimer frametimer = this.mc.getFrameTimer();
        int i = frametimer.getLastIndex();
        int j = frametimer.getIndex();
        long[] along = frametimer.getFrames();
        int k = i;
        int l = 0;
        int i1 = this.mc.mainWindow.getScaledHeight();
        drawRect(0, i1 - 60, 240, i1, -1873784752);

        while (k != j) {
            int j1 = frametimer.getLagometerValue(along[k], 30);
            int k1 = this.getFrameColor(MathHelper.clamp(j1, 0, 60), 0, 30, 60);
            this.drawVerticalLine(l, i1, i1 - j1, k1);
            ++l;
            k = frametimer.parseIndex(k + 1);
        }

        drawRect(1, i1 - 30 + 1, 14, i1 - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2.0F, (float) (i1 - 30 + 2), 14737632);
        this.drawHorizontalLine(0, 239, i1 - 30, -1);
        drawRect(1, i1 - 60 + 1, 14, i1 - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2.0F, (float) (i1 - 60 + 2), 14737632);
        this.drawHorizontalLine(0, 239, i1 - 60, -1);
        this.drawHorizontalLine(0, 239, i1 - 1, -1);
        this.drawVerticalLine(0, i1 - 60, i1, -1);
        this.drawVerticalLine(239, i1 - 60, i1, -1);
        if (this.mc.gameSettings.limitFramerate <= 120) {
            this.drawHorizontalLine(0, 239, i1 - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepthTest();
    }
}

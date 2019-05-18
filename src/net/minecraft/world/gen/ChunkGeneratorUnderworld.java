package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;

public class ChunkGeneratorUnderworld extends AbstractChunkGenerator<UnderworldGenSetting> {
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private final NoiseGeneratorOctaves lperlinNoise1;
    private final NoiseGeneratorOctaves lperlinNoise2;
    private final NoiseGeneratorOctaves perlinNoise1;
    private NoiseGeneratorPerlin surfaceNoise;
    private final NoiseGeneratorOctaves scaleNoise;
    private final NoiseGeneratorOctaves depthNoise;
    private final UnderworldGenSetting settings;
    private final IBlockState defaultBlock;
    private final IBlockState defaultFluid;

    public ChunkGeneratorUnderworld(World p_i48694_1_, BiomeProvider p_i48694_2_, UnderworldGenSetting p_i48694_3_) {
        super(p_i48694_1_, p_i48694_2_);
        this.settings = p_i48694_3_;
        this.defaultBlock = this.settings.getDefaultBlock();
        this.defaultFluid = this.settings.getDefaultFluid();
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
        this.lperlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.surfaceNoise = new NoiseGeneratorPerlin(sharedseedrandom, 4);
        sharedseedrandom.skip(1048);
        this.scaleNoise = new NoiseGeneratorOctaves(sharedseedrandom, 10);
        this.depthNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        p_i48694_1_.setSeaLevel(150);
    }

    public void prepareHeights(int p_185936_1_, int p_185936_2_, IChunk p_185936_3_) {
        int j = this.world.getSeaLevel() / 2 + 1;
        int sealevel = this.world.getSeaLevel();
        double[] adouble = this.func_202104_a(p_185936_1_ * 4, 0, p_185936_2_ * 4, 5, 17, 5);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int j1 = 0; j1 < 4; ++j1) {
            for(int k1 = 0; k1 < 4; ++k1) {
                for(int l1 = 0; l1 < 16; ++l1) {
                    double d1 = adouble[(j1 * 5 + k1) * 17 + l1];
                    double d2 = adouble[((j1) * 5 + k1 + 1) * 17 + l1];
                    double d3 = adouble[((j1 + 1) * 5 + k1) * 17 + l1];
                    double d4 = adouble[((j1 + 1) * 5 + k1 + 1) * 17 + l1];
                    double d5 = (adouble[(j1 * 5 + k1) * 17 + l1 + 1] - d1) * 0.125D;
                    double d6 = (adouble[(j1 * 5 + k1 + 1) * 17 + l1 + 1] - d2) * 0.125D;
                    double d7 = (adouble[((j1 + 1) * 5 + k1) * 17 + l1 + 1] - d3) * 0.125D;
                    double d8 = (adouble[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 1] - d4) * 0.125D;

                    for(int i2 = 0; i2 < 8; ++i2) {
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for(int j2 = 0; j2 < 4; ++j2) {
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.25D;

                            for(int k2 = 0; k2 < 4; ++k2) {
                                int heightBuf = 127;
                                boolean soild = false;
                                IBlockState iblockstate = AIR;

                                if (l1 * 8 + i2 < j-98) {
                                    soild=true;
                                    iblockstate = this.defaultBlock;
                                }

                                if (d15 > 0.0D) {
                                    soild=true;
                                    iblockstate = this.defaultBlock;
                                }

                                int x = j2 + j1 * 4;
                                int y = i2 + l1 * 8 + heightBuf;
                                int z = k2 + k1 * 4;
                                if (y < sealevel&&!soild) {
                                    iblockstate = this.defaultFluid;
                                }
                                p_185936_3_.setBlockState(blockpos$mutableblockpos.setPos(x, y, z), iblockstate, false);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

        for(int x = 0;x<16;x++){
            for(int y = 0;y<127;y++){
                for(int z = 0;z<16;z++){
                    p_185936_3_.setBlockState(blockpos$mutableblockpos.setPos(x, y, z), this.defaultBlock, false);
                }
            }
        }

    }

    protected void makeBedrock(IChunk p_205472_1_, Random rand) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = p_205472_1_.getPos().getXStart();
        int j = p_205472_1_.getPos().getZStart();

        for(BlockPos blockpos : BlockPos.getAllInBox(i, 0, j, i + 16, 0, j + 16)) {
            for(int k = 256; k > 253; --k) {
                if (k >= 255 - rand.nextInt(5)) {
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), k, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            //First Layer
            for (int y = 100; y>94;--y){
                p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
            for (int y = 93; y>89;--y){
                if (rand.nextInt(96-y)==0){
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            for (int y = 104; y>100;--y){
                if (rand.nextInt(y-98)==0){
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }

            //Second Layer
            for (int y = 74; y>70;--y){
                p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
            for (int y = 70; y>65;--y){
                if (rand.nextInt(71-y)==0){
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            for (int y = 76; y>74;--y){
                if (rand.nextInt(y-73)==0){
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }

            for(int l = 6; l >= 0; --l) {
                if (l <= rand.nextInt(7)) {
                    p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), l, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }


        }
        //Make a hole to enter deeper
        //First Layer hole
        if (rand.nextInt(10)==0){
            int xConer = rand.nextInt(14);
            int zConer = rand.nextInt(14);
            for (int y=104;y>89;--y){
                for (int x=0;x<2;x++){
                    for (int z=0;z<2;z++){
                        p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(xConer+x,y,zConer+z),Blocks.STONE.getDefaultState(),false);
                    }
                }
            }
        }
        //Second Layer hole
        if (rand.nextInt(8)==0){
            int xConer = rand.nextInt(12);
            int zConer = rand.nextInt(12);
            for (int y=76;y>65;--y){
                for (int x=0;x<4;x++){
                    for (int z=0;z<4;z++){
                        p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(xConer+x,y,zConer+z),Blocks.STONE.getDefaultState(),false);
                    }
                }
            }
        }

    }

    public double[] generateNoiseRegion(int p_205473_1_, int p_205473_2_) {
        return this.surfaceNoise.generateRegion((double)(p_205473_1_ << 4), (double)(p_205473_2_ << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
    }

    public void makeBase(IChunk p_202088_1_) {
        ChunkPos chunkpos = p_202088_1_.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setSeed(i, j);
        Biome[] abiome = this.biomeProvider.func_201539_b(i * 16, j * 16, 16, 16);
        p_202088_1_.setBiomes(abiome);
        this.prepareHeights(i, j, p_202088_1_);
        this.buildSurface(p_202088_1_, abiome, sharedseedrandom, this.world.getSeaLevel());
        this.makeBedrock(p_202088_1_, sharedseedrandom);
        p_202088_1_.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
        p_202088_1_.setStatus(ChunkStatus.BASE);
    }

    private double[] func_202104_a(int p_202104_1_, int p_202104_2_, int p_202104_3_, int p_202104_4_, int p_202104_5_, int p_202104_6_) {
        double[] adouble = new double[p_202104_4_ * p_202104_5_ * p_202104_6_];
        this.scaleNoise.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, 1, p_202104_6_, 1.0D, 0.0D, 1.0D);
        this.depthNoise.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, 1, p_202104_6_, 100.0D, 0.0D, 100.0D);
        double[] adouble1 = this.perlinNoise1.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        double[] adouble2 = this.lperlinNoise1.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 684.412D, 2053.236D, 684.412D);
        double[] adouble3 = this.lperlinNoise2.func_202647_a(p_202104_1_, p_202104_2_, p_202104_3_, p_202104_4_, p_202104_5_, p_202104_6_, 684.412D, 2053.236D, 684.412D);
        double[] adouble4 = new double[p_202104_5_];

        for(int i = 0; i < p_202104_5_; ++i) {
            adouble4[i] = Math.cos((double)i * Math.PI * 6.0D / (double)p_202104_5_) * 2.0D;
            double d2 = (double)i;
            if (i > p_202104_5_ / 2) {
                d2 = (double)(p_202104_5_ - 1 - i);
            }

            if (d2 < 4.0D) {
                d2 = 4.0D - d2;
                adouble4[i] -= d2 * d2 * d2 * 10.0D;
            }
        }

        int l = 0;

        for(int i1 = 0; i1 < p_202104_4_; ++i1) {
            for(int j = 0; j < p_202104_6_; ++j) {

                for(int k = 0; k < p_202104_5_; ++k) {
                    double d4 = adouble4[k];
                    double d5 = adouble2[l] / 512.0D;
                    double d6 = adouble3[l] / 512.0D;
                    double d7 = (adouble1[l] / 10.0D + 1.0D) / 2.0D;
                    double d8;
                    if (d7 < 0.0D) {
                        d8 = d5;
                    } else if (d7 > 1.0D) {
                        d8 = d6;
                    } else {
                        d8 = d5 + (d6 - d5) * d7;
                    }

                    d8 = d8 - d4;
                    if (k > p_202104_5_ - 4) {
                        double d9 = (double)((float)(k - (p_202104_5_ - 4)) / 3.0F);
                        d8 = d8 * (1.0D - d9) - 10.0D * d9;
                    }

                    if ((double)k < 0.0D) {
                        double d10 = (0.0D - (double)k) / 4.0D;
                        d10 = MathHelper.clamp(d10, 0.0D, 1.0D);
                        d8 = d8 * (1.0D - d10) - 10.0D * d10;
                    }

                    adouble[l] = d8;
                    ++l;
                }
            }
        }

        return adouble;
    }

    public void spawnMobs(WorldGenRegion p_202093_1_) {
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_177458_1_, BlockPos p_177458_2_) {
        Biome biome = this.world.getBiome(p_177458_2_);
        return biome.getSpawnableList(p_177458_1_);
    }

    public int spawnMobs(World p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
        return 0;
    }

    public UnderworldGenSetting getSettings() {
        return this.settings;
    }

    public int getGroundHeight() {
        return this.world.getSeaLevel() + 1;
    }

    public int getMaxHeight() {
        return 256;
    }
}

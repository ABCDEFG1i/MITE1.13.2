package net.minecraft.client.shader;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.JsonException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderLinkHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ShaderLinkHelper staticShaderLinkHelper;

   public static void setNewStaticShaderLinkHelper() {
      staticShaderLinkHelper = new ShaderLinkHelper();
   }

   public static ShaderLinkHelper getStaticShaderLinkHelper() {
      return staticShaderLinkHelper;
   }

   public void deleteShader(ShaderManager p_148077_1_) {
      p_148077_1_.getFragmentShaderLoader().func_195656_a();
      p_148077_1_.getVertexShaderLoader().func_195656_a();
      OpenGlHelper.glDeleteProgram(p_148077_1_.getProgram());
   }

   public int createProgram() throws JsonException {
      int i = OpenGlHelper.glCreateProgram();
      if (i <= 0) {
         throw new JsonException("Could not create shader program (returned program ID " + i + ")");
      } else {
         return i;
      }
   }

   public void linkProgram(ShaderManager p_148075_1_) throws IOException {
      p_148075_1_.getFragmentShaderLoader().attachShader(p_148075_1_);
      p_148075_1_.getVertexShaderLoader().attachShader(p_148075_1_);
      OpenGlHelper.glLinkProgram(p_148075_1_.getProgram());
      int i = OpenGlHelper.glGetProgrami(p_148075_1_.getProgram(), OpenGlHelper.GL_LINK_STATUS);
      if (i == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", p_148075_1_.getVertexShaderLoader().getShaderFilename(), p_148075_1_.getFragmentShaderLoader().getShaderFilename());
         LOGGER.warn(OpenGlHelper.glGetProgramInfoLog(p_148075_1_.getProgram(), 32768));
      }

   }
}

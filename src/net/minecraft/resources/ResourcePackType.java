package net.minecraft.resources;

public enum ResourcePackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directoryName;

   private ResourcePackType(String p_i47913_3_) {
      this.directoryName = p_i47913_3_;
   }

   public String getDirectoryName() {
      return this.directoryName;
   }
}

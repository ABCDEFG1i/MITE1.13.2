package net.minecraft.resources;

import java.util.Map;

public interface IPackFinder {
   <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_);
}

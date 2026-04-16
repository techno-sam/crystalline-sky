package io.github.slimeistdev.crystalline_sky.compat.neoforge;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.List;

@ImplClass
public class ModsImpl {
	public static boolean isModLoaded(String id) {
		List<ModInfo> mods = LoadingModList.get().getMods();
		for (ModInfo mod : mods) {
			if (mod.getModId().equals(id)) {
				return true;
			}
		}
		return false;
	}
}

package io.github.slimeistdev.crystalline_sky.neoforge;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.multiloader.Env;
import net.neoforged.fml.common.Mod;

@Mod(CrystallineSky.ID)
public class CrystallineSkyImpl {
	public CrystallineSkyImpl() {
		CrystallineSky.init();
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> CrystallineSkyClientImpl.init());
	}
}

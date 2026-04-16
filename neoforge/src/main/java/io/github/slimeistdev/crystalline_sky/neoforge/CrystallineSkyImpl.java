package io.github.slimeistdev.crystalline_sky.neoforge;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.multiloader.Env;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CrystallineSky.ID)
public class CrystallineSkyImpl {
	static IEventBus bus;

	public CrystallineSkyImpl(IEventBus bus) {
		CrystallineSkyImpl.bus = bus;

		CrystallineSky.init();
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> CrystallineSkyClientImpl.init());
	}
}

package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.DataComponentTypeHolder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class CrystallineDataComponentTypes {
	private static final CatnipRegistry REGISTRY = CrystallineSky.registry();

	public static final DataComponentTypeHolder<Unit> WEEPING_SKY_DEBUG_COLUMN_TOOL =
		REGISTRY.<Unit>dataComponentType("weeping_sky_debug_column_tool")
			.networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
			.persistent(Unit.CODEC)
			.register();

	public static void init() {}
}

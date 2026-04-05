package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.util.Unit;

import java.util.function.UnaryOperator;

public class CrystallineDataComponentTypes {
	public static final DataComponentType<Unit> WEEPING_SKY_DEBUG_COLUMN_TOOL = register(
		"weeping_sky_debug_column_tool",
		b -> b
			.networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
			.persistent(Unit.CODEC)
	);

	@SuppressWarnings("SameParameterValue")
	private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CrystallineSky.id(id), builderOperator.apply(DataComponentType.builder()).build());
	}

	public static void init() {}
}

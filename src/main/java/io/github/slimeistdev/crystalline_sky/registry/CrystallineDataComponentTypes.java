package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

import java.util.function.UnaryOperator;

public class CrystallineDataComponentTypes {
	public static final ComponentType<Unit> WEEPING_SKY_DEBUG_COLUMN_TOOL = register(
		"weeping_sky_debug_column_tool",
		b -> b
			.packetCodec(PacketCodec.unit(Unit.INSTANCE))
			.codec(Unit.CODEC)
	);

	@SuppressWarnings("SameParameterValue")
	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, CrystallineSky.id(id), builderOperator.apply(ComponentType.builder()).build());
	}

	public static void init() {}
}

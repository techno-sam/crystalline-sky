package io.github.slimeistdev.crystalline_sky.registry.client;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public enum CrystallineAtlases {
	SKYBOXES("skyboxes");

	public final ResourceLocation texture;
	public final ResourceLocation info;

	CrystallineAtlases(String name) {
		texture = CrystallineSky.id("textures/atlas/" + name + ".png");
		info = CrystallineSky.id(name);
	}

	public static void register(BiConsumer<ResourceLocation, ResourceLocation> registrar) {
		for (CrystallineAtlases atlas : values()) {
			registrar.accept(atlas.texture, atlas.info);
		}
	}
}

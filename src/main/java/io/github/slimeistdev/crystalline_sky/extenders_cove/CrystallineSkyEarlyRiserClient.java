package io.github.slimeistdev.crystalline_sky.extenders_cove;

import com.chocohead.mm.api.ClassTinkerers;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderPipelines;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.render.BlockRenderLayer;

class CrystallineSkyEarlyRiserClient {
	@Environment(EnvType.CLIENT)
	static void runClient() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

		// net.minecraft.client.render.BlockRenderLayer
		String blockRenderLayer = remapper.mapClassName("intermediary", "net.minecraft.class_11515");

		// RenderPipeline is not obfuscated
		ClassTinkerers.enumBuilder(blockRenderLayer, "Lcom/mojang/blaze3d/pipeline/RenderPipeline;", "I", "Z", "Z")
			.addEnum("CRYSTALLINE_SKY_SKY", () -> new Object[]{
				CrystallineRenderPipelines.SKY_PIPELINE,
				1536,
				false,
				false
			})
			.build();

		// net.minecraft.client.render.BlockRenderLayerGroup
		String blockRenderLayerGroup = remapper.mapClassName("intermediary", "net.minecraft.class_11531");

		ClassTinkerers.enumBuilder(blockRenderLayerGroup, "[L"+blockRenderLayer+";")
			.addEnum("CRYSTALLINE_SKY_SKY", () -> new Object[] {
				new BlockRenderLayer[] {BlockRenderLayerExt.CRYSTALLINE_SKY_SKY}
			})
			.build();
	}
}

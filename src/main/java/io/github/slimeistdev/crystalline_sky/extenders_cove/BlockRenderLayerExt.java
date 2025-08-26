package io.github.slimeistdev.crystalline_sky.extenders_cove;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BlockRenderLayer;

public class BlockRenderLayerExt {
	@Environment(EnvType.CLIENT)
	public static final BlockRenderLayer CRYSTALLINE_SKY_SKY = ClassTinkerers.getEnum(BlockRenderLayer.class, "CRYSTALLINE_SKY_SKY");
}

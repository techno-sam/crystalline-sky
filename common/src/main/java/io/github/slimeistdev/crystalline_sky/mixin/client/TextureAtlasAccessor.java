package io.github.slimeistdev.crystalline_sky.mixin.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
	@Accessor("width")
	int crystalline_sky$getWidth();

	@Accessor("height")
	int crystalline_sky$getHeight();
}

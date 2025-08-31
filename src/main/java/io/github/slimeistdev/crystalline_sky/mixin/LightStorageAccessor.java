package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightStorage.class)
public interface LightStorageAccessor {
	@Invoker("isSectionInEnabledColumn")
	boolean crystalline_sky$callIsSectionInEnabledColumn(long sectionPos);

	@Invoker("hasSection")
	boolean crystalline_sky$hasSection(long sectionPos);

	@Invoker("get")
	int crystalline_sky$get(long blockPos);

	@Invoker("set")
	void crystalline_sky$set(long blockPos, int lightLevel);

	@Invoker("method_51547")
	@Nullable ChunkNibbleArray crystalline_sky$method_51547(long sectionPos);
}

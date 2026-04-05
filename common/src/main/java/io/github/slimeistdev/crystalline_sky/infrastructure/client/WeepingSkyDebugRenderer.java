package io.github.slimeistdev.crystalline_sky.infrastructure.client;

import io.github.slimeistdev.crystalline_sky.infrastructure.WeepingStorage;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.ChunkSkyLight_Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.chunk.LightChunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class WeepingSkyDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft client;
	private @Nullable ColumnPos column = null;
	private boolean litMode = true;

	private static final int[] COLORS = {
		0xFFFF0000,
		0xFFFF7F00,
		0xFFFFFF00,
		0xFF00FF00,
		0xFF00FFFF,
		0xFF007FFF,
		0xFF0000FF,
		0xFF7F00FF,
		0xFFFF00FF,
		0xFFFF007F,
	};

	public WeepingSkyDebugRenderer(Minecraft client) {
		this.client = client;
	}

	public void setColumn(@Nullable ColumnPos column) {
		this.column = column;
	}

	public void setLitMode(boolean lit) {
		this.litMode = lit;
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (column == null)
			return;

		if (client.level == null)
			return;

		ChunkPos chunkPos = column.toChunkPos();
		LightChunk chunk = client.level.getChunkSource().getChunkForLighting(chunkPos.x, chunkPos.z);
		if (chunk == null)
			return;

		ChunkSkyLightSources chunkSkyLight = chunk.getSkyLightSources();
		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage == null)
			return;

		int localX = column.x() & 15;
		int localZ = column.z() & 15;

		int lowestSourceY = chunkSkyLight.getLowestSourceY(localX, localZ);

		Matrix4f matrix4f = matrices.last().pose();

		float x1 = (float) (column.x() - cameraX);
		float x2 = x1 + 1;
		float z1 = (float) (column.z() - cameraZ);
		float z2 = z1 + 1;

		int colorIndex = 0;

		Iterable<WeepingStorage.Run> runs = this.litMode
			? weepingStorage.iterateLitRuns(localX, localZ, lowestSourceY)
			: weepingStorage.iterateUnlitRuns(localX, localZ, lowestSourceY);
		for (WeepingStorage.Run run : runs) {
			int color = COLORS[colorIndex++ % COLORS.length];

			// limit things to a renderable range
			int bottomY = Math.max(-0xFFFF, run.bottomY());
			int topY = Math.min(run.topY(), 0xFFFF) + 1;

			float y1 = (float) (bottomY - cameraY);
			float y2 = (float) (topY - cameraY);

			// draw outlined box
			renderOutlinedBox(
				vertexConsumers, matrix4f,
				x1, x2,
				y1, y2,
				z1, z2,
				color
			);
		}
	}

	private static void renderOutlinedBox(MultiBufferSource vertexConsumers, Matrix4f matrix, float x1, float x2, float y1, float y2, float z1, float z2, int color) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.debugLineStrip(2.0));

		// render top face
		renderHorizontalSquare(matrix, x1, x2, y1, z1, z2, color, vertexConsumer);

		// render bottom face
		renderHorizontalSquare(matrix, x1, x2, y2, z1, z2, color, vertexConsumer);

		// the (x1, z1) vertical edge has been drawn, draw the other 3

		// (x2, z1)
		vertexConsumer.addVertex(matrix, x2, y2, z1).setColor(color);
		vertexConsumer.addVertex(matrix, x2, y1, z1).setColor(color);

		// (x2, z2)
		vertexConsumer.addVertex(matrix, x2, y1, z2).setColor(color);
		vertexConsumer.addVertex(matrix, x2, y2, z2).setColor(color);

		// (x1, z2)
		vertexConsumer.addVertex(matrix, x1, y2, z2).setColor(color);
		vertexConsumer.addVertex(matrix, x1, y1, z2).setColor(color);
	}

	private static void renderHorizontalSquare(Matrix4f matrix, float x1, float x2, float y, float z1, float z2, int color, VertexConsumer vertexConsumer) {
		vertexConsumer.addVertex(matrix, x1, y, z1).setColor(color);
		vertexConsumer.addVertex(matrix, x2, y, z1).setColor(color);
		vertexConsumer.addVertex(matrix, x2, y, z2).setColor(color);
		vertexConsumer.addVertex(matrix, x1, y, z2).setColor(color);
		vertexConsumer.addVertex(matrix, x1, y, z1).setColor(color);
	}

	@Override
	public void clear() {
		this.column = null;
	}
}

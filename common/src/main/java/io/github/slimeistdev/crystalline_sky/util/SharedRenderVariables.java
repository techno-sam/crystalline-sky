package io.github.slimeistdev.crystalline_sky.util;

import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Matrix4f;

public class SharedRenderVariables {
	public static final Matrix4f invSkyboxMat = new Matrix4f();
	// because chunk meshing is multithreaded :(
	public static final ThreadLocal<MutableInt> shadeFullBright = ThreadLocal.withInitial(() -> new MutableInt(0));
	private static int blockIrisShaderFramebufferBind = 0;

	public static boolean shouldShadeFullBright() {
		return shadeFullBright.get().intValue() > 0;
	}

	public static void pushShadeFullBright() {
		shadeFullBright.get().increment();
	}

	public static void popShadeFullBright() {
		shadeFullBright.get().decrement();
	}

	public static boolean shouldBlockIrisShaderFramebufferBind() {
		return blockIrisShaderFramebufferBind > 0;
	}

	public static void pushBlockIrisShaderFramebufferBind() {
		blockIrisShaderFramebufferBind++;
	}

	public static void popBlockIrisShaderFramebufferBind() {
		blockIrisShaderFramebufferBind--;
	}
}

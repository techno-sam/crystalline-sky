package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Holder.Reference.class)
public interface Holder$ReferenceAccessor<T> {
	@Accessor("owner")
	HolderOwner<T> crystalline_sky$getOwner();

	@Invoker("bindValue")
	void crystalline_sky$bindValue(T value);
}

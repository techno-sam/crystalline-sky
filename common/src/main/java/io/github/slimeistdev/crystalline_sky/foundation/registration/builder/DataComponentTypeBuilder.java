package io.github.slimeistdev.crystalline_sky.foundation.registration.builder;

import com.mojang.serialization.Codec;
import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.DataComponentTypeHolder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public class DataComponentTypeBuilder<T> extends AbstractBuilder<DataComponentType<?>, DataComponentType<T>, DataComponentTypeHolder<T>> {
	private final DataComponentType.Builder<T> builder;

	public DataComponentTypeBuilder(CatnipRegistry owner, String name) {
		super(owner, name, BuiltInRegistries.DATA_COMPONENT_TYPE);
		this.builder = DataComponentType.builder();
	}

	public DataComponentTypeBuilder<T> persistent(Codec<T> codec) {
		this.builder.persistent(codec);
		return this;
	}

	public DataComponentTypeBuilder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
		this.builder.networkSynchronized(streamCodec);
		return this;
	}

	public DataComponentTypeBuilder<T> cacheEncoding() {
		this.builder.cacheEncoding();
		return this;
	}

	@Override
	DataComponentType<T> build() {
		return builder.build();
	}

	@Override
	DataComponentTypeHolder<T> getHolder(HolderOwner<DataComponentType<?>> owner, ResourceKey<DataComponentType<?>> key) {
		//noinspection rawtypes,unchecked
		return new DataComponentTypeHolder<T>((HolderOwner) owner, (ResourceKey) key);
	}
}

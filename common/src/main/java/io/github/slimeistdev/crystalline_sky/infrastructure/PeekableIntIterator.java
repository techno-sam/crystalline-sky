package io.github.slimeistdev.crystalline_sky.infrastructure;

import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.NoSuchElementException;

public class PeekableIntIterator implements IntIterator {
	private final IntIterator wrapped;

	private boolean hasPeeked = false;
	private int peekedValue;

	public PeekableIntIterator(IntIterator wrapped) {
		this.wrapped = wrapped;
	}

	public int peek() {
		if (!hasPeeked) {
			if (!wrapped.hasNext()) {
				throw new NoSuchElementException();
			}
			peekedValue = wrapped.nextInt();
			hasPeeked = true;
		}
		return peekedValue;
	}

	@Override
	public int nextInt() {
		if (hasPeeked) {
			hasPeeked = false;
			return peekedValue;
		}

		return wrapped.nextInt();
	}

	@Override
	public boolean hasNext() {
		return hasPeeked || wrapped.hasNext();
	}
}

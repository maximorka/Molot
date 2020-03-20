package com.molot.lowlevel.rw.stub;

import com.molot.common.collection.BooleanQueue;
import com.molot.lowlevel.channel.IReadStream;
import com.molot.lowlevel.channel.IWriteStream;

public class StubReadWriteStream implements IWriteStream, IReadStream {
	private BooleanQueue bits = new BooleanQueue(1000);
	private int maxSizeInBits = 768 * 8;
	
	@Override
	public boolean canWrite() {
		return bits.size() <= maxSizeInBits;
	}

	@Override
	public void write(boolean bit) {
		bits.add(bit);
	}

	@Override
	public boolean canRead() {
		return bits.size() > 0;
	}

	@Override
	public boolean readBit() {
		return bits.poll();
	}

	public void reset() {
		bits.clear();
	}
	
	public BooleanQueue getBits() {
		return bits;
	}
}

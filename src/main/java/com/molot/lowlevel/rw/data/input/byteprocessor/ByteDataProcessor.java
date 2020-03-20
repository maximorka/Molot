package com.molot.lowlevel.rw.data.input.byteprocessor;

import com.molot.common.collection.BooleanQueue;
import com.molot.common.utils.ByteUtils;

public class ByteDataProcessor {
	private BooleanQueue rawBits = new BooleanQueue(1000);
	private ByteDataListener byteDataListener;
	
	public ByteDataProcessor(ByteDataListener byteDataListener) {
		this.byteDataListener = byteDataListener;
	}
	
	public void addBit(boolean bit) {
		rawBits.add(bit);
		
		while(rawBits.size() >= 8) {
			boolean[] bitArray = new boolean[8];
			for(int i = 0; i < 8; i++) {
				bitArray[i] = rawBits.poll();
			}
			
			if (byteDataListener != null) {
				byteDataListener.byteReceived(ByteUtils.getByte(bitArray));
			}
		}
	}
	
	public void reset() {
		rawBits.clear();
	}
}

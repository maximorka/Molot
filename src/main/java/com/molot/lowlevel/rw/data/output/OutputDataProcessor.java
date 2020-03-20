package com.molot.lowlevel.rw.data.output;

import com.molot.common.collection.BooleanArray;
import com.molot.common.utils.ByteUtils;
import com.molot.Core;
import com.molot.Protocol;
import com.molot.lowlevel.rw.ReaderWriter;
//import packet.com.molot.util.BitLogger;
import com.molot.util.Params;

public class OutputDataProcessor {
	public static final int WAKE_UP_BYTE_COUNT = Params.SETTINGS.getInt("modem-hot-byte-count");
	public static final boolean LOG = Params.SETTINGS.getBoolean("log-raw-bits");

	private BooleanArray bits = new BooleanArray(5000);
	
	private boolean canSend = true;
	private boolean rawMode ;
	
	//private BitLogger bitLogger;

	public OutputDataProcessor() {

		if (LOG) {
			//bitLogger = new BitLogger("output-processor");
		}
	}
	
	synchronized public void addByte(int b) {
		byte tmp = ByteUtils.getLastByteFromInt(b);
		boolean[] bitArray = ByteUtils.getBits(tmp);
		for(boolean bit: bitArray) {
			bits.add(bit);
		}
	}
	
	synchronized public void addBits(boolean[] bitsToAdd) {
		for(boolean bit: bitsToAdd) {
			bits.add(bit);
		}
	}
	
	synchronized public void addBit(boolean bit) {
		bits.add(bit);
	}
	
	private boolean[] createOutputStream() {
		if (Core.rw() == null) {
			addZeroBitsIfNeeded();
		} else {
			if (!rawMode && Core.rw().getBufferSizeInBits() < 768 * 8) {
				addZeroBitsIfNeeded();
			}
		}
		
		boolean[] result = new boolean[getOutputStreamBitCount()];

		fillOutputStream(result);
		bits.clear();
		
		return result;
	}
	
	synchronized public void flush(ReaderWriter writeStream) {
		if (!Core.isReady() && !rawMode && Core.rw().getWriter().canWrite()) {
			return;
		}
		
		if (!rawMode && Core.rw().getBufferSizeInBits() > 768 * 8 - 400) {
			addWakeUpBits();
		}
		
		if (writeStream.getWriter().canWrite()) {
			boolean[] bitsToSend = createOutputStream();
			
			if (LOG) {
				//bitLogger.log(bitsToSend);
			}
			
			byte[] bytes = ByteUtils.getBytes(bitsToSend);
			writeStream.writeBytesAsIs(bytes);
		}
	}

	private void addWakeUpBits() {
		for(int i = 0; i < WAKE_UP_BYTE_COUNT; i++) {
			for(boolean bit: Protocol.WAKE_UP_MARK) {
				bits.insertAsFirst(bit);
			}
		}
	}
	
	private void addZeroBitsIfNeeded() {
		if (bits.size() % 6 == 0) {
			return;
		}
		
		int currentSize = bits.size();
		int additionalBitCount = 6 - currentSize % 6;
		
		for(int i = 0; i < additionalBitCount; i++) {
			bits.insertAsFirst(false);
		}
	}
	
	private int getOutputStreamBitCount() {
		if (rawMode) {
			return bits.size();
		}
		
		return (bits.size() / 6) * 8;
	}
	
	private void fillOutputStream(boolean[] readyToSendBits) {
		int bytesIndex = 0;

		if (rawMode) {
			for(int i = 0; i< bits.size(); i++) {
				readyToSendBits[bytesIndex++] = bits.get(i);
			}
			return;
		}
		
		for(int i = 0; i < bits.size(); i++) {
			if (bytesIndex % 8 == 0 && bytesIndex < readyToSendBits.length - 2) {
				readyToSendBits[bytesIndex++] = true;
				readyToSendBits[bytesIndex++] = true;
			}
			//EXCEPTION DETECTED
			if (bytesIndex >= readyToSendBits.length) {
				System.out.println("EXCEPTION: " + bits.size() + ", " + readyToSendBits.length);
			}
			readyToSendBits[bytesIndex++] = bits.get(i);
		}
	}
	
	public void lockSend() {
		canSend = false;
	}
	
	public void unlockSend() {
		canSend = true;
	}
	
	public boolean canSend() {
		return canSend && bits.size() > 0;
	}
	
	public static void main(String[] args) {
//		OutputDataProcessor d = new OutputDataProcessor();
//		
//		for(int i = 0; i < 990; i++) {
//			d.addBit(false);
//		}
//		boolean[] bits = d.createOutputStream();
//		System.out.println("BLEN: " + bits.length);
		for(int i = 0; i < 200; i++) {
			int bitCount1 = i / 6 * 8;
			int bitCount2 = i / 3 * 4;
			if (bitCount1 != bitCount2) {
				System.out.println(i + ": " + bitCount1 + ", " + bitCount2); // 993 - 1320, 1324
			}
		}
	}

	public void reset() {
		lockSend();
		bits.clear();
		unlockSend();
	}
	
	public void setWorkInRawMode(boolean rawMode) {
		reset();
		this.rawMode = rawMode;
	}
	
	public void close() {
		//if (LOG && bitLogger != null) {
		//	bitLogger.save();
		//}
	}
}

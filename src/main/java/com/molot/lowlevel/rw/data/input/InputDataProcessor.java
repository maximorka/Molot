package com.molot.lowlevel.rw.data.input;

import com.molot.common.utils.ByteUtils;

import com.molot.util.Params;

public class InputDataProcessor {
	public static final int BUFFER_SIZE_IN_BYTES = 768;
	public static final boolean LOG = Params.SETTINGS.getBoolean("log-raw-bits");
	public static final int INFO_BIT_COUNT = 6;

	private InputDataEventListener dataListener;
	private boolean workInRawMode;
	
	//private BitLogger bitLogger;
	
	public InputDataProcessor(InputDataEventListener dataListener) {
		this();
		this.dataListener = dataListener;
	}
	
	public InputDataProcessor() {
		if (LOG) {
			//bitLogger = new BitLogger("input-processor");
		}
	}
	
	synchronized public void addByte(int b) {
		boolean[] bits = ByteUtils.getBits(ByteUtils.getLastByteFromInt(b));
		
		if (workInRawMode) {
			dataListener.informationBitsReceived(bits, 0);
		} else {
			if (bits[0] && bits[1]) {
				boolean[] subData = new boolean[INFO_BIT_COUNT];
				for(int i = 8 - INFO_BIT_COUNT; i < bits.length; i++) {
					subData[i - (8 - INFO_BIT_COUNT)] = bits[i];
				}
				if (LOG) {
					//bitLogger.log(subData);
				}
				
				dataListener.informationBitsReceived(bits, 8 - INFO_BIT_COUNT);
			} else if (!bits[0]) {
				int freeBufferPercent = 100 - b;

				dataListener.bufferPercentReceived(freeBufferPercent * BUFFER_SIZE_IN_BYTES / 100);
			}
		}
	}
	
	public void setWorkInRawMode(boolean workInRawMode) {
		this.workInRawMode = workInRawMode;
	}
	
	public void close() {
		//if (LOG && bitLogger != null) {
		//	bitLogger.save();
		//}
	}
}

package com.molot.lowlevel.rw;

import com.molot.lowlevel.channel.IChannel;

public interface ReaderWriter extends IChannel {
	public static final int MAX_BUFFER_SIZE_IN_BITS = 768 * 8;
	
	public void writeByte(int b);
	public void writeByteAsIs(byte b);
	public void writeBytesAsIs(byte[] bytes);
	public void reset();
	public void flush();
	public void workInRawMode(boolean rawMode);
	public void close();

	public void sendTest();
	public boolean isClosed();
	public int getBufferSizeInBits();
	public void reinit();
//	public void writeBits(boolean[] bits);
}

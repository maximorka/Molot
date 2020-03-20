package com.molot.lowlevel.rw.stub;

import com.molot.common.utils.ByteUtils;
import com.molot.lowlevel.channel.IReadStream;
import com.molot.lowlevel.channel.IWriteStream;
import com.molot.lowlevel.rw.ReaderWriter;
import com.molot.lowlevel.rw.data.input.InputDataEventListener;
import com.molot.lowlevel.rw.data.input.InputDataProcessor;
import com.molot.lowlevel.rw.data.output.OutputDataProcessor;

public class StubReaderWriter implements ReaderWriter, InputDataEventListener {
	private StubReadWriteStream writeStream, readStream;
	private OutputDataProcessor outputProcessor = new OutputDataProcessor();
	private InputDataProcessor inputDataProcessor = new InputDataProcessor(this);
	
	private boolean running = true;
	
	class UpdateThread extends Thread {
		@Override
		public void run() {
			while(running) {
				if (writeStream.canRead()) {
					for(int i = 0; i < 100; i++) {
						if (writeStream.canRead()) {
							readStream.write(writeStream.getBits().poll());
						} else{
							break;
						}
					}
				} else {
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public StubReaderWriter() {
		writeStream = new StubReadWriteStream();
		readStream = new StubReadWriteStream();
		
		new UpdateThread().start();
	}

	@Override
	public void sendTest() {
		System.out.println("ghnghjgh");
	}

	@Override
	public IReadStream getReader() {
		return readStream;
	}

	@Override
	public IWriteStream getWriter() {
		return writeStream;
	}

	@Override
	public int getSpeedInBytes() {
		return 1000;
	}

	@Override
	public void writeByte(int b) {
		outputProcessor.addByte(b);
	}

	@Override
	public void writeByteAsIs(byte b) {
		boolean[] bits = ByteUtils.getBits(b);
		for(boolean bit: bits) {
			writeStream.write(bit);
		}
	}

	@Override
	public void writeBytesAsIs(byte[] bytes) {
		for(byte b: bytes) {
			writeByteAsIs(b);
		}
	}

	@Override
	public void reset() {
		outputProcessor.reset();
		writeStream.reset();
		readStream.reset();
	}

	@Override
	public void flush() {
		outputProcessor.flush(this);
	}

	@Override
	public void workInRawMode(boolean rawMode) {
		outputProcessor.setWorkInRawMode(true);
		inputDataProcessor.setWorkInRawMode(true);
	}

	@Override
	public void close() {
		running = false;
	}

	@Override
	public boolean isClosed() {
		return running;
	}

	@Override
	public int getBufferSizeInBits() {
		return 768 * 8 - writeStream.getBits().size();
	}

	@Override
	public void informationBitsReceived(boolean[] bits, int excludeCount) {
		for(int i = excludeCount; i < bits.length; i++) {
			readStream.write(bits[i]);
		}
	}

	@Override
	public void bufferPercentReceived(int byteCount) {}

	@Override
	public void reinit() {
	}
}

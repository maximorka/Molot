package com.molot.lowlevel.rw.multi;

import com.molot.lowlevel.channel.IReadStream;
import com.molot.lowlevel.channel.IWriteStream;
import com.molot.lowlevel.rw.ReaderWriter;

public class MultiReaderWriter implements ReaderWriter {
	/**
	 * Звідки ми читаємо дані (COM-порт модем, наприклад)
	 */
	private ReaderWriter input;
	/**
	 * Куди ми пишемо дані (Ethernet, наприклад)
	 */
	private ReaderWriter output;
	
	public MultiReaderWriter(ReaderWriter input, ReaderWriter output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public void sendTest() {
		System.out.println("gfghgfht11111");
	}

	@Override
	public IReadStream getReader() {
		return input.getReader();
	}

	@Override
	public IWriteStream getWriter() {
		return output.getWriter();
	}

	@Override
	public int getSpeedInBytes() {
		return input.getSpeedInBytes();
	}

	@Override
	public void writeByte(int b) {
		if (b <= 192) { //команди
			input.writeByte(b);
		}
		output.writeByte(b);
	}

	@Override
	public void writeByteAsIs(byte b) {
		if (b <= 192) { // команди
			input.writeByteAsIs(b);
		}
		output.writeByteAsIs(b);
	}

	@Override
	public void reset() {
		input.reset();
		output.reset();
	}

	@Override
	public void flush() {
		output.flush();
		input.flush();
	}

	@Override
	public void workInRawMode(boolean rawMode) {
		input.workInRawMode(rawMode);
		output.workInRawMode(rawMode);
	}

	@Override
	public void close() {
		input.close();
		output.close();
	}
	
	public ReaderWriter getInput() {
		return input;
	}
	
	public ReaderWriter getOutput() {
		return output;
	}

	@Override
	public int getBufferSizeInBits() {
		return output.getBufferSizeInBits();
	}

	@Override
	public boolean isClosed() {
		return input.isClosed() || output.isClosed();
	}

	@Override
	public void writeBytesAsIs(byte[] bytes) {
		for(byte b: bytes) {
			writeByteAsIs(b);
		}
	}

	@Override
	public void reinit() {
		input.reinit();
		output.reinit();
	}
}

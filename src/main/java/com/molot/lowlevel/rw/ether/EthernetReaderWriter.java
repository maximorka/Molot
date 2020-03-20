package com.molot.lowlevel.rw.ether;

import com.molot.common.collection.BooleanQueue;
import com.molot.lowlevel.channel.IChannel;
import com.molot.lowlevel.channel.IReadStream;
import com.molot.lowlevel.channel.IWriteStream;
import com.molot.lowlevel.rw.ReaderWriter;
import com.molot.lowlevel.rw.data.input.InputDataEventListener;
import com.molot.lowlevel.rw.data.input.InputDataProcessor;
import com.molot.lowlevel.rw.data.output.OutputDataProcessor;
import com.molot.util.Params;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class EthernetReaderWriter implements ReaderWriter, IReadStream, IWriteStream, IChannel, InputDataEventListener {
	public static final byte HEARTBEAT_BYTE = Params.PROTOCOL.getByte("ethernet-reset-timeout-timer");
	public static final int SENDER_BUFFER_SIZE = 256;
	
	public static final int CHT_MODE = 0;
	public static final int DCHT_MODE = 1;
	
	private int heartbeatIntervalInSeconds = Params.SETTINGS.getInt("ethernet-heartbeat-timeout-in-seconds");
	
	private int heartbeatIntervalInMilliseconds = heartbeatIntervalInSeconds * 1000 * 2;
	private long backHeartbeatTime = System.currentTimeMillis();
	
	private Thread networkThread;
	private boolean running = true;
	
	private Socket socket;
	private DataInputStream iStream;
	private DataOutputStream oStream;
	private String ip;
	private int port;
	
	private int timeout = Params.SETTINGS.getInt("ethernet-connect-timeout");
	
	private InputDataProcessor inputProcessor;
	private OutputDataProcessor outputProcessor;
	
	private BooleanQueue receivedBitQ = new BooleanQueue(2000);
	
	private int workMode = 0;
	private int speed;
	
	private Queue<Byte> byteSendQ = new LinkedBlockingQueue<Byte>();
	
	private int bufferSizeInBits = InputDataProcessor.BUFFER_SIZE_IN_BYTES * 8;
	
	private volatile boolean setupMode;
	
	private int heartbeatCounter = 0;
	
	private volatile boolean canRead;

	private Runnable updateRunnable = new Runnable() {
		
		@Override
		public void run() {
			while(true) {
				if (!running) {
					return;
				}
				
				if (setupMode) {
					continue;
				}
				
				if (ip == null || port <= 0) {
					return;
				}
				
				if (connect()) {

					try {
						read();
						write();
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("Can't connect, close");
						closeSocket();
					}
				} else {
					System.out.println("disconnected...");
				}
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private boolean connect() {
			if (socket != null) {
				long difference = System.currentTimeMillis() - backHeartbeatTime;
				if (difference > heartbeatIntervalInMilliseconds) {
					backHeartbeatTime = System.currentTimeMillis();
					System.out.println("broken heartbeat, close & restart");
					closeSocket();
					return false;
				} else {
					return true;
				}
			}
			
			try {
				System.out.println("creating new socket");
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), timeout);
				
				iStream = new DataInputStream(socket.getInputStream());
				oStream = new DataOutputStream(socket.getOutputStream());
				System.out.println("Socket created!");
				
				init();
				
				backHeartbeatTime = System.currentTimeMillis();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("can't connect");
				iStream = null;
				oStream = null;
				socket = null;
				return false;
			}
		}
		
		private void read() throws IOException {
			int readCount = iStream.available();
			
			if (readCount > 0) {
				backHeartbeatTime = System.currentTimeMillis();
			}
			
			for(int i = 0; i < readCount; i++) {
				byte b = iStream.readByte();
				handleReadByte(b);
			}
		}
		
		private void handleReadByte(byte b) {
			inputProcessor.addByte(b);
		}
		
		private void write() throws IOException {

			if (byteSendQ.isEmpty() && !setupMode) {

				heartbeatCounter++;
				if (heartbeatCounter >= 200) {
					heartbeatCounter = 0;
					oStream.writeByte(HEARTBEAT_BYTE);
					oStream.flush();
				}
			} else {
				heartbeatCounter = 0;
			}
			
			int count = 0;
			while(bufferSizeInBits > 8) {
				if (!byteSendQ.isEmpty()) {
					byte toSend = byteSendQ.poll();
					System.out.println("B:"+toSend);
					oStream.writeByte(toSend);
					count++;
					if (count >= 10) {
						count = 0;
					}
				} else {
					break;
				}
				
				bufferSizeInBits -= 8;
				//System.out.println("BUF_SIZE:"+bufferSizeInBits);
			}
				
			oStream.flush();
		}
	};
	
	public EthernetReaderWriter(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		inputProcessor = new InputDataProcessor(this);
		outputProcessor = new OutputDataProcessor();
		
		networkThread = new Thread(updateRunnable);
		networkThread.start();
	}
	
	@Override
	public IReadStream getReader() {
		return this;
	}

	@Override
	public IWriteStream getWriter() {
		return this;
	}

	@Override
	public boolean canRead() {
		return !canRead && !receivedBitQ.isLocked() && receivedBitQ.size() > 0;
	}

	@Override
	public boolean readBit() {
		return receivedBitQ.poll();
	}

	@Override
	public boolean canWrite() {
		return bufferSizeInBits > 8;
	}

	@Override
	public void write(boolean bit) {
		bufferSizeInBits--;
		outputProcessor.addBit(bit);
	}

	@Override
	public int getSpeedInBytes() {
		if (workMode == DCHT_MODE) {
			return 2 * speed;
		};
		
		return speed;
	}

	public boolean isConnected() {
		return iStream != null;
	}

	@Override
	public void writeByte(int b) {
		//System.out.println("Et: "+b );
		bufferSizeInBits -= 8;
		outputProcessor.addByte(b);

	}

	@Override
	public void writeByteAsIs(byte b) {

		bufferSizeInBits -= 8;
		byteSendQ.add(b);
	}

	@Override
	public void reset() {
		heartbeatCounter = 0;
		outputProcessor.reset();
		receivedBitQ.clear();
	}

	@Override
	public void flush() {
		outputProcessor.flush(this);
	}

	@Override
	public void workInRawMode(boolean rawMode) {
		outputProcessor.setWorkInRawMode(rawMode);
		inputProcessor.setWorkInRawMode(rawMode);
	}

	@Override
	public void close() {
		System.out.println("close");
		running = false;

		byteSendQ.clear();
		closeSocket();
		
		inputProcessor.close();
		outputProcessor.close();
	}
	@Override
	public void sendTest(){
		System.out.println("SEND");
		byteSendQ.add((byte)6);
		byteSendQ.add((byte)27);

		if (socket != null) {
			try {
				System.out.println("SEND");
				socket.getOutputStream().write(6);
				socket.getOutputStream().write(27);
				socket.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	public void closeSocket() {
		try {
			if (socket != null) {
				socket.getOutputStream().write(Params.PROTOCOL.getInt("close-in-out"));
				socket.getOutputStream().write(Params.PROTOCOL.getInt("ethernet-reset-socket"));
				socket.getOutputStream().flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("socull");
				if (socket != null) {
					socket.close();
					socket = null;
				}
			} catch (Exception unhandled) {
				unhandled.printStackTrace();
			}
			
			byteSendQ.clear();
			
			socket = null;
			iStream = null;
			oStream = null;
		}
	}

	@Override
	public void informationBitsReceived(boolean[] bits, int excludeCount) {
		for(int i = excludeCount; i < bits.length; i++) {
			receivedBitQ.add(bits[i]);
		}
	}

	@Override
	public void bufferPercentReceived(int byteCount) {
		bufferSizeInBits = byteCount * 8;
	}
	
	private void init() {
		setupMode = true;

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				reset();
				if (Params.RUNTIME.exists("work-mode")) {
					System.out.println("reinit");
					
					byteSendQ.clear();
					outputProcessor.reset();
					
					byte initByte = Params.PROTOCOL.getByte("modem-setup");
					byte workMode = Params.RUNTIME.getByte("work-mode");

					
					byteSendQ.add(initByte);
					byteSendQ.add(workMode);
					
					byte packetSizeByteCommand = Params.PROTOCOL.getByte("set-received-packet-size");
					byte packetSize = Params.SETTINGS.getByte("received-packet-size");
					byteSendQ.add(packetSizeByteCommand);
					byteSendQ.add(packetSize);
				}
				setupMode = false;
			}
		}, 400);
		
	}
	
	public static void main(String[] args) {
		new EthernetReaderWriter("192.168.0.1", 6969);
	}

	@Override
	public int getBufferSizeInBits() {
		return bufferSizeInBits;
	}

	@Override
	public boolean isClosed() {
		return socket == null || socket.isClosed();
	}

	@Override
	public void writeBytesAsIs(byte[] bytes) {
		for(byte b: bytes) {
			writeByteAsIs(b);
		}
	}

	@Override
	public void reinit() {
		init();
	}
}

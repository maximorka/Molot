package com.molot.bit;

import com.molot.common.collection.BooleanArray;
import com.molot.common.utils.ByteUtils;
import com.molot.bit.state.Border;
import com.molot.bit.state.InPacketBorder;
import com.molot.bit.state.Receive;
import com.molot.bit.state.State;
import com.molot.bit.state.Wait;
import com.molot.util.Params;

/**
 * Кінцевий автомат для розбору бітового потоку та виявлення в ньому окремих пакетів. Щоб отримувати повідомлення про збирання 
 * пакету, необхідно встановити слухача методом setPacketListener
 * 
 * @author integer
 */
public class BitStateMachine {
	public static final int ONE_BIT_COUNT_IN_PACKET_MASK = Params.SETTINGS.getInt("packet-divide-mask-bit-count");
	public static final int STAFFING_BIT_COUNT = ONE_BIT_COUNT_IN_PACKET_MASK - 1;
	
	private BooleanArray rawStream;
	private BooleanArray information;
	
	private State wait = new Wait(this);
	private State border = new Border(this);
	private State receive = new Receive(this);
	private State inPacketBorder = new InPacketBorder(this);
	
	private boolean trueMaskBitsReceived;
	
	private State currentState;
	
	//private PacketListener packetListener;
	
	private long packetStartTime;
	
	public BitStateMachine() {
		rawStream = new BooleanArray();
		information = new BooleanArray();
		currentState = wait;
	}
	
//	public BitStateMachine(PacketListener listener) {
//		this();
//		packetListener = listener;
//	}
	
	/**
	 * Скидає стан кінцевого автомату
	 */
	public void reset() {
		information.clear();
		rawStream.clear();
		currentState = wait;
		trueMaskBitsReceived = false;
	}
	
	/**
	 * Додає "сирий" біт в кінцевий автомат
	 */
	public void addBit(boolean bit) {
		addRawBit(bit);
		
		//if (information.size() == PacketRuntime.getInstance().getLongPacketSizeInBits()) {
		//	currentState.receiveFullPacket();
	//	} else
			if (getLastBit1CountAtRawStream() >= ONE_BIT_COUNT_IN_PACKET_MASK) {
			currentState.receivePacketMaskBits();
		} else if (getLastBit1CountAtRawStream() == STAFFING_BIT_COUNT) {
			currentState.receiveStaffingBits();
		} else if (bit) {
			currentState.receiveBit1();
		} else if (!bit){
			currentState.receiveBit0();
		}
	}
	
	/**
	 * Додає массив "сирих" біт
	 */
	public void addBits(boolean ... bits) {
		for(boolean bit : bits) {
			addBit(bit);
		}
	}
	
	private int getLastBit1CountAtRawStream() {
		int result = 0;
		
		for(int i = rawStream.size()-1; i >= 0; i--) {
			if (rawStream.get(i)) {
				result ++;
			} else {
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Встановлює поточний стан автомату
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	
	/**
	 * Стан "Очікування"
	 */
	public State getWait() {
		return wait;
	}
	
	/**
	 * Стан "Отримання корисних даних"
	 */
	public State getReceive() {
		return receive;
	}
	
	/**
	 * Стан "Межа між пакетами"
	 */
	public State getBorder() {
		return border;
	}
	
	/**
	 * Стан "Потенційна межа всередині пакету"
	 */
	public State getInPacketBorder() {
		return inPacketBorder;
	}
	
	/**
	 * Додає інформаційний біт, якщо в даний час приймається пакет. Якщо пакет повністю зібраний - 
	 * відсилає поточному стану команду про збирання повного пакета.
	 * 
	 * Є три варіанти пакетів - 320 біт (повний інформаційний пакет),  і 88 біт (короткі пакети-квитанції)
	 */
	public void addInformationBit(boolean bit) {
		information.add(bit);
//		if (information.size() == PacketRuntime.getInstance().getShortPacketSizeInBits()) { //потенційно можливий прихід короткого пакету підтвердження з одним додатковим байтом
//			boolean[] tmpBits = getInformationBits();
//			Scrambler.scramble(tmpBits);
//			ShortPacket confirmPacket = new ShortPacket(tmpBits);
//
//			if (confirmPacket.getState() != ReceivedPacketState.dropped && confirmPacket.getNumber() > 60_000) {
//				notifyPacketReceived(getInformationBits());
//				reset();
//			}
//		} else if (information.size() == PacketRuntime.getInstance().getLongPacketSizeInBits()) {
//			currentState.receiveFullPacket();
//		}
	}
	
	private boolean[] getInformationBits() {
		boolean[] result = new boolean[information.size()];
		for(int i = 0, end = result.length; i < end; i++) {
			result[i] = information.get(i);
		}
		return result;
	}
	
//	public void notifyPacketReceived(boolean[] data) {
//		if (packetListener != null) {
//			long time = System.currentTimeMillis() - packetStartTime;
//			packetListener.packetReceived(data, time);
//		}
//	}
	
	public BooleanArray getInformation() {
		return information;
	}
	
	//public void setPacketListener(PacketListener packetListener) {
		//this.packetListener = packetListener;
	//}
	
	private void addRawBit(boolean bit) {
		rawStream.add(bit);
		if (rawStream.size() > ONE_BIT_COUNT_IN_PACKET_MASK) {
			rawStream.removeFirst();
		}
	}
	
	public void clearRawStream() {
		rawStream.clear();
	}

	public void notifyAboutPacketStart() {
		packetStartTime = System.currentTimeMillis();
		//if (packetListener != null) {
		//	packetListener.startReceive();
		//}
	}
	
	public boolean isTrueBitMaskReceived() {
		return trueMaskBitsReceived;
	}
	
	public void setTrue16BitsReceived(boolean true6BitsReceived) {
		this.trueMaskBitsReceived = true6BitsReceived;
	}
	
	public boolean getLastBitWithOffset(int offset) {
		int index = information.size() - offset - 1;
		if (index < 0) {
			return false;
		}
		
		return information.get(information.size() - offset - 1);
	}
	
	public static void main(String[] args) {
		BitStateMachine m = new BitStateMachine();
//		m.setPacketListener(new PacketListener() {
//
//			@Override
//			public void startReceive() {
//
//			}
//
//			@Override
//			public void packetReceived(boolean[] packet, long receiveTime) {
//				Scrambler.scramble(packet);
//				if (packet.length == 320) {
//					LongPacket p = new LongPacket(packet);
//					System.out.println(p.isCorrect());
//					System.out.println(p.getNumber());
//				} else {
//					ShortPacket sP = new ShortPacket(packet);
//					System.out.println(sP.getState());
//					System.out.println(sP.getNumber());
//				}
//			}
//		});
		

		String b4 = "0111111111111000011011001101011010001000110110001011101101011111000010010001001110100011011001011010110111101100011010001010100001000010100111110010110110011101101011000011010000000101110100100010111101110100011111110001000000000110011000011010010100101000100000011001110110111010100111000100100010101011000000001001101101011011111101";
		boolean[] bits = ByteUtils.getBits(b4);
		for(boolean bit: bits) {
			m.addBit(bit);
		}
	}
	
	@SuppressWarnings("unused")
	private static boolean has10AndMoreTrueBits(int value) {
		byte[] bytes = ByteUtils.convertIntToByteArray(value);
		String t = "";
		t += ByteUtils.bitsToString(ByteUtils.getBits(bytes[2]));
		t += ByteUtils.bitsToString(ByteUtils.getBits(bytes[3]));
		
		t = t.replace(" ", "");
		if (t.contains("1111111111") || t.endsWith("1")) {
			return true;
		} else {
			System.out.println(value + "- " + t);
		}
		return false;
	}

	public void clearInformationBits() {
		information.clear();
	}
}

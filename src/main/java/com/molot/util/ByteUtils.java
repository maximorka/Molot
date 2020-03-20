package com.molot.util;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByteUtils {
	public static final int MASK_15 = 1 << 15; //маска для зміни 15 біту в int. Необхідно для переведення останніх двох байт int в тип даних short

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static final ByteBuffer SHORT_BUFFER = ByteBuffer.allocate(2);
	private static final StringBuilder BIT_STRING_BUILDER = new StringBuilder();
	
	public static final int[] CRC_TABLE = {
			0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
			0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
			0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
			0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
			0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
			0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
			0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
			0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
			0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
			0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
			0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
			0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
			0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
			0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
			0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
			0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
			0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
			0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
			0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
			0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
			0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
			0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
			0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
			0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
			0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
			0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
			0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
			0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
			0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
			0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
			0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
			0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
	};
	
	private static Map<Integer, Byte> masks;
	
	static {
		masks = new HashMap<Integer, Byte>();
		for(int i = 7; i >= 0; i--) {
			byte result = 1;
			for(int j = 0; j < i; j++) {
				result *= 2;
			}
			masks.put(i, result);
		}
		
	}
	
	public static boolean[] getBits(byte b) {
		int index = b;
		if (b < 0) {
			index = 256 + b;
		}
		return ByteBitTable.BIT_TABLE[index];
	}
	
	public static boolean[] getBits(byte[] bytes) {
		boolean[] result = new boolean[8 * bytes.length];
		
		for(int byteIndex = 0; byteIndex < bytes.length; byteIndex++) {
			boolean[] bits = getBits(bytes[byteIndex]);
			System.arraycopy(bits, 0, result, byteIndex * 8, 8);
		}
		
		return result;
	}
	
	public static boolean[] getBits(List<Byte> bytes) {
		boolean[] result = new boolean[8 * bytes.size()];
		
		for(int byteIndex = 0; byteIndex < bytes.size(); byteIndex++) {
			boolean[] bits = getBits(bytes.get(byteIndex));
			System.arraycopy(bits, 0, result, byteIndex * 8, 8);
		}
		
		return result;
	}
	
	public static boolean[] getBitsFromBytes(byte ... bytes) {
		boolean[] result = new boolean[8 * bytes.length];
		
		for(int byteIndex = 0; byteIndex < bytes.length; byteIndex++) {
			boolean[] bits = getBits(bytes[byteIndex]);
			System.arraycopy(bits, 0, result, byteIndex * 8, 8);
		}
		
		return result;
	}
	
	/**
	 * Перетворює стрічку, що складається із "0" і "1" в бітовий масив. 
	 * "1" вважається як true, будь-який інший символ (за виключенням пробілу) - false
	 * Пробіли не вважаються за знаки 
	 */
	public static boolean[] getBits(String bits) {
		bits = bits.replace(" ", "");
		boolean[] result = new boolean[bits.length()];
		for(int i = 0; i < result.length; i++) {
			result[i] = bits.charAt(i) == '1' ? true : false;
		}
		return result;
	}
	
	public static byte getByte(boolean[] bits) {
		byte result = 0;
		for(int i = 0; i < 8; i++) {
			if (bits[i]) {
				byte power = 1;
				for(int powerIndex = 0; powerIndex < 7 - i; powerIndex++) {
					power *= 2;
				}
				result += power;
			}
		}
		return result;
	}
	
	public static byte getByte(String bits) {
		boolean[] tmp = new boolean[8];
		for(int i = 0; i < 8; i++) {
			boolean value = false;
			if (bits.charAt(i) == '1') {
				value = true;
			}
			tmp[i] = value;
		}
		
		return getByte(tmp);
	}
	
	public static byte[] getBytes(boolean[] bits) {
		boolean[] subBits = new boolean[8];
		int byteCount = bits.length/8;
		byte[] result = new byte[byteCount];
		for(int i = 0; i < byteCount; i++) {
			for(int j = 0; j < 8; j++) {
				subBits[j] = bits[i * 8 + j];
			}
			result[i] = getByte(subBits);
		}
		
		return result;
	}

	/**
	 * Обчислює контрольну суму переданих байтів у форматі CRC-16
	 */
	public static short calculateCrc16(byte[] exampleBytes) {
	        int crc = 0x0000;
	        for (byte b : exampleBytes) {
	            crc = (crc >>> 8) ^ CRC_TABLE[(crc ^ b) & 0xff];
	        }

	   return convertIntToShort(crc);
	}
	
	/**
	 * Переводить цілочисельний int в short. Якщо значення int виходить за межі short, 15 біт (знаковий для short), встановлюється в нуль.
	 * Таким чином, ми можемо однозначно перевести значення int, які поміщаються в перші два байти, в short
	 * @param result число, яке необхідно перевести
	 */
	public static short convertIntToShort(int result) {
		if (result > 32_767) {
			result ^= MASK_15;
			result *= -1;
		}
		
		return (short) result;
	}
	
	/**
	 * Переводить short в массив байтів
	 */
	public static byte[] convertShortToByteArray(short number) {
		SHORT_BUFFER.clear();
		SHORT_BUFFER.putShort(number);
		return SHORT_BUFFER.array();
	}
	
	/**
	 * Переводить int в массив байтів
	 */
	public static byte[] convertIntToByteArray(int number) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(number);
		return b.array();
	}
	
	/**
	 * Повертає два останні байти із бітового представлення цілого числа типу int
	 */
	public static byte[] getLastTwoBytesFromInt(int number) {
		return subArray(convertIntToByteArray(number), 2, 2);
	}
	
	/**
	 * Об"єднує декілька массивів байтів в один
	 * @return
	 */
	public static byte[] concatArrays(byte[] ... parts) {
		int size = 0;
		for(byte[] part : parts) {
			size += part.length;
		}
		ByteBuffer result = ByteBuffer.allocate(size);
		for(byte[] part : parts) {
			result.put(part);
		}
		
		return result.array();
	}
	
	/**
	 * Об"єднує декілька массивів бітів в один
	 * @return
	 */
	public static boolean[] concatArrays(boolean[] ... parts) {
		int size = 0;
		for(boolean[] part : parts) {
			size += part.length;
		}
		boolean[] result = new boolean[size];
		int index = -1;
		for(boolean[] part : parts) {
			for (boolean bit : part) {
				result[++index] = bit;
			}
		}
		
		return result;
	}
	
	/**
	 * Повертає частину массиву
	 * @param source вихідний массив
	 * @param start початковий індекс
	 * @param size розмір даних
	 * @return массив, який починається із start, розміром size байт
	 */
	public static byte[] subArray(byte[] source, int start, int size) {
		byte[] result = new byte[size];
		System.arraycopy(source, start, result, 0, size);
		return result;
	}
	
	/**
	 * Повертає частину массиву
	 * @param source вихідний массив
	 * @param start початковий індекс
	 * @param size розмір даних
	 * @return массив, який починається із start, розміром size байт
	 */
	public static boolean[] subArray(boolean[] source, int start, int size) {
		boolean[] result = new boolean[size];
		System.arraycopy(source, start, result, 0, size);
		return result;
	}
	
	/**
	 * Виводить на друк один байт в його двійковій формі
	 */
	public static void printByte(byte b) {
		boolean[] bits = getBits(b);
		for(int i = 0; i < 8; i++) {
			if (bits[i]) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}
		}
	}
	
	/**
	 * Виводить на друк масив байтів у їх двійковому представленні
	 */
	public static void printBytes(byte ... bytes) {
		for(int i = 0; i < bytes.length; i++) {
			printByte(bytes[i]);
			System.out.print(" ");
		}
	}
	
	public static long bytesToLong(byte[] array) {
		int offset = 0;
	    return
	      ((long)(array[offset]   & 0xff) << 56) |
	      ((long)(array[offset+1] & 0xff) << 48) |
	      ((long)(array[offset+2] & 0xff) << 40) |
	      ((long)(array[offset+3] & 0xff) << 32) |
	      ((long)(array[offset+4] & 0xff) << 24) |
	      ((long)(array[offset+5] & 0xff) << 16) |
	      ((long)(array[offset+6] & 0xff) << 8) |
	      ((long)(array[offset+7] & 0xff));
	  }
	
	public static int getInt(byte ... array) {
		int offset = 0;
	    return
	      ((array[offset]   & 0xff) << 24) |
	      ((array[offset+1] & 0xff) << 16) |
	      ((array[offset+2] & 0xff) << 8) |
	       (array[offset+3] & 0xff);
	  }
	

	/**
	 * Переводить число типу Long в масив байтів
	 */
	public static byte[] longToBytes(long l) {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putLong(l);
		return b.array();
    }
	
	public static byte[] prepareStreamToSend(boolean[] tmpBits) {
		BooleanArray tmpBitsToSend = new BooleanArray();
		
		for(int index = 0; index < tmpBits.length; index += 6) {
			tmpBitsToSend.add(true); 
			tmpBitsToSend.add(true);
			
			for(int i = index; i < index + 6; i++) {
				if (i < tmpBits.length) {
					tmpBitsToSend.add(tmpBits[i]);
				} else {
					tmpBitsToSend.add(false);
				}
			}
		}
		
		return ByteUtils.getBytes(tmpBitsToSend.getBits());
	}
	
	/**
	 * Конвертує 16 біт в цілочисельне число
	 * @param bits масив біт
	 * @param offset зміщення відносно початку бітового масиву
	 */
	public static int convert16BitsToInt(boolean[] bits, int offset) {
		boolean[] numberBits = ByteUtils.subArray(bits, offset, 2 * 8);
		int confirmNumber = 0;
		for(int i = 0; i < 16; i++){ 
			if (numberBits[15 - i]) {
				int mask = 1 << i;
				confirmNumber |= mask;
			}
		}
		return confirmNumber;
	}
	
	/**
	 * Конвертує перші 16 біт із вказаного масива біт в ціле число
	 */
	public static int convert16BitsToInt(boolean[] bits) {
		return convert16BitsToInt(bits, 0);
	}
	
	public static void printBits(boolean[] rawBits) {
		int index = 0;
		for(boolean bit: rawBits) {
			System.out.print(bit?"1":"0");
			index++;
			if (index >= 8) {
				System.out.print(" ");
				index = 0;
			}
		}
	}
	
	public static void printBits(BooleanArray rawBits) {
		int index = 0;
		for(int i = 0; i < rawBits.size(); i++) {
			boolean bit = rawBits.get(i);

			System.out.print(bit?"1":"0");
			index++;
			if (index >= 8) {
				System.out.print(" ");
				index = 0;
			}
		}
	}
	
	public static void printBits(List<Boolean> rawBits) {
		int index = 0;
		for(boolean bit: rawBits) {
			System.out.print(bit?"1":"0");
			index++;
			if (index >= 8) {
				System.out.print(" ");
				index = 0;
			}
		}
	}
	
	public static void printBitsAsDCHT(boolean[] rawBits) {
		for(int i = 0; i < rawBits.length; i += 2) {
			boolean b1 = rawBits[i];
			boolean b2 = rawBits[i+1];
			String result = "0";
			
			if (!b1 && !b2) {
				result = "0";
			} else if (!b1 && b2) {
				result = "1";
			} else if (b1 && !b2) {
				result = "2";
			} else if (b1 && b2) {
				result = "3";
			}
			
			System.out.print(result);
		}
	}

	public static boolean[] invertBits(boolean[] bits) {
		boolean[] result = new boolean[bits.length];
		for(int i = 0; i < bits.length; i++) {
			result[i] = !bits[i];
		}
		return result;
	}
	
	public static String bitsToString(boolean[] rawBits) {
		int index = 0;
		BIT_STRING_BUILDER.setLength(0);
		
		for(boolean bit: rawBits) {
			BIT_STRING_BUILDER.append(bit ? '1' : '0');
			index++;
			if (index >= 8) {
				BIT_STRING_BUILDER.append(' ');
				index = 0;
			}
		}
		return BIT_STRING_BUILDER.toString();
	}
	
	public static String bitsToStringWithoutSpaces(boolean[] rawBits) {
		BIT_STRING_BUILDER.setLength(0);
		
		for(boolean bit: rawBits) {
			BIT_STRING_BUILDER.append(bit ? '1' : '0');
		}
		return BIT_STRING_BUILDER.toString();
	}
	
	public static String intToByteString(int value) {
		boolean[] bits = getBits(getLastByteFromInt(value));
		return bitsToString(bits);
	}
	
	public static byte getLastByteFromInt(int b) {
		return convertIntToByteArray(b)[3];
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}

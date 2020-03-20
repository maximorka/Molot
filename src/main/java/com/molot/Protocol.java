package com.molot;

import com.molot.common.utils.ByteUtils;

/**
 * Константи, які безпосередньо стосуються протоколу
 *  
 * @author integer
 */
public class Protocol {
	public static final float LONG_WAIT_DELAY = 10f;							  // Скільки секунд потрібно чекати, перед тим як визначити, що немає відповіді від другої сторони
	public static final boolean[] WAKE_UP_MARK = ByteUtils.getBits("00011011");   // Початкова послідовність, яку необхідно послати в канал, щоб синхронізувати демодулятор
	public static final int MAX_FILE_PACKET_INFO_COUNT = 10_000;				  // Максимальна кількість пакетів, якими можна передати файл 
	public static final int PACKET_RD_SIZE = 12;								  // Мінімальна к-сть байт коду Ріда-Соломона для відновлення
	public static final int MAX_SEND_COUNT = 10;								  // Максимальна кількість повторів пакету	
	
	public static class CONFIRM {
		public static final int CONFIRM_PACKET_NUMBER_WITH_ADDITIONAL_INFO = 60_002;
	}
	
	public static class PRIORITY {
		public static final int SERVICE_SMS = 2;
		public static final int INFORMATION_SMS = 3;
		
		public static final int SERVICE_FILE = 6;
		public static final int INFORMATION_FILE = 7;
	}
	
	/**
	 * Налаштування для текстових повідомлень
	 */
	public static class SMS {
		public static final class RECEIVE {
			public static final int START = 60_004;
			public static final int PART = 60_006;
		}
		
		public static final class TRANSFER {
			public static final int START_CONFIRMED = 60_008;
			public static final int MESSAGE_CONFIRMED = 60_010;
			public static final int RESEND_MESSAGE = 60_012;
			public static final int GOOD_RECEIVE = 60_014;
		}
		
		public static final String SMS_TEXT_ENCODING = "Cp1251";
		public static final String OTHER_SIDE_NAME = "Прийнято";
		public static final String OUR_NAME = "Передано";
	}
	
	public static class FILE { // Номери пакетів, пов"язані з прийомом та передачею файлів
		public static class RECEIVE {                       // Номери пакетів, які пов"язані з прийомом файлу
			public static final int FILE_INIT = 60_016;     // Запит на початок передачі файлу. Корисне навантаження - 12 байт. Перші 8 байт - ідентифікатор, останні 4 - розмір файлу у байтах
			public static final int BREAK = 60_018; 		// Передаюча сторона відправила запит на переривання відправлення. У інформаційному полі - ідентифікатор файлу
			public static final int FILE_NOT_FOUND = 60_020;// ми послали запит на докачку файлу, але файл з таким ідентифікатором не знайдений
			public static final int WAITING = 60_036;		// Передаюча сторона не налаштована на прийом файлу
		}
		
		public static final class TRANSFER {                // Номери пакетів, які пов"язані з передачею файлу
			public static final int INIT_CONFIRMED = 60_022;// прийшло підтвердження про успішний прийом init-частини
			public static final int PART_CONFIRMED = 60_024;// прийшло підтвердження про успішний прийом частини файлу
			public static final int RESEND_CHUNKS = 60_026; // запит на пересилку частини файлу
			public static final int BREAK_TRANSFER = 60_028;// відміна прийому файлу
			public static final int FILE_CONFIRMED = 60_030;// прийнято весь файл
			public static final int GET_FILE = 60_032;	    // запит на докачку файлу
			public static final int UNKNOWN_FILE = 60_034;  // ми відсилаємо файл, але приймальна сторона не налаштована на прийом цього файлу
			public static final int CHUNK_DIAPAZON_CONFIRMED = 60_038;		// нам прислали повідомлення про успішний прийом діапазону чанків
		}
	}
}

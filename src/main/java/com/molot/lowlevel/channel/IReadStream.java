package com.molot.lowlevel.channel;

/**
 * Інтерфейс для читання даних з потоку
 * 
 */
public interface IReadStream {
	/**
	 * Чи є доступні для читання дані
	 */
	public boolean canRead();
	/**
	 * Повертає біт з потоку. Перед викликом даного методу слід викликати метод canRead(), щоб пересвідчитись, 
	 * чи є в потоці дані для читання
	 */
	public boolean readBit();
}

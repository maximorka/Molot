package com.molot.lowlevel.channel;

/**
 * Інтерфейс для запису даних
 * @author integer
 */
public interface IWriteStream {
	/**
	 * Чи можна записувати дані в потік
	 */
	public boolean canWrite();
	/**
	 * Записує один біт в потік. Перед викликом даного методу слід викликати метод canWrite(), щоб 
	 * пересвідчитись, чи можна записувати дані в потік
	 */
	public void write(boolean bit);
}

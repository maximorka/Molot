package com.molot.lowlevel.channel;

/**
 * Абстрактний інтерфейс для читання та запису даних
 * 
 * @author integer
 */
public interface IChannel {
	/**
	 * Повертає потік, з якого можна читати окремі біти
	 */
	public IReadStream getReader();
	/**
	 * Повертає потік, в який можна записувати окремі біти
	 */
	public IWriteStream getWriter();
	/**
	 * Швидкість передачі\прийому в бітах
	 */
	public int getSpeedInBytes();
}

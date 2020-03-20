package com.molot.lowlevel.rw.data.input;

public interface InputDataEventListener {
	/**
	 * Отримані інформаційні біти.
	 * @param bits біти
	 * @param excludeCount скільки біт з початку потрібно ігнорувати. Наприклад, нам 
	 * прийшло 8 bits і excludeCount = 2. У такому випадку ми читаємо лише останні 6 біт із bits, 
	 * перші два біти службові
	 */
	public void informationBitsReceived(boolean[] bits, int excludeCount);
	
	/**
	 * Отриманий розмір буфера.
	 * 
	 * @param byteCount кількість байт, які можна записати в буфер
	 */
	public void bufferPercentReceived(int byteCount);
}

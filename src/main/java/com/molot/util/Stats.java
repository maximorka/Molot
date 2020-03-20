package com.molot.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Зберігання і накопичення статистики у вигляді цілочисельних даних
 * 
 * @author integer
 */
public class Stats {
	private static final String[][] DEF_MAP = {
		{
			"Час роботи, секунд",
			"working_time"
		},
		{
			"Технічна швидкість",
			"techical_speed"
		},
		{
			"", ""
		},
		{
			"Пакетів відправлено",
			"sent_packet_count",
		},
		{
			"   з стаффінг бітом",
			"packets_with_staffing_bits"
		},
		{
			"Пакетів прийнято",
			"received_packet_count"
		},
		{
			"   Пакети підтвердження",
			"received_confirm_packet_count"
		},
		{
			"   СМС пакети",
			"received_sms_packet_count"
		},
		{
			"   З помилками",
			"received_bad_packet_count"
		},
		{
			"     з них виправлено",
			"received_bad_and_corrected_packet_count"
		},
		{
			"     не вдалось виправити",
			"received_bad_and_uncorrected_packet_count"
		},
		{
			"Біт відправлено",
			"sent_bit_count"
		},
		{
			"Біт прийнято",
			"received_bit_count"
		},
		{
			"Пакети для відновлення по мажоритару:",
			"majoritary_packet_count"
		},
		{
			"  успішно відновлені:",
			"repaired_majoritary_packet_count"
		}
	};
	private Map<String, Integer> intValues = new HashMap<>();
	
	private int packetCountInQueue;
	
	private String operationName;
	private int operationProgress;
	
	public float workingTime;
	
	public void addInt(String name, int count) {
		setInt(name, getInt(name) + count);
	}
	
	public void setInt(String name, int count) {
		intValues.put(name, count);
	}
	
	public int getInt(String name) {
		if (intValues.containsKey(name)) {
			return intValues.get(name);
		} else {
			return 0;
		}
	}
	
	public int getStatCount() {
		return DEF_MAP.length;
	}
	
	public String getNameByIndex(int index) {
		return DEF_MAP[index][0];
	}
	
	public int getValueByIndex(int index) {
		return getInt(DEF_MAP[index][1]);
	}
	
	public void setPacketCountInQueue(int packetCountInQueue) {
		this.packetCountInQueue = packetCountInQueue;
	}
	
	public int getPacketCountInQueue() {
		return packetCountInQueue;
	}
	
	/**
	 * Встановлює операцію, що виконується в даний момент, і її стан (відсоток виконання)
	 * @param name текстова назва операції
	 * @param progress прогрес операції
	 */
	public void setOperation(String name, int progress) {
		operationName = name;
		operationProgress = progress;
	}
	
	/**
	 * Повертає назву операції, що виконується в даний момент
	 */
	public String getOperationName() {
		return operationName;
	}
	
	/**
	 * Повертає стан виконання поточної операції (у відсотках, від 1 до 100)
	 */
	public int getOperationProgress() {
		return operationProgress;
	}

	public void clear() {
		operationProgress = 0;
		operationName = null;
		intValues.clear();
		workingTime = 0;
	}
}

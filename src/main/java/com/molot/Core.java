package com.molot;

import com.molot.bit.BitStateMachine;
import com.molot.lowlevel.channel.IWriteStream;
import com.molot.lowlevel.rw.ReaderWriter;
import com.molot.task.TaskWorker;

//import ua.com.integer.file.Files;
//import ua.com.integer.file.storage.FileSaver;
//import ua.com.integer.log.LogWriter;
//import ua.com.integer.message.Messages;
//import ua.com.integer.packet.handler.PacketProcessor;
//import ua.com.integer.packet.imp.AbstractPacket;
//import ua.com.integer.packet.queue.PacketQueue;
//import ua.com.integer.packet.queue.filter.PacketFilter;
//import ua.com.integer.sound.Sounds;
//import ui.main.WaitDialog;

/**
 * Ядро програми. Являє собою "фасад" для зручного доступу до всіх елементів програми. 
 * Більшість методів - статичні, для зручності використання
 * 
 * @author 1nt3g3r
 */
public class Core {
	public static final String TAG = "Ядро";

	private static Core instance = new Core();
	private boolean running;

	private BitStateMachine bitMachine = new BitStateMachine();
	private Configuration configuration = new Configuration();


	//private Files files = new Files();
	//private Messages messages = new Messages();
	private TaskWorker taskWorker = new TaskWorker();
	
	//private FileSaver fileSaver = new FileSaver();
	
	//private Sounds sounds = new Sounds();
	
	//private LogWriter logger = new LogWriter();
	
	private boolean isSynchronized;
	private boolean[] tmpBits = new boolean[10_000];
	
	class UpdateThread extends Thread {
		@Override
		public void run() {
			while(true) {
				if (running) {
					//System.out.println("Hello!");
					update();
				}
				
				try {
					Thread.sleep(config().timeBetweenUpdateInMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Core() {
		new UpdateThread().start();
	}
	
	/**
	 * Повертає унікальний екземпляр "ядра"
	 * @return
	 */
	public static Core getInstance() {
		return instance;
	}
	
	public static Core i() {
		return instance;
	}
	
	public static void setChannel(ReaderWriter channel) {
		config().channel = channel;
		channel.workInRawMode(false);
	}
	
	public void reset() {
		bitMachine.reset();
		taskWorker.reset();
	}
	
	/**
	 * Оновлює ядро з частотою 10 раз в секунду
	 */
	public void update() {
		if (!isReady()) {
			return;
		}

		readBits();
		//pq.update(configuration.timeBetweenUpdate);
		writeBits();
		//updateStats();
		
		//taskWorker.update(configuration.timeBetweenUpdate);
		//messages.update(configuration.timeBetweenUpdate);
		//files.getReceiveMachine().update(configuration.timeBetweenUpdate);
		//files.getTransferMachine().update(configuration.timeBetweenUpdate);
	}
	
	private void updateStats() {
		//configuration.stats.setPacketCountInQueue(pq.getPacketCount());
		//stats().workingTime+= configuration.timeBetweenUpdate;
		//stats().setInt("working_time", (int) stats().workingTime);
	}
	
	private void readBits() {
		if (rw() == null) {
			System.out.println("not init modem");
			//stats().setOperation("Модем не ініціалізовано!", 0);
			return;
		}
		
		int readBitCount = 0;
		
		while(rw().getReader().canRead()) {
			boolean bit = rw().getReader().readBit();
			if (readBitCount >= tmpBits.length) {
				break;
			}
			tmpBits[readBitCount] = bit;
			readBitCount++;
		}

		for(int i = 0; i < readBitCount; i++) {
			bitMachine.addBit(tmpBits[i]);
		}
		
		//configuration.stats.addInt("received_bit_count", readBitCount);
	}
	
	private void writeBits() {
		if (rw() == null && !isReady()) {

		//	stats().setOperation("Модем не ініціалізовано!", 0);
			return;
		}
		
		int sendPacketCount = 0;
		
//		while(pq.hasData()) {
//			if (rw().getBufferSizeInBits() - 4000 >= pq.getDataSize()) {
//
//				sendPacketCount++;
				IWriteStream writeStream = rw().getWriter();
//
//				writeStartPacketMask(writeStream);
//
				boolean[] packetBits ={true,false,false,true,true,true,false,false};
//
//				if (packetBits.length >= 320) {
//					packetHandler.log("");
//					packetHandler.log("ПАКЕТ №" + pq.getCurrentPacket().getNumber() + " ВІДПРАВЛЕНО");
//					packetHandler.logBits(packetBits);
//
//					packetHandler.log("ОРИГІНАЛЬНИЙ ВМІСТ");
//					packetHandler.logBits(ByteUtils.getBits(pq.getCurrentPacket().getRawBytes()));
//				}
				for(int i = 0; i < packetBits.length; i++) {

					writeStream.write(packetBits[i]);
				}


//				if (packetBits.length%8 != 0) {
//					stats().addInt("packets_with_staffing_bits", 1);
//				}
//				pq.nullCurrentPacket();
//
//				//pq.update(0f);
//			} else {
//				break;
//			}
//		}
//
//		if (sendPacketCount > 0) {
			rw().flush();
//		}
	}

	private void writeStartPacketMask(IWriteStream writeStream) {
		writeStream.write(false);
		for(int i = 0; i < BitStateMachine.ONE_BIT_COUNT_IN_PACKET_MASK; i++) {
			writeStream.write(true);
		}
		writeStream.write(false);
	}

	//public static void sendPacket(AbstractPacket packet, int priority, int maxSendCount) {
		//instance.taskWorker.postSendPacketTask(packet, priority, maxSendCount);
	//}
	
	/**
	 * Повертає інтерфейс для управління як файлами що приймаються, так і файлами, що відправляються
	 * @return
	 */
	//public static Files files() {
	//	return instance.files;
	//}
	
	/**
	 * Повертає інтерфейс для управління текстовими повідомленнями
	 * @return
	 */
	//public static Messages messages() {
		//return instance.messages;
	//}

	/**
	 * Видаляє всі пакети, що попадають під вказаний фільтр
	 */
//	public static void removePackets(PacketFilter filter) {
//		pq().removePackets(filter);
//	}
	
	/**
	 * Повертає чергу повідомлень
	 */
//	public static PacketQueue pq() {
//		return instance.pq;
//	}
	
	/**
	 * Повертає статистику роботи програми. Вона включає в себе різноманітні параметри - наприклад, кількість прийнятих та переданих пакетів
	 */
//	public static Stats stats() {
//		return instance.configuration.stats;
//	}
	
	/**
	 * Повертає поточну конфігурацію. Конфігурація являє собою набір певних класів - канал зв"язку, статистика та ін.
	 * @return
	 */
	public static Configuration config() {
		return instance.configuration;
	}
	
	/**
	 * Повертає канал, з яким ми працюємо. Канал - це абстракція, в яку можна писати і з якої можна читати дані. 
	 * Зазвичай вам не потрібно використовувати канал напряму, а достатньо працювати на пакетному рівні
	 */
	public static ReaderWriter rw() {
		return instance.configuration.channel;
	}
	
	public static boolean isReady() {

		return instance != null && instance.configuration != null && instance.configuration.channel != null && instance.running ;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Повертає інформаційну швидкість (біт за секунду)
	 * @return
	 */
//	public static int getReceiveSpeed() {
//		return instance.packetHandler.getSpeedInBitsPerSecond();
//	}
//
	/**
	 * Повертає час (в мілісекундах) прийому останнього пакету
	 */
//	public static long getLastReceivedPacketTime() {
//		return instance.packetHandler.getLastReceivedPacketTime();
//	}
//
	/**
	 * Якщо звук в налаштуваннях ввімкнений, програє файл з вказаним іменем.
	 * 
	 * Якщо оператор нічого не робить - через 30 секунд буде подано спеціальний звуковий сигнал
	 */
//	public static void playSound(String soundName) {
//		if (Params.SETTINGS.getBoolean("sound-enabled")) {
//			instance.sounds.play(soundName, new Runnable() {
//				public void run() {
//					new Timer().schedule(new TimerTask() {
//						@Override
//						public void run() {
//							instance.sounds.loop("tone");
//						}
//					}, 30_000);
//				}
//			});
//		}
//	}
	
//	public static void stopAllSounds() {
//		instance.sounds.stopAll();
//	}
//
//	public static void log(String message) {
//		instance.logger.log(message);
//	}
	
//	public static float getWaitSynchroTime() {
//		//float time = (float) (2f * Math.pow(10, 4) / Math.pow(Params.SPEED.getSpeed() / 2, 2));
////		if (time < 1f) {
////			time = 1f;
////		}
////		return time;
//	}
//
//	public static void showWaitDialog(String text, float time, Runnable finishRunnable) {
//		WaitDialog.show(text, time, finishRunnable);
//	}

	public static void setSynchronized(boolean sync) {
		instance.isSynchronized = sync;
	}
	
//	public static FileSaver fileSaver() {
//		return instance.fileSaver;
//	}
}

package com.molot.task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import ua.com.integer.packet.imp.AbstractPacket;

public class TaskWorker {
	private List<CoreTask> tasks = new CopyOnWriteArrayList<>();
	
	public void update(float delta) {
		for(int i = 0; i < tasks.size(); i++) {
			tasks.get(i).run();
		}
		tasks.clear();
	}
	
	public void postTask(CoreTask task) {
		tasks.add(task);
	}
	
//	public void postSendPacketTask(AbstractPacket packet, int priority, int maxSendCount) {
//		SendPacketTask com.molot.task = new SendPacketTask();
//		com.molot.task.init(packet, priority, maxSendCount);
//		postTask(com.molot.task);
//	}

	public void reset() {
		tasks.clear();
	}
}

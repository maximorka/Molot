package com.molot.task;
//
//import Core;
//import ua.com.integer.Protocol;
//import ua.com.integer.packet.imp.AbstractPacket;
//import ua.com.integer.packet.queue.SendPacketInfo;
//
//public class SendPacketTask extends CoreTask {
//	private AbstractPacket packet;
//	private int priority;
//	private int maxSendCount;
//
//	public void init(AbstractPacket packet, int priority, int maxSendCount) {
//		this.packet = packet;
//		this.priority = priority;
//		this.maxSendCount = maxSendCount;
//	}
//
//	@Override
//	public void run() {
//		SendPacketInfo info = new SendPacketInfo(Protocol.MAX_SEND_COUNT, 1f);
//		info.maxSendCount = maxSendCount;
//		info.elapsedTime = info.timeBetweenSendInSeconds * 2f;
//
//		if (Core.pq().addPacket(packet, info, priority)) {
//			Core.stats().addInt("sent_packet_count", 1);
//		}
//	}
//}

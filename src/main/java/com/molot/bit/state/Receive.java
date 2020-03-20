package com.molot.bit.state;

import com.molot.bit.BitStateMachine;

public class Receive extends State {
	public Receive(BitStateMachine machine) {
		super(machine);
	}
	
	@Override
	public void receiveBit0() {
		machine.addInformationBit(false);
	}
	
	@Override
	public void receiveBit1() {
		machine.addInformationBit(true);
	}
	
	@Override
	public void receiveStaffingBits() {
		machine.addInformationBit(true);
		machine.setCurrentState(machine.getInPacketBorder());
	}
	
	@Override
	public void receiveFullPacket() {
		//machine.notifyPacketReceived(machine.getInformation().getBits());

		machine.reset();
		machine.setCurrentState(machine.getWait());
	}
	
	@Override
	public String toString() {
		return "receive";
	}
}

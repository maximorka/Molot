package com.molot.bit.state;

import com.molot.bit.BitStateMachine;

public class Border extends State {
	public Border(BitStateMachine machine) {
		super(machine);
	}
	
	@Override
	public void receiveBit0() {
		if (machine.isTrueBitMaskReceived()) {
			machine.setTrue16BitsReceived(false);
			machine.clearRawStream();
			machine.clearInformationBits();
			machine.notifyAboutPacketStart();
			machine.setCurrentState(machine.getReceive());
		} 
	}
	
	@Override
	public void receivePacketMaskBits() {
		machine.setTrue16BitsReceived(true);
		machine.clearRawStream();
	}
	
	@Override
	public String toString() {
		return "border";
	}
}

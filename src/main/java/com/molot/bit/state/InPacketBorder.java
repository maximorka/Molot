package com.molot.bit.state;

import com.molot.bit.BitStateMachine;

public class InPacketBorder extends State {
	public InPacketBorder(BitStateMachine machine) {
		super(machine);
	}
	
	@Override
	public void receivePacketMaskBits() {
		machine.addInformationBit(true);
		machine.setTrue16BitsReceived(true);
		machine.setCurrentState(machine.getBorder());
	}
	
	@Override
	public void receiveBit0() {
		if (machine.isTrueBitMaskReceived()) {
			boolean startBit = machine.getLastBitWithOffset(BitStateMachine.ONE_BIT_COUNT_IN_PACKET_MASK + 2);
			if (!startBit) {
				machine.notifyAboutPacketStart();
				machine.reset();
				machine.setCurrentState(machine.getReceive());
			}
		}
		machine.setCurrentState(machine.getReceive());
	}
	
	@Override
	public void receiveBit1() {
		machine.addInformationBit(true);
	}
	
	@Override
	public String toString() {
		return "in_packet_border";
	}
}

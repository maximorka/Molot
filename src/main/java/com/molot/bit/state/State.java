package com.molot.bit.state;

import com.molot.bit.BitStateMachine;

public abstract class State {
	protected BitStateMachine machine;
	
	public State(BitStateMachine machine) {
		this.machine = machine;
	}
	
	public void receiveBit1() {};
	public void receiveBit0() {};
	public void receivePacketMaskBits() {};
	public void receiveStaffingBits() {};
	public void receiveFullPacket() {};
}

package com.molot.bit.state;

import com.molot.bit.BitStateMachine;

public class Wait extends State {
	public Wait(BitStateMachine machine) {
		super(machine);
	}
	
	@Override
	public void receiveBit0() {
		machine.setCurrentState(machine.getBorder());
	}
	
	@Override
	public String toString() {
		return "wait";
	}
}

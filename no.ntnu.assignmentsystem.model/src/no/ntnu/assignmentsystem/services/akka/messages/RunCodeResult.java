package no.ntnu.assignmentsystem.services.akka.messages;

import java.io.Serializable;

public class RunCodeResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String result;
	
	public RunCodeResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}
}

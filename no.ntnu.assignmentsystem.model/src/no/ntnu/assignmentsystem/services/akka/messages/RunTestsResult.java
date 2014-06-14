package no.ntnu.assignmentsystem.services.akka.messages;

import java.io.Serializable;

public class RunTestsResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String output;
	
	public RunTestsResult(String output) {
		this.output = output;
	}
}

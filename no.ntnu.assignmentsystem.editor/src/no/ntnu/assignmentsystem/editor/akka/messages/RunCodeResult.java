package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class RunCodeResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public String output;
	
	public RunCodeResult(String result) {
		this.output = result;
	}
}

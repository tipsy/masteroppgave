package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class Debug implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String value;
	
	public Debug(String value) {
		this.value = value;
	}
}

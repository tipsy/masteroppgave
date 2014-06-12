package no.ntnu.assignmentsystem.services.akka.messages;

import java.io.Serializable;

public class UpdateSourceCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String id;
	public final String sourceCode;
	
	public UpdateSourceCode(String id, String sourceCode) {
		this.id = id;
		this.sourceCode = sourceCode;
	}
}

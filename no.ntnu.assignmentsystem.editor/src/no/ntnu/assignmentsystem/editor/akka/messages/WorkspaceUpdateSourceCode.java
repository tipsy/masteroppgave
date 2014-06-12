package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class WorkspaceUpdateSourceCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String sourceCode;
	
	public WorkspaceUpdateSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
}

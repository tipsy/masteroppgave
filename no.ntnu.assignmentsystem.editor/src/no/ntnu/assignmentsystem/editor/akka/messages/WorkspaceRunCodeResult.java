package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class WorkspaceRunCodeResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public String output;
	
	public WorkspaceRunCodeResult(String result) {
		this.output = result;
	}
}

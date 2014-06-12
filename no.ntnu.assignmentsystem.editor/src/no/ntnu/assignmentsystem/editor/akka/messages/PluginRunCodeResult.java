package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class PluginRunCodeResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public String output;
	
	public PluginRunCodeResult(String result) {
		this.output = result;
	}
}

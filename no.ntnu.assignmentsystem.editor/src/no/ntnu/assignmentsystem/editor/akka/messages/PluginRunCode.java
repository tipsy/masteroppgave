package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class PluginRunCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String qualifiedClassName;
	
	public PluginRunCode(String qualifiedClassName) {
		this.qualifiedClassName = qualifiedClassName;
	}
}

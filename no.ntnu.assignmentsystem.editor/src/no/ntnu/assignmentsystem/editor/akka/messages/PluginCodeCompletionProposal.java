package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class PluginCodeCompletionProposal implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String name;
	public final String completion;
	
	public PluginCodeCompletionProposal(String name, String completion) {
		this.name = name;
		this.completion = completion;
	}
}

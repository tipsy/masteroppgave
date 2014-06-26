package no.ntnu.assignmentsystem.editor.akka.messages;

import java.io.Serializable;

public class PluginCodeCompletionProposal implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String completion;
	public final int score;
	
	public PluginCodeCompletionProposal(String completion, int score) {
		this.completion = completion;
		this.score = score;
	}
}

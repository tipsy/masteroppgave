package no.ntnu.assignmentsystem.services.akka.messages;

import java.io.Serializable;

public class CodeCompletionProposal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String completion;
	
	public CodeCompletionProposal(String completion) {
		this.completion = completion;
	}
}

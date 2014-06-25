package no.ntnu.assignmentsystem.editor.akka;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginCodeCompletionProposal;

import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.compiler.IProblem;

public class AkkaCompletionRequestor extends CompletionRequestor {
	public interface Delegate {
		void codeCompletionResult(AkkaCompletionRequestor requestor, List<PluginCodeCompletionProposal> proposals);
	}
	
	private final Delegate delegate;
	
	private List<PluginCodeCompletionProposal> proposals;
	
	public AkkaCompletionRequestor(Delegate delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void beginReporting() {
		super.beginReporting();
		System.out.println("[1] beginReporting");
		
		proposals = new ArrayList<>();
	}
	
	@Override
	public void endReporting() {
		super.endReporting();
//		System.out.println("[5] endReporting");
		
		delegate.codeCompletionResult(this, proposals);
		proposals = null;
	}
	
	@Override
	public void acceptContext(CompletionContext context) {
		super.acceptContext(context);
		
//		System.out.println("[2] acceptContext:" + context);
	}
	
	@Override
	public void accept(CompletionProposal proposal) {
//		System.out.println("[3] accept:" + proposal);
		
		String name = new String(proposal.getName());
		String completion = new String(proposal.getCompletion());
		
		System.out.println(name + ":" + completion);
		
		proposals.add(new PluginCodeCompletionProposal(name, completion));
	}
	
	@Override
	public void completionFailure(IProblem problem) {
		System.out.println("[4] completionFailure:" + problem);
		
		super.completionFailure(problem);
	}
}

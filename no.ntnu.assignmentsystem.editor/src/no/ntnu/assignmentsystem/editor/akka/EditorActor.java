package no.ntnu.assignmentsystem.editor.akka;

import org.eclipse.core.runtime.CoreException;

import no.ntnu.assignmentsystem.editor.akka.messages.Ready;
import no.ntnu.assignmentsystem.editor.akka.messages.RunCode;
import no.ntnu.assignmentsystem.editor.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.editor.akka.messages.UpdateSourceCode;
import no.ntnu.assignmentsystem.editor.jdt.ProjectManager;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class EditorActor extends UntypedActor {
	private final ActorRef workspaceActor;
	private final ProjectManager projectManager;
	
	public EditorActor(ActorRef workspaceActor, ProjectManager projectManager) {
		this.workspaceActor = workspaceActor;
		this.projectManager = projectManager;
	}
	
	@Override
	public void preStart() {
		System.out.println("Starting EditorActor with WorkspaceActor:" + workspaceActor);
		
		workspaceActor.tell(new Ready(), getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UpdateSourceCode) {
			handleUpdateSourceCode();
		}
		else if (message instanceof RunCode) {
			handleRunCode();
		}
		else {
			unhandled(message);
		}
	}
	
	
	// --- Private methods ---
	
	private void handleUpdateSourceCode() {
		System.out.println("Wants to update source code");
	}
	
	private void handleRunCode() {
		try {
			String result = projectManager.runMain();
			getSender().tell(new RunCodeResult(result), getSelf());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

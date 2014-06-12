package no.ntnu.assignmentsystem.editor.akka;

import org.eclipse.core.runtime.CoreException;

import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceReady;
import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceRunCode;
import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceRunCodeResult;
import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceUpdateSourceCode;
import no.ntnu.assignmentsystem.editor.jdt.ProjectManager;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class WorkspaceActor extends UntypedActor {
	private final ActorRef workspaceActor;
	private final ProjectManager projectManager;
	
	public WorkspaceActor(ActorRef workspaceActor, ProjectManager projectManager) {
		this.workspaceActor = workspaceActor;
		this.projectManager = projectManager;
	}
	
	@Override
	public void preStart() {
		System.out.println("Starting EditorActor with WorkspaceActor:" + workspaceActor);
		
		workspaceActor.tell(new WorkspaceReady(), getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof WorkspaceUpdateSourceCode) {
			handleUpdateSourceCode();
		}
		else if (message instanceof WorkspaceRunCode) {
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
			getSender().tell(new WorkspaceRunCodeResult(result), getSelf());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

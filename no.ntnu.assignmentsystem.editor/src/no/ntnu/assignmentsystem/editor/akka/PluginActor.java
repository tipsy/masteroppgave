package no.ntnu.assignmentsystem.editor.akka;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginReady;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMain;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunCodeResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginUpdateSourceCode;
import no.ntnu.assignmentsystem.editor.jdt.WorkspaceManager;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class PluginActor extends UntypedActor {
	private final ActorRef editorActor;
	private final WorkspaceManager workspaceManager;
	
	public PluginActor(ActorRef editorActor, WorkspaceManager workspaceManager) {
		this.editorActor = editorActor;
		this.workspaceManager = workspaceManager;
	}
	
	@Override
	public void preStart() {
		System.out.println(getSelf() + ": Pre-start");
		
		editorActor.tell(new PluginReady(), getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof PluginUpdateSourceCode) {
			handleUpdateSourceCode((PluginUpdateSourceCode)message);
		}
		else if (message instanceof PluginRunMain) {
			handleRunCode((PluginRunMain)message);
		}
		else {
			unhandled(message);
		}
	}
	
	
	// --- Handlers ---
	
	private void handleUpdateSourceCode(PluginUpdateSourceCode updateSourceCode) throws JavaModelException, CoreException {
		workspaceManager.updateSourceCode(updateSourceCode.packageName, updateSourceCode.fileName, updateSourceCode.sourceCode);
	}
	
	private void handleRunCode(PluginRunMain runCode) throws CoreException {
		String result = workspaceManager.runMain(runCode.qualifiedClassName);
		getSender().tell(new PluginRunCodeResult(result), getSelf());
	}
}

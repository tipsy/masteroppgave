package no.ntnu.assignmentsystem.editor.akka;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginReady;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMain;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMainResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunTests;
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
			handleRunMain((PluginRunMain)message);
		}
		else if (message instanceof PluginRunTests) {
			handleRunTests((PluginRunTests)message);
		}
		
		else {
			unhandled(message);
		}
	}
	
	
	// --- Handlers ---
	
	private void handleRunMain(PluginRunMain runMain) throws CoreException {
		String result = workspaceManager.runMain(runMain.qualifiedClassName);
		getSender().tell(new PluginRunMainResult(result), getSelf());
	}
	
	private void handleRunTests(PluginRunTests runTests) throws CoreException {
		String result = workspaceManager.runTests(runTests.qualifiedClassName);
//		getSender().tell(new PluginRunTestsResult(result), getSelf());
	}
	
	private void handleUpdateSourceCode(PluginUpdateSourceCode updateSourceCode) throws JavaModelException, CoreException, IOException {
		workspaceManager.updateSourceCode(updateSourceCode.packageName, updateSourceCode.fileName, updateSourceCode.sourceCode);
	}
}

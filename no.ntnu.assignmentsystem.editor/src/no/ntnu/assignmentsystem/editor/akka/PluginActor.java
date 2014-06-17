package no.ntnu.assignmentsystem.editor.akka;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.JUnitCore;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginReady;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMain;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMainResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunTests;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunTestsResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginTestResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginUpdateSourceCode;
import no.ntnu.assignmentsystem.editor.jdt.WorkspaceManager;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class PluginActor extends UntypedActor implements AkkaTestRunListenerDelegate {
	private final ActorRef editorActor;
	private final WorkspaceManager workspaceManager;
	
	public PluginActor(ActorRef editorActor, WorkspaceManager workspaceManager) {
		this.editorActor = editorActor;
		this.workspaceManager = workspaceManager;
	}
	
	@Override
	public void preStart() {
		System.out.println(getSelf() + ": Pre-start");
		
		JUnitCore.addTestRunListener(new AkkaTestRunListener(this));
		
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
		workspaceManager.runTests(runTests.qualifiedClassName);
		// NOTE: Result is sent from the testRunCompleted() delegate method
	}
	
	private void handleUpdateSourceCode(PluginUpdateSourceCode updateSourceCode) throws JavaModelException, CoreException, IOException {
		workspaceManager.updateSourceCode(updateSourceCode.packageName, updateSourceCode.fileName, updateSourceCode.sourceCode);
	}
	
	
	// --- AkkaTestRunListenerDelegate ---

	@Override
	public void testRunCompleted(AkkaTestRunListener listener, List<PluginTestResult> testResults) {
		editorActor.tell(new PluginRunTestsResult(testResults), getSelf());
	}
}

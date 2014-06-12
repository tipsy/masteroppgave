package no.ntnu.assignmentsystem.services.akka;

import java.io.File;

import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceReady;
import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceRunCode;
import no.ntnu.assignmentsystem.editor.akka.messages.WorkspaceRunCodeResult;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.akka.messages.RunCode;
import no.ntnu.assignmentsystem.services.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.services.coderunner.CommandRunner;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import no.ntnu.assignmentsystem.services.coderunner.StartPluginCommands;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.UntypedActorWithStash;
import akka.japi.Procedure;
import akka.remote.RemoteActorRefProvider;

public class EditorActor extends UntypedActorWithStash {
	private final Services services;
	private final String userId;
	private final String problemId;
	
	private final CommandRunner commandRunner = new CommandRunner(new DefaultRuntimeExecutor());
	private final StartPluginCommands startPluginCommands = new StartPluginCommands(
		new File("/Applications/Eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar"),
		"no.ntnu.assignmentsystem.editor.Editor"
	);
	
	private ActorRef editorActor;
	private ActorRef consumer;
	
	public EditorActor(Services services, String userId, String problemId) {
		this.services = services;
		this.userId = userId;
		this.problemId = problemId;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println("Starting up EditorActor");
		
		File tempFile = File.createTempFile("AssignmentSystem-", "");
		tempFile.delete();
		tempFile.mkdir();
		
		String command = startPluginCommands.getStartPluginCommand(tempFile, getRemoteAddressString());
//		System.out.println(command);
		commandRunner.runCommands(new String[] {command});
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof WorkspaceReady) {
			System.out.println("Received message from plugin actor:" + getSender());
			editorActor = getSender();
			
			unstashAll();
			getContext().become(onReceiveWhenReady, false);
		}
		else {
			stash();
		}
	}
	
	
	// --- Private methods ---
	
	private Procedure<Object> onReceiveWhenReady = message -> {
		if (message instanceof RunCode) {
			handleRunCode((RunCode)message);
		}
		else if (message instanceof WorkspaceRunCodeResult) {
			handleRunCodeResult((WorkspaceRunCodeResult)message);
		}
		else {
			unhandled(message);
		}
	};
	
	private void handleRunCode(RunCode runCode) {
//		String result = services.runCodeProblem(userId, problemId);
//		getSender().tell(new RunCodeResult(result), getSelf());
		consumer = getSender();
		editorActor.tell(new WorkspaceRunCode(), getSelf());
	}
	
	private void handleRunCodeResult(WorkspaceRunCodeResult workspaceRunCodeResult) {
		consumer.tell(new RunCodeResult(workspaceRunCodeResult.output), getSelf());
	}
	
	private String getRemoteAddressString() {
		Address address;
		if (context().system().provider() instanceof RemoteActorRefProvider) {
			address = ((RemoteActorRefProvider)context().provider()).transport().addresses().head();
		} else {
		    throw new UnsupportedOperationException("Need RemoteActorRefProvider");
		}
		
		return getSelf().path().toStringWithAddress(address);
	}
}

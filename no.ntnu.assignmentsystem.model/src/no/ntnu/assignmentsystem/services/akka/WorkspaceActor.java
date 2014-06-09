package no.ntnu.assignmentsystem.services.akka;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;

import no.ntnu.assignmentsystem.editor.akka.messages.Debug;
import no.ntnu.assignmentsystem.editor.akka.messages.Ready;
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

public class WorkspaceActor extends UntypedActorWithStash {
	private final Services services;
	private final String userId;
	private final String problemId;
	
	private final CommandRunner commandRunner = new CommandRunner(new DefaultRuntimeExecutor());
	private final StartPluginCommands startPluginCommands = new StartPluginCommands(
		new File("/Applications/Eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar"),
		"no.ntnu.assignmentsystem.editor.Editor"
	);
	
	private ActorRef editorActor;
	
	public WorkspaceActor(Services services, String userId, String problemId) {
		this.services = services;
		this.userId = userId;
		this.problemId = problemId;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println("Starting up WorkspaceActor");
		
		File tempFile = File.createTempFile("AssignmentSystem-", "");
		tempFile.delete();
		tempFile.mkdir();
		
		String command = startPluginCommands.getStartPluginCommand(tempFile, getRemoteAddressString());
		System.out.println(command);
		Process[] processes = commandRunner.runCommands(new String[] {command});
//		
//		// TODO: The following is debug code
//		InputStream inputStream = processes[0].getInputStream();
//		InputStream errorStream = processes[0].getErrorStream();
//		InputStream combinStream = new SequenceInputStream(inputStream, errorStream);
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(combinStream));
//		bufferedReader.lines().forEach(line -> {
//			System.out.println("SUB-PROCESS: " + line);
//		});
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Ready) {
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
			handleRunCode();
		}
		else if (message instanceof Debug) {
			handleDebug((Debug)message);
		}
		else {
			unhandled(message);
		}
	};
	
	private void handleRunCode() {
		String result = services.runCodeProblem(userId, problemId);
		getSender().tell(new RunCodeResult(result), getSelf());
	}
	
	private void handleDebug(Debug debug) {
		System.out.println(debug.value);
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

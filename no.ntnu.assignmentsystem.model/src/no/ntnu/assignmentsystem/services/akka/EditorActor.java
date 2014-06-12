package no.ntnu.assignmentsystem.services.akka;

import java.io.File;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginReady;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunCode;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunCodeResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginUpdateSourceCode;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.akka.messages.RunCode;
import no.ntnu.assignmentsystem.services.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.services.akka.messages.UpdateSourceCode;
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
	
	private ActorRef pluginActor;
	private ActorRef consumerActor;
	
	public EditorActor(Services services, String userId, String problemId) {
		this.services = services;
		this.userId = userId;
		this.problemId = problemId;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println("EditorActor pre-start");
		
		startPlugin();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof PluginReady) {
			System.out.println("Received message from PluginActor:" + getSender());
			pluginActor = getSender();
			
			unstashAll();
			getContext().become(onReceiveWhenReady, false);
		}
		else {
			stash();
		}
	}
	
	private Procedure<Object> onReceiveWhenReady = message -> {
		if (getSender().equals(pluginActor) == false) {
			consumerActor = getSender();
		}
		
		if (message instanceof RunCode) {
			handleRunCode((RunCode)message);
		}
		else if (message instanceof UpdateSourceCode) {
			handleUpdateSourceCode((UpdateSourceCode)message);
		}
		else if (message instanceof PluginRunCodeResult) {
			handlePluginRunCodeResult((PluginRunCodeResult)message);
		}
		else {
			unhandled(message);
		}
	};
	
	
	// --- Handlers ---
	
	private void handleRunCode(RunCode runCode) {
		pluginActor.tell(new PluginRunCode("example.HelloWorld"), getSelf());
	}
	
	private void handleUpdateSourceCode(UpdateSourceCode updateSourceCode) {
		String sourceCode = "package example;\n" +
			"\n" +
			"import java.util.*;\n" +
			"\n" +
			"public class HelloWorld {\n" +
			"  public static void main(String[] args) {\n" +
			"    System.out.println(\"Hello world\");\n" +
			"  }\n" +
			"}\n";
		pluginActor.tell(new PluginUpdateSourceCode("example", "HelloWorld.java", sourceCode), getSelf());
	}
	
	private void handlePluginRunCodeResult(PluginRunCodeResult pluginRunCodeResult) {
		consumerActor.tell(new RunCodeResult(pluginRunCodeResult.output), getSelf());
	}
	
	
	// --- Private methods ---
	
	private void startPlugin() throws Exception {
		File tempFile = File.createTempFile("AssignmentSystem-", "");
		tempFile.delete();
		tempFile.mkdir();
		
		String command = startPluginCommands.getStartPluginCommand(tempFile, getRemoteAddressString());
		commandRunner.runCommands(new String[] {command});
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

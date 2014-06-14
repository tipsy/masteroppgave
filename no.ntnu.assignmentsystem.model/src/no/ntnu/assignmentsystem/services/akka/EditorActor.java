package no.ntnu.assignmentsystem.services.akka;

import java.io.File;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginReady;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMain;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginRunMainResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginUpdateSourceCode;
import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.ImplementationFile;
import no.ntnu.assignmentsystem.model.SourceCodeFile;
import no.ntnu.assignmentsystem.services.ModelServices;
import no.ntnu.assignmentsystem.services.akka.messages.RunMain;
import no.ntnu.assignmentsystem.services.akka.messages.RunMainResult;
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
	private final ModelServices modelServices;
	private final String userId;
	private final String problemId;
	
	private final CommandRunner commandRunner = new CommandRunner(new DefaultRuntimeExecutor());
	private final StartPluginCommands startPluginCommands = new StartPluginCommands(
		new File("/Applications/Eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar"),
//		new File("C:/eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20140224-1459.jar"),
		"no.ntnu.assignmentsystem.editor.BackgroundApplication"
	);
	
	private ActorRef pluginActor;
	private ActorRef consumerActor;
	
	public EditorActor(ModelServices modelServices, String userId, String problemId) {
		this.modelServices = modelServices;
		this.userId = userId;
		this.problemId = problemId;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println(getSelf() + ": Pre-start");
		
		startPlugin();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof PluginReady) {
			System.out.println("Received message from PluginActor:" + getSender());
			pluginActor = getSender();
			
			bootstrapPlugin();
			
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
		
		if (message instanceof RunMain) {
			handleRunMain((RunMain)message);
		}
		else if (message instanceof UpdateSourceCode) {
			handleUpdateSourceCode((UpdateSourceCode)message);
		}
		else if (message instanceof PluginRunMainResult) {
			handlePluginRunMainResult((PluginRunMainResult)message);
		}
		else {
			unhandled(message);
		}
	};
	
	
	// --- Handlers ---
	
	private void handleRunMain(RunMain runMain) {
		modelServices.getProblem(problemId).ifPresent(problem -> {
			CodeProblem codeProblem = (CodeProblem)problem;
			ImplementationFile mainImplementationFile = codeProblem.getMainImplementationFile();
			
			String qualifiedClassName = String.format("%s.%s", mainImplementationFile.getPackageName(), getClassName(mainImplementationFile));
			
			pluginActor.tell(new PluginRunMain(qualifiedClassName), getSelf());
		});
	}
	
	private void handleUpdateSourceCode(UpdateSourceCode updateSourceCode) {
		modelServices.getSourceCodeFile(updateSourceCode.fileId).ifPresent(sourceCodeFile -> {
			// TODO: Save to model
			System.out.println("Sending update message to plugin");
			pluginActor.tell(new PluginUpdateSourceCode(sourceCodeFile.getPackageName(), getFileName(sourceCodeFile), updateSourceCode.sourceCode), getSelf());
		});
	}
	
	private void handlePluginRunMainResult(PluginRunMainResult pluginRunMainResult) {
		consumerActor.tell(new RunMainResult(pluginRunMainResult.output), getSelf());
	}
	
	
	// --- Private methods ---
	
	private void startPlugin() throws Exception {
		File tempFile = File.createTempFile("AssignmentSystem-", "");
		tempFile.delete();
		tempFile.mkdir();
		
		if (System.getenv().get("debug") != null) {
			System.out.println("Remote address: " + getRemoteAddressString()); // TODO: Remove
		}
		else {
			String command = startPluginCommands.getStartPluginCommand(tempFile, getRemoteAddressString());
			commandRunner.runCommands(new String[] {command});
		}
	}
	
	private void bootstrapPlugin() {
		modelServices.getProblem(problemId).ifPresent(problem -> {
			CodeProblem codeProblem = (CodeProblem)problem;
			codeProblem.getSourceCodeFiles().stream().forEach(sourceCodeFile -> {
				pluginActor.tell(new PluginUpdateSourceCode(sourceCodeFile.getPackageName(), getFileName(sourceCodeFile), sourceCodeFile.getSourceCode()), getSelf());
			});
		});
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
	
	private static String getFileName(SourceCodeFile sourceCodeFile) {
		return new File(sourceCodeFile.getFilePath()).getName();
	}
	
	private static String getClassName(SourceCodeFile sourceCodeFile) {
		return getFileName(sourceCodeFile).replace(".java", "");
	}
}

package no.ntnu.assignmentsystem.services.akka;

import java.io.File;

import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.akka.messages.RunCode;
import no.ntnu.assignmentsystem.services.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.services.coderunner.CommandRunner;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import no.ntnu.assignmentsystem.services.coderunner.StartPluginCommands;

import akka.actor.UntypedActor;

public class WorkspaceActor extends UntypedActor {
	private final Services services;
	private final String userId;
	private final String problemId;
	
	private final CommandRunner commandRunner = new CommandRunner(new DefaultRuntimeExecutor());
	private final StartPluginCommands startPluginCommands = new StartPluginCommands(
		new File("/Applications/Eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar"),
		"no.ntnu.assignmentsystem.editor.Editor"
	); 
	
	public WorkspaceActor(Services services, String userId, String problemId) {
		this.services = services;
		this.userId = userId;
		this.problemId = problemId;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println("Starting up");
		String command = startPluginCommands.getStartPluginCommand(null);
		commandRunner.runCommands(new String[] {command});
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof RunCode) {
			String result = services.runCodeProblem(userId, problemId);
			getSender().tell(new RunCodeResult(result), getSelf());
		}
	}
}

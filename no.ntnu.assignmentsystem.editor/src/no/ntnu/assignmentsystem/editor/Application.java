package no.ntnu.assignmentsystem.editor;

import no.ntnu.assignmentsystem.editor.akka.PluginActor;
import no.ntnu.assignmentsystem.editor.jdt.WorkspaceManager;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.util.Timeout;

public class Application implements IApplication {
	private static final long timeoutSeconds = 5;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		System.out.println("Application started");
		
		String[] arguments = (String[])context.getArguments().get("application.args");
		if (arguments.length == 1) {
			String path = arguments[0];

			System.out.println("Received remote address:" + path);
			
			ActorSelection selection = Activator.actorSystem.actorSelection(path);
			Timeout timeout = new Timeout(Duration.create(timeoutSeconds, "seconds"));
			Future<ActorRef> future = selection.resolveOne(timeout);
			ActorRef editorActor = (ActorRef)Await.result(future, timeout.duration());
			
			System.out.println("Resolved EditorActor:" + editorActor);
			
			WorkspaceManager projectManager = new WorkspaceManager("PluginProject"); // TODO: Make it changeable
			Activator.actorSystem.actorOf(Props.create(PluginActor.class, editorActor, projectManager));
		}
		
		return null;
	}

	@Override
	public void stop() {
	}
}

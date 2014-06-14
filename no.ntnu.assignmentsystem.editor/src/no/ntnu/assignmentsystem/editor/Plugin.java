package no.ntnu.assignmentsystem.editor;

import no.ntnu.assignmentsystem.editor.akka.PluginActor;
import no.ntnu.assignmentsystem.editor.jdt.WorkspaceManager;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.util.Timeout;

public class Plugin {
	private static final long timeoutSeconds = 5;
	
	public static void start(String path) throws Exception {
		System.out.println("Starting plugin with path:" + path);
		
		ActorSelection selection = Activator.actorSystem.actorSelection(path);
		Timeout timeout = new Timeout(Duration.create(timeoutSeconds, "seconds"));
		Future<ActorRef> future = selection.resolveOne(timeout);
		ActorRef editorActor = (ActorRef)Await.result(future, timeout.duration());
		
		System.out.println("Resolved remote actor:" + editorActor);
		
		WorkspaceManager projectManager = new WorkspaceManager("PluginProject"); // TODO: Make it changeable
		Activator.actorSystem.actorOf(Props.create(PluginActor.class, editorActor, projectManager));
	}
}

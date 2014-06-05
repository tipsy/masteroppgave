package no.ntnu.assignmentsystem.editor.akka;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.BundleContext;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.osgi.ActorSystemActivator;

public class Activator extends ActorSystemActivator {

	public static ActorSystem actorSystem;
	
	@Override
	public void configure(BundleContext context, ActorSystem system) {
		System.out.println("Configuring actor system");
		actorSystem = system;
		registerService(context, system);
		
		ActorRef masterActor = system.actorOf(Props.create(Master.class));
		masterActor.tell(new Master.Message(), null);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject[] projects = root.getProjects();
		
		for (IProject project : projects) {
			System.out.println(project.getName());
		}
	}
}

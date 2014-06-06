package no.ntnu.assignmentsystem.editor;

import no.ntnu.assignmentsystem.editor.akka.EditorActor;

import org.osgi.framework.BundleContext;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.osgi.ActorSystemActivator;

public class Activator extends ActorSystemActivator {
	@Override
	public void configure(BundleContext context, ActorSystem system) {
		System.out.println("Configuring actor system...");
//		ConfigFactory.parseFile(new File("actor_reference.conf"));
//		ConfigFactory.parseFile(new File("remote_reference.conf"));
		registerService(context, system);
		
		system.actorOf(Props.create(EditorActor.class), "editor");
	}
}

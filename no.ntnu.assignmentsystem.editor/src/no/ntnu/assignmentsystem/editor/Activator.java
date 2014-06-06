package no.ntnu.assignmentsystem.editor;

import org.osgi.framework.BundleContext;

import akka.actor.ActorRef;
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
		
		ActorRef masterActor = system.actorOf(Props.create(Master.class), "master");
		masterActor.tell(new Master.Message(), null);
	}
}

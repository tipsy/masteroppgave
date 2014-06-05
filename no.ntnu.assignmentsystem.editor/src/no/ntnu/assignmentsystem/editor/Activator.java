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
		registerService(context, system);
		
		ActorRef masterActor = system.actorOf(Props.create(Master.class));
		masterActor.tell(new Master.Message(), null);
	}
}

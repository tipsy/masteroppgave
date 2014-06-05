package no.ntnu.assignmentsystem.editor;

import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	@Override
	public void preStart() {
		System.out.println("Starting akka...");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			System.out.println("Message received");
		}
	}
	
	public static class Message {
	}
}

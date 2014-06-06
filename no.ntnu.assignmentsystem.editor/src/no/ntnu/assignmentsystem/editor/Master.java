package no.ntnu.assignmentsystem.editor;

import java.io.Serializable;

import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	@Override
	public void preStart() {
		System.out.println("Starting akka..." + getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			System.out.println("Message received");
		}
	}
	
	public static class Message implements Serializable {
		private static final long serialVersionUID = 1L;
	}
}

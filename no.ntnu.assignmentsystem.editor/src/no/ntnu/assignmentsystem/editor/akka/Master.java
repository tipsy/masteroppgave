package no.ntnu.assignmentsystem.editor.akka;

import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	@Override
	public void preStart() throws Exception {
		System.out.println("Starting akka");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		if (message instanceof ???) {
//			
//		}
	}

}

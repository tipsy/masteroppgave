package no.ntnu.assignmentsystem.editor.akka;

import no.ntnu.assignmentsystem.editor.akka.messages.UpdateSourceCode;
import akka.actor.UntypedActor;

public class EditorActor extends UntypedActor {
	@Override
	public void preStart() {
		System.out.println("Starting akka..." + getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UpdateSourceCode) {
			System.out.println("Wants to update source code");
		}
	}
}

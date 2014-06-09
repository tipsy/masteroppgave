package no.ntnu.assignmentsystem.editor.akka;

import no.ntnu.assignmentsystem.editor.akka.messages.Ready;
import no.ntnu.assignmentsystem.editor.akka.messages.UpdateSourceCode;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class EditorActor extends UntypedActor {
	private final ActorRef workspaceActor;
	
	public EditorActor(ActorRef workspaceActor) {
		this.workspaceActor = workspaceActor;
	}
	
	@Override
	public void preStart() {
		System.out.println("Starting EditorActor with WorkspaceActor:" + workspaceActor);
		
		workspaceActor.tell(new Ready(), getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UpdateSourceCode) {
			System.out.println("Wants to update source code");
		}
	}
}

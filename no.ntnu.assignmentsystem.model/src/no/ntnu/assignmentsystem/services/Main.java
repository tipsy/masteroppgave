package no.ntnu.assignmentsystem.services;

import java.io.File;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import no.ntnu.assignmentsystem.editor.akka.messages.RunCode;
import no.ntnu.assignmentsystem.editor.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;

public class Main {
	public static void main(String[] args) {
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		Services services = new MainServices(modelLoader);
//		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
//		System.out.println(services.getProblem("10", "4")); // CodeProblem

//		services.updateSourceCodeFile("10", "7", "Code");
//		System.out.println(((CodeProblemView)services.getProblem("10", "4")).getSourceCodeFiles()); // CodeProblem
//		System.out.println(services.runCodeProblem("10", "4")); // CodeProblem
//		System.out.println(services.testCodeProblem("10", "4")); // CodeProblem
		
		ActorSystem testActorSystem = ActorSystem.create();
		
		ActorRef workspaceActor = services.createWorkspace("10", "4");
		testActorSystem.actorOf(Props.create(TestActor.class, workspaceActor));
	}
	
	public static class TestActor extends UntypedActor {
		private final ActorRef workspaceActor;
		
		public TestActor(ActorRef workspaceActor) {
			this.workspaceActor = workspaceActor;
		}
		
		@Override
		public void preStart() throws Exception {
			super.preStart();
			
			workspaceActor.tell(new RunCode(), getSelf());
		}
		
		@Override
		public void onReceive(Object message) throws Exception {
			if (message instanceof RunCodeResult) {
				RunCodeResult result = (RunCodeResult)message;
				System.out.println(getSelf() + ": " + result.output);
			}
		}
	}
}

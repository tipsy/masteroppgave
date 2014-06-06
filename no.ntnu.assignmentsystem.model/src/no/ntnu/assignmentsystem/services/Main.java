package no.ntnu.assignmentsystem.services;

import java.io.File;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.akka.messages.RunCode;
import no.ntnu.assignmentsystem.services.akka.messages.RunCodeResult;

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
		
		ActorSystem testActorSystem = ActorSystem.create("Test");
		ActorRef testActor = testActorSystem.actorOf(Props.create(TestActor.class));
		
		ActorRef workspaceActor = services.createWorkspace("10", "4");
		testActor.tell(new Start(workspaceActor), null);
	}
	
	public static class TestActor extends UntypedActor {
		@Override
		public void onReceive(Object message) throws Exception {
			if (message instanceof Start) {
				Start start = (Start)message;
				System.out.println("Start");
				start.actor.tell(new RunCode(), getSelf());
			}
			else if (message instanceof RunCodeResult) {
				RunCodeResult result = (RunCodeResult)message;
				System.out.println(getSelf() + ": " + result.getResult());
			}
		}
	}
	
	public static class Start {
		public ActorRef actor;
		
		public Start(ActorRef actor) {
			this.actor = actor;
		}
	}
}

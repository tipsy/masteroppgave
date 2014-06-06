package no.ntnu.assignmentsystem.services;

import java.io.File;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;

public class Main {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("PiSystem");
//		ActorSelection selection = context.actorSelection("akka.tcp://app@10.0.0.1:2552/user/serviceA/worker");
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		Services services = new ServicesImpl(modelLoader);
//		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
//		System.out.println(services.getProblem("10", "4")); // CodeProblem

//		services.updateSourceCodeFile("10", "7", "Code");
//		System.out.println(((CodeProblemView)services.getProblem("10", "4")).getSourceCodeFiles()); // CodeProblem
		System.out.println(services.runCodeProblem("10", "4")); // CodeProblem
//		System.out.println(services.testCodeProblem("10", "4")); // CodeProblem
	}
	
	public class TestActor extends UntypedActor {
		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
		}
	}
	
	public static class TestMessage {
	}
}

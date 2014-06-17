package no.ntnu.assignmentsystem.services;

import java.io.File;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.akka.messages.RunMain;
import no.ntnu.assignmentsystem.services.akka.messages.RunMainResult;
import no.ntnu.assignmentsystem.services.akka.messages.RunTests;
import no.ntnu.assignmentsystem.services.akka.messages.RunTestsResult;
import no.ntnu.assignmentsystem.services.akka.messages.TestResult;
import no.ntnu.assignmentsystem.services.akka.messages.UpdateSourceCode;

public class Main {
	public static void main(String[] args) {
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		ModelServices modelServices = new ModelServices(modelLoader);
		Services services = new MainServices(modelServices);
		
//		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
//		System.out.println(services.getProblem("10", "4")); // CodeProblem

//		services.updateSourceCodeFile("10", "7", "Code");
//		System.out.println(((CodeProblemView)services.getProblem("10", "4")).getSourceCodeFiles()); // CodeProblem
//		System.out.println(services.runCodeProblem("10", "4")); // CodeProblem
//		System.out.println(services.testCodeProblem("10", "4")); // CodeProblem
		
		ActorSystem testActorSystem = ActorSystem.create();
		
		ActorRef editorActor = services.createEditor("10", "4");
		testActorSystem.actorOf(Props.create(TestActor.class, editorActor));
	}
	
	public static class TestActor extends UntypedActor {
		private final ActorRef editorActor;
		
		public TestActor(ActorRef editorActor) {
			this.editorActor = editorActor;
		}
		
		@Override
		public void preStart() throws Exception {
			super.preStart();

//			editorActor.tell(new RunMain(), getSelf());
//			editorActor.tell(new RunTests(), getSelf());
		}
		
		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println(getSelf() + ": Received message:" + message);
			
			if (message instanceof RunMainResult) {
				handleRunMainResult((RunMainResult)message);
				
			}
			else if (message instanceof RunTestsResult) {
				handleRunTestsResult((RunTestsResult)message);
			}
		}
		
		private void handleRunMainResult(RunMainResult result) {
			System.out.println(result.output);
			
			String sourceCode = "package stateandbehavior;\n" +
					"public class Main {\n" +
					"    public static void main(String[] args) {\n" +
					"        System.out.println(\"Hello, World3!\");\n" +
					"    }\n" +
					"}\n";
			editorActor.tell(new UpdateSourceCode("5", sourceCode), getSelf());
//			editorActor.tell(new RunMain(), getSelf());
		}
		
		private void handleRunTestsResult(RunTestsResult result) {
			for (TestResult testResult : result.testResults) {
				System.out.println(testResult.methodName + "-" + testResult.status.name());
			}
		}
	}
}

package no.ntnu.assignmentsystem.services;

import java.io.File;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import no.ntnu.assignmentsystem.editor.akka.messages.UpdateSourceCode;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;

public class Main {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("PiSystem");
		ActorSelection selection = system.actorSelection("akka.tcp://bundle-734-ActorSystem@127.0.0.1:2552/user/editor");
//		Future<ActorRef> future = selection.resolveOne(Timeout.intToTimeout(5));
		selection.tell(new UpdateSourceCode(), ActorRef.noSender());
//		system.shutdown();
		
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
}

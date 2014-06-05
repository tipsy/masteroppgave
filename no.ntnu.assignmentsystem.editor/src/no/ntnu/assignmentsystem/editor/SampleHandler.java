package no.ntnu.assignmentsystem.editor;

import no.ntnu.assignmentsystem.editor.akka.Bootstrap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import akka.actor.ActorSystem;

public class SampleHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Starting from SampleHandler...");
		System.out.println(no.ntnu.assignmentsystem.editor.akka.Activator.actorSystem.settings().LogLevel());
//		Bootstrap.bootstrap();
//		GenerateProject.doIt();
		
		return null;
	}
}

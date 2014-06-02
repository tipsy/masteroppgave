package no.ntnu.assignmentsystem.editor;

import no.ntnu.assignmentsystem.editor.akka.Bootstrap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class SampleHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Bootstrap.bootstrap();
//		GenerateProject.doIt();
		
		return null;
	}
}

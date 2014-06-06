package no.ntnu.assignmentsystem.editor;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		System.out.println("Application started");
		
		return null;
	}

	@Override
	public void stop() {
	}
}

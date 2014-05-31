package no.ntnu.assignmentsystem.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

class GenerateProject {
	public static void doIt() {
		System.out.println("Doing it...");
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject[] projects = root.getProjects();
		
		for (IProject project : projects) {
			System.out.println(project.getName());
		}
	}
}

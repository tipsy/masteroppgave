package no.ntnu.assignmentsystem.model;

import org.eclipse.emf.ecore.resource.Resource;

public interface ModelLoader {
	UoD getUoD();
	Resource getResource();
	ModelFactory getFactory();
}

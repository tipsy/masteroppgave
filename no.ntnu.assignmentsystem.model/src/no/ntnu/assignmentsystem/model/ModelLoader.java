package no.ntnu.assignmentsystem.model;

import org.eclipse.emf.ecore.EObject;

public interface ModelLoader {
	EObject findObject(String id);
	ModelFactory getFactory();
	void save();
}

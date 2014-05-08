package no.ntnu.assignmentsystem.services;

import no.ntnu.assignmentsystem.services.ServicesFactory;

class BaseViewFactory {
	protected static ServicesFactory factory() {
		return ServicesFactory.eINSTANCE;
	}
}

package no.ntnu.assignmentsystem.services.impl;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.services.AssignmentView;
import no.ntnu.assignmentsystem.services.ServicesFactory;

class AssignmentViewFactory {
	public static AssignmentView createCourseView(Assignment assignment) {
		AssignmentView assignmentView = ServicesFactory.eINSTANCE.createAssignmentView();
		Mapper.copyAttributes(assignment, assignmentView);
		return assignmentView;
	}
}

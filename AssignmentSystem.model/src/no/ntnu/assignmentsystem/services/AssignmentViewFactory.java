package no.ntnu.assignmentsystem.services;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.services.AssignmentView;

class AssignmentViewFactory extends BaseViewFactory {
	public static AssignmentView createCourseView(Assignment assignment) {
		AssignmentView assignmentView = factory().createAssignmentView();
		Mapper.copyAttributes(assignment, assignmentView);
		return assignmentView;
	}
}

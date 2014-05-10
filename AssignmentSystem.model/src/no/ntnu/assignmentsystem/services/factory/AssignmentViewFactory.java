package no.ntnu.assignmentsystem.services.factory;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.services.AssignmentView;

public class AssignmentViewFactory extends BaseViewFactory {
	public static AssignmentView createCourseView(Assignment assignment) {
		AssignmentView assignmentView = factory().createAssignmentView();
		Mapper.copyAttributes(assignment, assignmentView);
		return assignmentView;
	}
}

package no.ntnu.assignmentsystem.services.impl;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.services.AssignmentView;

public class AssignmentViewFactory {
	public static AssignmentView createCourseView(Assignment assignment) {
		AssignmentView assignmentView = new AssignmentViewImpl();
		assignmentView.setId(assignment.getId());
		assignmentView.setTitle(assignment.getTitle());
		return assignmentView;
	}
}

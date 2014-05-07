package controllers;

import no.ntnu.assignmentsystem.services.CourseView;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.impl.ServicesImpl;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.assignment.aProblem;
import views.html.assignment.allAssignments;
import views.html.leaderboards;
import views.html.studentProgress;

import java.io.File;
import java.util.List;

public class MainController extends Controller {

    public static Result index() {
        Services services = new ServicesImpl(new File("../AssignmentSystem.model/model/UoD.xmi"));

        List<CourseView> courses = services.getCourses();

        return ok(views.html.index.render(courses.get(0).getTitle()));
//        return ok(views.html.index.render("Hello from Java"));
    }

    public static Result serveAllAssignments() {
        return( ok(allAssignments.render()) );
    }

    public static Result serveLeaderboards() {
        return( ok(leaderboards.render()) );
    }

    public static Result serveStudentProgress() {
        return( ok(studentProgress.render()) );
    }

    public static Result serveProblem(String aID, String pID) {
        return( ok(aProblem.render(aID, pID)) );
    }
}

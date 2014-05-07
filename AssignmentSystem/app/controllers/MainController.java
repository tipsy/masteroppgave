package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.assignment.aProblem;
import views.html.assignment.allAssignments;

public class MainController extends Controller {

    public static Result index() {
//        Services services = new ServicesImpl(new File("../AssignmentSystem.model/model/UoD.xmi"));
//        List<CourseView> courses = services.getCourses();
//        return ok(views.html.index.render(courses.get(0).getTitle()));
        return redirect(routes.MainController.serveAllAssignments());
    }

    public static Result serveAllAssignments() {
        return ok(allAssignments.render());
    }

    public static Result serveProblem(String aID, String pID) {
        return ok(aProblem.render(aID, pID));
    }
}

package controllers;

import no.ntnu.assignmentsystem.model.ModelFactory;
import no.ntnu.assignmentsystem.model.ModelPackage;
import no.ntnu.assignmentsystem.model.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.assignment.allAssignments;
import views.html.leaderboards;
import views.html.studentProgress;

public class MainController extends Controller {
    
    public static Result index() {
        ModelPackage.eINSTANCE.eClass();
        ModelFactory factory = ModelFactory.eINSTANCE;
        User person = factory.createUser();
        person.setEmail("Christian");
        //dette er en gittest

        return ok(views.html.index.render(person.toString()));
//        return ok(views.html.index.render("Hello from Java"));
    }

    public static Result assignmentOverview() {
        return( ok(allAssignments.render()) );
    }

    public static Result leaderboardsOverview() {
        return( ok(leaderboards.render()) );
    }

    public static Result serveStudentProgress() {
        return( ok(studentProgress.render()) );
    }

}

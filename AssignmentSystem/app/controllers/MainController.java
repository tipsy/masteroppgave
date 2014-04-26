package controllers;

import no.ntnu.assignmentsystem.model.ModelFactory;
import no.ntnu.assignmentsystem.model.ModelPackage;
import no.ntnu.assignmentsystem.model.User;
import play.mvc.Controller;
import play.mvc.Result;

public class MainController extends Controller {
    
    public static Result index() {
        ModelPackage.eINSTANCE.eClass();
        ModelFactory factory = ModelFactory.eINSTANCE;
        User person = factory.createUser();
        person.setEmail("Christian");

        return ok(views.html.index.render(person.toString()));
//        return ok(views.html.index.render("Hello from Java"));
    }
    
}

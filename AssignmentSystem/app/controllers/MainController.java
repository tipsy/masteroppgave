package controllers;

import no.ntnu.assignmentsystem.eagle.EagleFactory;
import no.ntnu.assignmentsystem.eagle.EaglePackage;
import no.ntnu.assignmentsystem.eagle.Person;
import play.mvc.Controller;
import play.mvc.Result;

public class MainController extends Controller {
    
    public static Result index() {
        EaglePackage.eINSTANCE.eClass();
        EagleFactory factory = EagleFactory.eINSTANCE;
        Person person = factory.createPerson();
        person.setName("Christian");

        return ok(views.html.index.render(person.toString()));
//        return ok(views.html.index.render("Hello from Java"));
    }
    
}

package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import utility.Utility;
import views.html.assignment.aProblem;
import views.html.assignment.allAssignments;

public class AssignmentController extends Controller {

    public static Result serveAllAssignments() {
        return ok(allAssignments.render( Utility.services.getAssignments("10") ));
    }

    public static Result serveProblem(String aID, String pID) {
        return ok(aProblem.render(aID, pID));
    }

    public static WebSocket<String> websocketTest() {
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                out.write("Hello from PlayFramework!");
            }
        };
    }

}

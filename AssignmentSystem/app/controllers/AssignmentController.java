package controllers;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import utility.Utility;
import views.html.assignment.aProblem;
import views.html.assignment.allAssignments;

public class AssignmentController extends Controller {

    public static Result serveAllAssignments() {
//        return ok(allAssignments.render( Utility.services.getAssignments("10") ));
        return ok(allAssignments.render());
    }

    public static Result serveProblem(String aID, String pID) {
        return ok(aProblem.render(aID, pID));
    }

    public static WebSocket<String> openEditorSocket(String pID) {
//        ActorRef workspaceActor = Utility.services.createWorkspace("10", pID);
//        ActorRef webSocketActor = Akka.system().actorOf(Props.create(WebSocketActor.class, workspaceActor));

        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
//                webSocketActor.tell(new WebSocketActor.Init(in, out), ActorRef.noSender());
            }
        };
    }

    public static class WebSocketActor extends UntypedActor {
        private final ActorRef workspaceActor;

        private WebSocket.In<String> in;
        private WebSocket.Out<String> out;

        public WebSocketActor(ActorRef workspaceActor) {
            this.workspaceActor = workspaceActor;
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Init) {
                Init init = (Init)message;
                in = init.in;
                out = init.out;

                out.write("This is sent from actor!");
                in.onMessage(System.out::println);
            }
        }

        public static class Init {
            public final WebSocket.In<String> in;
            public final WebSocket.Out<String> out;

            public Init(WebSocket.In<String> in, WebSocket.Out<String> out) {
                this.in = in;
                this.out = out;
            }
        }
    }
}

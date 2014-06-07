package controllers;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import global.Global;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.assignment.aProblem;
import views.html.assignment.allAssignments;

public class AssignmentController extends Controller {

    public static Result serveAllAssignments() {
        System.out.println("Serve all assignments");
//        return ok(allAssignments.render(Global.services.getAssignments("10")));
        return ok(allAssignments.render());
    }

    public static Result serveProblem(String aID, String pID) {
        return ok(aProblem.render(aID, pID));
    }

    public static WebSocket<String> openEditorSocket(String pID) {
        System.out.println(Global.services);
        ActorRef workspaceActor = Global.services.createWorkspace("10", "4");
        System.out.println(workspaceActor);
        ActorRef webSocketActor = Akka.system().actorOf(Props.create(WebSocketActor.class, (ActorRef)null));

        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                webSocketActor.tell(new WebSocketActor.Init(in, out), ActorRef.noSender());
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

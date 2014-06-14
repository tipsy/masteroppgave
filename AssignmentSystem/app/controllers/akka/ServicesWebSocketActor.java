package controllers.akka;

import akka.actor.ActorRef;
import no.ntnu.assignmentsystem.services.akka.messages.RunMain;
import no.ntnu.assignmentsystem.services.akka.messages.RunMainResult;
import no.ntnu.assignmentsystem.services.akka.messages.RunTests;
import no.ntnu.assignmentsystem.services.akka.messages.RunTestsResult;
import no.ntnu.assignmentsystem.services.akka.messages.UpdateSourceCode;

import java.util.HashMap;
import java.util.Map;

public class ServicesWebSocketActor extends DynamicWebSocketActor {
    private final ActorRef workspaceActor;

    public ServicesWebSocketActor(ActorRef workspaceActor) {
        this.workspaceActor = workspaceActor;
    }

    @Override
    protected void sendMessage(Object message) {
        System.out.println("Sending message:" + message);
        workspaceActor.tell(message, getSelf());
    }

    @Override
    protected void sendData(String data) {
        System.out.println("Sending data:" + data);
        out.write(data);
    }

    @Override
    protected Map<String, Class> getClassMapping() {
        Map<String, Class> classMapping = new HashMap<>();
        classMapping.put("runMain", RunMain.class);
        classMapping.put("runMainResult", RunMainResult.class);
        classMapping.put("runTests", RunTests.class);
        classMapping.put("runTestsResult", RunTestsResult.class);
        classMapping.put("updateSourceCode", UpdateSourceCode.class);

        return classMapping;
    }
}

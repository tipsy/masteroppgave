package akka;

import akka.actor.UntypedActor;
import play.mvc.WebSocket;

public abstract class WebSocketActor extends UntypedActor {
    protected WebSocket.In<String> in;
    protected WebSocket.Out<String> out;

    @Override
    public void onReceive(Object message) throws Exception {
        // TODO: Queue incoming message before Init?
        if (message instanceof Init) {
            handleInit((Init)message);
        }
        else {
            actorInputDispatcher(message);
        }
    }

    private void handleInit(Init init) {
        in = init.in;
        out = init.out;

        in.onMessage(this::webSocketInputDispatcher);
    }

    protected abstract void actorInputDispatcher(Object message);

    protected abstract void webSocketInputDispatcher(String data);

    public static class Init {
        public final WebSocket.In<String> in;
        public final WebSocket.Out<String> out;

        public Init(WebSocket.In<String> in, WebSocket.Out<String> out) {
            this.in = in;
            this.out = out;
        }
    }
}

package controllers.akka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;

import java.io.IOException;
import java.util.Map;

public abstract class DynamicWebSocketActor extends WebSocketActor {
    private final ObjectMapper objectMapper = new ObjectMapper() {{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        registerModule(new ParanamerModule());
    }};

    @Override
    protected void actorInputDispatcher(Object message) {
        System.out.println("Received message from actor:" + message);

        try {
            String type = getClassMapping().entrySet().stream().filter(
                entry -> entry.getValue().equals(message.getClass())
            ).findAny().get().getKey();

            MessageWrapper<?> messageWrapper = new MessageWrapper<>(type, message);
            String data = objectMapper.writeValueAsString(messageWrapper);

            sendData(data);
        } catch (JsonProcessingException e) {
            // TODO: Write error to client instead?
            e.printStackTrace();
        }
    }

    @Override
    protected void webSocketInputDispatcher(String data) {
        System.out.println("Received data from web socket:" + data);

        try {
            JsonNode rootNode = objectMapper.readTree(data);
            JsonNode typeNode = rootNode.get(MessageWrapper.typeKey);
            JsonNode messageNode = rootNode.get(MessageWrapper.messageKey);

            String type = typeNode.asText();
            Class clazz = getClassMapping().get(type);

            Object message = objectMapper.treeToValue(messageNode, clazz);

            sendMessage(message);
        } catch (IOException e) {
            // TODO: Write error to actor instead?
            e.printStackTrace();
        }
    }

    protected abstract void sendMessage(Object message);

    protected abstract void sendData(String data);

    protected abstract Map<String, Class> getClassMapping();

    public static class MessageWrapper<T> {
        public static final String typeKey = "type";
        public static final String messageKey = "message";

        public String type;
        public T message;

        public MessageWrapper(String type, T message) {
            this.type = type;
            this.message = message;
        }
    }
}

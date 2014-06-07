package akka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

// TODO: Temp
public class Test implements Serializable {
    public final String value;

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public Test(@JsonProperty("value") String value) {
        this.value = value;
    }
}

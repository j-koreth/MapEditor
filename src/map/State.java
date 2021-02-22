package map;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.Objects;

public class State implements Serializable {
    String name;
    Color color;

    public State(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(name, state.name) && Objects.equals(color, state.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}

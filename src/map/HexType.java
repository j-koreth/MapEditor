package map;

import java.io.Serializable;
import java.util.Objects;

public abstract class HexType {
    private static final long serialVersionUID = 1L;

    abstract String getName();
    abstract String getStrokeColor();
    abstract String getFillColor();
}

class Terrain extends HexType implements Serializable {
    String name;
    private int terrainCost;
    boolean traversable;
    String hexCode;

    public Terrain(String name, String hexCode) {
        this.name = name;
        this.traversable = false;
        this.hexCode = hexCode;
    }

    public Terrain(String name, int terrainCost, String hexCode) {
        this.name = name;
        this.terrainCost = terrainCost;
        this.traversable = true;
        this.hexCode = hexCode;
    }

    public int getTerrainCost() {
        return terrainCost;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStrokeColor() {
        return "#000000";
    }

    @Override
    public String getFillColor() {
        return hexCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        map.Terrain terrain = (map.Terrain) o;
        return terrainCost == terrain.terrainCost && traversable == terrain.traversable && name.equals(terrain.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terrainCost, traversable);
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "name='" + name + '\'' +
                ", terrainCost=" + terrainCost +
                ", traversable=" + traversable +
                ", hexCode='" + hexCode + '\'' +
                '}';
    }
}

class State extends HexType implements Serializable {
    String name;
    String hexCode;

    public State(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        map.State state = (map.State) o;
        return Objects.equals(name, state.name) && Objects.equals(hexCode, state.hexCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hexCode);
    }

    @Override
    public String getStrokeColor() {
        return hexCode;
    }

    @Override
    public String getFillColor() {
        return hexCode;
    }

    @Override
    public String toString() {
        return "State{" +
                "name='" + name + '\'' +
                ", hexCode='" + hexCode + '\'' +
                '}';
    }
}


class Building extends HexType implements Serializable {
    String name;

    enum Shape {Circle, Square}

    Shape shape;

    public Building(String name) {
        this.name = name;
        shape = Shape.Circle;
    }

    public Building(String name, Shape shape) {
        this.name = name;
        this.shape = shape;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStrokeColor() {
        return "#000000";
    }

    @Override
    public String getFillColor() {
        return "#000000";
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", shape=" + shape +
                '}';
    }
}

class Player extends HexType implements Serializable {
    @Override
    String getName() {
        return "Player";
    }

    @Override
    String getStrokeColor() {
        return "#E90D15";
    }

    @Override
    String getFillColor() {
        return "#E90D15";
    }
}
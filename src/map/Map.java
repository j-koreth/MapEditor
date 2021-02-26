package map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.*;

abstract class Map {
    MapData mapData;
    boolean selected = false;
    GraphicsContext gc;

    public Map(MapData mapData, GraphicsContext gc){
        this.mapData = mapData;
        this.gc = gc;
    }

    abstract void drawMap();
}

class BasicMap extends Map{
    HexType type;

    public BasicMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
        type = new HexType() {
            @Override
            public String getName() {
                return "Basic";
            }

            @Override
            public String getStrokeColor() {
                return "#000000";
            }

            @Override
            public String getFillColor() {
                return "#FFFFFF";
            }
        };
    }

    public void drawMap(){
        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            drawHex(mapData.getPoints(entry.getKey()), type);
        }
    }

    public void drawHex(List<Point> points, HexType type){
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        if(type != null){
            gc.setStroke(Color.web(type.getStrokeColor()));
            gc.setFill(Color.web(type.getFillColor()));
            gc.strokePolygon(x, y, 6);
            gc.fillPolygon(x, y, 6);
        }
    }
}

class TerrainMap extends BasicMap{
    public TerrainMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    @Override
    public void drawMap() {
        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            drawHex(mapData.getPoints(entry.getKey()), entry.getValue().terrain);
        }
    }
}

class PoliticalMap extends BasicMap{
    public PoliticalMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
        gc.setGlobalAlpha(.5);
    }

    @Override
    public void drawMap() {
        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            drawHex(mapData.getPoints(entry.getKey()), entry.getValue().state);
        }
    }
}

class ActionMap extends BasicMap{
    HashSet<Hexagon> movable;

    public ActionMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
        gc.setGlobalAlpha(.5);
        type = new HexType() {
            @Override
            public String getName() {
                return "Action";
            }

            @Override
            public String getStrokeColor() {
                return "#BFBFBF";
            }

            @Override
            public String getFillColor() {
                return "#BFBFBF";
            }
        };
    }

    public void setMovable(Hexagon a, int maxRange){
        movable = new HashSet<>();
        LinkedList<Hexagon> frontier = new LinkedList<>();
        frontier.add(a);

        HashMap<Hexagon, Integer> cost_so_far = new HashMap<>();
        movable.add(a);
        cost_so_far.put(a, 0);

        while(!frontier.isEmpty()){
            Hexagon current = frontier.remove();

            for(Hexagon neighbor : mapData.getNeighbors(current)){
                HexData neighborHexData = mapData.getHexData(neighbor);
                if(neighborHexData.terrain.traversable){
                    int new_cost = cost_so_far.get(current) + neighborHexData.terrain.getTerrainCost();
                    if(new_cost <= maxRange){
                        if(!cost_so_far.containsKey(neighbor) || new_cost < cost_so_far.get(neighbor)){
                            cost_so_far.put(neighbor, new_cost);
                            movable.add(neighbor);
                            frontier.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    public void drawMap() {
        if(movable != null){
            for(Hexagon hex : movable){
                drawHex(mapData.getPoints(hex), type);
            }
        }
    }
}

class HexInfoMap extends Map{
    public HexInfoMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    public void drawMap() {
        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            drawHex(entry.getKey(), mapData.hex_to_pixel(entry.getKey()));
        }
    }

    public void drawHex(Hexagon a, Point center){
        gc.setFill(Color.BLACK);
        gc.fillText("" + a.x, center.getX() - 14, center.getY()  - 6);
        gc.fillText("" + a.y, center.getX() + 6 , center.getY() + 5);
        gc.fillText("" + a.z, center.getX() - 14 , center.getY() + 14);
    }
}

class BuildingMap extends Map{
    public BuildingMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    void drawMap() {
        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            if(entry.getValue().building != null){
                drawHex(entry.getValue(), mapData.hex_to_pixel(entry.getKey()));
            }
        }
    }

    public void drawHex(HexData hexData, Point center){
        gc.setStroke(Color.web(hexData.building.getStrokeColor()));
        gc.setFill(Color.web(hexData.building.getFillColor()));
        switch (hexData.building.shape){
            case Circle:
                gc.fillRect(center.getX() - 8, center.getY() - 8, 16, 16);
                break;
            case Square:
                gc.fillOval(center.getX() - 8, center.getY() - 8, 16, 16);
                break;
        }
    }
}
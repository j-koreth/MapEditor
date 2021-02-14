package map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.*;

abstract class Map {
    MapData mapData;
    boolean selected = false;
    GraphicsContext gc;

    public Map(MapData mapData, GraphicsContext gc){
        this.mapData = mapData;
        this.gc = gc;
    }

    public void drawMap(){
        gc.clearRect(0,0,1000,1000);

        for(HashMap.Entry<Hexagon, HexData> entry: mapData.data.entrySet()){
            drawHex(mapData.hex_points(entry.getKey()), entry.getValue());
        }
    }

    abstract void drawHex(List<Point> hex_points, HexData value);
}

class BasicMap extends Map{
    public BasicMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    public void drawHex(List<Point> points, HexData hexData){
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
        gc.strokePolygon(x, y, 6);
        gc.fillPolygon(x, y, 6);
    }
}

class TerrainMap extends Map{
    public TerrainMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    public void drawHex(List<Point> points, HexData hexData){
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        gc.setStroke(Color.BLACK);
        switch (hexData.type){
            case Land:
                gc.setFill(Color.WHEAT);
                gc.strokePolygon(x, y, 6);
                gc.fillPolygon(x, y, 6);
                break;
            case Ocean:
                gc.setFill(Color.BLUE);
                gc.strokePolygon(x, y, 6);
                gc.fillPolygon(x, y, 6);
                break;
            case Lake:
                gc.setFill(Color.LIGHTBLUE);
                gc.strokePolygon(x, y, 6);
                gc.fillPolygon(x, y, 6);
                break;
        }
    }
}

class ActionMap extends Map{
    public ActionMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
        gc.setGlobalAlpha(.5);
    }

    public void drawHex(List<Point> points, HexData hexData){
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();

        if(hexData.modifier != null){
            switch (hexData.modifier){
                case Move:
                    gc.setStroke(Color.WHITE);
                    gc.setFill(Color.WHITE);
                    gc.fillPolygon(x, y, 6);
                    gc.strokePolygon(x, y, 6);
                    break;
            }
        }
    }
}

class PoliticalMap extends Map{
    public PoliticalMap(MapData mapData, GraphicsContext gc) {
        super(mapData, gc);
    }

    @Override
    public void drawHex(List<Point> points, HexData hexData){
    }
}
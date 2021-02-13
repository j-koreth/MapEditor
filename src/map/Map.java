package map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.*;

public class Map implements Serializable{
    enum TerrainType {Ocean, Land, Lake};
    enum Modifier {Move};

    HashMap<Hexagon, HexData> map;
    Orientation layout_pointy = new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0,  Math.sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5);
    List<Hexagon> directions = Arrays.asList(new Hexagon(1,0), new Hexagon(1,-1), new Hexagon(0,-1), new Hexagon(-1,0), new Hexagon(-1,1), new Hexagon(0,1));
    Layout layout;

    public Map(){
        map = new HashMap<>();

        for (int r = 0; r < 16; r++) {
            int r_offset = (int) Math.floor(r/2);
            for (int q = -r_offset; q < 20 - r_offset; q++) {
                map.put(new Hexagon(q, r), new HexData());
            }
        }
        layout = new Layout(layout_pointy, new Point(25,25), new Point(20,20));

    }

    public Map(double sizeX, double sizeY, int mapX, int mapY){
        for (int r = 0; r < 15; r++) {
            int r_offset = (int) Math.floor(r/2);
            for (int q = -r_offset; q < 21 - r_offset; q++) {
                map.put(new Hexagon(q, r), new HexData());
            }
        }
        layout = new Layout(layout_pointy, new Point(sizeX,sizeY), new Point(mapX,mapY));
    }

    public void drawMap(GraphicsContext gc){
        for(HashMap.Entry<Hexagon, HexData> entry: map.entrySet()){
            drawHex(hex_points(layout, entry.getKey()), gc, entry.getValue());
        }
    }

    public void clearMap(GraphicsContext gc){
        gc.clearRect(0,0,1000,1000);
    }

    public void loadMap(HashMap<Hexagon, HexData> map){
        this.map = map;
    }

    public void drawHex(List<Point> points, GraphicsContext gc, HexData hexData){
        double[] x = points.stream().mapToDouble(Point::getX).toArray();
        double[] y = points.stream().mapToDouble(Point::getY).toArray();


        if(hexData.modifier != null){
            switch (hexData.modifier){
                case Move:
                    gc.setStroke(Color.RED);
                    gc.setFill(Color.WHITE);
                    gc.fillPolygon(x, y, 6);
                    gc.strokePolygon(x, y, 6);
                    break;
            }
        }
        else{
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


    Point hex_to_pixel(Layout layout, Hexagon h) {
        Orientation M = layout.orientation;
        double x = (M.f0 * h.x + M.f1 * h.y) * layout.size.x;
        double y = (M.f2 * h.x + M.f3 * h.y) * layout.size.y;
        return new Point(x + layout.origin.x, y + layout.origin.y);
    }

    Point hex_corner_offset(Layout layout, int corner) {
        Point size = layout.size;
        double angle = 2.0 * Math.PI *
                (layout.orientation.start_angle + corner) / 6;
        return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }

    List<Point> hex_points(Layout layout, Hexagon h) {
        List<Point> corners = new ArrayList<>();
        Point center = hex_to_pixel(layout, h);
        for (int i = 0; i < 6; i++) {
            Point offset = hex_corner_offset(layout, i);
            corners.add(new Point(center.x + offset.x,center.y + offset.y));
        }
        return corners;
    }

    Hexagon pixel_to_Hex(Layout layout, Point p){
        Orientation M = layout.orientation;

        Point pt = new Point((p.getX() - layout.origin.x) / layout.size.x,
                (p.getY() - layout.origin.y) / layout.size.y);

        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;

        return new Hexagon((int) Math.round(q), (int) Math.round(r));
    }

    HexData getHexData(Hexagon a){
        return map.get(a);
    }

    Hexagon add(Hexagon a, Hexagon b){
        return new Hexagon(a.x + b.x, a.y + b.y);
    }

    Hexagon subtract(Hexagon a, Hexagon b){
        return new Hexagon(a.x - b.x, a.y - b.y);
    }

    Hexagon multiply(Hexagon a, Hexagon b){
        return new Hexagon(a.x - b.x, a.y - b.y);
    }

    int length(Hexagon a){
        return (Math.abs(a.x) + Math.abs(a.y) + Math.abs(a.z) / 2);
    }

    int distance(Hexagon a, Hexagon b){
        return length(subtract(a,b));
    }

    public Hexagon neighbor(Hexagon a, int direction){
        return add(a, directions.get(direction));
    }

    public ArrayList<Hexagon> getNeighbors(Hexagon a){
        ArrayList<Hexagon> neighbors = new ArrayList<>();
        for(int x = 0; x < 6; x++){
            neighbors.add(neighbor(a, x));
        }
        return neighbors;
    }
}

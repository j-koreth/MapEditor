package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapData implements Serializable {
    HashMap<Hexagon, HexData> data;
    Orientation layout_pointy = new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0,  Math.sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5);
    List<Hexagon> directions = Arrays.asList(new Hexagon(1,0), new Hexagon(1,-1), new Hexagon(0,-1), new Hexagon(-1,0), new Hexagon(-1,1), new Hexagon(0,1));
    Layout layout = new Layout(layout_pointy, new Point(25,25), new Point(25,25));

    Point origin;

    public MapData(){
        data = new HashMap<>();

        //Rectangle Map
        for (int r = 0; r < 15; r++) {
            int r_offset = (int) Math.floor(r/2);
            for (int q = -r_offset; q < 22 - r_offset; q++) {
                data.put(new Hexagon(q, r), new HexData());
            }
        }
    }

    public HashMap<Hexagon, HexData> getData() {
        return data;
    }

    public void setData(HashMap<Hexagon, HexData> data) {
        this.data = data;
    }

    Point hex_to_pixel(Hexagon h) {
        Orientation M = layout.orientation;
        double x = (M.f0 * h.x + M.f1 * h.y) * layout.size.x;
        double y = (M.f2 * h.x + M.f3 * h.y) * layout.size.y;
        return new Point(x + layout.origin.x, y + layout.origin.y);
    }

    Point hex_corner_offset(int corner) {
        Point size = layout.size;
        double angle = 2.0 * Math.PI *
                (layout.orientation.start_angle + corner) / 6;
        return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }

    List<Point> hex_points(Hexagon h) {
        List<Point> corners = new ArrayList<>();
        Point center = hex_to_pixel(h);
        for (int i = 0; i < 6; i++) {
            Point offset = hex_corner_offset(i);
            corners.add(new Point(center.x + offset.x,center.y + offset.y));
        }
        return corners;
    }

    Hexagon pixel_to_Hex(Point p){
        Orientation M = layout.orientation;

        Point pt = new Point((p.getX() - layout.origin.x) / layout.size.x,
                (p.getY() - layout.origin.y) / layout.size.y);

        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;

        return new Hexagon((int) Math.round(q), (int) Math.round(r));
    }

    HexData getHexData(Hexagon a){
        return data.get(a);
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

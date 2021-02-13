package map;

import java.io.Serializable;

public class Layout implements Serializable {
    Orientation orientation;
    Point size;
    Point origin;

    public Layout(Orientation orientation, Point size, Point origin) {
        this.orientation = orientation;
        this.size = size;
        this.origin = origin;
    }
}

package items;

import java.util.List;

public class Polygon extends LayerElement {

    public Polygon(String layer, List<Point> points) {
        super(layer, points);
    }

    public Polygon(Polygon polygon) {
        super(polygon);
    }

    @Override
    public LayerElement slightCopy() {
        return new Polygon(this);
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "points=" + points +
                ", layer=" + layer +
                '}';
    }

}

package items;

import java.util.List;

public class Wire extends LayerElement {

    private final double width;

    public Wire(double width, String layer, List<Point> points) {
        super(layer, points);
        this.width = width;
    }

    public Wire(Wire wire) {
        super(wire);
        width = wire.width;
    }

    public double width() {
        return width;
    }

    @Override
    public LayerElement slightCopy() {
        return new Wire(this);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o)
               || (o instanceof Wire wire)
               && (super.equals(wire))
               && (width == wire.width);
    }

    @Override
    public String toString() {
        return "Wire{" +
                "width=" + width +
                ", points=" + points +
                ", layer=" + layer +
                '}';
    }

    @Override
    public String coordinatesToString() {
        return (int) width + " " + super.coordinatesToString();
    }

}

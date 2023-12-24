package items;

public record BlockWire(String layer, Edge edge, double width) {

    public boolean isHorizontal() {
        return edge.isHorizontal();
    }

    public boolean isVertical() {
        return edge.isVertical();
    }

    public double x() {
        return edge.x();
    }

    public double y() {
        return edge.y();
    }

    public String coordinatesToString() {
        return String.join(" ", String.valueOf((int) width),
                edge.point1().coordinatesToString(), edge.point2().coordinatesToString());
    }

}

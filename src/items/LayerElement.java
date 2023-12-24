package items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class LayerElement {

    protected final String layer;

    protected final List<Point> points;

    protected final List<Edge> edges;

    public LayerElement(String layer, List<Point> points) {
        this.layer = layer;
        this.points = points;
        this.edges = createEdges(points);
    }

    public LayerElement(LayerElement layerElement) {
        this(layerElement.layer, new ArrayList<>(layerElement.points));
    }

    public abstract LayerElement slightCopy();

    private List<Edge> createEdges(List<Point> points) {
        List<Edge> newEdges = new ArrayList<>();
        if (points.size() > 0) {
            for (int i = 0; i < points().size() - 1; ++i) {
                newEdges.add(createEdge(points.get(i), points.get(i + 1)));
            }
            if (this instanceof Polygon) {
                newEdges.add(createEdge(points.get(points.size() - 1), points.get(0)));
            }
        }
        return newEdges;
    }

    private Edge createEdge(Point point1, Point point2) {
        return new Edge(point1, point2);
    }

    public String layer() {
        return layer;
    }

    public List<Point> points() {
        return points;
    }

    public List<Edge> edges() {
        return edges;
    }

    public boolean isAnyPointInBlock(Block block) {
        return points.stream()
                .anyMatch(point -> point.isInsideBlock(block));
    }

    public boolean isAllPointsInBlock(Block block) {
        return points.stream()
                .allMatch(point -> point.isInsideBlock(block));
    }

    public boolean isNonePointInBlock(Block block) {
        return points.stream()
                .noneMatch(point -> point.isInsideBlock(block));
    }

    public String coordinatesToString() {
        return points.stream()
                .map(Point::coordinatesToString)
                .collect(Collectors.joining(" "));
    }

    public void replacePointsList(List<Point> points) {
        this.points.clear();
        this.points.addAll(points);
    }

    public List<Point> pointsInsideBlock(Block block) {
        return points.stream()
                .filter(point -> point.isInsideBlock(block))
                .toList();
    }

    public List<Point> pointsOutsideBlock(Block block) {
        return points.stream()
                .filter(point -> !point.isInsideBlock(block))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        return (this == o)
               || (o instanceof LayerElement layerElement)
               && (points.equals(layerElement.points)) && (layer.equals(layerElement.layer));

    }

    @Override
    public int hashCode() {
        return Objects.hash(points, layer);
    }

}

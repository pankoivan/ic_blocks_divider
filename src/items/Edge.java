package items;

import java.util.Optional;
import static utils.Utils.doubleEqualsGreater;
import static utils.Utils.doubleEquals;
import static utils.Utils.doubleEqualsLess;

public record Edge(Point point1, Point point2) {

    public Edge(Point point1, Point point2) {
        if (isHorizontal(point1, point2)) {
            this.point1 = Point.leftPoint(point1, point2);
            this.point2 = Point.rightPoint(point1, point2);
        } else if (isVertical(point1, point2)) {
            this.point1 = Point.bottomPoint(point1, point2);
            this.point2 = Point.topPoint(point1, point2);
        } else {
            this.point1 = point1;
            this.point2 = point2;
        }
    }

    public boolean isHorizontal() {
        return isHorizontal(point1, point2);
    }

    public boolean isVertical() {
        return isVertical(point1, point2);
    }

    public double x() {
        return point1.x();
    }

    public double y() {
        return point1.y();
    }

    public Optional<Point> intersectInPoint(Edge edge) {
        if (isHorizontal() && edge.isVertical()) {
            return auxiliaryIntersectionInPoint(this, edge);
        } else if (isVertical() && edge.isHorizontal()) {
            return auxiliaryIntersectionInPoint(edge, this);
        }
        return Optional.empty();
    }

    private static boolean isHorizontal(Point point1, Point point2) {
        return doubleEquals(point1.y(), point2.y());
    }

    private static boolean isVertical(Point point1, Point point2) {
        return doubleEquals(point1.x(), point2.x());
    }

    private static Optional<Point> auxiliaryIntersectionInPoint(Edge hor, Edge ver) {
        return auxiliaryIntersection(hor, ver)
                ? Optional.of(new Point(ver.x(), hor.y()))
                : Optional.empty();
    }

    private static boolean auxiliaryIntersection(Edge hor, Edge ver) {
        return doubleEqualsGreater(ver.x(), hor.point1.x()) && doubleEqualsLess(ver.x(), hor.point2.x())
                && doubleEqualsLess(ver.point1.y(), hor.y()) && doubleEqualsGreater(ver.point2.y(), hor.y());
    }

}

package utils;

import items.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public final class Utils {

    public static boolean doubleGreater(double double1, double double2) {
        return Double.compare(double1, double2) > 0;
    }

    public static boolean doubleEqualsGreater(double double1, double double2) {
        return Double.compare(double1, double2) >= 0;
    }

    public static boolean doubleEquals(double double1, double double2) {
        return Double.compare(double1, double2) == 0;
    }

    public static boolean doubleLess(double double1, double double2) {
        return Double.compare(double1, double2) < 0;
    }

    public static boolean doubleEqualsLess(double double1, double double2) {
        return Double.compare(double1, double2) <= 0;
    }

    private static List<Double> sorted(List<BlockWire> blockWires,
                                       Predicate<BlockWire> predicate, ToDoubleFunction<BlockWire> supplier) {
        return blockWires.stream()
                .filter(predicate)
                .mapToDouble(supplier)
                .distinct()
                .sorted()
                .boxed()
                .toList();
    }

    public static List<Double> sortedUniqueYsOfHorizontals(List<BlockWire> blockWires) {
        return sorted(blockWires, BlockWire::isHorizontal, BlockWire::y);
    }

    public static List<Double> sortedUniqueXsOfVerticals(List<BlockWire> blockWires) {
        return sorted(blockWires, BlockWire::isVertical, BlockWire::x);
    }

    public static boolean isLayerElementPassThroughBlock(LayerElement element, Block block) {
        for (BlockWire blockWire : block.blockWires()) {
            if (isLayerElementIntersectEdge(element, blockWire.edge())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLayerElementIntersectEdge(LayerElement element, Edge edge2) {
        for (Edge edge1 : element.edges()) {
            if (intersectionOfEdgesInPoint(edge1, edge2).isPresent()) {
                return true;
            }
        }
        return false;
    }

    public static Optional<Point> intersectionOfEdgesInPoint(Edge edge1, Edge edge2) {
        return edge1.intersectInPoint(edge2);
    }

    public static int numberOfIntersectionsOfLayerElementAndEdge(LayerElement element, Edge edge2) {
        int i = 0;
        for (Edge edge1 : element.edges()) {
            if (intersectionOfEdgesInPoint(edge1, edge2).isPresent()) {
                ++i;
            }
        }
        return i;
    }

    public static void cutLayerElementByBlock(LayerElement element, Block block, boolean cutMode) {

        List<Point> newPoints = new ArrayList<>(element.pointsInsideBlock(block));
        for (Edge edge : element.edges()) {
            for (BlockWire blockWire : block.blockWires()) {
                intersectionOfEdgesInPoint(edge, blockWire.edge()).ifPresent(newPoints::add);
            }
        }

        if (element instanceof Polygon polygon) {
            if (isPointInsidePolygon(block.leftBottom(), polygon)) {
                newPoints.add(block.leftBottom());
            }
            if (isPointInsidePolygon(block.leftTop(), polygon)) {
                newPoints.add(block.leftTop());
            }
            if (isPointInsidePolygon(block.rightTop(), polygon)) {
                newPoints.add(block.rightTop());
            }
            if (isPointInsidePolygon(block.rightBottom(), polygon)) {
                newPoints.add(block.rightBottom());
            }
        }

        if (!cutMode) {
            replaceWhenNotCutAllMode(element, block, newPoints);
        }

        element.replacePointsList(makeCutLayerElement(element, newPoints));

    }

    private static List<Double> nearestPoints(LayerElement element, Block block) {

        double leftNearestX = Integer.MIN_VALUE, topNearestY = Integer.MAX_VALUE,
                rightNearestX = Integer.MAX_VALUE, bottomNearestY = Integer.MIN_VALUE;

        for (Point point : element.pointsOutsideBlock(block)) {
            if (doubleEqualsLess(point.x(), block.leftX()) && doubleGreater(point.x(), leftNearestX)) {
                leftNearestX = point.x();
            }
            if (doubleEqualsGreater(point.y(), block.topY()) && doubleLess(point.y(), topNearestY)) {
                topNearestY = point.y();
            }
            if (doubleEqualsGreater(point.x(), block.rightX()) && doubleLess(point.x(), rightNearestX)) {
                rightNearestX = point.x();
            }
            if (doubleEqualsLess(point.y(), block.bottomY()) && doubleGreater(point.y(), bottomNearestY)) {
                bottomNearestY = point.y();
            }
        }

        return List.of(leftNearestX, topNearestY, rightNearestX, bottomNearestY);
    }

    private static void replaceWhenNotCutAllMode(LayerElement element, Block block, List<Point> newPoints) {

        List<Double> nearestPoints = nearestPoints(element, block);

        newPoints.replaceAll(point -> doubleEquals(point.x(), block.leftX())
                ? new Point(point.x() - step(point.x(), nearestPoints.get(0)), point.y())
                : point);

        newPoints.replaceAll(point -> doubleEquals(point.y(), block.topY())
                ? new Point(point.x(), point.y() + step(point.y(), nearestPoints.get(1)))
                : point);

        newPoints.replaceAll(point -> doubleEquals(point.x(), block.rightX())
                ? new Point(point.x() + step(point.x(), nearestPoints.get(2)), point.y())
                : point);

        newPoints.replaceAll(point -> doubleEquals(point.y(), block.bottomY())
                ? new Point(point.x(), point.y() - step(point.y(), nearestPoints.get(3)))
                : point);

    }

    private static List<Point> makeCutLayerElement(LayerElement layerElement, List<Point> points) {

        List<Point> finalPoints = new ArrayList<>();
        Point current = points.get(0);
        finalPoints.add(current);

        while (!points.isEmpty()) {

            List<Point> sameYPoints = new ArrayList<>(pointsWithSameY(points, current));
            List<Point> sameXPoints = new ArrayList<>(pointsWithSameX(points, current));

            Point newPoint = makeNextEdge(sameYPoints, layerElement, current, points, finalPoints);

            if (current.equals(newPoint)) {
                newPoint = makeNextEdge(sameXPoints, layerElement, current, points, finalPoints);
            }

            if (current.equals(newPoint)) {
                points.remove(current);
            } else {
                current = newPoint;
            }

        }

        return finalPoints;

    }

    private static Point makeNextEdge(List<Point> samePoints, LayerElement element, Point current,
                                      List<Point> points, List<Point> finalPoints) {
        for (Point same : samePoints) {
            if (conditionForMakingNextEdge(element, current, same)) {
                points.remove(current);
                finalPoints.add(same);
                return same;
            }
        }
        return current;
    }

    private static boolean conditionForMakingNextEdge(LayerElement element, Point current, Point same) {
        return element.edges().contains(new Edge(current, same))
                || !element.points().contains(current)
                || !element.points().contains(same);
    }

    private static List<Point> pointsWithSameX(List<Point> points, Point point) {
        return points.stream()
                .filter(p -> doubleEquals(p.x(), point.x()) && !p.equals(point))
                .toList();
    }

    private static List<Point> pointsWithSameY(List<Point> points, Point point) {
        return points.stream()
                .filter(p -> doubleEquals(p.y(), point.y()) && !p.equals(point))
                .toList();
    }

    private static Edge rayToLeft(Point point) {
        return new Edge(point, new Point(Double.NEGATIVE_INFINITY, point.y()));
    }

    private static Edge rayToTop(Point point) {
        return new Edge(point, new Point(point.x(), Double.POSITIVE_INFINITY));
    }

    private static Edge rayToRight(Point point) {
        return new Edge(point, new Point(Double.POSITIVE_INFINITY, point.y()));
    }

    private static Edge rayToBottom(Point point) {
        return new Edge(point, new Point(point.x(), Double.NEGATIVE_INFINITY));
    }

    private static boolean isPointInsidePolygon(Point point, Polygon polygon) {
        return numberOfIntersectionsOfLayerElementAndEdge(polygon, rayToLeft(point)) % 2 != 0
                || numberOfIntersectionsOfLayerElementAndEdge(polygon, rayToTop(point)) % 2 != 0
                || numberOfIntersectionsOfLayerElementAndEdge(polygon, rayToRight(point)) % 2 != 0
                || numberOfIntersectionsOfLayerElementAndEdge(polygon, rayToBottom(point)) % 2 != 0
                || polygon.points().contains(point);
    }

    private static double step(double double1, double double2) {
        return Math.abs(double1 - double2) / 4;
    }

}

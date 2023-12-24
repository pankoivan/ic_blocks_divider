package items;

import static utils.Utils.doubleGreater;
import static utils.Utils.doubleEquals;
import static utils.Utils.doubleLess;

public record Point(double x, double y) {

    public boolean isInsideBlock(Block block) {
        return rightAndTop(block.leftBottom()) && leftAndBottom(block.rightTop());
    }

    public boolean left(Point point) {
        return doubleLess(x, point.x);
    }

    public boolean top(Point point) {
        return doubleGreater(y, point.y);
    }

    public boolean right(Point point) {
        return doubleGreater(x, point.x);
    }

    public boolean bottom(Point point) {
        return doubleLess(y, point.y);
    }

    public Point minusPoint(Point point) {
        return point.isZeroZero()
                ? this
                : new Point(x - point.x, y - point.y);
    }

    public Point plusPoint(Point point) {
        return point.isZeroZero()
                ? this
                : new Point(x + point.x, y + point.y);
    }

    public String coordinatesToString() {
        return (int) x + " " + (int) y;
    }

    private boolean rightAndTop(Point point) {
        return right(point) && top(point);
    }

    private boolean leftAndBottom(Point point) {
        return left(point) && bottom(point);
    }

    private boolean isZeroZero() {
        return doubleEquals(x, 0) && doubleEquals(y, 0);
    }

    public static Point leftPoint(Point point1, Point point2) {
        return point1.left(point2) ? point1 : point2;
    }

    public static Point bottomPoint(Point point1, Point point2) {
        return point1.bottom(point2) ? point1 : point2;
    }

    public static Point rightPoint(Point point1, Point point2) {
        return point1.right(point2) ? point1 : point2;
    }

    public static Point topPoint(Point point1, Point point2) {
        return point1.top(point2) ? point1 : point2;
    }

}

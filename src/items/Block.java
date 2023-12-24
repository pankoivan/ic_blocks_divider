package items;

import utils.Constants;

import java.util.ArrayList;
import java.util.List;

public record Block(int id, int cellNumber, String cellName, String cellScale,
                    Point leftBottom, Point leftTop, Point rightTop, Point rightBottom,
                    List<LayerElement> layerElements, List<BlockWire> blockWires) {

    public double leftX() {
        return leftBottom.x();
    }

    public double topY() {
        return leftTop.y();
    }

    public double rightX() {
        return rightTop.x();
    }

    public double bottomY() {
        return rightBottom.y();
    }

    public static List<Block> makeBlocks(Cell cell, List<Double> xs, List<Double> ys) {
        List<Block> blocks = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < xs.size() - 1; ++i) {
            for (int j = 0; j < ys.size() - 1; ++j) {
                blocks.add(makeBlock(id, cell.number(), cell.name(), cell.scale(),
                        xs.get(i), ys.get(j), xs.get(i), ys.get(j + 1),
                        xs.get(i + 1), ys.get(j + 1), xs.get(i + 1), ys.get(j)));
                ++id;
            }
        }
        return blocks;
    }

    private static Block makeBlock(int id, int cellNumber, String cellName, String cellScale,
                                  double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        Point leftBottom = new Point(x1, y1);
        Point leftTop = new Point(x2, y2);
        Point rightTop = new Point(x3, y3);
        Point rightBottom = new Point(x4, y4);

        return new Block(id, cellNumber, cellName, cellScale,
                leftBottom, leftTop, rightTop, rightBottom, new ArrayList<>(),
                makeBlockWires(leftBottom, leftTop, rightTop, rightBottom));

    }

    private static List<BlockWire> makeBlockWires(Point leftBottom, Point leftTop, Point rightTop, Point rightBottom) {

        BlockWire blockWire1 = new BlockWire(Constants.B1, new Edge(leftBottom, leftTop), 0);
        BlockWire blockWire2 = new BlockWire(Constants.B1, new Edge(leftTop, rightTop), 0);
        BlockWire blockWire3 = new BlockWire(Constants.B1, new Edge(rightTop, rightBottom), 0);
        BlockWire blockWire4 = new BlockWire(Constants.B1, new Edge(rightBottom, leftBottom), 0);

        return new ArrayList<>(List.of(blockWire1, blockWire2, blockWire3, blockWire4));

    }

}

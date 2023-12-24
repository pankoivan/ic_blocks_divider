package items;

import java.util.List;

public record Cell(String comments, int number, String name, String scale,
                   List<LayerElement> layerElements, List<BlockWire> blockWires) {

    public boolean hasBlockWires() {
        return !blockWires.isEmpty();
    }

}

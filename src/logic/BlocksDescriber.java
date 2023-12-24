package logic;

import items.*;
import utils.Settings;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BlocksDescriber {

    private final Path src;

    private final Path dest;

    private final boolean append;

    private final boolean cut;

    private final boolean cutMode;

    private final Circuit circuit;

    public BlocksDescriber(Path src, Path dest, boolean append, boolean cut, boolean cutMode) throws IOException {
        this.src = src;
        this.dest = dest;
        this.append = append;
        this.cut = cut;
        this.cutMode = cutMode;
        circuit = createCircuit();
    }

    private Circuit createCircuit() throws IOException {
        return new SourceFileParser(src).createCircuit();
    }

    private List<Block> divideCellIntoBlocks(Cell cell) {
        return Block.makeBlocks(
                cell, Utils.sortedUniqueXsOfVerticals(cell.blockWires()), Utils.sortedUniqueYsOfHorizontals(cell.blockWires())
        );
    }

    private void fillBlocks(Cell cell, List<Block> blocks) {
        for (LayerElement element : cell.layerElements()) {
            for (Block block : blocks) {
                if (element.isAnyPointInBlock(block) || Utils.isLayerElementPassThroughBlock(element, block)) {
                    block.layerElements().add(element.slightCopy());
                }
            }
        }
    }

    private void cutLayerElementsInBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            for (LayerElement element : block.layerElements()) {
                Utils.cutLayerElementByBlock(element, block, cutMode);
            }
        }
    }

    private void changeCoordinateSystemForBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            for (LayerElement element : block.layerElements()) {
                element.points().replaceAll(point -> point.minusPoint(block.leftBottom()));
            }
            block.blockWires().replaceAll(blockWire -> new BlockWire(
                    blockWire.layer(), new Edge(blockWire.edge().point1().minusPoint(block.leftBottom()),
                    blockWire.edge().point2().minusPoint(block.leftBottom())), blockWire.width()
            ));
        }
    }

    private List<Block> finalBlocks(Cell cell) {
        List<Block> blocks = divideCellIntoBlocks(cell);
        fillBlocks(cell, blocks);
        if (cut) {
            cutLayerElementsInBlocks(blocks);
        }
        changeCoordinateSystemForBlocks(blocks);
        return blocks;
    }

    public void describeBlocksForAllCells() throws IOException {
        if (append) {
            withAppend();
        } else {
            withoutAppend();
        }
    }

    private void withAppend() throws IOException {
        try (DestFileWriter writer = new DestFileWriter(
                Paths.get(dest.toString(), src.getFileName() + " with blocks.cif")
        )) {
            writer.writeOwnComment("Схема вместе с блоками, на которые она была разбита по шинам типов \"B1\" и \"B2\"");
            writer.writeCells(circuit.cells());
            for (Cell cell : circuit.cells()) {
                if (cell.hasBlockWires()) {
                    writer.writeBlocks(finalBlocks(cell));
                }
            }
            writer.writeEndOfFile();
        }
    }

    private void withoutAppend() throws IOException {
        for (Cell cell : circuit.cells()) {
            if (cell.hasBlockWires()) {
                List<Block> blocks = finalBlocks(cell);
                for (Block block : blocks) {
                    try (DestFileWriter writer = new DestFileWriter(
                            Paths.get(dest.toString(), "Block " + block.id() + " for " + cell.name() + " from file " +
                                    Settings.getSrc().getFileName() + ".cif")
                    )) {
                        writer.writeOwnComment("Отдельный блок, окружённый шинами типа \"B1\"");
                        writer.writeBlock(block);
                        writer.writeEndOfFile();
                    }
                }
            }
        }
    }

}

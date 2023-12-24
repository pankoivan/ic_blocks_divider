package logic;

import items.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DestFileWriter implements AutoCloseable {

    private final Path dest;

    private final FileWriter fileWriter;

    public DestFileWriter(Path dest) throws IOException {
        this.dest = dest;
        fileWriter = createFileWriter();
    }

    private FileWriter createFileWriter() throws IOException {
        return new FileWriter(dest.toFile());
    }

    public void writeCells(List<Cell> cells) throws IOException {
        for (Cell cell : cells) {
            writeCell(cell);
        }
    }

    public void writeCell(Cell cell) throws IOException {
        write("\nDS " + cell.number() + " " + cell.scale());
        write("9 " + cell.name());
        write(cell.comments());
        for (LayerElement element : cell.layerElements()) {
            write(String.join(" ", "L", element.layer()));
            String label = element instanceof Polygon ? "P" : "W";
            write(String.join(" ", label, element.coordinatesToString()));
        }
        for (BlockWire blockWire : cell.blockWires()) {
            write(String.join(" ", "L", blockWire.layer()));
            write(String.join(" ", "W", blockWire.coordinatesToString()));
        }
        write("DF");
    }

    public void writeBlocks(List<Block> blocks) throws IOException {
        for (Block block : blocks) {
            writeBlock(block);
        }
    }

    public void writeBlock(Block block) throws IOException {
        write2("\n\n\n\n\n\n\n");
        writeOwnComment("Для построения исходной схемы левый нижний угол этого блока должен быть установлен в точку:");
        writeOwnComment("[" + block.leftBottom().x() + ", " + block.leftBottom().y() + "]");
        write2("\n");
        write("DS " + block.id() + " " + block.cellScale());
        write("9 " + block.cellName() + "_" + block.id());
        for (LayerElement element : block.layerElements()) {
            write(String.join(" ", "L", element.layer()));
            String label = element instanceof Polygon ? "P" : "W";
            write(String.join(" ", label, element.coordinatesToString()));
        }
        for (BlockWire blockWire : block.blockWires()) {
            write(String.join(" ", "L", blockWire.layer()));
            write(String.join(" ", "W", blockWire.coordinatesToString()));
        }
        write("DF");
    }

    public void writeOwnComment(String comment) throws IOException {
        write("(" + comment + ")");
    }

    public void writeEndOfFile() throws IOException {
        fileWriter.write("E\n");
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }

    private void write(String string) throws IOException {
        fileWriter.write(string + ";\n");
    }

    private void write2(String string) throws IOException {
        fileWriter.write(string);
    }

}

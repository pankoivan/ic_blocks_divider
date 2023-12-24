package logic;

import items.*;
import utils.Constants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SourceFileParser {

    private final Path src;

    private final String text;

    public SourceFileParser(Path src) throws IOException {
        this.src = src;
        text = readSourceFile();
    }

    private String readSourceFile() throws IOException {
        return String.join("\n", Files.readAllLines(src, Charset.forName("windows-1251")));
    }

    public Circuit createCircuit() {
        return new Circuit(createCells());
    }

    private List<Cell> createCells() {
        List<Cell> cells = new ArrayList<>();
        for (List<String> cellLines : getCellsLines()) {
            cells.add(createCell(cellLines));
        }
        return cells;
    }

    private Cell createCell(List<String> cellLines) {

        List<String> numberScaleName = createNumberScaleNameAndRemoveThem(cellLines);
        int number = Integer.parseInt(numberScaleName.get(0));
        String scale = numberScaleName.get(1);
        String name = numberScaleName.get(2);

        String comments = createCommentsAndRemoveThem(cellLines);

        List<LayerElement> layerElements = new ArrayList<>();
        List<BlockWire> blockWires = new ArrayList<>();
        fillLayerElementsAndBlockWires(cellLines, layerElements, blockWires);

        return new Cell(comments, number, name, scale, layerElements, blockWires);

    }

    public void fillLayerElementsAndBlockWires(List<String> cellLines, List<LayerElement> layerElements, List<BlockWire> blockWires) {

        int i = 0;
        while (i < cellLines.size() - 1) {

            String f = cellLines.get(i);
            String s = cellLines.get(i + 1);

            String layer = f.substring(f.indexOf(" ") + 1);
            List<String> coordinates = createCoordinates(s.substring(s.indexOf(" ") + 1));

            if (s.startsWith("P")) {
                layerElements.add(createPolygon(layer, coordinates));
                i += 2;
            } else if (s.startsWith("W")) {
                if (List.of(Constants.BLOCK_WIRE_TYPES).contains(layer)) {
                    blockWires.addAll(createBlockWires(layer, coordinates));
                } else {
                    layerElements.add(createWire(layer, coordinates));
                }
                i += 2;
            } else if (s.startsWith("L")) {
                layerElements.add(createPolygon(layer, new ArrayList<>()));
                ++i;
            } else if (s.startsWith("4N")) {
                layerElements.add(createPolygon(layer, new ArrayList<>()));
                i += 2;
            } else if (s.startsWith("DF")) {
                layerElements.add(createPolygon(layer, new ArrayList<>()));
                ++i;
            }

        }

    }

    private List<String> createCoordinates(String string) {
        return Arrays.stream(string.split("\\s"))
                .filter(str -> !str.equals(""))
                .toList();
    }

    private Polygon createPolygon(String layer, List<String> coordinates) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < coordinates.size() - 1; i += 2) {
            points.add(new Point(
                    Double.parseDouble(coordinates.get(i)), Double.parseDouble(coordinates.get(i + 1))
            ));
        }
        return new Polygon(layer, points);
    }

    private Wire createWire(String layer, List<String> coordinates) {
        List<Point> points = new ArrayList<>();
        for (int i = 1; i < coordinates.size() - 1; i += 2) {
            points.add(new Point(
                    Double.parseDouble(coordinates.get(i)), Double.parseDouble(coordinates.get(i + 1))
            ));
        }
        return new Wire(Double.parseDouble(coordinates.get(0)), layer, points);
    }

    private List<BlockWire> createBlockWires(String layer, List<String> coordinates) {
        List<BlockWire> blockWires = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        for (int i = 1; i < coordinates.size() - 1; i += 2) {
            points.add(new Point(
                    Double.parseDouble(coordinates.get(i)), Double.parseDouble(coordinates.get(i + 1))
            ));
        }
        for (int i = 0; i < points.size() - 1; i += 1) {
            blockWires.add(
                    new BlockWire(layer, new Edge(points.get(i), points.get(i + 1)), Double.parseDouble(coordinates.get(0)))
            );
        }
        return blockWires;
    }

    private List<String> createNumberScaleNameAndRemoveThem(List<String> cellLines) {

        String first = cellLines.get(0);
        String second = cellLines.get(1);

        String number = first.substring(3, first.indexOf(" ", 3));
        String scale = first.substring(first.indexOf(" ", 3) + 1);
        String name = second.substring(second.indexOf(" ") + 1);

        cellLines.remove(0);
        cellLines.remove(0);

        return new ArrayList<>(List.of(number, scale, name));

    }

    private String createCommentsAndRemoveThem(List<String> cellLines) {

        StringBuilder comments = new StringBuilder();
        Iterator<String> iterator = cellLines.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.matches("\\(.*?\\)")) {
                comments.append(next).append(";\n");
                iterator.remove();
            }
        }
        return comments.toString();

    }

    private List<List<String>> getCellsLines() {

        List<List<String>> cellsLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("DS.*?DF;", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            cellsLines.add(getCellLines(matcher.group()));
        }
        return cellsLines;

    }

    private List<String> getCellLines(String line) {
        return new ArrayList<>(Stream.of(line.split(";"))
                .map(String::trim)
                .toList());
    }

}

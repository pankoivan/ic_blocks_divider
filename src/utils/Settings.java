package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class Settings {

    private static final String name = "Settings.txt";

    private static final Path src;

    private static final Path dest;

    private static final boolean writeMode;

    private static final boolean cut;

    private static final boolean cutMode;

    static {

        try {

            List<String> lines = readAllLines();
            src = defineSrc(lines.get(0));
            dest = defineDest(lines.get(1));
            writeMode = defineWriteMode(lines.get(2));
            cut = defineCut(lines.get(3));
            cutMode = cut && defineCutMode(lines.get(4));

        } catch (IOException e) {
            throw new Error("The file \"" + name + "\" was not found or errors occurred when opening it for reading. " +
                    "Put the file \"" + name + "\" in the same folder where the archive \"blocks.jar\" is located.", e);

        } catch (IndexOutOfBoundsException e) {
            throw new Error("The file \"" + name + "\" contains few than 4 non-empty lines or contains few than " +
                    "5 non-empty lines when non-empty line 4 says that figures should be cut.", e);

        } catch (InvalidPathException e) {
            throw new Error("First and second non-empty lines in the file \"" + name + "\" are not correct paths.", e);

        } catch (URISyntaxException e) {
            throw new Error("Some errors occurred when converting URL to URI", e);
        }

    }

    private static Path getAbsolutePath() throws URISyntaxException {
        /*return Paths.get(new File(Settings.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI())
                .getParent(), name);*/
        return Paths.get("Settings.txt");
    }

    private static List<String> readAllLines() throws IOException, URISyntaxException {
        return Files.readAllLines(getAbsolutePath()).stream()
                .filter(s -> !s.equals(""))
                .toList();
    }

    private static Path defineSrc(String src) throws InvalidPathException {
        return Paths.get(src.trim());
    }

    private static Path defineDest(String dest) throws InvalidPathException {
        return Paths.get(dest.trim());
    }

    private static boolean defineWriteMode(String writeMode) {
        return List.of("true", "yes", "append", "single file", "да", "один файл", "в один файл", "одним файлом")
                .contains(writeMode.trim().toLowerCase());
    }

    private static boolean defineCut(String cut) {
        return List.of("true", "yes", "cut", "truncate", "обрезать", "резать", "усечь", "усекать")
                .contains(cut.trim().toLowerCase());
    }

    private static boolean defineCutMode(String cutMode) {
        return List.of("true", "yes", "all", "entirely", "да", "полностью", "целиком")
                .contains(cutMode.trim().toLowerCase());
    }

    public static Path getSrc() {
        return src;
    }

    public static Path getDest() {
        return dest;
    }

    public static boolean getWriteMode() {
        return writeMode;
    }

    public static boolean getCut() {
        return cut;
    }

    public static boolean getCutMode() {
        return cutMode;
    }

}

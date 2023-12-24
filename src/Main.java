import logic.*;
import utils.Settings;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            start();
            System.out.println("The program has finished successfully");
            System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void start() throws IOException {
        BlocksDescriber describer = new BlocksDescriber(
                Settings.getSrc(), Settings.getDest(), Settings.getWriteMode(), Settings.getCut(), Settings.getCutMode()
        );
        describer.describeBlocksForAllCells();
    }

}

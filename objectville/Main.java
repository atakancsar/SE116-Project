package objectville;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java objectville.Main <mapfile> <ticks>");
            return;
        }
        try {
            MapParser parser = new MapParser();
            char[][] map = parser.readAndParseByLine(args[0]);
            System.out.println("Map loaded: " + map.length + " rows x " + map[0].length + " columns");
        } catch (IOException e) {
            System.err.println("Error reading map: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: invalid map file - " + e.getMessage());
        }
    }
}

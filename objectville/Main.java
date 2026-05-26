package objectville;

import java.io.IOException;

import objectville.cells.Cell;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Use like this format: java objectville.Main <mapfile>");
            return;
        }
        String mapPath = args[0];
        try {
            MapParser mParser = new MapParser();
            Creator creator = new Creator();
            char[][] map = mParser.readAndParseByLine(mapPath);
            Cell[][] city = creator.cityCreator(map);
            System.out.println("City grid created: " + city.length + " x " + city[0].length + " cells");
        } catch (IOException e) {
            System.err.println("Error reading map file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: invalid map file - " + e.getMessage());
        }
    }
}

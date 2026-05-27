package objectville;

import java.io.FileNotFoundException;
import java.io.IOException;

import objectville.cells.Cell;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Use like this format: java -jar ObjectVilleGame.jar <mapfile> <tickcount>");
            return;
        }

        String mapPath = args[0];
        int ticks;

        try {
            ticks = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: tick count must be a valid integer.");
            return;
        }

        if (ticks <= 0) {
            System.err.println("Error: tick count must be a positive integer.");
            return;
        }

        try {
            MapParser mParser = new MapParser();
            Creator creator = new Creator();

            char[][] map = mParser.readAndParseByLine(mapPath);
            Cell[][] city = creator.cityCreator(map);
            City city1 = new City(city);
            city1.simulate(ticks);
        } catch (FileNotFoundException e) {
            System.err.println("Error: map file not found: " + mapPath);
        } catch (IOException e) {
            System.err.println("Error reading map file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: invalid map file - " + e.getMessage());
        }
    }
}

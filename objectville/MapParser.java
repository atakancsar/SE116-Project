package objectville;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapParser {

    public char[][] readAndParseByLine(String path) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
                line = br.readLine();
            }
        }

        if (lines.size() == 0) {
            throw new IllegalArgumentException("map file is empty");
        }

        char[][] map = new char[lines.size()][];
        String aLine;
        String validChars = "HICPWTFDSER";

        for (int i = 0, size = lines.get(0).length(); i < lines.size(); i++) {
            aLine = lines.get(i);
            if (aLine.length() != size)
                throw new IllegalArgumentException(
                        "map is not rectangular row " + i + " has not same length with the other rows.");
            map[i] = new char[size];

            for (int j = 0; j < aLine.length(); j++) {
                char temp = Character.toUpperCase(aLine.charAt(j));
                if (validChars.indexOf(temp) != -1) {
                    map[i][j] = temp;
                } else
                    throw new IllegalArgumentException("Argument of: " + aLine.charAt(j) + " is not a valid argument.");
            }
        }
        return map;
    }
}

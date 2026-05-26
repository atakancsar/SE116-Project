package objectville;

import objectville.cells.Cell;
import objectville.cells.EmptyCell;
import objectville.cells.Road;
import objectville.cells.ServiceBuilding;
import objectville.cells.UtilityProvider;
import objectville.cells.Zone;

//  This class used for city and cell creation.
public class Creator {
    public Cell[][] cityCreator(char[][] map) {
        Cell[][] rCity = new Cell[map.length][map[0].length];
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                rCity[row][col] = cellCreator(map, row, col);
            }
        }
        return rCity;
    }

    public Cell cellCreator(char[][] map, int row, int col) {
        switch (map[row][col]) {
            case 'H':
                return new Zone.HousingZone('H', row, col);
            case 'I':
                return new Zone.IndustrialZone('I', row, col);
            case 'C':
                return new Zone.CommercialZone('C', row, col);
            case 'P':
                return new UtilityProvider.PowerPlant('P', row, col);
            case 'W':
                return new UtilityProvider.WaterStation('W', row, col);
            case 'T':
                return new UtilityProvider.TelecomHub('T', row, col);
            case 'F':
                return new ServiceBuilding.FireStation('F', row, col);
            case 'D':
                return new ServiceBuilding.DoctorClinic('D', row, col);
            case 'S':
                return new ServiceBuilding.School('S', row, col);
            case 'R':
                return new Road('R', row, col);
            case 'E':
                return new EmptyCell('E', row, col);
            default:
                throw new IllegalArgumentException("Unkown char argument. ");
        }
    }
}

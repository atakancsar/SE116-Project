package objectville;

import java.util.ArrayList;
import java.util.function.Consumer;

import objectville.cells.*;

public class City {
    private final Cell[][] city;
    private final ArrayList<Zone> zones;
    private final ArrayList<UtilityProvider> utilities;
    private final ArrayList<ServiceBuilding> services;

    public City(Cell[][] city) {
        this.city = city;
        zones = new ArrayList<>();
        utilities = new ArrayList<>();
        services = new ArrayList<>();

        Consumer<Cell> classify = cell -> {
            if (cell instanceof Zone)
                zones.add((Zone) cell);
            else if (cell instanceof UtilityProvider)
                utilities.add((UtilityProvider) cell);
            else if (cell instanceof ServiceBuilding)
                services.add((ServiceBuilding) cell);
        };

        for (int i = 0; i < city.length; i++) {
            for (int j = 0; j < city[i].length; j++) {
                classify.accept(city[i][j]);
            }
        }
    }

    public Cell[][] getCity() {
        return city;
    }

    public Cell getLeftNeighbor(Cell cell) {
        if (cell.getColumn() != 0) {
            return city[cell.getRow()][cell.getColumn() - 1];
        } else {
            return null;
        }
    }

    public Cell getTopNeighbor(Cell cell) {
        if (cell.getRow() != 0) {
            return city[cell.getRow() - 1][cell.getColumn()];
        } else {
            return null;
        }
    }

    public Cell getBottomNeighbor(Cell cell) {
        if (cell.getRow() != city.length - 1) {
            return city[cell.getRow() + 1][cell.getColumn()];
        } else {
            return null;
        }
    }

    public Cell getRightNeighbor(Cell cell) {
        if (cell.getColumn() != city[0].length - 1) {
            return city[cell.getRow()][cell.getColumn() + 1];
        }
        return null;
    }

    private void distributeServices() {
        zones.forEach(zone -> {
            zone.setHasSecurity(false);
            zone.setHasHealth(false);
            zone.setHasEducation(false);
        });

        for (ServiceBuilding service : services) {
            for (Zone zone : zones) {
                if (service.coversZone(zone)) {
                    switch (service.getServiceType()) {
                        case "security":
                            zone.setHasSecurity(true);
                            System.out.println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                                    + ") received security service");
                            break;
                        case "health":
                            if (zone instanceof Zone.HousingZone) {
                                zone.setHasHealth(true);
                                System.out
                                        .println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                                                + ") received health service");
                            }
                            break;
                        case "education":
                            if (zone instanceof Zone.HousingZone) {
                                zone.setHasEducation(true);
                                System.out
                                        .println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                                                + ") received education service");
                            }
                            break;
                    }
                }
            }
        }
    }

    public void simulate(int ticks) {
        for (int tick = 1; tick <= ticks; tick++) {
            System.out.println("Tick " + tick);
            distributeServices();
        }
    }
}

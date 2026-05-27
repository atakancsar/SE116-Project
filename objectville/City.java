package objectville;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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

    private void distributeUtilities() {
        zones.forEach(zone -> {
            zone.setElectricityReceived(0);
            zone.setWaterReceived(0);
            zone.setInternetReceived(0);
        });

        // Group providers by utility type. Each provider have their own BFS,
        // Each "give" is printed immediately.
        java.util.Map<String, java.util.List<UtilityProvider>> byType = new java.util.LinkedHashMap<>();
        for (UtilityProvider up : utilities) {
            byType.computeIfAbsent(up.getUtilityType(), k -> new ArrayList<>()).add(up);
        }

        for (java.util.Map.Entry<String, java.util.List<UtilityProvider>> entry : byType.entrySet()) {
            String utilityType = entry.getKey();
            java.util.List<UtilityProvider> providers = entry.getValue();

            java.util.Map<Zone, Integer> received = new java.util.HashMap<>();

            for (UtilityProvider utility : providers) {
                Queue<Cell> bfsQueue = new LinkedList<>();
                boolean[][] isVisited = new boolean[city.length][city[0].length];
                int remainingResource = utility.getProducedAmount();

                bfsQueue.add(utility);
                isVisited[utility.getRow()][utility.getColumn()] = true;

                while (remainingResource > 0 && !bfsQueue.isEmpty()) {
                    Cell current = bfsQueue.poll();
                    if (current instanceof Zone) {
                        Zone zone = (Zone) current;

                        int alreadyGot = received.getOrDefault(zone, 0);
                        int demand = zone.getDemand(utilityType);
                        int stillNeeds = Math.max(0, demand - alreadyGot);
                        int give = Math.min(remainingResource, stillNeeds);

                        if (give > 0) {
                            if (utilityType.equals("electricity")) {
                                zone.setElectricityReceived(zone.getElectricityReceived() + give);
                            } else if (utilityType.equals("water")) {
                                zone.setWaterReceived(zone.getWaterReceived() + give);
                            } else if (utilityType.equals("internet")) {
                                zone.setInternetReceived(zone.getInternetReceived() + give);
                            }
                            received.put(zone, alreadyGot + give);
                            remainingResource -= give;

                            System.out.println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                                    + ") received " + give + " " + utilityType);
                        }
                    }
                    Cell[] neighbors = { getLeftNeighbor(current), getRightNeighbor(current), getTopNeighbor(current),
                            getBottomNeighbor(current) };
                    for (Cell neighbor : neighbors) {
                        if (neighbor != null && !isVisited[neighbor.getRow()][neighbor.getColumn()]
                                && !(neighbor instanceof EmptyCell)) {
                            isVisited[neighbor.getRow()][neighbor.getColumn()] = true;
                            bfsQueue.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    public void simulate(int ticks) {
        for (int tick = 1; tick <= ticks; tick++) {
            System.out.println("Tick " + tick);
            distributeServices();
            distributeUtilities();
        }
    }
}

package objectville;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    private void distributeResources() {
        List<Zone.HousingZone> houses = new ArrayList<>();
        List<Zone.IndustrialZone> industries = new ArrayList<>();
        List<Zone.CommercialZone> commercials = new ArrayList<>();

        zones.forEach(z -> {
            if (z instanceof Zone.HousingZone)
                houses.add((Zone.HousingZone) z);
            else if (z instanceof Zone.IndustrialZone)
                industries.add((Zone.IndustrialZone) z);
            else if (z instanceof Zone.CommercialZone)
                commercials.add((Zone.CommercialZone) z);
        });

        int totalPopulation = houses.stream().mapToInt(Zone.HousingZone::getOutput).sum();
        int totalGoods = industries.stream().mapToInt(Zone.IndustrialZone::getOutput).sum();
        int totalLifestyle = commercials.stream().mapToInt(Zone.CommercialZone::getOutput).sum();

        int industrialAndCommercialCount = industries.size() + commercials.size();
        int commercialCount = commercials.size();
        int houseCount = houses.size();

        int distributePopulationForEach = industrialAndCommercialCount > 0
                ? totalPopulation / industrialAndCommercialCount
                : 0;
        int distributeGoodsForEach = commercialCount > 0 ? totalGoods / commercialCount : 0;
        int distributeLifestyleForEach = houseCount > 0 ? totalLifestyle / houseCount : 0;

        houses.forEach(h -> h.setLifestyleReceived(distributeLifestyleForEach));
        industries.forEach(i -> i.setPopulationReceived(distributePopulationForEach));
        commercials.forEach(c -> {
            c.setPopulationReceived(distributePopulationForEach);
            c.setGoodsReceived(distributeGoodsForEach);
        });

        for (Zone z : zones) {
            if (z instanceof Zone.CommercialZone) {
                if (distributePopulationForEach > 0) {
                    System.out.println(z.getShortName() + " at (" + z.getRow() + "," + z.getColumn()
                            + ") received " + distributePopulationForEach + " population");
                }
                if (distributeGoodsForEach > 0) {
                    System.out.println(z.getShortName() + " at (" + z.getRow() + "," + z.getColumn()
                            + ") received " + distributeGoodsForEach + " goods");
                }
            } else if (z instanceof Zone.IndustrialZone) {
                if (distributePopulationForEach > 0) {
                    System.out.println(z.getShortName() + " at (" + z.getRow() + "," + z.getColumn()
                            + ") received " + distributePopulationForEach + " population");
                }
            } else if (z instanceof Zone.HousingZone) {
                if (distributeLifestyleForEach > 0) {
                    System.out.println(z.getShortName() + " at (" + z.getRow() + "," + z.getColumn()
                            + ") received " + distributeLifestyleForEach + " lifestyle");
                }
            }
        }
    }

    private void updateZones() {
        for (Zone zone : zones) {
            int oldLevel = zone.getLevel();
            zone.updateLevel();
            int newLevel = zone.getLevel();

            int newOutput = zone.computeOutput();
            zone.setOutput(newOutput);

            String resource = zone.getProducedResource();
            System.out.println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn() + ") generated "
                    + newOutput + " " + resource);

            if (newLevel > oldLevel) {
                System.out.println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                        + ") levels up from " + oldLevel + " to " + newLevel);
            } else if (newLevel < oldLevel) {
                System.out.println(zone.getShortName() + " at (" + zone.getRow() + "," + zone.getColumn()
                        + ") levels down from " + oldLevel + " to " + newLevel);
            }
        }
    }

    public void simulate(int ticks) {
        for (int tick = 1; tick <= ticks; tick++) {
            System.out.println("Tick " + tick);
            distributeServices();
            distributeUtilities();
            if (tick > 1) {
                distributeResources();
            }
            updateZones();
        }
    }
}

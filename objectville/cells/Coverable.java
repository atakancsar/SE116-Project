package objectville.cells;

public interface Coverable {
    boolean coversZone(Zone zone);

    String getServiceType();

    int getRadius();
}

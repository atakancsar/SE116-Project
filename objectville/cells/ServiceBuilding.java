package objectville.cells;

public abstract class ServiceBuilding extends Cell implements Coverable {
    private int radius;

    public ServiceBuilding(char form, int row, int column, int radius) {
        super(form, row, column);
        this.radius = radius;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean coversZone(Zone zone) {
        int dr = this.getRow() - zone.getRow();
        int dc = this.getColumn() - zone.getColumn();
        return dr * dr + dc * dc <= radius * radius;
    }

    @Override
    public abstract String getServiceType();

    public static class FireStation extends ServiceBuilding {

        public FireStation(char form, int row, int column) {
            super(form, row, column, 5);
        }

        @Override
        public String getServiceType() {
            return "security";
        }
    }

    public static class DoctorClinic extends ServiceBuilding {

        public DoctorClinic(char form, int row, int column) {
            super(form, row, column, 3);
        }

        @Override
        public String getServiceType() {
            return "health";
        }
    }

    public static class School extends ServiceBuilding {

        public School(char form, int row, int column) {
            super(form, row, column, 4);
        }

        @Override
        public String getServiceType() {
            return "education";
        }
    }
}

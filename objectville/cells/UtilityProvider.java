package objectville.cells;

public abstract class UtilityProvider extends Cell implements Producer {
    public static final int output = 100;

    public UtilityProvider(char form, int row, int column) {
        super(form, row, column);
    }

    public abstract String getUtilityType();

    @Override
    public String getProducedResource() {
        return getUtilityType();
    }

    @Override
    public int getProducedAmount() {
        return output;
    }

    public static class PowerPlant extends UtilityProvider {
        public PowerPlant(char form, int row, int column) {
            super(form, row, column);
        }

        @Override
        public String getUtilityType() {
            return "electricity";
        }
    }

    public static class WaterStation extends UtilityProvider {
        public WaterStation(char form, int row, int column) {
            super(form, row, column);
        }

        @Override
        public String getUtilityType() {
            return "water";
        }
    }

    public static class TelecomHub extends UtilityProvider {
        public TelecomHub(char form, int row, int column) {
            super(form, row, column);
        }

        @Override
        public String getUtilityType() {
            return "internet";
        }
    }

}

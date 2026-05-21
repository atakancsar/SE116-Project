package objectville.cells;

public abstract class Zone extends Cell implements Producer, Consumer {
    private int electricityReceived, waterReceived, internetReceived;
    private boolean hasSecurity, hasHealth, hasEducation;
    private int output;

    public Zone(char form, int row, int column) {
        super(form, row, column);
        electricityReceived = 0;
        waterReceived = 0;
        internetReceived = 0;
        hasSecurity = false;
        hasEducation = false;
        hasHealth = false;
        output = 0;
    }

    public int computeM() {
        return Math.min(electricityReceived, Math.min(waterReceived, internetReceived));
    }

    public int getElectricityReceived() {
        return electricityReceived;
    }

    public void setElectricityReceived(int electricityReceived) {
        this.electricityReceived = electricityReceived;
    }

    public int getWaterReceived() {
        return waterReceived;
    }

    public void setWaterReceived(int waterReceived) {
        this.waterReceived = waterReceived;
    }

    public int getInternetReceived() {
        return internetReceived;
    }

    public void setInternetReceived(int internetReceived) {
        this.internetReceived = internetReceived;
    }

    public boolean isHasSecurity() {
        return hasSecurity;
    }

    public void setHasSecurity(boolean hasSecurity) {
        this.hasSecurity = hasSecurity;
    }

    public boolean isHasHealth() {
        return hasHealth;
    }

    public void setHasHealth(boolean hasHealth) {
        this.hasHealth = hasHealth;
    }

    public boolean isHasEducation() {
        return hasEducation;
    }

    public void setHasEducation(boolean hasEducation) {
        this.hasEducation = hasEducation;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public abstract int computeOutput();

    public abstract void updateLevel();

    @Override
    public abstract String getProducedResource();

    @Override
    public int getProducedAmount() {
        return output;
    }

    @Override
    public int getDemand() {
        return Math.max(output, 1);
    }

    public int getDemand(String utilityType) {
        return getDemand();
    }

    public static class HousingZone extends Zone {
        private int lifestyleReceived;

        public HousingZone(char form, int row, int column) {
            super(form, row, column);
        }

        public int getLifestyleReceived() {
            return lifestyleReceived;
        }

        public void setLifestyleReceived(int lifestyleReceived) {
            this.lifestyleReceived = lifestyleReceived;
        }

        @Override
        public String getProducedResource() {
            return "population";
        }

        @Override
        public int computeOutput() {
            int m = computeM();
            if (getLevel() == 0)
                return 0;
            if (getLevel() == 1)
                return m;
            if (getLevel() == 2)
                return 2 * m;
            return 2 * m + lifestyleReceived;
        }

        @Override
        public void updateLevel() {
            int m = computeM();
            if (m == 0) { setLevel(0); return; }

            if (getLevel() == 0) { setLevel(1); return; }

            if (getLevel() == 1) {
                if (isHasEducation() && isHasHealth() && isHasSecurity()) setLevel(2);
                return;
            }

            if (getLevel() == 2) {
                if (!(isHasEducation() && isHasHealth() && isHasSecurity())) setLevel(1);
                else if (lifestyleReceived > 0) setLevel(3);
                return;
            }

            if (!(isHasEducation() && isHasHealth() && isHasSecurity()) || lifestyleReceived <= 0) setLevel(2);
        }
    }

    public static class IndustrialZone extends Zone {
        private int populationReceived;

        public IndustrialZone(char form, int row, int column) {
            super(form, row, column);
        }

        public int getPopulationReceived() {
            return populationReceived;
        }

        public void setPopulationReceived(int populationReceived) {
            this.populationReceived = populationReceived;
        }

        @Override
        public String getProducedResource() {
            return "goods";
        }

        @Override
        public int computeM() {
            return Math.min(getElectricityReceived(), getWaterReceived());
        }

        @Override
        public int getDemand(String utilityType) {
            if ("internet".equals(utilityType)) return 0;
            return getDemand();
        }

        @Override
        public int computeOutput() {
            int m = computeM();
            if (getLevel() == 0)
                return 0;
            if (getLevel() == 1)
                return m;
            if (getLevel() == 2)
                return 2 * m;
            return 2 * m + populationReceived;
        }

        @Override
        public void updateLevel() {
            int m = computeM();
            if (m == 0) { setLevel(0); return; }

            if (getLevel() == 0) { setLevel(1); return; }

            if (getLevel() == 1) {
                if (isHasSecurity()) setLevel(2);
                return;
            }

            if (getLevel() == 2) {
                if (!isHasSecurity()) setLevel(1);
                else if (populationReceived > 0) setLevel(3);
                return;
            }

            if (!isHasSecurity() || populationReceived == 0) setLevel(2);
        }
    }

    public static class CommercialZone extends Zone {
        private int populationReceived, goodsReceived;

        public CommercialZone(char form, int row, int column) {
            super(form, row, column);
        }

        public int getPopulationReceived() {
            return populationReceived;
        }

        public void setPopulationReceived(int populationReceived) {
            this.populationReceived = populationReceived;
        }

        public int getGoodsReceived() {
            return goodsReceived;
        }

        public void setGoodsReceived(int goodsReceived) {
            this.goodsReceived = goodsReceived;
        }

        @Override
        public String getProducedResource() {
            return "lifestyle";
        }

        @Override
        public int computeOutput() {
            int m = computeM();
            if (getLevel() == 0)
                return 0;
            if (getLevel() == 1)
                return m;
            if (getLevel() == 2)
                return 2 * m;
            return 2 * m + Math.min(populationReceived, goodsReceived);
        }

        @Override
        public void updateLevel() {
            int m = computeM();
            if (m == 0) { setLevel(0); return; }

            if (getLevel() == 0) { setLevel(1); return; }

            if (getLevel() == 1) {
                if (isHasSecurity()) setLevel(2);
                return;
            }

            if (getLevel() == 2) {
                if (!isHasSecurity()) setLevel(1);
                else if (populationReceived > 0 && goodsReceived > 0) setLevel(3);
                return;
            }

            if (!isHasSecurity() || populationReceived == 0 || goodsReceived == 0) setLevel(2);
        }
    }
}

public class Gene {
    private double minSpeed;
    private double maxSpeed;
    private int[] startingPosition;
    private double k;
    private double directionVariation;

    double getMinSpeed() {
        return this.minSpeed;
    }
    void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    double getMaxSpeed() {
        return this.maxSpeed;
    }
    void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    int[] getStartingPosition() {
        return this.startingPosition;
    }
    void setStartingPosition(int[] startingPosition) {
        this.startingPosition = startingPosition;
    }

    double getK() {
        return this.k;
    }
    void setK(double k) {
        this.k = k;
    }

    double getDirectionVariation() {
        return this.directionVariation;
    }
    void setDirectionVariation(double directionVariation) {
        this.directionVariation = directionVariation;
    }
}

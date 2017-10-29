public class Gene {
    double minSpeed;
    double maxSpeed;
    int[] startingArea;
    double K; // 0.75 - 1.25 // For sigmoid
    double directionVariation; // For changing how direct the agent moves towards the food // +/-10

    // Constructor(s)
    public Gene(double minSpeed, double maxSpeed, int[] startingArea, double k, double directionVariation) {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.startingArea = startingArea;
        this.K = k;
        this.directionVariation = directionVariation;
    }

    // minSpeed
    double getMinSpeed() {
        return this.minSpeed;
    }
    void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    // maxSpeed
    double getMaxSpeed() {
        return this.maxSpeed;
    }
    void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    // startingArea
    int[] getStartingArea() {
        return this.startingArea;
    }
    void setStartingArea(int[] area) {
        this.startingArea = area;
    }

    // K
    double getK() {
        return this.K;
    }
    void setK(double K) {
        this.K = K;
    }

    // directionVariation
    double getDirectionVariation() {
        return this.directionVariation;
    }
    void setDirectionVariation(double directionVariation) {
        this.directionVariation = directionVariation;
    }
}

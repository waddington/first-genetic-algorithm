public class Gene {
    double minSpeed;
    double maxSpeed;
    int[] startingArea;
    double K; // 0.75 - 1.25 // For sigmoid

    // Constructor(s)
    Gene() {}
    public Gene(double minSpeed, double maxSpeed, int[] startingArea, double k) {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.startingArea = startingArea;
        this.K = k;
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
}

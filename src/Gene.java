public class Gene {
    String[] steps;

    void setSteps(String[] steps) {
        this.steps = steps;
    }
    String getStep(int stepNumber) {
        return this.steps[stepNumber];
    }
    String[] getSteps() {
        return this.steps;
    }
}

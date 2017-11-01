public class Utilities {
    // Map x from range a-b to new range c-d
    static double linearTransform(double a, double b, double c, double d, double x) {
        double mappedNumber = 0;

        // y = (x - a)/(b - a) * (d - c) + c
        mappedNumber = (x - a)/(b - a) * (d - c) + c;

        return mappedNumber;
    }

    // Ensure that the current position is within the bounds of the box
    static int[] fitToBounds(int[] position, int inset) {
        int[] checkedPosition = position;

        if (checkedPosition[0] <= (0 + inset))
            checkedPosition[0] = (0 + inset);
        else if (checkedPosition[0] >= (701 - inset))
            checkedPosition[0] = (701 - inset);

        if (checkedPosition[1] <= (0 +inset))
            checkedPosition[1] = (0 + inset);
        else if (checkedPosition[1] >= (521 - inset))
            checkedPosition[1] = (521 - inset);

        return checkedPosition;
    }

    // Get speed of agent using a sigmoid function
    static double getSpeedUsingSigmoid(double aLow, double aHigh, double bLow, double bHigh, double energyLevel, Gene gene) {
        double rawSpeedOutput;

        // f(x) = 1 / (1 + exp(-K * x))
        double x = linearTransform(aLow,aHigh, bLow,bHigh, energyLevel);

        rawSpeedOutput = 1 / (1 + Math.pow(Math.E, (gene.getK() * x)));

        return linearTransform(0,1, gene.getMinSpeed(),gene.getMaxSpeed(), rawSpeedOutput);
    }
}

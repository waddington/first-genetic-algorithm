import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ScoreTracker {
    static int populationSize;
    static double mutationRate;
    static ArrayList<double[]> scores;
    static String fileName;
    static BufferedWriter writer;

    ScoreTracker(int populationSize, double mutationRate) {
        ScoreTracker.populationSize = populationSize;
        ScoreTracker.mutationRate = mutationRate;
        ScoreTracker.scores = new ArrayList<>();
        ScoreTracker.fileName = "P"+populationSize+"_M"+((""+mutationRate).replace('.', '-'))+".txt";
        try {
            ScoreTracker.writer = new BufferedWriter(new FileWriter(new File(ScoreTracker.fileName)));
        } catch (IOException e) {
            System.out.println("Error creating ouput file.");
        }
    }

    static void addScore(int max, int min, double mean) {
        double[] scores = new double[] {max, min, mean};
        ScoreTracker.scores.add(scores);
    }

    static void writeToFile(String s) {
        try {
            writer.write(s);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error could not write to output file.");
        }
    }

    // This function is called when the program ends
    static void saveToFile() {
        for (int i=0; i<ScoreTracker.scores.size(); i++) {
            double[] score = ScoreTracker.scores.get(i);
            int max = (int) score[0];
            int min = (int) score[1];
            double mean = score[2];

            String out = max+";"+min+";"+mean;
            writeToFile(out);
        }

        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("Error closing writer.");
        }
    }
}

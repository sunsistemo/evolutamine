import java.util.Arrays;
import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class Population
{
    private int size;
    private double[][] population;
    private double[] fitness;    
    private final int N = 10;

    public Population(int size, Random rnd)
    {
        this.size = size;
        population = new double[size][N];
        fitness = new double[size];
        populate(rnd);
    }

    private void populate(Random rnd)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < N; j++) {
                population[i][j] = rnd.nextDouble() * 5.0;
                if (rnd.nextBoolean()) {
                    population[i][j] *= -1;
                }
            }
        }
    }

    public void print()
    {
        for (int i = 0; i < size; i++) {
            System.out.println(Arrays.toString(population[i]));
        }
    }

    public void calculateFitness(ContestEvaluation evaluation)
    {
        for (int i = 0; i < size; i++) {
            fitness[i] = (double) evaluation.evaluate(population[i]);
        }
    }

    public void printFitness()
    {
        System.out.println(Arrays.toString(fitness));
    }

    public void selectParents()
    {
        
    }
}

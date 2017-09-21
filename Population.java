import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class Population
{
    private int size;
    private double[][] population;
    private double[][] offspring;
    private double[] fitness;
    private double[] fitnessOffspring;
    private double[] propFitness;
    private double[] propFitnessOffspring;
    private final int N = 10;
    private List<double[]> matingPool;
    private final int numParents = 2;

    public Population(int size, Random rnd)
    {
        this.size = size;
        population = new double[size][N];
        offspring = null;
        fitness = new double[size];
        fitnessOffspring = null;
        propFitness = new double[size];
        propFitnessOffspring = null;
        matingPool = new ArrayList<double[]>();
        populate(rnd);        
    }
    
    /*
     * Private (auxiliary) functions
     */ 
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

    private int[] getFittest(int n)
    {
        int[] elite = new int[n];
        for (int i = 0; i < n; i++) {
            elite[i] = i;
        }
        for (int i = n; i < size; i++) {
            int index = minFitness(elite);
            if (fitness[i] > fitness[elite[index]]) {
                elite[index] = i;
            }
        }
        return elite;
    }
    
    private int minFitness(int[] indices)
    {
        int minIndex = 0;
        for (int i = 0; i < indices.length; i++) {
            if (fitness[indices[i]] < fitness[indices[minIndex]]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    /*
     * EA Components
     */ 
    public void calculateFitness(ContestEvaluation evaluation)
    {
        double totalFitness = 0.0;
        for (int i = 0; i < size; i++) {
            fitness[i] = (double) evaluation.evaluate(population[i]);
            totalFitness += fitness[i];
        }
        for (int i = 0; i < size; i++) {
            propFitness[i] = fitness[i] / totalFitness;
        }
    }
    
    // VERY ugly to have a seperate function for this. rethink data structures...
    public void calculateFitnessOffspring(ContestEvaluation evaluation)
    {
        fitnessOffspring = new double[size];
        propFitnessOffspring = new double[size];
        double totalFitness = 0.0;
        
        for (int i = 0; i < size; i++) {
            fitnessOffspring[i] = (double) evaluation.evaluate(offspring[i]);
            totalFitness += fitnessOffspring[i];
        }
        for (int i = 0; i < size; i++) {
            propFitnessOffspring[i] = fitnessOffspring[i] / totalFitness;
        }
    }
    
    public void selectParents()
    {
        // First version: Generational model
        matingPool.clear();
        
        // roulette wheel algorithm p.83
        Random rnd = new Random();
        while (matingPool.size() < size) {
            double r = rnd.nextDouble();
            double cumProbability = 0.0;
            int i = 0;
            while ((cumProbability += propFitness[i]) < r) {
                i++;
            }
            matingPool.add(population[i]);
        }
    }
    
    public void crossover()
    {
        offspring = new double[size][N];
        double[][] parents = new double[numParents][N];
        double[][] children = new double[numParents][N];
        Random rnd = new Random();
        
        for (int i = 0; i< size; i += numParents) {
            for (int j = 0; j < numParents; j++) {
                int index = rnd.nextInt(matingPool.size());
                parents[j] = matingPool.get(index);
                matingPool.remove(index);
            }
            
            int parent = 0;
            int split = rnd.nextInt(N-2) + 1; // split should be in interval [1,N-1]
            for (int j = 0; j < N; j++) {
                if (j == split) {
                    parent = 1 - parent;
                }
                children[0][j] = parents[parent][j];
                children[1][j] = parents[1-parent][j];
            }
            
            for (int j = 0; j < numParents; j++) {
                offspring[i] = children[j];
            }
        }
    }
    
    public void selectSurvivors()
    {
        // First version: Generational model. entire generation is replaced by offspring        
        population = offspring;
        fitness = fitnessOffspring;
        propFitness = propFitnessOffspring;
        
        offspring = null;
        fitnessOffspring = null;
        propFitnessOffspring = null;        
    }
    
    /* 
     * Print functions 
     */
    public void print()
    {
        for (int i = 0; i < size; i++) {
            System.out.println(Arrays.toString(population[i]));
        }
    }
    
    public void printFitness()
    {
        System.out.println(Arrays.toString(fitness));
    }
}

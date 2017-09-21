import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class Population
{
    private int size;
    private double[][] population;
    private double[] fitness;
    private double[] propFitness;    
    private final int N = 10;
    private List<double[]> matingPool;
    private List<double[]> offspring;
    private final int numParents = 2;

    public Population(int size, Random rnd)
    {
        this.size = size;
        population = new double[size][N];
        fitness = new double[size];
        propFitness = new double[size];
        matingPool = new ArrayList<double[]>();
        offspring = new ArrayList<double[]>();
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
        double totalFitness = 0.0;
        for (int i = 0; i < size; i++) {
            fitness[i] = (double) evaluation.evaluate(population[i]);
            totalFitness += fitness[i];
        }
        for (int i = 0; i < size; i++) {
            propFitness[i] = fitness[i] / totalFitness;
        }
    }

    public void printFitness()
    {
        System.out.println(Arrays.toString(fitness));
    }
    
    public void printPropFitness()
    {
        Arrays.sort(propFitness);
        System.out.println(Arrays.toString(propFitness));
    }

    public void printFittest(int n)
    {
        System.out.println(Arrays.toString(getFittest(n)));
    }
    
    
    public void selectParents()
    {
        // First version: Generational model
        matingPool.clear();
        //System.out.println("Mating Pool size: " + matingPool.size());
        // roulette wheel algorithm p.83
        Random rnd = new Random();
        while (matingPool.size() < size) {
            double r = rnd.nextDouble();
            double cumProbability = 0.0;
            int i = 0;
            while (cumProbability < r) {
                cumProbability += propFitness[i];
                i++;
            }
            matingPool.add(population[i]);
        }
    }
    
    public void printParents()
    {
        for (double[] cand:matingPool) {
            System.out.println(Arrays.toString(cand));
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
    
    public void crossover()
    {
        double[][] parents = new double[numParents][N];
        double[][] children = new double[numParents][N];
        Random rnd = new Random();
        
        for (int i = 0; i < numParents; i++) {
            int index = rnd.nextInt(matingPool.size());
            parents[i] = matingPool.get(index);
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
        
        for (int i = 0; i < numParents; i++) {
            offspring.add(children[i]);
        }        
    }
}

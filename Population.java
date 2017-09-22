import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class Population
{
    private int size;
    private List<Individual> population;
    private List<Individual> matingPool;
    private List<Individual> offspring;
    private final int N = 10;
    private final int numParents = 2;
    private double sumFitness;
    
    
    public Population(int size, Random rnd)
    {
        this.size = size;
        population = new ArrayList<Individual>();
        matingPool = new ArrayList<Individual>();
        offspring = new ArrayList<Individual>();
        sumFitness = 0.0;
        populate(rnd);
    }
    
    
    /*
     * Private (auxiliary) functions
     */ 
    private void populate(Random rnd)
    {
        for (int i = 0; i < size; i++) {
            double[] candidate = new double[N];
            for (int j = 0; j < N; j++) {
                candidate[j] = rnd.nextDouble() * 5.0;
                if (rnd.nextBoolean()) {
                    candidate[j] *= -1;
                }
            }
            population.add(new Individual(candidate));
        }
    }


    /*
     * EA Components
     */ 
    public int calculateFitness(ContestEvaluation evaluation, String select)
    {
        List<Individual> candidates;
        if (select.equals("POPULATION")) {
            candidates = population;
        } else if (select.equals("OFFSPRING")) {
            candidates = offspring;
        } else {
            candidates = new ArrayList<Individual>();
        }
        
        sumFitness = 0.0;
        for (Individual ind: candidates) {
            ind.fitness = (double) evaluation.evaluate(ind.value);
            sumFitness += ind.fitness;
        }
        for (Individual ind: candidates) {
            ind.probability = ind.fitness / sumFitness;
        }
        
        return candidates.size();
    }
       
    public void selectParents()
    {
        // First version: Generational model
        matingPool.clear();
        
        // Stochastic Universal Sampling (SUS) algorithm p.84
        Random rnd = new Random();
        double r = (rnd.nextDouble() / ((double) size));        
        //System.out.printf("r/size: %.8f\n", r);
        //System.out.println("1/size=" + (1/((double) size)));
        int i = 0;
        double cumProbability = 0.0;
        while (matingPool.size() < size) {
            Individual candidate = population.get(i);
            cumProbability += candidate.probability;
            
            //System.out.println("i: " + i + " mating pool size: " + matingPool.size() + " cumProb: " + cumProbability);
            int cycle = 1;
            while (r <= cumProbability && cycle <= size) {
                //System.out.println("cycle: " + cycle + " r: " + r + " cumProb: " + cumProbability + " Adding candidate: " + i);
                matingPool.add(candidate);
                r += 1 / ((double) size);
                cycle++;
            }
            
            i++;
        }
    }
    
    public void crossover()
    {
        offspring.clear();
        Individual[] parents = new Individual[numParents];
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
                children[0][j] = parents[parent].value[j];
                children[1][j] = parents[1-parent].value[j];
            }
            
            for (int j = 0; j < numParents; j++) {
                offspring.add(new Individual(children[j]));
            }
        }
    }
    
    public void selectSurvivors()
    {
        // First version: Generational model. entire generation is replaced by offspring
        population.clear();
        for (Individual child: offspring) {
            population.add(child);
        }
        offspring.clear();
    }
    
    
    /* 
     * Print functions
     */
    public void print()
    {
        for (int i = 0; i < size; i++) {
            System.out.println(Arrays.toString(population.get(i).value));
        } 
    }
}

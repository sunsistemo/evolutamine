import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

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
     * 
     * EA COMPONENTS 
     * 
     */
    
    /*
     * Initialisation
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
     * Evaluation
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
        
        return candidates.size(); // return number of evaluations performed
    }
    
    /*
     * Parent Selection
     */ 
    public void selectParents()
    {
        matingPool.clear();
        //psFPS();
        psRS();
        
        // Stochastic Universal Sampling (SUS) algorithm p.84
        Random rnd = new Random();
        double r = (rnd.nextDouble() / ((double) size));
        int i = 0;
        double cumProbability = 0.0;
        while (matingPool.size() < size) {
            cumProbability += population.get(i).probability;
                        
            while (r <= cumProbability) {
                matingPool.add(population.get(i));
                r += 1 / ((double) size);
            }
            i++;
        }
    }
    
    // Parent Selection: Fitness Proportional Selection
    private void psFPS() 
    {
        for (Individual ind: population) {
            ind.probability = ind.fitness / sumFitness;
        }
    }
    
    // Parent Selection: Ranking Selection
    private void psRS()
    {
        sortPopulation();
        
        int rank = population.size() - 1;
        for (int i = 0; i < population.size(); i++) {
            population.get(i).rank = rank-i;
        }
        
        for (Individual ind: population) {
            ind.probability = linearRankProbability(ind.rank);
        }
    }
    
    private double linearRankProbability(int rank)
    {
        double s = 2.0;
        return ((2 - s) / size) + ((2*rank*(s-1)) / (size*(size-1)));
    }

    /*
     * Recombination
     */
    public void crossover()
    {
        offspring.clear();
        double[][] parents = new double[numParents][N];
        double[][] children = new double[numParents][N];
        Random rnd = new Random();
        
        for (int i = 0; i< size; i += numParents) {
            for (int j = 0; j < numParents; j++) {
                int index = rnd.nextInt(matingPool.size());
                parents[j] = matingPool.get(index).value;
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
                offspring.add(new Individual(children[j]));
            }
        }
    }
    
    /*
     * Mutation
     */
    public void mutate(double rate)
    {
        for (Individual ind: population) {
            ind.mutate(rate);
        }
        for (Individual ind: offspring) {
            ind.mutate(rate);
        }
    }
    
    /*
     * Survivor Selection
     */
    public void selectSurvivors()
    {
        // (m + l) selection. merge parents and offspring and keep top m
        population.addAll(offspring);
        sortPopulation();
        population.subList(size, 2*size).clear(); //remove the worst half of the population
    }
    
    private void replacePopulationWithOffspring()
    {
        // First version: Generational model. entire generation is replaced by offspring
        population.clear();
        for (Individual child: offspring) {
            population.add(child);
        }
        offspring.clear();
    }
    
    /* 
     *
     * AUXILIARY FUNCTIONS
     *
     */
    
    // Sort the population based on fitness: high to low
    private void sortPopulation()
    {
        population.sort(Comparator.comparing(Individual::fitness, Comparator.reverseOrder()));
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
    
    public void printFitness()
    {
        int i = 0;
        String s = "[";
        for (Individual ind: population) {
            s += (ind.fitness + ", ");
            i++;
            if (i % 8 == 0) {
                s += "\n";
            }
        }
        s += "]\n";
        System.out.print(s);
    }
}

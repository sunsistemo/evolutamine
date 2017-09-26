import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final double MUTATION_RATE = 0.1;
    private final double MUTATION_STEP_SIZE = 0.05;
    private final int numParents;
    private double sumFitness;
    private Random rnd;
    
    
    public Population(int size, Random rnd)
    {
        this.size = size;
        population = new ArrayList<Individual>();
        matingPool = new ArrayList<Individual>();
        offspring = new ArrayList<Individual>();
        numParents = 2;
        sumFitness = 0.0;
        this.rnd = rnd;
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
        psRS("linear");
        //~ psRS("exponential");
        
        // Stochastic Universal Sampling (SUS) algorithm p.84
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
    private void psRS(String ranking)
    {
        sortPopulation();
        
        int rank = population.size() - 1;
        for (int i = 0; i < population.size(); i++) {
            population.get(i).rank = rank-i;
        }
        
        if (ranking.equals("linear")) {
            for (Individual ind: population) {
                ind.probability = linearRankProbability(ind.rank);
            }            
        } else if (ranking.equals("exponential")) {
            double normalisation = 0.0;            
            for (Individual ind: population) {
                double p = exponentialRankProbability(ind.rank);
                ind.probability = p;
                normalisation += p;
            }
            for (Individual ind: population) {
                ind.probability /= normalisation;
            }
        }
    }
    
    // Parent Selection: Ranking Selection Probability (Page 82)
    private double linearRankProbability(int rank)
    {
        double s = 2.0;
        return ((2 - s) / size) + ((2*rank*(s-1)) / (size*(size-1)));
    }
    
    // Parent Selection: Ranking Selection Probability (Page 82)
    private double exponentialRankProbability(int rank)
    {
        return (1 - Math.exp(-1 * rank));
    }
    
    /*
     * Recombination
     */
    public void crossover()
    {
        offspring.clear();
        double[][] parents = new double[numParents][N];
        double[][] children;
        Random rnd = new Random();
        
        for (int i = 0; i < size; i += numParents) {
            for (int j = 0; j < numParents; j++) {
                int index = rnd.nextInt(matingPool.size());
                parents[j] = matingPool.get(index).value;
                matingPool.remove(index);
            }
            
            children = wholeArithmeticRecombination(parents);
            
            for (int j = 0; j < numParents; j++) {
                offspring.add(new Individual(children[j]));
            }
        }
    }
    
    private double[][] discreteRecombination(double[][] parents)
    {
        double[][] children = new double[numParents][N];
        int parent = 0;
        int split = rnd.nextInt(N-2) + 1; // split should be in interval [1,N-1]
        for (int j = 0; j < N; j++) {
            if (j == split) {
                parent = 1 - parent;
            }
            children[0][j] = parents[parent][j];
            children[1][j] = parents[1-parent][j];
        }
        return children;
    }
    
    private double[][] simpleArithmeticRecombination(double[][] parents)
    {
        double[][] children = new double[numParents][N];
        int parent = 0;
        int k = rnd.nextInt(N-2) + 1; // split should be in interval [1,N-1]
        double alpha = rnd.nextDouble();
        
        for (int i = 0; i < numParents; i++) {
            for (int j = 0; j < N; j++) {
                if (j < k) {
                    children[i][j] = parents[parent][j];
                } else {
                    children[i][j] = alpha * parents[1-parent][j] + (1 - alpha) * parents[parent][j];
                }
            }
            parent = 1 - parent;
        }
        
        return children;
    }
    
    private double[][] singleArithmeticRecombination(double[][] parents)
    {
        double[][] children = new double[numParents][N];
        int k = rnd.nextInt(N-2) + 1; // split should be in interval [1,N-1]
        double alpha = rnd.nextDouble();
        
        for (int j = 0; j < N; j++) {
            if (j == (k-1)) {
                children[0][j] = alpha * parents[1][j] + (1 - alpha) * parents[0][j];
                children[1][j] = alpha * parents[0][j] + (1 - alpha) * parents[1][j];
            } else {
                children[0][j] = parents[0][j];
                children[1][j] = parents[1][j];
            }
        }
        
        return children;
    }
    
    private double[][] wholeArithmeticRecombination(double[][] parents)
    {
        double[][] children = new double[numParents][N];
        int k = rnd.nextInt(N-2) + 1; // split should be in interval [1,N-1]
        double alpha = rnd.nextDouble();
        
        for (int j = 0; j < N; j++) {
            children[0][j] = alpha * parents[0][j] + (1 - alpha) * parents[1][j];
            children[1][j] = alpha * parents[1][j] + (1 - alpha) * parents[0][j];            
        }                
            
        return children;
    }
    
    /*
     * Mutation
     */
    public void mutate()
    {
        for (Individual ind: population) {
            ind.mutate(MUTATION_RATE, MUTATION_STEP_SIZE);
        }
        for (Individual ind: offspring) {
            ind.mutate(MUTATION_RATE, MUTATION_STEP_SIZE);
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
        /* 
         * commented code gives java.lang.BootstrapMethodError: call site initialization exception
         * population.sort(Comparator.comparing(Individual::fitness, Comparator.reverseOrder()));
         * for now, don't use lambda expressions
         */ 
        
        population.sort(Comparator.comparingDouble(Individual::fitness).reversed());
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

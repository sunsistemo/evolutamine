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
    private double offspringRatio;
    private int offspringSize;
    private List<Individual> population;
    private List<Individual> matingPool;
    private List<Individual> offspring;
    private final int N = 10;
    private final int numParents;
    private double sumFitness;
    private Random rnd;
    private Options options;


    public Population(int size, Options options, Random rnd)
    {
        this.size = size;
        this.options = options;
        this.rnd = rnd;

        population = new ArrayList<Individual>();
        matingPool = new ArrayList<Individual>();
        offspring = new ArrayList<Individual>();

        numParents = 2;
        sumFitness = 0.0;
        offspringRatio = 1.0;
        offspringSize = (int) (size * offspringRatio);

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
            population.add(new Individual(candidate, rnd));
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
        int evals = 0;
        for (Individual ind: candidates) {
            if (!ind.evaluated()) {
                ind.setFitness((double) evaluation.evaluate(ind.value));
                evals++;
            }
            sumFitness += ind.fitness;
        }

        return evals; // return number of evaluations performed
    }

    /*
     * Parent Selection
     */
    public void selectParents()
    {
        matingPool.clear();

        if (options.multimodal) {
            for (Individual parent: population) {
                matingPool.add(parent);
            }
        } else {
            switch(options.parentSelection) {
                case LINEAR_RANKING:
                case EXPONENTIAL_RANKING:
                    rankingSelection();
                    break;
                case FPS:
                    fitnessProportionalSelection();
                    break;
            }
        }
    }

    // Parent Selection: Fitness Proportional Selection
    private void fitnessProportionalSelection()
    {
        for (Individual ind: population) {
            ind.probability = ind.fitness / sumFitness;
        }
        sampleParents();
    }

    // Parent Selection: Ranking Selection
    private void rankingSelection()
    {
        sortPopulation();
        int rank = population.size() - 1;
        for (int i = 0; i < population.size(); i++) {
            population.get(i).rank = rank-i;
        }

        switch (options.parentSelection) {
            case LINEAR_RANKING:
                for (Individual ind: population) {
                    ind.probability = linearRankProbability(ind.rank);
                }
                break;
            case EXPONENTIAL_RANKING:
                double normalisation = 0.0;
                for (Individual ind: population) {
                    double p = exponentialRankProbability(ind.rank);
                    ind.probability = p;
                    normalisation += p;
                }
                for (Individual ind: population) {
                    ind.probability /= normalisation;
                }
                break;
        }
        sampleParents();
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

    private void sampleParents()
    {
        // Stochastic Universal Sampling (SUS) algorithm p.84
        double r = (rnd.nextDouble() / ((double) offspringSize));
        int i = 0;
        double cumProbability = 0.0;
        while (matingPool.size() < offspringSize) {
            cumProbability += population.get(i).probability;

            while (r <= cumProbability) {
                matingPool.add(population.get(i));
                r += 1 / ((double) offspringSize);
            }
            i++;
        }
    }

    /*
     * Recombination
     */
    public void crossover()
    {
        offspring.clear();
        double[][] parents = new double[numParents][N];
        double[][] children;

        for (int i = 0; i < offspringSize; i += numParents) {
            for (int j = 0; j < numParents; j++) {
                int index = rnd.nextInt(matingPool.size());
                parents[j] = matingPool.get(index).value;
                matingPool.remove(index);
            }

            children = wholeArithmeticRecombination(parents);

            for (int j = 0; j < numParents; j++) {
                offspring.add(new Individual(children[j], rnd));
            }
        }
    }

    // Deterministic Crowding p94
    public void deterministicCrowding()
    {
        offspring.clear();

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
        double alpha = rnd.nextDouble();

        for (int j = 0; j < N; j++) {
            children[0][j] = alpha * parents[0][j] + (1 - alpha) * parents[1][j];
            children[1][j] = alpha * parents[1][j] + (1 - alpha) * parents[0][j];
        }

        return children;
    }

    private double[][] blendRecombination(double[][] parents)
    {
        // Blend Crossover p. 67
        double[][] children = new double[numParents][N];
        double alpha = 0.5;

        for (int p = 0; p < numParents; p++) {
            double u = rnd.nextDouble();
            double gamma = (1 - 2 * alpha) * u - alpha;

            for (int j = 0; j < N; j++) {
                children[p][j] = (1 - gamma) * parents[0][j] + gamma * parents[1][j];

                if (children[p][j] > 5) {
                    children[p][j] = 5;
                } else if (children[p][j] < -5) {
                    children[p][j] = -5;
                }
            }
        }
        return children;
    }

    /*
     * Mutation
     */
    public void mutate()
    {
        //This is conceptually wrong: if you mutate the parents, the already evaluated fitness does not reflect actual fitness of mutated parent
        //for (Individual ind: population) {
            //ind.mutate(Options.Mutation.UNCORRELATED_N);
        //}
        for (Individual ind: offspring) {
            ind.mutate(options.mutation);
        }
    }

    /*
     * Survivor Selection
     */
    public void selectSurvivors()
    {
        // (μ + λ) selection. merge parents and offspring and keep top μ
        population.addAll(offspring);
        sortPopulation();
        population.subList(size, size + offspringSize).clear(); //remove the worst half of the population
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

    // Calculates the distance between two individuals
    private double distance(Individual a, Individual b)
    {
        double d = 0.0;
        for (int i = 0; i < a.value.length; i++)
        {
            d += Math.abs(a.value[i] - b.value[i]);
        }
        return d;
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

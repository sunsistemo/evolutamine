import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class Population implements EAPopulation
{
    private int size;
    private double offspringRatio;
    private int offspringSize;
    private List<Individual> population;
    private List<Individual> matingPool;
    private List<Individual> offspring;
    private final int N = 10;
    private final int numParents = 2;
    private Random rnd;
    private Options options;
    private Individual[] exchange;

    public Population(int size, Options options, Random rnd)
    {
        this.size = size;
        this.options = options;
        this.rnd = rnd;

        population = new ArrayList<Individual>();
        matingPool = new ArrayList<Individual>();
        offspring = new ArrayList<Individual>();

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
            population.add(new Individual(candidate));
        }
    }

    /*
     * Evaluation
     */
    public int evaluateInitialPopulation(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (Individual parent: population) {
            if (!parent.evaluated()) {
                parent.setFitness((double) evaluation.evaluate(parent.value));
                evals++;
            }
        }
        return evals; // return number of evaluations performed
    }

    public int evaluateOffspring(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (Individual child: offspring) {
            if (!child.evaluated()) {
                child.setFitness((double) evaluation.evaluate(child.value));
                evals++;
            }
        }
        return evals; // return number of evaluations performed
    }

    /*
     * Parent Selection
     */
    public void selectParents()
    {
        switch(options.parentSelection) {
            case RANDOM_PAIRING:
                for (Individual parent: population) {
                    matingPool.add(parent);
                }
                break;
            case LINEAR_RANKING:
            case EXPONENTIAL_RANKING:
                rankingSelection();
                break;
            case FPS:
                if (options.fitnessSharing) {
                    applyFitnessSharing();
                }
                fitnessProportionalSelection();
                break;
        }
    }

    // Parent Selection: Fitness Proportional Selection
    private void fitnessProportionalSelection()
    {
        double sumFitness = 0.0;
        for (Individual ind: population) {
            sumFitness += ind.fitness;
        }
        for (Individual ind: population) {
            ind.probability = ind.fitness / sumFitness;
        }
        sampleParents();
    }

    // Multimodality: Fitness Sharing
    private void applyFitnessSharing()
    {
        double fitness;
        double sumSharing;

        for (int i = 0; i < size; i++) {
            sumSharing = 0.0;
            for (int j = 0; j < size; j++)
            {
                sumSharing += distance(population.get(i), population.get(j));
            }
            population.get(i).setFitness(population.get(i).fitness / sumSharing);
        }
    }

    private double sh(double distance)
    {
        int alpha = 1;
        double share = 5.0;
        if (distance <= share) {
            return 1 - Math.pow((distance/share), (double) alpha);
        } else {
            return 0.0;
        }
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
        if(options.crowding) {
            deterministicCrowding();
            return;
        }

        offspring.clear();
        double[][] parents = new double[numParents][N];
        double[][] children;

        for (int i = 0; i < offspringSize; i += numParents) {
            for (int j = 0; j < numParents; j++) {
                int index = rnd.nextInt(matingPool.size());
                parents[j] = matingPool.get(index).value;
                matingPool.remove(index);
            }

            children = recombination(parents);

            for (int j = 0; j < numParents; j++) {
                offspring.add(new Individual(children[j]));
            }
        }
    }

    private double[][] recombination(double[][] parents)
    {
        switch (options.recombination) {
            case DISCRETE:
                return discreteRecombination(parents);
            case SIMPLE_ARITHMETIC:
                return simpleArithmeticRecombination(parents);
            case SINGLE_ARITHMETIC:
                return singleArithmeticRecombination(parents);
            case WHOLE_ARITHMETIC:
                return wholeArithmeticRecombination(parents);
            case BLEND_RECOMBINATION:
                return blendRecombination(parents);
        }
        return parents;
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
        for (Individual child: offspring) {
            child.mutate(options.mutation, rnd);
        }
    }

    /*
     * Survivor Selection
     */
    public void selectSurvivors()
    {
        switch(options.survivorSelection) {
            case GENERATIONAL:
                replacePopulationWithOffspring();
                break;
            case MU_PLUS_LAMBDA:
                muPlusLambdaSelection();
                break;
            case DISTANCE_TOURNAMENT:
                distanceTournamentSelection();
                break;
        }
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

    private void muPlusLambdaSelection()
    {
        // (μ + λ) selection. merge parents and offspring and keep top μ
        int mu = size;
        int lambda = offspringSize;
        population.addAll(offspring);
        sortPopulation();
        population.subList(mu, mu+lambda).clear(); // Keep the best μ
    }

    private void distanceTournamentSelection()
    {
        List<Individual> newGeneration = new ArrayList<Individual>();

        for (int i = 0; i < size; i += numParents) {
            Individual p1 = matingPool.get(i);
            Individual p2 = matingPool.get(i+1);

            Individual o1 = offspring.get(i);
            Individual o2 = offspring.get(i+1);

            double d11 = distance(p1, o1);
            double d12 = distance(p1, o2);
            double d21 = distance(p2, o1);
            double d22 = distance(p2, o2);

            if ((d11 + d22) < (d12 + d21)) {
                if(o1.fitness > p1.fitness) {
                    newGeneration.add(o1);
                } else {
                    newGeneration.add(p1);
                }
                if(o2.fitness > p2.fitness) {
                    newGeneration.add(o2);
                } else {
                    newGeneration.add(p2);
                }
            } else {
                if(o2.fitness > p1.fitness) {
                    newGeneration.add(o2);
                } else {
                    newGeneration.add(p1);
                }
                if(o1.fitness > p2.fitness) {
                    newGeneration.add(o1);
                } else {
                    newGeneration.add(p2);
                }
            }
        }
        population.clear();
        matingPool.clear();
        offspring.clear();

        for (Individual candidate: newGeneration) {
            population.add(candidate);
        }
        newGeneration.clear();
    }

    /*
     *
     * MultiModal functions
     *
     */

    // Deterministic Crowding p94
    public void deterministicCrowding()
    {
        double[][] parents = new double[numParents][N];
        double[][] children;

        Collections.shuffle(matingPool);

        for (int i = 0; i < size; i += numParents) {
            parents[0] = matingPool.get(i).value;
            parents[1] = matingPool.get(i+1).value;

            children = recombination(parents);

            for (int j = 0; j < numParents; j++) {
                offspring.add(new Individual(children[j]));
            }
        }
    }

    /*
     *
     * AUXILIARY FUNCTIONS
     *
     */

    /*
     * Functions to select individuals for exchange in the Island Model (p.94-96)
     */
    public void selectBest(int n)
    {
        exchange = new Individual[n];
        sortPopulation();
        for (int i = 0; i < n; i++) {
            exchange[i] = population.get(i);
        }
    }

    public void selectFromFittestHalf(int n)
    {
        exchange = new Individual[n];
        sortPopulation();
        for (int i = 0; i < n; i++) {
            int r = rnd.nextInt(size/2);
            exchange[i] = population.get(r);
        }
    }

    public void selectRandom(int n)
    {
        exchange = new Individual[n];
        for (int i = 0; i < n; i++) {
            int r = rnd.nextInt(size);
            exchange[i] = population.get(r);
        }
    }

    public Individual[] getSelectedForExchange()
    {
        Individual[] tmp = new Individual[exchange.length];
        for (int i = 0; i < exchange.length; i++) {
            tmp[i] = new Individual(exchange[i].value);
        }
        return tmp;
    }

    public void addExchange(Individual[] individuals)
    {
        for (Individual ind: individuals) {
            population.add(ind);
        }
    }

    /*
     * Sort the population based on fitness: high to low
     */
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
     * Remove the n worst individuals from the population
     */
    public void removeWorst(int n)
    {
        sortPopulation();
        population.subList(size-n, size).clear();
    }

    /*
     * Calculates the distance between two individuals
     */
    private double distance(Individual a, Individual b)
    {
        double d = 0.0;
        for (int i = 0; i < a.value.length; i++) {
            d += Math.abs(a.value[i] - b.value[i]);
        }
        return d;
    }

    public int size()
    {
        return size;
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

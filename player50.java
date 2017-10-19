import java.lang.Exception;
import java.lang.Math;
import java.util.Properties;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;


public class player50 implements ContestSubmission
{
    Random rnd;
    ContestEvaluation evaluation;
    private int evaluation_limit;
    private int populationSize;
    private int cycle;
    private String name;
    private EAPopulation population;
    private Options options;


    public player50()
    {
        name = "evolutamine";
        rnd = new Random();
        populationSize = 50;
    }

    public void setSeed(long seed)
    {
        // Set seed of algorithm's random process
        rnd.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation)
    {
        // Set evaluation problem used in the run
        this.evaluation = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluation_limit = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        if (isMultimodal) { System.out.println("Function is Multimodal."); }
        if (hasStructure) { System.out.println("Function has structure."); }
        if (isSeparable) { System.out.println("Function is separable."); }

        boolean bentCigar = !(isMultimodal || hasStructure || isSeparable);
        boolean katsuura  = isMultimodal && !(hasStructure || isSeparable);
        boolean schaffers = isMultimodal && hasStructure && !isSeparable;

        options = new Options();
        if (bentCigar) {
            populationSize = 50;

            double tau = 0.025;
            double tau2 = 5;
            double epsilon = 0.1;
            options.mutationParameters(tau, tau2, epsilon);
        }
        if (katsuura) {
            populationSize = 250;
            int subPopulations = 5;
            int exchangeRound = 50;
            if (subPopulations > 1) {
                options.islandModel(subPopulations, exchangeRound);
            }

            double tau = 0.02;
            double tau2 = 2;
            double epsilon = 0.2;
            options.mutationParameters(tau, tau2, epsilon);
        }
        if (schaffers) {
            populationSize = 50;
            int subPopulations = 1;
            int exchangeRound = 50;
            if (subPopulations > 1) {
                options.islandModel(subPopulations, exchangeRound);
            }

            double tau = 0.02;
            double tau2 = 3;
            double epsilon = 0.25;
            options.mutationParameters(tau, tau2, epsilon);
        }

        // print population size settings
        System.out.print("Population size: " + populationSize);
        if (options.islandModel) {
            System.out.print(" (" + options.subPopulations + " subpopulations of size ");
            System.out.print((populationSize / options.subPopulations) + ")");
        }
        System.out.println();
    }

    public void run()
    {
        int evals = evaluation_limit;
        //int evals = 2*populationSize;
        double eval_frac = ((double) evals) / evaluation_limit;
        double mutation_epsilon = options.epsilon;

        // Create initial population and evaluate the fitness
        if (options.islandModel) {
            population = new IslandModel(populationSize, new Options(options), rnd);
        } else {
            population = new Population(populationSize, new Options(options), rnd);
        }
        evals -= population.evaluateInitialPopulation(evaluation);

        cycle = 0;
        while (evals > 0) {
            if (options.islandModel) {
                if (cycle % options.exchangeRound == 0) {
                    ((IslandModel) population).exchangeIndividuals();
                }
            }

            // Time dependent variables
            eval_frac = ((double) evals) / evaluation_limit;
            mutation_epsilon = options.epsilon * Math.pow(eval_frac, 4);

            // Select Parents
            population.selectParents();

            // Apply crossover / mutation operators
            population.crossover();
            population.mutate(mutation_epsilon);

            // Check fitness of unknown function
            try {
                evals -= population.evaluateOffspring(evaluation);
            } catch (NullPointerException e) {
                System.out.println("\033[1mEvaluation limit reached!\033[0m");
                break;
            }

            // Select survivors
            population.selectSurvivors();

            cycle++;
        }
        System.out.println("Evolutionary Cycles: " + cycle);
    }
}

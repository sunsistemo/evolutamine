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
        // Do sth with property values, e.g. specify relevant settings of your algorithm

        options = new Options();
        if (bentCigar) {
            populationSize = 50;
        }
        if (katsuura) {
            populationSize = 500;
            int subPopulations = 10;
            int exchangeRound = 50;
            options.islandModel(subPopulations, exchangeRound);
        }
        if (schaffers) {
            populationSize = 1000;
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

            population.selectParents();

            // Apply crossover / mutation operators
            population.crossover();
            population.mutate();

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

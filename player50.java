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
    private Population population;
    private IslandModel subPopulations;
    private boolean islandModel;
    private Options options;


    public player50()
    {
        name = "evolutamine";
        rnd = new Random();
        populationSize = 100;
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

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        options = new Options();
        islandModel = isMultimodal && !hasStructure;

        if (isMultimodal) {
            System.out.println("Function is Multimodal.");
            //populationSize = 1000;
            if (!hasStructure) {
                populationSize *= 10;
            }
        }
        if (hasStructure) {
            System.out.println("Function has structure.");
        }
        if (isSeparable) {
            System.out.println("Function is separable.");
        }

        System.out.println("Population size: " + populationSize);
    }

    public void run()
    {
        int evals = evaluation_limit;
        //int evals = 2*populationSize;

        // Create initial population and evaluate the fitness
        if (islandModel) {
            subPopulations = new IslandModel(populationSize, options, rnd);
            evals -= subPopulations.evaluateInitialPopulation(evaluation);
        } else {
            population = new Population(populationSize, options, rnd);
            evals -= population.evaluateInitialPopulation(evaluation);
        }

        cycle = 0;
        while (evals > 0) {
            System.out.println("Evolutionary Cycle: " + cycle);
            if (islandModel) {
                if (cycle % 20 == 0)
                {
                    //System.out.println("IslandModel: Exchange Individuals.");
                    subPopulations.exchangeIndividuals();
                }

                // Select parents
                subPopulations.selectParents();

                // Apply crossover / mutation operators
                subPopulations.crossover();
                subPopulations.mutate();

                // Check fitness of unknown function
                try {
                    evals -= subPopulations.evaluateOffspring(evaluation);
                } catch (NullPointerException e) {
                    System.out.println("\033[1mEvaluation limit reached!\033[0m");
                    break;
                }

                // Select survivors
                subPopulations.selectSurvivors();
            } else {
                // Select parents
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
            }
            cycle++;
        }
        System.out.println("Evolutionary Cycles: " + cycle);
    }
}

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
    private final double MUTATION_RATE = 0.1;
    private int evaluation_limit;
    private int populationSize;
    private Population population;
    String name;

    public player50()
    {
        name = "evolutamine";
        rnd = new Random();
        populationSize = 100;
    }

    public void setSeed(long seed)
    {
        // Set seed of algortihms random process
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
        populationSize = Math.max(populationSize, (evaluation_limit/1000));
        System.out.println("Population size: " + populationSize);
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            System.out.println("Function is multimodal.");
        } else {
            // Do sth else
        }
        
        if (hasStructure) {
            System.out.println("Function has structure.");
        }
        
        if (isSeparable) {
            System.out.println("Function is separable.");
        }
    }

    public void run()
    {
        int evals = evaluation_limit;
        //int evals = 30;
        
        
        // init population
        population = new Population(populationSize, rnd);
        // calculate fitness
        evals -= population.calculateFitness(evaluation, "POPULATION");
        
        while (evals > 0) {
            // Select parents
            population.selectParents();
            
            // Apply crossover / mutation operators
            population.crossover();
            population.mutate(MUTATION_RATE);

            // Check fitness of unknown function
            try {
                evals -= population.calculateFitness(evaluation, "OFFSPRING");                
            } catch (NullPointerException e) {
                System.out.println("\033[1mEvaluation limit reached!\033[0m");
                break;
            }

            // Select survivors
            population.selectSurvivors();
        }
    }
}

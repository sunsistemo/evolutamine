import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

import java.util.Properties;
import java.util.Random;


public class player50 implements ContestSubmission
{
    Random rnd;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private Population population;
    private final int populationSize = 100;
    String name;

    public player50()
    {
        name = "evolutamine";
        rnd = new Random();
    }

    public void setSeed(long seed)
    {
        // Set seed of algortihms random process
        rnd.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation)
    {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
    }

    public void run()
    {
        // Run your algorithm here
        int evals = 0;

        // init population
        population = initPopulation();
        population.populate(rnd);

        // calculate fitness
        while (evals < evaluations_limit_) {
            // Select parents
            // Apply crossover / mutation operators
            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown function
            Double fitness = (double) evaluation_.evaluate(child);
            evals++;
            // Select survivors
        }

    }

    private Population initPopulation()
    {
        return new Population(populationSize);
    }
}

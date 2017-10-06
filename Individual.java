import java.lang.Math;
import java.util.Random;


public class Individual
{
    public double[] value;
    public double fitness;
    public double probability;
    public int rank;
    private double[] sigma;
    private final double UB = 5.0;
    private final double LB = -UB;
    private boolean evaluated;


    public Individual(double[] value)
    {
        this.value = value;
        fitness = 0.0;
        probability = 0.0;
        rank = 0;

        sigma = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            sigma[i] = Options.MUTATION_STEP_SIZE;
        }

        evaluated = false;
    }

    public double fitness()
    {
        return this.fitness;
    }

    public void setFitness(double fitness)
    {
        this.fitness = fitness;
        this.evaluated = true;
    }

    public boolean evaluated()
    {
        return this.evaluated;
    }

    public void mutate(Options.Mutation method, Random rnd)
    {
        switch(method) {
            case UNIFORM:
                uniformMutation(rnd);
                break;
            case NON_UNIFORM:
                nonUniformMutation(rnd);
                break;
            case UNCORRELATED:
                uncorrelatedMutationWithOneStepSize(rnd);
                break;
            case UNCORRELATED_N:
                uncorrelatedMutationWithNStepSizes(rnd);
                break;
            case CORRELATED:
                correlatedMutation(rnd);
                break;
        }
    }

    private void uniformMutation(Random rnd)
    {
        for (int i = 0; i < value.length; i++) {
            double r = rnd.nextDouble();
            if (r < Options.MUTATION_RATE) {
                value[i] = rnd.nextDouble() * UB;
                if (rnd.nextBoolean()) {
                    value[i] *= -1;
                }
            }
        }
    }

    // Page 57 of the book. Mutation probability per gene is 1, but sd controls to which extent
    private void nonUniformMutation(Random rnd)
    {
        for (int i = 0; i < value.length; i++) {
            double h = rnd.nextGaussian() * sigma[0];
            value[i] = boundedAdd(value[i], h);
        }
    }

    private void uncorrelatedMutationWithOneStepSize(Random rnd)
    {
        double tau = 0.9;
        double epsilon = 0.025;
        double gamma = tau * rnd.nextGaussian();
        sigma[0] *= Math.exp(gamma);
        sigma[0] = Math.max(sigma[0], epsilon);

        for (int i = 0; i < value.length; i++) {
            value[i] = boundedAdd(value[i], sigma[0] * rnd.nextGaussian());
        }
    }

    private void uncorrelatedMutationWithNStepSizes(Random rnd)
    {
        double tau = 0.05;    // local learning rate
        double tau2 = 0.9;   // global learning rate
        double epsilon = 0.001;
        double gamma = tau2 * rnd.nextGaussian();

        for (int i = 0; i < value.length; i++) {
            double g = rnd.nextGaussian();
            sigma[i] *= Math.exp(gamma + tau * g);
            sigma[i] = Math.max(sigma[i], epsilon);
            value[i] = boundedAdd(value[i], sigma[i] * g);
        }
    }

    private void correlatedMutation(Random rnd)
    {

    }

    // check so v stays in domain of function
    private double boundedAdd(double v, double dv)
    {
        if (dv < 0) {
            v = Math.max(v + dv, LB) ;
        } else if (dv > 0) {
            v = Math.min(v + dv, UB);
        }
        return v;
    }
}

import java.lang.Math;
import java.util.Random;


public class Individual
{
    public double[] value;
    public double fitness;
    public double sigma;
    public double probability;
    public int rank;
    Random rnd;
    private final double UB = 5.0;
    private final double LB = UB * -1;

    public Individual(double[] value)
    {
        this.value = value;
        fitness = 0.0;
        probability = 0.0;
        rank = 0;
        sigma = 0.05;
        rnd = new Random();
    }

    public double fitness()
    {
        return this.fitness;
    }

    public void mutate(double rate, double stepSize)
    {
        nonUniformMutation(stepSize);
    }

    private void uniformMutation(double rate)
    {
        for (int i = 0; i < value.length; i++) {
            double r = rnd.nextDouble();
            if (r < rate) {
                value[i] = rnd.nextDouble() * 5.0;
                if (rnd.nextBoolean()) {
                    value[i] *= -1;
                }
            }
        }
    }
    
    // Page 57 of the book. Mutation probability per gene is 1, but sd controls to which extent
    private void nonUniformMutation(double sd)
    {
        for (int i = 0; i < value.length; i++) {
            double h = rnd.nextGaussian() * sd;
            value[i] = boundedAdd(value[i], h);
        }
    }

    private void uncorrelatedMutationWithOneStepSize()
    {
        double tau = 0.9;
        double epsilon = 0.025;
        double gamma = tau * rnd.nextGaussian();
        sigma *= Math.exp(gamma);
        sigma = Math.max(sigma, epsilon);

        for (int i = 0; i < value.length; i++) {
            value[i] = boundedAdd(value[i], sigma * rnd.nextGaussian());
        }
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

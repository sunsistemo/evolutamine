import java.lang.Math;
import java.util.Random;


public class Individual
{
    public double[] value;
    public double fitness;
    public double[] sigma;
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
        sigma = new double[value.length];
        rnd = new Random();

        for (int i = 0; i < value.length; i++) {
            sigma[i] = 0.05;
        }
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
        sigma[0] *= Math.exp(gamma);
        sigma[0] = Math.max(sigma[0], epsilon);

        for (int i = 0; i < value.length; i++) {
            value[i] = boundedAdd(value[i], sigma[0] * rnd.nextGaussian());
        }
    }

    private void uncorrelatedMutationWithNStepSizes()
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

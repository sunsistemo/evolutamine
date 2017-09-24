import java.lang.Math;
import java.util.Random;


public class Individual
{
    public double[] value;
    public double fitness;
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
        rnd = new Random();
    }

    public double fitness()
    {
        return this.fitness;
    }

    public void mutate(double rate, double stepSize)
    {
        //uniformMutation(rate);
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
    
    // Page 57 of the book. Mutation probability per gene is 1, but sigma controls to which extent
    private void nonUniformMutation(double sd)
    {
        for (int i = 0; i < value.length; i++) {
            double h = rnd.nextGaussian() * sd;
            value[i] += h;
            if (h < 0) {
                value[i] = Math.max(value[i], LB);
            } else if (h > 0) {
                value[i] = Math.min(value[i], UB);
            }
        }    
    }
}

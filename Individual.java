import java.util.Random;


public class Individual
{
    public double[] value;
    public double fitness;
    public double probability;
    public int rank;
    
    
    public Individual(double[] value)
    {
        this.value = value;
        fitness = 0.0;
        probability = 0.0;
        rank = 0;
    }
       
    public double fitness()
    {
        return this.fitness;
    }
    
    public void mutate(double rate)
    {
        Random rnd = new Random();
        double r = rnd.nextDouble();
        if (r < rate) {
            // mutation 
        }
    }
}

public class Individual
{
    public double[] value;
    public double fitness;
    public double probability;
    
    
    public Individual(double[] value)
    {
        this.value = value;
        fitness = 0.0;
        probability = 0.0;
    }
       
    public double fitness()
    {
        return this.fitness;
    }
    
    public void mutate()
    {
        
    }
}

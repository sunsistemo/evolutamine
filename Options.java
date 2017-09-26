public class Options
{
    static final double MUTATION_RATE = 0.1;
    static final double MUTATION_STEP_SIZE = 0.05;
        
    public enum Crossover
    {
        DISCRETE, SIMPLE_ARITHMETIC, SINGLE_ARITHMETIC, WHOLE_ARITHMETIC, BLEND_ARITHMETIC;
    }
    
    public enum Mutation
    {
        UNIFORM, NON_UNIFORM, UNCORRELATED, UNCORRELATED_N, CORRELATED;
    }
    
    public enum Population
    {
        PARENTS, OFFSPRING;
    }
    
    public Options()
    {
        
    }
}



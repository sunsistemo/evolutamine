public class Options
{
    public enum Crossover
    {
        DISCRETE, SIMPLE_ARITHMETIC, SINGLE_ARITHMETIC, WHOLE_ARITHMETIC, BLEND_ARITHMETIC
    }
    
    public enum Mutation
    {
        UNIFORM, NON_UNIFORM, UNCORRELATED, UNCORRELATED_N, CORRELATED
    }
    
    public enum Population
    {
        PARENTS, OFFSPRING
    }
    
    public Options()
    {
        
    }
}



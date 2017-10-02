public class Options
{
    static final double MUTATION_RATE = 0.1;
    static final double MUTATION_STEP_SIZE = 0.05;

    public ParentSelection parentSelection;
    public Crossover crossover;
    public Mutation mutation;
    public SurvivorSelection survivorSelection;

    boolean multimodal;

    public enum ParentSelection
    {
        FPS, LINEAR_RANKING, EXPONENTIAL_RANKING, RANDOM_PAIRING;
    }

    public enum Crossover
    {
        DISCRETE, SIMPLE_ARITHMETIC, SINGLE_ARITHMETIC, WHOLE_ARITHMETIC, BLEND_ARITHMETIC;
    }

    public enum Mutation
    {
        UNIFORM, NON_UNIFORM, UNCORRELATED, UNCORRELATED_N, CORRELATED;
    }

    public enum SurvivorSelection
    {
        GENERATIONAL, MU_PLUS_LAMBDA, DISTANCE_TOURNAMENT;
    }

    public Options()
    {
        this(false);
    }

    public Options(boolean multimodal)
    {
        this.multimodal = multimodal;
        parentSelection = ParentSelection.LINEAR_RANKING;
        crossover = Crossover.WHOLE_ARITHMETIC;
        mutation = Mutation.UNCORRELATED_N;
        survivorSelection = SurvivorSelection.MU_PLUS_LAMBDA;
    }

    public void setDeterministicCrowding()
    {
        parentSelection = ParentSelection.RANDOM_PAIRING;
        crossover = Crossover.WHOLE_ARITHMETIC;
        mutation = Mutation.UNCORRELATED_N;
        survivorSelection = SurvivorSelection.DISTANCE_TOURNAMENT;
    }
}

public class Options
{
    static final double MUTATION_RATE = 0.1;
    static final double MUTATION_STEP_SIZE = 0.05;

    static int subPopulations;
    static int exchangeRound;
    static final int NUM_EXCHANGES = 5;

    public ParentSelection parentSelection;
    public Recombination recombination;
    public Mutation mutation;
    public SurvivorSelection survivorSelection;

    boolean crowding;
    boolean islandModel;

    public enum ParentSelection
    {
        FPS, LINEAR_RANKING, EXPONENTIAL_RANKING, RANDOM_PAIRING;
    }

    public enum Recombination
    {
        DISCRETE, SIMPLE_ARITHMETIC, SINGLE_ARITHMETIC, WHOLE_ARITHMETIC, BLEND_RECOMBINATION;
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
        crowding = false;
        islandModel = false;
        subPopulations = 1;
        exchangeRound = 0;
        parentSelection = ParentSelection.LINEAR_RANKING;
        recombination = Recombination.WHOLE_ARITHMETIC;
        mutation = Mutation.UNCORRELATED_N;
        survivorSelection = SurvivorSelection.MU_PLUS_LAMBDA;
    }

    public Options(Options opt)
    {
        this.crowding = opt.crowding;
        this.islandModel = opt.islandModel;
        this.subPopulations = opt.subPopulations;
        this.exchangeRound = opt.exchangeRound;
        this.parentSelection = opt.parentSelection;
        this.recombination = opt.recombination;
        this.mutation = opt.mutation;
        this.survivorSelection = opt.survivorSelection;
    }

    public void setDeterministicCrowding()
    {
        crowding = true;
        parentSelection = ParentSelection.RANDOM_PAIRING;
        survivorSelection = SurvivorSelection.DISTANCE_TOURNAMENT;
    }

    public void setIslandModel(int subPopulations, int exchangeRound)
    {
        islandModel = true;
        this.subPopulations = subPopulations;
        this.exchangeRound = exchangeRound;
    }
}

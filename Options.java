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
    boolean fitnessSharing;
    boolean islandModel;

    static double tau;
    static double tau2;
    static double epsilon;

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
        fitnessSharing = false;
        islandModel = false;
        subPopulations = 1;
        exchangeRound = 0;
        parentSelection = ParentSelection.LINEAR_RANKING;
        recombination = Recombination.WHOLE_ARITHMETIC;
        mutation = Mutation.UNCORRELATED_N;
        survivorSelection = SurvivorSelection.MU_PLUS_LAMBDA;
        tau = 0.025;
        tau2 = 5;
        epsilon = 0.001;
    }

    public Options(Options opt)
    {
        this.crowding = opt.crowding;
        this.fitnessSharing = opt.fitnessSharing;
        this.islandModel = opt.islandModel;

        this.subPopulations = opt.subPopulations;
        this.exchangeRound = opt.exchangeRound;

        this.parentSelection = opt.parentSelection;
        this.recombination = opt.recombination;
        this.mutation = opt.mutation;
        this.survivorSelection = opt.survivorSelection;

        this.tau = opt.tau;
        this.tau2 = opt.tau2;
        this.epsilon = opt.epsilon;
    }

    public void islandModel(int subPopulations, int exchangeRound)
    {
        islandModel = true;
        crowding = false;
        fitnessSharing = false;
        this.subPopulations = subPopulations;
        this.exchangeRound = exchangeRound;
    }

    public void fitnessSharing()
    {
        fitnessSharing = true;
        crowding = false;
        islandModel = false;
        parentSelection = ParentSelection.FPS;
    }

    public void deterministicCrowding()
    {
        crowding = true;
        fitnessSharing = false;
        islandModel = false;
        parentSelection = ParentSelection.RANDOM_PAIRING;
        survivorSelection = SurvivorSelection.DISTANCE_TOURNAMENT;
    }

    public void mutationParameters(double localTau, double globalTau, double epsilon)
    {
        this.tau = localTau;
        this.tau2 = globalTau;
        this.epsilon = epsilon;
    }
}

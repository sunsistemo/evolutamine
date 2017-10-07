import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class IslandModel
{
    private Population[] populations;
    private final int NUM_POPULATIONS;

    public IslandModel(int populationSize, Options opt, Random rnd)
    {
        NUM_POPULATIONS = opt.NUM_POPULATIONS;
        populations = new Population[NUM_POPULATIONS];
        int subPopulationSize = populationSize / NUM_POPULATIONS;
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i] = new Population(subPopulationSize, opt, rnd);
        }
    }

    public int evaluateInitialPopulation(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            evals += populations[i].evaluateInitialPopulation(evaluation);
        }
        return evals;
    }

    public void selectParents()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i].selectParents();
        }
    }

    public void crossover()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i].crossover();
        }
    }

    public void mutate()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i].mutate();
        }
    }

    public int evaluateOffspring(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            evals += populations[i].evaluateOffspring(evaluation);
        }
        return evals;
    }

    public void selectSurvivors()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i].selectSurvivors();
        }
    }

    public void exchangeIndividuals()
    {
        for (int i = 0; i <= NUM_POPULATIONS; i++)
        {

        }
    }
}

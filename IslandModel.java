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

        System.out.println();
        System.out.println("Population size: " + populationSize);
        System.out.println("Subpopulations: " + NUM_POPULATIONS);
        System.out.println("Subpopulation size: " + subPopulationSize);
        System.out.println();

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
        //for (int i = 0; i < NUM_POPULATIONS; i++)
        //{
            //System.out.println("Population" + i + ".size()=" + populations[i].size());
        //}

        int n = Options.NUM_EXCHANGES;
        for (int i = 0; i < NUM_POPULATIONS; i++)
        {
            populations[i].selectRandom(n);
            populations[i].removeWorst(n);
        }

        for (int i = 1; i <= NUM_POPULATIONS; i++)
        {
            int neighbour = i;
            if (i == NUM_POPULATIONS) {
                neighbour = 0;
            }
            //System.out.println("Population " + (i-1) + " adds selected from population " + neighbour);
            populations[i-1].addExchange(populations[neighbour].getSelectedForExchange());
        }

        //for (int i = 0; i < NUM_POPULATIONS; i++)
        //{
            //System.out.println("Population" + i + ".size()=" + populations[i].size());
        //}
    }
}

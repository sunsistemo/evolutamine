import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class IslandModel implements EAPopulation
{
    private Population[] populations;
    private final int NUM_POPULATIONS;

    public IslandModel(int populationSize, Options opt, Random rnd)
    {
        NUM_POPULATIONS = opt.subPopulations;
        populations = new Population[NUM_POPULATIONS];
        int subPopulationSize = populationSize / NUM_POPULATIONS;

        Options islandOptions = new Options(opt);
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            if (i % 2 == 0) {
                islandOptions.parentSelection = Options.ParentSelection.EXPONENTIAL_RANKING;
            } else {
                islandOptions.parentSelection = Options.ParentSelection.LINEAR_RANKING;
            }
            populations[i] = new Population(subPopulationSize, new Options(islandOptions), rnd);
        }
    }

    public int evaluateInitialPopulation(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            evals += populations[i].evaluateInitialPopulation(evaluation);
        }
        return evals;
    }

    public void selectParents()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations[i].selectParents();
        }
    }

    public void crossover()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations[i].crossover();
        }
    }

    public void mutate()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations[i].mutate();
        }
    }

    public int evaluateOffspring(ContestEvaluation evaluation)
    {
        int evals = 0;
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            evals += populations[i].evaluateOffspring(evaluation);
        }
        return evals;
    }

    public void selectSurvivors()
    {
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations[i].selectSurvivors();
        }
    }

    public void exchangeIndividuals()
    {
        int n = Options.NUM_EXCHANGES;
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations[i].selectBest(n);
            populations[i].removeWorst(n);
        }

        for (int i = 1; i <= NUM_POPULATIONS; i++) {
            int neighbour = i;
            if (i == NUM_POPULATIONS) {
                neighbour = 0;
            }
            populations[i-1].addExchange(populations[neighbour].getSelectedForExchange());
        }
    }
}

import java.util.Random;

import org.vu.contest.ContestEvaluation;


public class IslandModel
{
   private Population[] populations;
   private final int NUM_POPULATIONS = 5;

   public IslandModel(int populationSize, Options opt, Random rnd)
   {
      populations = new Population[NUM_POPULATIONS];
      int subPopulationSize = populationSize / NUM_POPULATIONS;
      for (int i = 0; i < NUM_POPULATIONS; i++)
      {
         populations[i] = new Population(subPopulationSize, opt, rnd);
      }
   }

   public void evaluateInitialPopulation(ContestEvaluation evaluation)
   {

   }

   public void selectParents()
   {

   }

   public void crossover()
   {

   }

   public void mutate()
   {

   }

   public void selectSurvivors()
   {

   }
}

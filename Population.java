import java.util.Arrays;
import java.util.Random;


public class Population
{
    int size;
    private double[][] population;
    private double[] fitness;
    final int N = 10;

    public Population(int size, Random rnd) 
    {
        this.size = size;
        population = new double[size][N];
        fitness = new double[size];
        populate(rnd);
    }

    public void populate(Random rnd)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < N; j++) {
                population[i][j] = rnd.nextDouble() * 5.0;
                if (rnd.nextBoolean()) {
                    population[i][j] *= -1;
                }
            }
        }
    }

    public void print() 
    {
        for (int i = 0; i < size; i++) {
            System.out.println(Arrays.toString(population[i]));
        }
    }
}

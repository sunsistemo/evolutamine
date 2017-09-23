# evolutamine
Evolutionary Computing 2017, Vrije Universiteit Amsterdam

An evolutionary algorithm (EA) to maximise three continuous optimisation problems in 10 dimensions within a restricted 
evaluation budget: Bent Cigar, Katsuura and Schaffers F7. 

A fitness function is provided that scales the performance of the algorithm between 0 and 10. A score of 0 means that 
the algorithm scores as good as a random search. A value of 10 means that the algorithm found the global optimum.

The search space is [-5,5]^10 (meaning that every variable must have a value between [-5,5]). One can check whether 
the functions have the following properties to adapt the EA in the right way: whether it is multimodal, whether it 
is separable, whether it has a strong structure and the number of available evaluations.

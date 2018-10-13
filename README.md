# 2SATSolver
2SAT Solver for 50.004 2D Project

# Instructions
To compile Java files, run this in your command line from the src directory
```
javac CNFReader.class TwoSATSolver.class 
```

To execute program, run this in your command line from the bin directory
```
java TwoSATSolver
```
The program will prompt for the cnf file. Type the cnf file name.
```
Input File : [CNF file name].cnf
```

# Results
The program will print **FORMULA SATISFIABLE** if the formula has a solution. A solution to the formula will be printed in the next line. If there were no clauses in the CNF file, the formula is trivially solvable. If no solution exists, the program will print **FORMULA UNSATISFIABLE**.

# Method
The solver implements Kosaraju's Algorithm to solve the problem.
We note that if we let (A OR B) = TRUE,
we observe that (A OR B) == (-A -> B) AND (-B -> A)
Therefore, we can express (A OR B) as a graph with edges (-A,B) and (-B,A)
 
For every clause, the graph has 2 edges, 2m edges.
For every boolean variable, there is 1 node.
 
If edge(X,-X) exists in the graph,there is a contradiction.
If edge(-X,X) exists in the graph, there is a contradiction.
If edge(X,-X) and edge(-X,X) both exist, it is an unfeasible assignment.

Therefore, the formula is unsatisfiable if X and -X exists in the
same Strongly Connected Component (SCC)

# Resources
https://www.geeksforgeeks.org/strongly-connected-components/
https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
http://www.geeksforgeeks.org/2-satisfiability-2-sat-problem/

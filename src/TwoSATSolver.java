/**
 * The TwoSATSolver program accepts a cnf file, parses it, and identifies if
 * the formula is satisfiable. If it is satisfiable, the program also 
 * identifies a solution. 
 *  
 * The solver implements Kosaraju's Algorithm to solve the problem.
 * We note that if we let (A OR B) = TRUE,
 * we observe that (A OR B) == (-A -> B) AND (-B -> A)
 * Therefore, we can express (A OR B) as a graph with edges (-A,B) and (-B,A)
 * 
 * For every clause, the graph has 2 edges, 2m edges.
 * For every boolean variable, there is 1 node.
 * 
 * If edge(X,-X) exists in the graph,there is a contradiction.
 * If edge(-X,X) exists in the graph, there is a contradiction.
 * If edge(X,-X) and edge(-X,X) both exist, it is an unfeasible assignment.
 *
 * Therefore, the formula is unsatisfiable if X and -X exists in the
 * same Strongly Connected Component (SCC)
 *
 * SCC: A directed graph G = (V,E) is strongly connected if, for all pairs of
 * vertices u and v, there's a path from u to v and v to u.
 *
 * Resources: http://www.geeksforgeeks.org/2-satisfiability-2-sat-problem/
 *
 * @author Ivan Chuang 
 * @version 1.0
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Scanner;

public class TwoSATSolver {

	private static int m;
	private static int n;
	private static ArrayList<Integer> a;
	private static ArrayList<Integer> b;

	// maps to store graph and transpose
	public static final Map<Integer, ArrayList<Integer>> graph = new HashMap<>();
	public static final Map<Integer, ArrayList<Integer>> graphT = new HashMap<>(); 

	// maps to store visited nodes
	public static final Map<Integer, Boolean> visited = new HashMap<>();
	public static final Map<Integer, Boolean> visitedRev = new HashMap<>();

	// stack stores visited vertices in topological order
	static Stack<Integer> stack = new Stack<Integer>();
	// hash map is a graph of sccs
	static Map<Integer, ArrayList<Integer>> sccGraph = new HashMap<>();
	// counter keeps count of sccs
	static int sccCounter = 1;
	// hash map to store solutions
	static Map<Integer, Boolean> solutionMap = new HashMap<>();

	public static void main(String[] args){
		final String filetype = "cnf";
		String input;
		System.out.print("Input File: ");
		try{
			Scanner scan = new Scanner(System.in);
			input = scan.next();
		} catch (Exception e){
			System.err.println(e);
			return;
		}
		if (!filetype.equals(input.substring(input.lastIndexOf(".") + 1, input.length()))){
			System.err.println("[-] INVALID INPUT");
			return;
		}
		try{
			CNFReader reader = new CNFReader(input);
			System.out.println("[+] cnf file loaded. . .");
			reader.generate();
			System.out.println("[+] cnf file parsed. . .");
			m = reader.getM();
			if (m==0){
				System.out.println("FORMULA SATISFIABLE");
				System.out.println("Solution: Trivial (0 clauses)");
				return;
			}
			n = reader.getN();
			a = reader.getA();
			b = reader.getB();
			System.out.println("[+] SAT Solver initiated. . .");
			long started = System.nanoTime();
			solve(m,n,a,b);
			long time = System.nanoTime();
			long timeTaken = time - started;
			System.out.println(" ");
			System.out.println("[+] Time: " + timeTaken/1000000.0 + "ms");
		} catch (Exception e){
			System.err.println("[-] FILE NOT FOUND");
			return;
		}
	};

	public static void solve(int m,int n,ArrayList<Integer> a,ArrayList<Integer> b){
		generateGraphs(m,n,a,b);

		// first dfs traverse graph to create stack
		for (Integer i : graph.keySet()){
			if(!visited.get(i)){
				dfs(i);
			}
		}

		// second dfs traversal
		while(!stack.isEmpty()){
			int v = stack.peek();
			stack.pop();
			if (!visitedRev.get(v)){
				revDfs(v);
				sccCounter++;
			}
		}

		sccCounter--;

		//check 2sat validity
		if(checkSCC()){
			System.out.println("FORMULA SATISFIABLE");
			System.out.print("Solution: ");
			generateSolutions();
		} else {
			System.out.println("FORMULA UNSATISFIABLE");
		}
	}

	// method passes data parsed from cnf file to addEdge method
	public static void generateGraphs(int m,int n,ArrayList<Integer> a,ArrayList<Integer> b){
		for (int i = 0; i < m; i++){
			addEdge(a.get(i),b.get(i),graph);
			addEdge(-a.get(i),-b.get(i),graphT);
		}
	}

	// method inserts vertices and constructs graph and transpose
	// also marks nodes as unvisited for visited and reverse
	public static void addEdge(int u, int v, Map<Integer,ArrayList<Integer>> graph){
		// vertices marked unvisited for first dfs
		visited.put(-u, false);
		visitedRev.put(-u, false);
		visited.put(v, false);
		visitedRev.put(v, false);

		// reverse vertices marked unvisited for second dfs
		visited.put(-v, false);
		visitedRev.put(-v, false);
		visited.put(u, false);
		visitedRev.put(u, false);

		// if graph contains -u, check if has edge to v, else insert
		if (graph.containsKey(-u)){
			if (!graph.get(-u).contains(v)){
				graph.get(-u).add(v);
			}
		}

		// if graph not contains -u, create -u, add edge to v
		if (!graph.containsKey(-u)){
			ArrayList<Integer> x = new ArrayList<>();
			x.add(v);
			graph.put(-u,x);
			x = null;
		}

		// if graph contains -v, check if has edge to u, else insert
		if (graph.containsKey(-v)){
			if (!graph.get(-v).contains(u)){
				graph.get(-v).add(u);
			}
		}

		// if graph not contains -v, create -v, add edge to u
		if (!graph.containsKey(-v)){
			ArrayList<Integer> x = new ArrayList<>();
			x.add(u);
			graph.put(-v,x);
			visited.put(-v,false);
			visitedRev.put(-v,false);
			x = null;
		}
	}

	// first dfs traversal visits all adjacent nodes of any given vertex and adds them to the stack
	public static void dfs(int u){
		if (visited.get(u)){
			return;
		}
		
		// mark as visited
		visited.put(u,true);

		if(graph.get(u)==null){
			stack.push(u);
			return;
		}

		for (int i = 0; i < graph.get(u).size(); i++){
			dfs(graph.get(u).get(i));
		}

		stack.push(u);
	}

	// second dfs traversal on transpose to identify SCCs
	public static void revDfs(int u){
		if (visitedRev.get(u)){
			return;
		}

		visitedRev.put(u,true);

		if (graphT.get(u) == null){
			createSccGraph(u, sccCounter, sccGraph);
			return;
		} 

		for (int i = 0; i < graphT.get(u).size(); i++){
			revDfs(graphT.get(u).get(i));
		}

		createSccGraph(u, sccCounter, sccGraph);
	}

	// create graph of SCCs
	public static void createSccGraph(int u, int scc, Map<Integer, ArrayList<Integer>> m){
		if (m.containsKey(scc)){
			if(m.get(scc)==null){
				ArrayList<Integer> x = new ArrayList<>();
				x.add(u);
				m.put(scc,x);
			} else {
				m.get(scc).add(u);
			}
		} else {
			ArrayList<Integer> x = new ArrayList<>();
			x.add(u);
			m.put(scc,x);
		}
	}

	// detects cycles by checking if v and -v are in the same scc
	public static boolean checkSCC(){
		for (Integer i : sccGraph.keySet()){
			for (Integer j: sccGraph.get(i)){
				if (sccGraph.get(i).contains(-j)){
					return false;
				}
			}
		}
		return true;
	}

	// method assigns boolean variables to find a feasible solution
	public static void generateSolutions(){
		for (int i = sccCounter; i >= 1; i--){
			for (Integer j : sccGraph.get(i)){
				if (!solutionMap.containsKey(j)){
					solutionMap.put(j, true);
					solutionMap.put(-j, false);
				}
			}
		}

		for (int i = 1; i <= solutionMap.size()/2;i++){
			boolean v = solutionMap.get(i);
			if(v) {
				System.out.print("1 ");
			} else {
				System.out.print("0 ");
			}
		}
	}

}

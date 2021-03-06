/**
 * The CNFReader program accepts a cnf file, parses it, and returns:
 *   - m, the number of clauses 
 *   - n, the number of variables
 *   - sccfile, an array list of array lists of integers, to be passed to a graph algorithm.
 *   	+ example: [[1, 3, -4], [4, 2], [-3]]
 * 
 * In the sccfile elements in the inner list are connected by ORs
 * while the inner lists are connected by ANDs.
 * Hence, example: [[1, 3, -4], [4, 2], [-3]] in conjunctive normal form
 * would be: (1+3+-4)*(4+2)*(-3), where + is AND, * is OR, and - is NOT.
 * 
 * @author Ivan Chuang 
 * @version 1.0
 */

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.lang.Exception;

public class CNFReader{
	String filename;
	ArrayList<Integer> a = new ArrayList<Integer>();
	ArrayList<Integer> b = new ArrayList<Integer>();
	BufferedReader in;
	String s;
	int m, n;

	//Constructor for a new CNFReader object.
	CNFReader(String filename) throws Exception{
		in = new BufferedReader(new FileReader(filename));
	}

	//Method to parse the cnf file.
	//Parses for m, the number of clauses, and n, the number of variables.
	//Produces an list of nodes, a, that have edges to the respective nodes in list b. 
	public void generate() throws Exception{
		while ((s=in.readLine())!=null){
			if (s.charAt(0) == 'c') {
			} else if (s.charAt(0) == 'p') {
				String[] items = s.split(" ");
				m = Integer.parseInt(items[items.length-1]);
				n = Integer.parseInt(items[items.length-2]);
			} else {
				s = s.replace(" 0","");
				String[] items = s.split(" ");
				ArrayList<Integer> intList = new ArrayList<Integer>();
				if (items.length > 1){
					a.add(Integer.parseInt(items[0]));
					b.add(Integer.parseInt(items[1]));
				} else if (items.length == 1){
					a.add(Integer.parseInt(items[0]));
					b.add(Integer.parseInt(items[0]));
				}
			}
		}
	}

	//Getter Method for m value.
	public int getM(){
		return m;
	}

	//Getter Method for n value.
	public int getN(){
		return n;
	}

	//Getter Method for list a.
	public ArrayList<Integer> getA(){
		return a;
	}

	//Getter Method for list a.
	public ArrayList<Integer> getB(){
		return b;
	}

}

package Graph_searching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Graph_searching {

	private static String[][] actions = new String[100][6];
	private static int actionsCount;
	private static int[] finalStates;
	private static int[] initialStates;
	private static HashMap<String, Integer> stateIndex = new HashMap<String, Integer>();
	
	
	private static int fileReader(String fileName) throws FileNotFoundException{
		Scanner file = new Scanner(new File(fileName));
		int lineCounter = 0;
		while(file.hasNextLine()){
			String line = file.nextLine();
			actions[lineCounter] = line.split(" ");
			lineCounter++;
		}
		file.close();
		return lineCounter;
	}
	
	//this function is going through the array of action and when it adds all existing there states
	private static void getAllStates(){
		for (int i = 0; i < actionsCount; i++) {
			if (stateIndex.get(actions[i][0]) == null)
				stateIndex.put(actions[i][0], stateIndex.size());
			
			if (stateIndex.get(actions[i][3]) == null)
				stateIndex.put(actions[i][3], stateIndex.size());	
		}
	}
	
	//sets initial states given as argument
	private static void getInitialStates(String argLine){
		initialStates = new int[stateIndex.size()];
		String[] ini = argLine.split("\\s*,\\s*");
		for (int i = 0; i < ini.length; i++){
			String[] temp = ini[i].split(" ");
			if (temp.length>1)
			initialStates[stateIndex.get(temp[0])] = Integer.parseInt(temp[1]);
		}
	}
	
	//sets final states given as argument, those of existing states which were not given, they are set as -1
	private static void getFinalStates(String argLine){
		finalStates = new int[stateIndex.size()];
		for (int i = 0; i < stateIndex.size(); i++)
			finalStates[i] = -1;
		String[] ini = argLine.split(", ");
		for (int i = 0; i < ini.length; i++){
			String[] temp = ini[i].split(" ");
			finalStates[stateIndex.get(temp[0])] = Integer.parseInt(temp[1]);
		}
	}
	
	private static int getDifference(int[] stateA, int[] stateB){ // for the sequence of states it specifies the "steps"
		for (int i = 0; i < stateIndex.size(); i++)
			if (stateA[i] != stateB[i])
				return i;
		return -1;
	}
	
	private static void getSequenceOfActions(int[][] statesSequence){ //from the sequence of states it gives sequence of actions
		int[] actionSequence = new int[statesSequence.length-1];//-1 because we have first states, so we reach n-1 states
		
		for (int i = 1; i < statesSequence.length; i++){//from one, because we compare second states with first, third with second etc
			int diff = getDifference(statesSequence[i], statesSequence[i-1]); //diff is part of state that changed
			
			for (int j = 0; j < actionsCount; j++){ //through all of actions we search for suitable one
				if (stateIndex.get(actions[j][3]) == diff 
					&& Integer.parseInt(actions[j][4]) == statesSequence[i-1][diff]
						&& Integer.parseInt(actions[j][5]) == statesSequence[i][diff]){
					
					if (Integer.parseInt(actions[j][1]) == statesSequence[i-1][stateIndex.get(actions[j][0])]) {
						actionSequence[i-1] = j;
						break;
					}
				}
			}
		}
		
		System.out.println("Positions of automatas in array: " + stateIndex.entrySet().toString()); //just to clarify
		System.out.println("\nAction sequence: ");
		for (int i = 0; i < actionSequence.length; i++)
			System.out.println(Arrays.toString(actions[actionSequence[i]]).replace(","," "));
	}
	
	
	//writes the graph down in .dot file
	private static void getGraph(HashMap<Integer, List<Integer>> edges, List<int[]> states, String file) throws IOException{
		File graphFile = new File(file);
		FileWriter fw = new FileWriter(graphFile);
		fw.write("digraph {\n\n");
		fw.flush();
		for (int i = 0; i < states.size(); i++){
			if (edges.get(i)!= null)
				for (int j = 0; j < edges.get(i).size(); j++) {
						String str = Arrays.toString(states.get(i)) + " -> " + Arrays.toString(states.get((edges.get(i).get(j))));
						fw.write(str.replaceAll("[\\[\\]]", "\"") + "\n");
						fw.flush();
				}
			}
		fw.write("\n}");
		fw.flush();
		fw.close();
	}
	List<int[]> states = new ArrayList<int[]>();
	HashMap<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>(); //just the number of >>states<<
	
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		Scanner scan = new Scanner(new File("graph.dot"));
		
		actionsCount = fileReader(args[0]);
		getAllStates();
		getInitialStates(args[1]);
		getFinalStates(args[2]);
		Graph graph = new Graph(actions, actionsCount, stateIndex, initialStates);
		getGraph(graph.edges, graph.states, "graph.dot");
		long endTime1 = System.currentTimeMillis();
		DFS dfs = new DFS(graph.states, graph.edges, graph.getIndexOf(initialStates), finalStates, stateIndex.size());
		if (dfs.isFound) {
			System.out.println("It's possible to reach final state " + Arrays.toString(graph.states.get(dfs.finalStatesIndex)) 
					+ " with "+ args[2].toString() +" from state: " + Arrays.toString(initialStates));
			getSequenceOfActions(dfs.getArrayOfStates());
		}
		else
			System.out.println("It's not possible to reach final state " + Arrays.toString(graph.states.get(dfs.finalStatesIndex))
					+ " with "+ args[2].toString() +" from state: " + Arrays.toString(initialStates));
		long endTime = System.currentTimeMillis();
		float time = (float)(endTime - startTime)/1000;
		float time1 = (float)(endTime1 - startTime)/1000;
		System.out.println();
		System.out.println("Executed in " + time + " s");
		System.out.println("Graph built in " + time1 + " s");
	
	}
}
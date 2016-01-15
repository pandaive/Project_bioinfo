
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Graph {
	
	public List<int[]> states = new ArrayList<int[]>();
	public HashMap<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>(); //just the number of >>states<<
	private static HashMap<String, Integer> stateIndex = new HashMap<String, Integer>();
	private int[] newStates = new int[3];
	public int numberOfStates;
	private static HashMap<String, List<String[]>> processes = new HashMap<String, List<String[]>>();
	private static HashMap<String, List<Integer>> objectives = new HashMap<String, List<Integer>>();
	public static HashMap<Integer, List<String[]>> solutions = new HashMap<Integer, List<String[]>>();
	
	public Graph(String[][] actions, int numberOfActions, HashMap<String, Integer> stateIndex, int[] initialStates,
			HashMap<String, List<String[]>> processes, HashMap<String, List<Integer>> objectives, HashMap<Integer, List<String[]>> solutions){
		this.stateIndex = stateIndex;
		numberOfStates = stateIndex.size();
		states.add(initialStates);
		this.processes.putAll(processes);
		this.objectives.putAll(objectives);
		this.solutions.putAll(solutions);
		construct_graph(actions, numberOfActions);
	}
	
	private int[] getValueOf(int stateNumber){
		int[] newVal = new int[numberOfStates];
		for (int i = 0; i < numberOfStates; i++)
			newVal[i] = states.get(stateNumber)[i];
		return newVal;
	}

	private boolean searchStatesFor(int[] state){
		for (int i = states.size()-1; i >= 0; i--) {
			int countMatches = 0;
			for (int j = 0; j < numberOfStates; j++){
				if (states.get(i)[j] != state[j])
				break;
			else
				countMatches++;
			}
			if (countMatches == numberOfStates)
				return true;
		}
		return false;
	}
	
	public int getIndexOf(int[] state){	
		for (int i = states.size()-1; i >= 0; i--) {
			int countMatches=0;
			for (int j = 0; j < numberOfStates; j++)
				if (states.get(i)[j] != state[j])
					break;
				else
					countMatches++;
			if (countMatches == numberOfStates)
				return i;
			}
		return -1;
	}
	
	private void construct_graph(String[][] actions, int numberOfActions){
		int current; //number (position) of states in states list
		for(current = 0; current < states.size(); current++){ //while there are states to analyze
			for (int i = 0; i < numberOfActions; i++){ //for all possible actions check, if they match current states
				
				if (states.get(current)[stateIndex.get(actions[i][0])] == Integer.parseInt(actions[i][1])
						&& states.get(current)[stateIndex.get(actions[i][3])] == Integer.parseInt(actions[i][4])
						&& matchProcess(actions[i])) {
					newStates = getValueOf(current); //getting actual state, that is simple (silly?) way to get new one
					newStates[stateIndex.get(actions[i][3])] = Integer.parseInt(actions[i][5]); //by updating with changed value
					
					if (searchStatesFor(newStates)) { //if new state already exist in the list
						if (edges.containsKey(current)){ //and if current state is already in edges map
							if (!edges.get(current).contains(getIndexOf(newStates))) //and if there is not that edge already there
							edges.get(current).add(getIndexOf(newStates)); //add the edge between this one and current one
						}
						else {
							List<Integer> temp = new ArrayList<Integer>(1); // else add current state to map with edge to new state
							temp.add(getIndexOf(newStates));
							edges.put(current, temp);
							//System.out.println(current + ": " + Arrays.toString(states.get(current)) + " -> " + Arrays.toString(newStates));
						}
					}
					else {
						states.add(newStates); //if new state doesn't exists in the list, add it

						if (!edges.containsKey(current)) { //same as above, if current state is not in map yet
							List<Integer> temp = new ArrayList<Integer>(1);
							temp.add(getIndexOf(newStates));
							edges.put(current, temp);
							//System.out.println(current + ": " + Arrays.toString(states.get(current)) + " -> " + Arrays.toString(newStates));
							
						}
						else {
							edges.get(current).add(getIndexOf(newStates)); //if it is, add the edge between this one and current one
						}
					}
				}
			}
		}
	}

	private boolean matchProcess(String[] action) {
		String[] objArr;
		for (String obj: objectives.keySet()) {
			objArr = obj.split(" ");
			for (int sol: objectives.get(obj)) {
				if (solutions.containsKey(sol))
					for (String[] proc: solutions.get(sol)) {
						if (action[0].equals(proc[0]) && action[1].equals(proc[1]) && action[3].equals(objArr[0]))
							return true;
					}
				}
		}
		return false;
	}
}
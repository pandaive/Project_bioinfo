

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class DFS {

	private List<int[]> states = new ArrayList<int[]>();
	private int numberOfStates;
	private HashMap<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>(); //just the number of >>states<<

	private int[] finalStates;
	public int finalStatesIndex;

	private List<Integer> visited = new ArrayList<Integer>();
	public boolean isFound = false;

	private Stack stack = new Stack();

	public DFS(List<int[]> states, HashMap<Integer, List<Integer>> edges, int initialStates, int[] finalStates, int numberOfStates){
		this.states = states;
		this.edges = edges;
		this.finalStates = finalStates;
		this.numberOfStates = numberOfStates;
		this.finalStatesIndex = getIndexOfFinalStates();

		stack.push(initialStates);
		visited.add(initialStates);	
		Searching(initialStates);
		//System.out.println(Arrays.toString(stack.toArray()));
	}

	private int getIndexOfFinalStates(){
		for (int i = states.size()-1; i >= 0; i--) {
			int countMatches=0;
			for (int j = 0; j < numberOfStates; j++)
				if (states.get(i)[j] != finalStates[j])
					break;
				else
					countMatches++;
			if (countMatches == numberOfStates)
				return i;
			}
		states.add(finalStates);
		return states.size()-1;
	}

	public int[][] getArrayOfStates(){ //gets array of states in sequence to reach the final state
		int temp;
		int[][] statesSequence = new int[stack.size()][numberOfStates];
		for (int i = stack.size() - 1; i >= 0; i-- ){
			temp = (int) stack.pop();
			for (int j = 0; j < numberOfStates; j ++){
				statesSequence[i][j] = states.get(temp)[j];
			}
		}
		return statesSequence;
	}

	private boolean isVisited(int state){ //checks if the state has been already visited
		if (visited.contains(state))
			return true;
		return false;
	}

	private boolean ifContainsUnvisitedChildren(int state){
		if (edges.get(state) == null)
			return false;
		for (int i = 0; i < edges.get(state).size(); i++ )
			if (!isVisited(edges.get(state).get(i)))
				return true;
		return false;
	}

	private int getFirstUnvisitedChild(int state) {
		for (int i = 0; i < edges.get(state).size(); i++ )
			if (!isVisited(edges.get(state).get(i)))
				return edges.get(state).get(i);
		return -1;
	}

	private boolean isFinal(int state){
		int i = 0;
		for (i = 0; i < numberOfStates; i++){
			if (states.get(state)[i] != states.get(finalStatesIndex)[i] && states.get(finalStatesIndex)[i] != -1 )
				return false;
		}
		finalStatesIndex = state;
		return true;
	}

	private void Searching(int current){
		if (isFinal(current)) {
			isFound = true;
			return;
		}
		else {
			if (!ifContainsUnvisitedChildren(current)) {
				stack.pop();
				if (stack.isEmpty())
					return;
				else
					Searching((int) stack.lastElement());
			}
			else {
				int next = getFirstUnvisitedChild(current);
				stack.push(next);
				visited.add(next);
				Searching(next);
			}
		}
	}
}
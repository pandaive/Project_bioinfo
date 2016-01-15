import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Way_searching_v9 {
	
	private static String[][] actions = new String[3500][6];
	private static HashMap<String, List<String[]>> processes = new HashMap<String, List<String[]>>();
	private static HashMap<String, List<Integer>> objectives = new HashMap<String, List<Integer>>();
	public static HashMap<String, String[]> objectives_redir = new HashMap<String, String[]>();
	public static HashMap<Integer, List<String[]>> solutions = new HashMap<Integer, List<String[]>>();
	private static HashMap<String, Integer> stateIndex = new HashMap<String, Integer>();
	private static int numberOfActions;
	
	boolean oneSolution = false;


	public Way_searching_v9(HashMap<String, List<String[]>> processes, HashMap<String, List<Integer>> objectives, HashMap<String, String[]> objectives_redir, 
			HashMap<Integer, List<String[]>> solutions, int numberOfStates, HashMap<String, Integer> stateIndex,
			int[] initialStates, int numberOfActions, String[][] actions, String processToFind, boolean oneSolution){
		this.processes = processes;
		this.objectives = objectives;
		this.objectives_redir = objectives_redir; //redirected
		this.solutions = solutions;
		this.stateIndex = stateIndex;
		this.numberOfActions = numberOfActions;
		this.actions = actions;
		List<Path> result = new ArrayList<Path>();
		result.addAll(solve(initialStates, processToFind.split(" "), null));
		
		for (int i = 0; i < result.size(); i++) {
			System.out.println("Solution " + (i + 1) + ":");
			for (String[] action: result.get(i).listOfActions)
				System.out.println(Arrays.toString(action).replaceAll("[\\[\\],]", ""));
			System.out.println();
		}
		
	}
	
	private ProcessOrder ordering(List<String[]> processList, String[] target, int[] currentStates) {
		int currentStateOfTarget = currentStates[stateIndex.get(target[0])]; 
		List<String[]> processListOrdered = new ArrayList<String[]>();
		List<String[]> finalActionsList = new ArrayList<String[]>();
		//situation when we have one process changing the target at once
		if (processList.size() == 1) { 
			String[] process = processList.get(0);
			String[] action = findAction(process[0], process[1], target[0], String.valueOf(currentStateOfTarget), target[1], currentStates);
			if (action != null) {
				finalActionsList.add(action);
				ProcessOrder processOrder = new ProcessOrder(processList, finalActionsList);
				return processOrder;
			}
		}
		List<String[]> actionsOfTarget = new ArrayList<String[]>();
		//when there are more processes or the process doesn't change the target at once
		for (int i = 0; i < numberOfActions; i++) {
			if (actions[i][3].equals(target[0])) {
				actionsOfTarget.add(actions[i]);
			}
		}
		//after finding all actions changing the target, we look for correct sequence of actions
		while (currentStateOfTarget!=Integer.parseInt(target[1])) {
			for (String[] action: actionsOfTarget) {
				if (Integer.parseInt(action[4]) == currentStateOfTarget) {
					for (String[] process: processList) {
						if (action[0].equals(process[0]) && action[1].equals(process[1])) {
							finalActionsList.add(action);
							if (processListOrdered.size() == 0 || (processListOrdered.size() > 0 && !processListOrdered.get(processListOrdered.size()-1).equals(process)))
							processListOrdered.add(process);
							currentStateOfTarget = Integer.parseInt(action[5]);
							break;
						}
					}
					if (currentStateOfTarget == Integer.parseInt(target[1]))
						break;
				}
			}
		}
		ProcessOrder processOrder = new ProcessOrder(processListOrdered, finalActionsList);
		return processOrder;
	}
	
	private String[] findAction(String firstAutomata, String firstState, String secondAutomata,
			String secondState, String thirdState, int[] currentStates){ //finding action for the objective, process, from actions array
		for (int i = 0; i < numberOfActions; i++){
			if (actions[i][0].equals(firstAutomata) && actions[i][1].equals(firstState)
					&& actions[i][3].equals(secondAutomata) && actions[i][4].equals(secondState) && actions[i][5].equals(thirdState)){
				return actions[i];
			}
		}
		return null;
	}
	
	private List<Path> solve(int[] currentStates, String[] process, String[] objectiveForced) {
		List<Path> result = new ArrayList<Path>(); //we start with creating list of result, containing solution(s)
		
		if (currentStates[stateIndex.get(process[0])] != Integer.parseInt(process[1])) { //if a state needed is not current
			String processString = process[0] + " " + process[1];
			
			if (processes.containsKey(processString) || objectiveForced != null) { //if we have an objective for this process
				String[] objective = new String[3];
				if (objectiveForced != null) { //if it's redirected check, we have a special objective
					objective = new String[3];
					for (int i = 0; i < 3; i++) //copy good objective
						objective[i] = objectiveForced[i];
				}
				else
					for (String[] obj: processes.get(processString)) { //among all objectives look for the good one, that it fits current states
						if (Integer.parseInt(obj[1]) == currentStates[stateIndex.get(obj[0])]) { //if the objective matches
							for (int i = 0; i < 3; i++) //copy good objective
							objective[i] = obj[i];
							break;
						}
					}
				//we have objective now
				String objectiveString = objective[0] + " " + objective[1] + " " + objective[2];
				List<Path> preResult = new ArrayList<Path>(); //this list is for eventual redirections and the partial result
				if (objectives_redir.containsKey(objectiveString)) { //if there is a redirection, we divide objective into parts
					String[] objTemp = {objective[0], objective[1], objectives_redir.get(objectiveString)[1]};
					String[] procTemp = {objective[0], objectives_redir.get(objectiveString)[1]};
					boolean ok = true;
					try {
						preResult.addAll(solve(currentStates, procTemp, objTemp)); //then we add solving partial objective to final result, as what we are doing first of all
					}
					catch (NullPointerException e) {
						ok = false;
					}
					if (ok) {
						objective[1] = objectives_redir.get(objectiveString)[1]; //update
						objectiveString = objective[0] + " " + objective[1] + " " + objective[2]; //update
					}
				}
				else {
					List<String[]> emptyList = new ArrayList<String[]>();
					Path path = new Path(currentStates, emptyList);
					preResult.add(path);
				}

				if (objectives.containsKey(objectiveString)) { //if there is a solution
					for (int solution: objectives.get(objectiveString)) { //for all of them
						if (solutions.containsKey(solution)) { //if there are processes
							//////ordering
							List<String[]> adequateActions = new ArrayList<String[]>(); //actions made by each process in solutions
							List<String[]> processesOrdered = new ArrayList<String[]>(); //and order of those processes
								ProcessOrder po = ordering(solutions.get(solution), process, currentStates);
								adequateActions.addAll(po.adequateActions);
								processesOrdered.addAll(po.processes);
							//////ordering end, in processesOrdered we have order in which we have to apply processes
								
								List<Path> list = new ArrayList<Path>();
								for (int i = 0; i < preResult.size(); i++) {
									Path tempPath = new Path(preResult.get(i).currentStates, preResult.get(i).listOfActions);
									list.add(tempPath);
								}
								int pathSize = list.size();
								int k = 0; //k is position in the list of adequate actions
								for (int path = 0; path < pathSize; path++) {
									for (int processes = 0; processes < processesOrdered.size(); processes++) {
										List<String[]> toAddList = new ArrayList<String[]>();
										
										while (adequateActions.size() > k && adequateActions.get(k)[0].equals(processesOrdered.get(processes)[0]) 
												&& adequateActions.get(k)[1].equals(processesOrdered.get(processes)[1])) {
											toAddList.add(adequateActions.get(k));
											k++;
										}
										List<Path> tempRes = new ArrayList<Path>();
										try {
											tempRes.addAll(solve(list.get(path).currentStates, processesOrdered.get(processes), null));
										}
										catch (NullPointerException e) {
											break;
										}
										for (int j = 1; j < tempRes.size(); j++) {
											Path tempPath = new Path(list.get(path).currentStates, list.get(path).listOfActions);
											list.add(tempPath);
											list.get(list.size()-1).currentStates = tempRes.get(j).currentStates;
											list.get(list.size()-1).listOfActions.addAll(tempRes.get(j).listOfActions);
											
											list.get(list.size()-1).currentStates[stateIndex.get(objective[0])] = Integer.parseInt(toAddList.get(toAddList.size()-1)[5]);
											list.get(list.size()-1).listOfActions.addAll(toAddList);
										}

										list.get(path).currentStates = tempRes.get(0).currentStates;
										list.get(path).listOfActions.addAll(tempRes.get(path).listOfActions);
										list.get(path).listOfActions.addAll(toAddList);
										list.get(path).currentStates[stateIndex.get(objective[0])] = Integer.parseInt(toAddList.get(toAddList.size()-1)[5]);
										
									}
									k = 0;
									result.addAll(list);
								}
						}
						else {
							continue;
						}
						
						if (oneSolution) 
							break; //end of one solution treating, so if we need just one, we stop here 
						
					}
				}
				else { //objective doesn't have a solution
					return null;
				}
				return result;
			}
			else { //process doesn't have an objective
				return null;
			}
		}
		else { // given automaton is in a given state already, so no actions needed, return just current states
			List<String[]> emptyList = new ArrayList<String[]>();
			Path path = new Path(currentStates, emptyList);
			result.add(path);
			return result;
		}
	}
	
}
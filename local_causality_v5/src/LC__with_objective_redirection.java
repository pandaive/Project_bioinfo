import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class LC__with_objective_redirection {
	
	private static String[][] actions = new String[3500][6];
	private static HashMap<String, List<String[]>> processes = new HashMap<String, List<String[]>>();
	private static HashMap<String, List<Integer>> objectives = new HashMap<String, List<Integer>>();
	private static HashMap<Integer, List<String[]>> solutions = new HashMap<Integer, List<String[]>>();
	public static HashMap<String, String[]> objectives_redir = new HashMap<String, String[]>();
	private static HashMap<String, Integer> stateIndex = new HashMap<String, Integer>();
	private static int numberOfStates;
	private static int numberOfActions;
	private static int[] initialStates;

	private static int fileReader(String fileName) throws FileNotFoundException{
		Scanner file = new Scanner(new File(fileName));
		int lineCounter = 0;
		String line;
		String[] action;
		while(file.hasNextLine()){
			line = file.nextLine();
			action = line.split(" ");
			for (int i = 0; i < 6; i++) {
			actions[lineCounter][i] = action[i];
			}
			lineCounter++;
		}
		file.close();
		return lineCounter;
	}
	

	private static void graphReader(String fileName) throws FileNotFoundException{
		Scanner file = new Scanner(new File(fileName));
		while(file.hasNextLine()){
			String line = file.nextLine();
			String[] temp = line.split(" ");
			switch(temp[0]){
		case "process":
				String process = temp[1] + " " + temp[2];
				String[] tempProcValue = {temp[5], temp[6], temp[7]};
				if (!processes.containsKey(process)){
					List<String[]> tempValue = new ArrayList<String[]>(1); 
					tempValue.add(tempProcValue);
					processes.put(process, tempValue); 
				}
				else
					processes.get(process).add(tempProcValue);
				break;
			case "objective":
				String objective = temp[1] + " " + temp[2] + " " + temp[3];
				if (temp[5].equals("objective")){
					//System.out.println(Arrays.toString(temp));
					String obj_redir[] = {temp[6], temp[7], temp[8]};
					objectives_redir.put(objective, obj_redir);
					break;
				}
				if (!objectives.containsKey(objective)) {
					List<Integer> tempValue = new ArrayList<Integer>();
					tempValue.add(Integer.parseInt(temp[6]));
					objectives.put(objective, tempValue);
				}
				else
					objectives.get(objective).add(Integer.parseInt(temp[6]));
				break;
			case "solution":
				List<String[]> tempValue = new ArrayList<String[]>(1);
				String[] tempSolValue = {temp[4], temp[5]};
				tempValue.add(tempSolValue);
				if (!solutions.containsKey(Integer.parseInt(temp[1]))){
					solutions.put(Integer.parseInt(temp[1]), tempValue); 
				}
				else
					solutions.get(Integer.parseInt(temp[1])).add(tempSolValue);
				break;
			}
		}
		file.close();
	}
	
	private static int getInitialStates(String argLine){
		initialStates = new int[stateIndex.size()];
		String[] ini = argLine.split("\\s*,\\s*");
		for (int i = 0; i < ini.length; i++){
			String[] temp = ini[i].split(" ");
			if (temp.length>1)
			initialStates[stateIndex.get(temp[0])] = Integer.parseInt(temp[1]);
		}
		return ini.length;
	}
	
	private static void getAllStates(){
		for (int i = 0; i < numberOfActions; i++) {
			if (stateIndex.get(actions[i][0]) == null)
				stateIndex.put(actions[i][0], stateIndex.size());
			
			if (stateIndex.get(actions[i][3]) == null)
				stateIndex.put(actions[i][3], stateIndex.size());	
		}
	}
	
	private static void readInitialState(String file) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(file));
		String state = sc.nextLine();
		numberOfStates = getInitialStates(state);
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		long startTime = System.currentTimeMillis();
		numberOfActions = fileReader(args[3]);
		getAllStates();
		graphReader(args[2]);
		//numberOfStates = getInitialStates(args[0]);
		readInitialState(args[0]);
		String toFind = args[1];
		//System.out.println(objectives_redir.values());
		System.out.println(numberOfStates);
		Way_searching_v9 way = new Way_searching_v9(processes, objectives, objectives_redir, solutions, numberOfStates, 
			stateIndex, initialStates, numberOfActions, actions, toFind, false);
		long endTime = System.currentTimeMillis();
		float time = (float)(endTime - startTime)/1000;
		System.out.println("Executed in " + time + " s");
	}

}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Sequence_Linking_Optimized {
	
	static String[] files = {"S1.txt", "S2.txt"};
	static String[][] sequences = new String[2][10];;
	static int lengths[] = new int[2];
	static int[] currentStates = new int[3];
	static HashMap<Character, Integer> stateIndex = new HashMap<Character, Integer>();
	
	static int[][] pre_Conditions = new int[2][3];
	static int[][] post_Conditions = new int[2][3];
	
	
	private static int fileReader(String fileName, int seq) throws FileNotFoundException{
		Scanner file = new Scanner(new File(fileName));
		int lineCounter = 0;
		while(file.hasNextLine()){
			String line = file.nextLine();
			sequences[seq][lineCounter] = line;
			lineCounter++;
		}
		file.close();
		return lineCounter;
	}
	
	
	private static void getSequences(String[] files) throws FileNotFoundException{
		for (int i = 0; i < files.length; i++)
			lengths[i] = fileReader(files[i], i);
	}
	
	
	private static boolean applySequence(int seqNumber){
		for (int i = 0; i < 3; i++) {
			pre_Conditions[seqNumber][i] = -1;
			post_Conditions[seqNumber][i] = -1;
			currentStates[i] = -1;
		}
		
		for (int i = 0; i < lengths[seqNumber]; i++){
			int stateChanger = stateIndex.get(sequences[seqNumber][i].charAt(0));
			if (pre_Conditions[seqNumber][stateChanger] == -1) {
				pre_Conditions[seqNumber][stateChanger] = Character.getNumericValue(sequences[seqNumber][i].charAt(2));
				currentStates[stateChanger] = pre_Conditions[seqNumber][stateChanger];
			}
			
			if (Character.getNumericValue(sequences[seqNumber][i].charAt(2)) != currentStates[stateChanger]) {
				System.out.println("Applying sequence not possible. [1]");
				return false;
			}
				
			int stateToChange = stateIndex.get(sequences[seqNumber][i].charAt(7));
			if (pre_Conditions[seqNumber][stateToChange] == -1) {
				pre_Conditions[seqNumber][stateToChange] = Character.getNumericValue(sequences[seqNumber][i].charAt(9));			
				currentStates[stateToChange] = pre_Conditions[seqNumber][stateToChange];
			}
			
			int oldValue = Character.getNumericValue(sequences[seqNumber][i].charAt(9));
			int newValue = Character.getNumericValue(sequences[seqNumber][i].charAt(11));
			
			if (oldValue != currentStates[stateToChange]) { 
				System.out.println("Applying sequence not possible.[2]");
				return false;
			}
			else			
				currentStates[stateToChange] = newValue;
		}

		for (int i = 0; i < 3; i++)
			post_Conditions[seqNumber][i] = currentStates[i];

		return true;
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		stateIndex.put('a', 0);
		stateIndex.put('b', 1);
		stateIndex.put('c', 2);
		
		getSequences(files);
		if (applySequence(0) && applySequence(1)){
			//S1;S2
			for (int i = 0; i < 3; i++){
				if (pre_Conditions[1][i] != -1 && pre_Conditions[1][i] != post_Conditions[0][i] ){
					System.out.print("S1;S2 not possible");
					break;
				}
				if (i == 2) { //if i == 2 it means that it's the last part of loop, so if we are here, pre and post conditions match each other. Didn't have other idea for that
					System.out.print("S1;S2 possible. Final states:");
					for (int j = 0; j < 3; j++)
						System.out.print(" " + post_Conditions[1][j]);
				}
			}
			
			//S2;S1
			for (int i = 0; i < 3; i++){
				if (pre_Conditions[0][i] != -1 && pre_Conditions[0][i] != post_Conditions[1][i] ){
					System.out.print("\nS2;S1 not possible");
					break;
				}
				if (i == 2) {
					System.out.print("\nS2;S1 possible. Final states:");
					for (int j = 0; j < 3; j++)
						System.out.print(" " + post_Conditions[0][j]);
				}
			}
		}
	}
}
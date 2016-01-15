import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Sequence_Linking {
	
	static String[][] sequences;
	static int lengths[] = new int[2];
	static int[] currentStates;
	static HashMap<Character, Integer> stateIndex = new HashMap<Character, Integer>();

	/*
	 * Reads file and counts lines.
	 */
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
	
	/*
	 * Gets the sequences and put their lengths into the length array.
	 */
	private static void getSequences(String[] files) throws FileNotFoundException{
		for (int i = 0; i < files.length; i++)
			lengths[i] = fileReader(files[i], i);
	}

	/*
	 * I think I have restored what we have done on meeting, it doesn't look very pretty because of this states' arrays
	 * indexes problem I've mentioned before, but works.
	 * First function is for checking if sequence can be applied with given initial states, used at the
	 * beginning of applySequence function.
	 * I've split applySequence function in matching part and main part for the clarity of it.
	 */
	private static boolean matchSequenceWithInitialStates(int seqNumber, int[] initialStates){
		int[] x = new int[currentStates.length]; //x array is filled with -1 and each cell is changed when its state is found
		int statesCountdown = currentStates.length; //each time the new state is found countdown is decremented, so we just need
														//to check if this one is 0, not check all cells of x, to finish
		for (int i = 0; i < currentStates.length; i++)
			x[i] = -1;
		
		for (int j = 0; j < lengths[seqNumber]; j++) {
			
			//for and positionInLine are to check both of states in line in the same way. 
			//First state is at position 0, second at 7 in line
			for (int k = 0; k < 2; k++){
				int positionInLine;
				if (k == 0) positionInLine = 0; 
				else positionInLine = 7;
				
				int a = stateIndex.get(sequences[seqNumber][j].charAt(positionInLine));
				if (x[a] == -1) {
					if (Character.getNumericValue(sequences[seqNumber][j].charAt(positionInLine+2)) != currentStates[a])
						return false;
					else {
						x[a] = 0;
						statesCountdown--;
						if (statesCountdown == 0) 
							return true;
					}
				}
			}
		}
		return true;
	}
	
	/*
	 * And finally, applySequence.
	 */
	private static boolean applySequence(int seqNumber, int[] initialStates){
		currentStates = initialStates;
		if (!matchSequenceWithInitialStates(seqNumber, currentStates)) {
			System.out.println("Applying sequence not possible with current states.");
			return false;
		}
			
		for (int i = 0; i < lengths[seqNumber]; i++){
			//checking if value of first state matches current state value
			int stateChanger = stateIndex.get(sequences[seqNumber][i].charAt(0));
			if (Character.getNumericValue(sequences[seqNumber][i].charAt(2)) != currentStates[stateChanger]) {
				System.out.println("Applying sequence not possible.");
				return false;
			}
			
			//checking if values of second state matches current state value
			int stateToChange = stateIndex.get(sequences[seqNumber][i].charAt(7));			
			
			int oldValue = Character.getNumericValue(sequences[seqNumber][i].charAt(9)); //that's for clarity, it looks better then
			int newValue = Character.getNumericValue(sequences[seqNumber][i].charAt(11)); //with old/new value instead of whole line
			
			if (oldValue != currentStates[stateToChange]) { 
				System.out.println("Applying sequence not possible.");
				currentStates = initialStates;
				return false;
			}
			else			
				currentStates[stateToChange] = newValue;
		}
		
		System.out.print("Sequence applied. Final states:");
		for (int i = 0; i < currentStates.length; i++)
		System.out.print(" " + currentStates[i]);
		System.out.println();
		return true;
	}
	
	/*
	 * Sequence linking, just for two of them, for now. But for the example with linking one hundred sequences with one,
	 * it shouldn't be a problem.
	 */
	private static boolean linkSequences(int seq1, int seq2, int[] initialStates){
		if (applySequence(seq1, initialStates))
			return applySequence(seq2, currentStates);
		else return false;
	}
	
	/*
	 * And I didn't get values by input, just written them here in code, because I thought that it's not necessary.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		stateIndex.put('a', 0);
		stateIndex.put('b', 1);
		stateIndex.put('c', 2);
		
		String[] files = {"S1.txt", "S3.txt"};
		sequences = new String[2][10];
		int[] initialStates  = {0, 1, 0};
		getSequences(files);
		if (linkSequences(0, 1, initialStates))
			System.out.println("Linking those two sequences is possible.");
		else
			System.out.println("Linking those two sequences is not possible.");
	}
}
import java.util.ArrayList;
import java.util.List;


public class SolveVariables {
	List<Path> result = new ArrayList<Path>();
	String[] process = new String[2];
	String[] objective = new String[3];
	String[] objectiveForced = new String[3];
	int solution;
	int[] currentStates;
	List<String[]> adequateActions = new ArrayList<String[]>(); //actions made by each process in solutions
	List<String[]> processesOrdered = new ArrayList<String[]>(); //and order of those processes
	List<Path> preResult = new ArrayList<Path>(); //this list is for eventual redirections (NEW)
	
	
	public SolveVariables(int numberofStates){
		currentStates = new int[numberofStates];
	}
	
	public SolveVariables(SolveVariables v){
		this.result.clear();
		this.result.addAll(v.result);
		this.process = v.process;
		this.objective = v.objective;
		this.objectiveForced = v.objectiveForced;
		this.solution = v.solution;
		this.currentStates = v.currentStates;
		this.adequateActions.clear();
		this.adequateActions.addAll(v.adequateActions);
		this.processesOrdered.clear();
		this.processesOrdered.addAll(v.processesOrdered);
		this.preResult.clear();
		this.preResult.addAll(v.preResult);
	}
	
	public void updateVariables(SolveVariables v){
		this.result.clear();
		this.result.addAll(v.result);
		this.process = v.process;
		this.objective = v.objective;
		this.objectiveForced = v.objectiveForced;
		this.solution = v.solution;
		this.currentStates = v.currentStates;
		this.adequateActions.clear();
		this.adequateActions.addAll(v.adequateActions);
		this.processesOrdered.clear();
		this.processesOrdered.addAll(v.processesOrdered);
		this.preResult.clear();
		this.preResult.addAll(v.preResult);
	}
	
}

import java.util.ArrayList;
import java.util.List;


public class ProcessOrder {
	List<String[]> adequateActions = new ArrayList<String[]>();
	List<String[]> processes = new ArrayList<String[]>();

	public ProcessOrder(List<String[]> processes, List<String[]> adequateActions){
		this.adequateActions.addAll(adequateActions); 
		this.processes.addAll(processes);
	}
	
	public ProcessOrder(){
		
	}
}

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Path {
	int[] currentStates;
	List<String[]> listOfActions;
	
	public Path(){
		
	}
	
	public Path(int[] currentStates, List<String[]> listOfActions) {
		this.currentStates = new int[currentStates.length];
		for (int i = 0; i < currentStates.length; i++)
			this.currentStates[i] = currentStates[i];
		this.listOfActions = new ArrayList<String[]>();
		if (listOfActions.size() > 0)
			for (int i = 0; i < listOfActions.size(); i++) {
				String[] temp = new String[6];
				for (int j = 0; j < 6; j++)
					temp[j] = listOfActions.get(i)[j];
				this.listOfActions.add(temp);
			}
	}
	
	public List<String[]> getList(){
		return listOfActions;
	}
}

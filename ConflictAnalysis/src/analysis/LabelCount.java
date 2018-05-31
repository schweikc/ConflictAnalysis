package analysis;

public class LabelCount {
	private String criterionType;
	private int count;
	
	LabelCount(String criterionType){
		this.criterionType = criterionType;
		count = 0;
	}
	
	public void updateCount(){
		count++;
	}
	
	public String getCriterionType(){
		return criterionType;
	}
	
	public int getCount(){
		return count;
	}

}

package datamanagement.labo2.evaluation;

public class PrecisionRecallPoint {
	
	private double precision;
	private double recall;
	private boolean isRelevant;
	
	

	public PrecisionRecallPoint(double precision, double recall, boolean isRelevant) {
		super();
		this.precision = precision;
		this.recall = recall;
		this.isRelevant = isRelevant;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public boolean isRelevant() {
		return isRelevant;
	}

	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}
	
}

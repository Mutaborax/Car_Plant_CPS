package book.cpps.mechatronicsagents.horizontal;


public class FailurePrediction implements java.io.Serializable {

	private String failureDescription;
	private int when;

	public FailurePrediction(String failureDescription, int when) {
		this.failureDescription = failureDescription;
		this.when = when;
	}

	public String getFailureDescription() {
		return failureDescription;
	}

	public int getWhen() {
		return when;
	}
}

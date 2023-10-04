package book.cpps.mechatronicsagents.horizontal;

public class Electronics extends Component{

	public static final String ELECTRONICS_FAILURE ="ELECT_FAILURE";
	public Electronics(String componentName) {
		super(componentName);
	}

	@Override
	public FailurePrediction predictFailure() {

			boolean inFailure = rnd.nextInt(10) < 4;
			if (inFailure) {
				predictedFailure = new FailurePrediction(ELECTRONICS_FAILURE, rnd.nextInt(20) + 10);
					return predictedFailure;
			}else {
				predictedFailure = null;
				return null;
			}
	}
}

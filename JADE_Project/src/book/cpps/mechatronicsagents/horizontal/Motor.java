package book.cpps.mechatronicsagents.horizontal;


public class Motor extends Component{
	public static final String MOTOR_FAILURE ="MOTOR_FAILURE";

	public Motor(String componentName) {
		super(componentName);
	}

	@Override
	public FailurePrediction predictFailure() {
		boolean inFailure = rnd.nextInt(10) < 4;
		if (inFailure) {
			predictedFailure = new FailurePrediction(MOTOR_FAILURE, rnd.nextInt(20) + 10);
				return predictedFailure;
		}else {
			predictedFailure = null;
			return null;
		}
	}
}

package book.cpps.mechatronicsagents.horizontal;


public class Trim extends Component {

	public static final String TRIM_FAILURE ="TRIM_FAILURE";
		public Trim(String componentName) {
			super(componentName);
		}

		@Override
		public FailurePrediction predictFailure() {

			boolean inFailure = rnd.nextInt(10) < 4;
			if (inFailure) {
				predictedFailure = new FailurePrediction(TRIM_FAILURE, rnd.nextInt(20) + 10);
					return predictedFailure;
			}else {
				predictedFailure = null;
				return null;
			}
		}
	}


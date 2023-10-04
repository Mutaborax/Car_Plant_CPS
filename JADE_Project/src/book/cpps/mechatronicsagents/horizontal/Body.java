package book.cpps.mechatronicsagents.horizontal;

public class Body extends Component{

	public static final String BODY_FAILURE = "BODY_FAILURE";
	public Body(String componentName){
			super(componentName);
				}
		@Override
		public FailurePrediction predictFailure() {
				boolean inFailure = rnd.nextInt(10) < 4;
				if (inFailure) {
					predictedFailure = new FailurePrediction(BODY_FAILURE, rnd.nextInt(20) + 10);
						return predictedFailure;
				}else {
					predictedFailure = null;
					return null;
				}
			}
		}




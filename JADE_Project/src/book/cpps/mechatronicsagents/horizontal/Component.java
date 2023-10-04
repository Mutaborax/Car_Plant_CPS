package book.cpps.mechatronicsagents.horizontal;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public class Component implements Serializable {

	protected FailurePrediction predictedFailure;
	protected Random rnd = new Random();

	private final String componentName;

	public Component(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentName() {
		return componentName;
	}

	public FailurePrediction predictFailure() {
		boolean inFailure = rnd.nextBoolean();
		if (inFailure){
			predictedFailure = new FailurePrediction("Generic Failure", rnd.nextInt(20) + 10);
				return predictedFailure;

		} else {
			return null;
		}
	}

	@Override
	public int hashCode() {
	int hash = 5;
	hash = 53 * hash + Objects.hashCode(this.predictedFailure) ;
	return hash;
			}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
		return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
		return false;
		 }
	 final Component other = (Component) obj;
		 if (!Objects.equals(this.predictedFailure, other.predictedFailure)) {
		 return false;
		 }
	 return true;
		}
	}
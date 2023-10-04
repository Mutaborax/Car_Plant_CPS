package book.cpps.mechatronicsagents.horizontal;

import java.io.Serializable;
import java.util.Objects;


class Pair <T0, T1> implements Serializable {

	private T0 first;
	private T1 second;

	public Pair(T0 first, T1 second) {
		this.first = first;
		this.second = second;
	}


	public T0 getFirst(){

		return first;
	}

	public void setFirst(T0 first) {
		this.first = first;
	}

	public T1 getSecond(){

		return second;
	}

	public void setSecond(T1 second) {
		this.second = second;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode (this.first) ;
		hash = 17 * hash + Objects.hashCode (this.second) ;
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
	 final Pair<?, ?> other = (Pair<?, ?>) obj;
		 if (!Objects.equals(this.first, other.first)) {
		 return false;
		 }
		 if (!Objects.equals(this.second, other.second)) {
		 return false;
		 }
		 return true;
	 }
}

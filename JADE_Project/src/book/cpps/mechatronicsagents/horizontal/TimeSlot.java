package book.cpps.mechatronicsagents.horizontal;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


 public class TimeSlot implements Comparable<TimeSlot>,
 	Serializable {
		private Date from;
		private int duration;


		public TimeSlot(Date from, int duration) {
			this.from = from;
			this.duration = duration;
		}
		public Date getFrom() {
			return from;
		}

		public int getDuration() {
			return duration;
		}

		public static Date addDaysToDate(Date date, int days) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime (date);
				calendar.add(Calendar.DAY_OF_MONTH, days);
					return calendar.getTime();
				}

		@Override
		public int compareTo(TimeSlot o) {
			return addDaysToDate(from, duration) .compareTo (
			addDaysToDate(o.getFrom(), o.getDuration()));
		}

			@Override
		public int hashCode() {
		int hash = 5;
		hash = 53 * hash + Objects.hashCode(this.from) ;
		hash = 53 * hash + this.duration;
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
		 final TimeSlot other = (TimeSlot) obj;
			 if (this.duration != other.duration) {
			 return false;
			 }
			 if (!Objects.equals(this.from, other.from)) {
			 return false;
			 }
		 return true;
			 		}
 				}

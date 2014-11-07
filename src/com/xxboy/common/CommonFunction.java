package com.xxboy.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class CommonFunction {
	public static final class XDate {
		public static final String YEAR_FORMAT = "yyyy";
		public static final String MONTH_FORMAT = "MM";
		public static final String DAY_FORMAT = "dd";

		private String year = null;
		private String month = null;
		private String day = null;

		private static SimpleDateFormat YEAR_SDF = new SimpleDateFormat(
				YEAR_FORMAT, Locale.getDefault());
		private static SimpleDateFormat MONTH_SDF = new SimpleDateFormat(
				MONTH_FORMAT, Locale.getDefault());
		private static SimpleDateFormat DAY_SDF = new SimpleDateFormat(
				DAY_FORMAT, Locale.getDefault());

		private Date date;

		public XDate() {
			this.date = new Date();
		}

		public String getYear() {
			return year != null ? year : (year = YEAR_SDF.format(this.date));
		}

		public String getMonth() {
			return month != null ? month
					: (month = MONTH_SDF.format(this.date));
		}

		public String getDay() {
			return day != null ? day : (day = DAY_SDF.format(this.date));
		}

	}

}

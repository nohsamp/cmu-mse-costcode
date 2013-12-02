/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 14, 2013
 */
public class StringUtils {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	/**
	 * Converts date into formatted string
	 * 
	 * @param date
	 *            - date to be converted
	 * @param pattern
	 *            - pattern of the string representation
	 * @return string representation of the date
	 */
	public static String getDateAsString(Date date, final String pattern) {

		if (date == null || isNullOrEmpty(pattern)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(date);
	}

	/**
	 * Parses a date object from the string data
	 * 
	 * @param strDate
	 *            - string data
	 * @param pattern
	 *            - date pattern
	 * @return date object
	 */
	public static Date getDateFromString(final String strDate,
			final String pattern) {

		Date date = null;
		try {
			date = new SimpleDateFormat(pattern, Locale.US).parse(strDate);
		} catch (ParseException e) {
			Logger.logError(StringUtils.class, "Cannot parse string date: "
					+ strDate, e);
		}

		return date;
	}

	/**
	 * Checks whether the given string is empty or null
	 * 
	 * @param value
	 *            - a string to be validated
	 * @return <b>true</b> - if the given string is null or empty, <b>false</b>
	 *         - if not
	 */
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty();
	}

	/**
	 * Limits the length of the string to the given max length and adds
	 * additional ending
	 * 
	 * @param originalValue
	 *            - a string to be limited
	 * @param maxLength
	 *            - maximum length of the final string
	 * @param appendChars
	 *            - a characters to be added to the end
	 * @return limited string
	 */
	public static String getLimitedString(String originalValue, int maxLength,
			String appendChars) {

		if (originalValue == null) {
			return "";
		}
		if (originalValue.length() <= maxLength) {
			return originalValue;
		}
		if (appendChars == null) {
			appendChars = "";
		}

		String limitedValue = originalValue.substring(0, maxLength
				- appendChars.length() - 1)
				+ appendChars;

		return limitedValue;
	}

	@SuppressLint("DefaultLocale")
	public static String getMoneyFormatString(double value) {

		return String.format("%.2f", value);
	}

	// -------------------------------------------------------------------------
	// HELPER METHODS
	// -------------------------------------------------------------------------

}

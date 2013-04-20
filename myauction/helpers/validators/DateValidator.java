package myauction.helpers.validators;

import java.sql.Date;

public class DateValidator extends Validator<Date> {
	private static IntegerValidator monthValidator = new IntegerValidator("month", 1, 12);
	private static IntegerValidator[] dayValidators = {
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 28),
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 30),
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 30),
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 30),
		new IntegerValidator("day", 1, 31),
		new IntegerValidator("day", 1, 30),
		new IntegerValidator("day", 1, 31)
	};
	private static IntegerValidator yearValidator = new IntegerValidator("year", 1900, 3000);
	private static IntegerValidator hourValidator = new IntegerValidator("hour", 0, 23);
	private static IntegerValidator minuteValidator = new IntegerValidator("minute", 0, 59);
	private static IntegerValidator secondValidator = new IntegerValidator("second", 0, 59);

	public DateValidator(String flag) {
		super(flag);
	}
	public Date validate(String date) {
		return null;
	}
	public Date validate(String month, String day, String year, String hour, String minute, String second) throws ValidationException {
		Date date;
		int monthAsNum = 0;
		int dayAsNum = 0;
		int yearAsNum = 0;
		int hourAsNum = 0;
		int minuteAsNum = 0;
		int secondAsNum = 0;

		try {
			monthAsNum = monthValidator.validate(month);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Month: " + e.getMessage());
		}

		try {
			dayAsNum = dayValidators[monthAsNum-1].validate(day);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Day: " + e.getMessage());
		}

		try {
			yearAsNum = yearValidator.validate(year);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Year: " + e.getMessage());
		}

		try {
			hourAsNum = hourValidator.validate(hour);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Hour: " + e.getMessage());
		}

		try {
			minuteAsNum = minuteValidator.validate(minute);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Minute: " + e.getMessage());
		}

		try {
			secondAsNum = secondValidator.validate(second);
		} catch (ValidationException e) {
			throw new ValidationException(flag, "Second: " + e.getMessage());
		}

		date = new Date(yearAsNum, monthAsNum, dayAsNum);
		date.setHours(hourAsNum);
		date.setMinutes(minuteAsNum);
		date.setSeconds(secondAsNum);

		return date;
	}
}

package myauction.helpers.validators;

public class IntegerValidator extends Validator {
	int minValue;
	int maxValue;

	public IntegerValidator(int min, int max) {
		super();

		minValue = min;
		maxValue = max;
	}

	public void validate(String input) throws ValidationException {
		int parsedInt;

		try {
			parsedInt = Integer.parseInt(input);
		} catch (Exception e) {
			throw new ValidationException("Not an integer");
		}

		if (parsedInt < minValue) {
			throw new ValidationException("Min. value: " + minValue);
		} else if (parsedInt > maxValue) {
			throw new ValidationException("Max. value: " + maxValue);
		}
	}
}
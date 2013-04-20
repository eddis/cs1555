package myauction.helpers.validators;

public class IntegerValidator extends Validator<Integer> {
	int minValue;
	int maxValue;

	public IntegerValidator(String flag, int min, int max) {
		super(flag);

		minValue = min;
		maxValue = max;
	}

	public Integer validate(String input) throws ValidationException {
		int parsedInt;

		try {
			parsedInt = Integer.parseInt(input);
		} catch (Exception e) {
			throw new ValidationException(flag, "Not an integer");
		}

		if (parsedInt < minValue) {
			throw new ValidationException(flag, "Min. value: " + minValue);
		} else if (parsedInt > maxValue) {
			throw new ValidationException(flag, "Max. value: " + maxValue);
		}

		return new Integer(parsedInt);
	}
}
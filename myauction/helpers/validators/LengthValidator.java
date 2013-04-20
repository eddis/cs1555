package myauction.helpers.validators;

public class LengthValidator extends Validator<String> {
	int minLength;
	int maxLength;

	public LengthValidator(String flag, int min, int max) {
		super(flag);
		
		minLength = min;
		maxLength = max;
	}

	public String validate(String input) throws ValidationException {
		if (input.length() < minLength) {
			throw new ValidationException(flag, "Min. chars: " + minLength);
		} else if (input.length() > maxLength) {
			throw new ValidationException(flag, "Max. chars: " + maxLength);
		}

		return input;
	}
}
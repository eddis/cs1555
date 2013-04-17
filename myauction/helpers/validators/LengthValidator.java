package myauction.helpers.validators;

public class LengthValidator extends Validator {
	int minLength;
	int maxLength;

	public LengthValidator(int min, int max) {
		super();
		
		minLength = min;
		maxLength = max;
	}

	public void validate(String input) throws ValidationException {
		if (input.length() < minLength) {
			throw new ValidationException("Min. chars: " + minLength);
		} else if (input.length() > maxLength) {
			throw new ValidationException("Max. chars: " + maxLength);
		}
	}
}
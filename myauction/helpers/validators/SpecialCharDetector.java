package myauction.helpers.validators;

public class SpecialCharDetector extends Validator<String> {
	String character;

	public SpecialCharDetector(String character) {
		super("<");

		this.character = character;
	}

	public String validate(String input) throws ValidationException {
		if (input.equals(character)) {
			throw new SpecialCharException(flag, character);
		}

		return input;
	}
}
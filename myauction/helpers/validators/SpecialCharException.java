package myauction.helpers.validators;

public class SpecialCharException extends ValidationException {
	public SpecialCharException(String flag, String character) {
		super(flag, character);
	}
}
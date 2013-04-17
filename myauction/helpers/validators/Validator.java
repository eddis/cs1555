package myauction.helpers.validators;

public interface Validator {
	public void validate(String input) throws ValidationException;
}
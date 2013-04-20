package myauction.helpers.validators;

public abstract class Validator<T> {
	protected String flag;

	public Validator(String flag) {
		this.flag = flag;
	}

	public abstract T validate(String input) throws ValidationException;
}
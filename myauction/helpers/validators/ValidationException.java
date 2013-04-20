package myauction.helpers.validators;

public class ValidationException extends Exception {
	private String flag;

	public ValidationException(String flag, String message) {
		super(message);

		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}
}
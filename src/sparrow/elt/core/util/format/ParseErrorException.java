package sparrow.elt.core.util.format;

public class ParseErrorException extends RuntimeException {

	public ParseErrorException() {
		a = new String("Unexpected character.");
	}

	public ParseErrorException(String s) {
		a = new String(s);
	}

	public String toString() {
		return a;
	}

	String a;
}

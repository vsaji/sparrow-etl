package sparrow.etl.core.util.format;

public class Test {

	public static void main(String[] arg) {
		double d = 123.45678901234567890;
		int sign = 1;

		for (int i = 0; i <= 10; i++) {
			sign *= -1;
			d *= sign;
			Format.printf("d=%+15.*f =%-15.*f=%f\n", new Parameters(i).add(d)
					.add(i).add(d).add(d));
		}

		System.out.println("My Text:"
				+ Format.sprintf("Hello %s!\n", new Parameters("world")));
		Format.printf("Hello %10s!\n", new Parameters("world"));
		Format.printf("Hello %-10.3s!\n", new Parameters("world"));

	}

}

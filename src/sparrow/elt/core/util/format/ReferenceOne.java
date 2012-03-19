package sparrow.elt.core.util.format;

import java.util.Properties;

class ReferenceOne {

	ReferenceOne() {
	}

	static void a() {
		try {
			String s = System.getProperties().getProperty("line.separator");
			if (s.length() == 1) {
				if (s.charAt(0) == '\r')
					_fldfor = 1;
				else
					_fldfor = 2;
			} else {
				_fldfor = 3;
			}
		} catch (SecurityException securityexception) {
			_fldfor = 2;
		}
	}

	static String a(String s) {
		if (_fldfor == 2)
			return s;
		if (_fldfor == 1) {
			StringBuffer stringbuffer = new StringBuffer(s);
			for (int i = 0; i < stringbuffer.length(); i++)
				if (stringbuffer.charAt(i) == '\n')
					stringbuffer.setCharAt(i, '\r');

			return stringbuffer.toString();
		}
		if (_fldfor == 3) {
			StringBuffer stringbuffer1 = new StringBuffer(s);
			for (int j = 0; j < stringbuffer1.length(); j++) {
				char c = stringbuffer1.charAt(j);
				if (c == '\n')
					stringbuffer1.insert(j++, '\r');
				if (c == '\r'
						&& (++j == stringbuffer1.length() || stringbuffer1
								.charAt(j) != '\n'))
					stringbuffer1.insert(j, '\n');
			}

			return stringbuffer1.toString();
		} else {
			a();
			return a(s);
		}
	}

	static String a(int i, char c) {
		String s = new String();
		for (; i > 0; i--)
			s = s + c;

		return s;
	}

	static boolean _mthif(int i) {
		return i == 3 || i == 1 || i == 2;
	}

	static boolean a(int i) {
		return i == 4 || i == 8 || i == 12;
	}

	public static String a(String s, int i, int j, int k, char c, char c1) {
		if (s == null || i < 0 || j < 0 || !_mthif(k))
			return null;
		String s1;
		if (j > 0) {
			int l = s.length();
			if (j > l)
				j = l;
			s1 = s.substring(0, j);
		} else {
			s1 = new String(s);
		}
		int i1 = s1.length();
		if (i > i1) {
			int j1 = i - i1;
			if (k == 1)
				s1 = s1 + a(j1, c1);
			else if (k == 3)
				s1 = a(j1, c) + s1;
			else if (k == 2) {
				int k1 = j1 / 2;
				int l1 = j1 - k1;
				s1 = a(k1, c) + s1 + a(l1, c1);
			}
		}
		return s1;
	}

	public static String a(char c, int i, int j, char c1, char c2) {
		if (i < 0 || !_mthif(j))
			return null;
		else
			return a(String.valueOf(c), i, 0, j, c1, c2);
	}

	static char a(long l, int i) {
		char c = '\0';
		if (l >= 0L) {
			if (i == 8)
				c = '+';
			else if (i == 12)
				c = ' ';
		} else if (l < 0L)
			c = '-';
		return c;
	}

	static char a(double d, int i) {
		char c = '\0';
		if (d >= 0.0D) {
			if (i == 8)
				c = '+';
			else if (i == 12)
				c = ' ';
		} else if (d < 0.0D)
			c = '-';
		return c;
	}

	static char a(int i, int j) {
		return a(i, j);
	}

	static String a(long l, int i, int j, boolean flag, int k) {
		if (i < 0 || !_mthif(j) || !a(k))
			return null;
		String s;
		if (l < 0L) {
			if (l == 0x8000000000000000L)
				s = String.valueOf(l).substring(1);
			else
				s = String.valueOf(-l);
		} else {
			s = String.valueOf(l);
		}
		char c = a(l, k);
		if (flag) {
			if (c != 0) {
				if (i > 0)
					i--;
				s = c + a(s, i, 0, j, '0', ' ');
			} else {
				s = a(s, i, 0, j, '0', ' ');
			}
		} else if (c != 0)
			s = a(c + s, i, 0, j, ' ', ' ');
		else
			s = a(s, i, 0, j, ' ', ' ');
		return s;
	}

	static String a(int i, int j, int k, boolean flag, int l) {
		return a(i, j, k, flag, l);
	}

	static String a(double d, int i, int j, int k, boolean flag, int l) {
		if (i < 0 || j < 0 || !_mthif(k) || !a(l))
			return null;
		String s;
		if (d < 0.0D)
			s = _mthif(-d, j);
		else
			s = _mthif(d, j);
		char c = a(d, l);
		if (flag) {
			if (c != 0) {
				if (i > 0)
					i--;
				s = c + a(s, i, 0, k, '0', ' ');
			} else {
				s = a(s, i, 0, k, '0', ' ');
			}
		} else if (c != 0)
			s = a(c + s, i, 0, k, ' ', ' ');
		else
			s = a(s, i, 0, k, ' ', ' ');
		return s;
	}

	static byte[] _mthdo(double d, int i) {
		byte abyte0[] = new byte[i + 1];
		long l = 1L;
		long l1 = 10L * (long) d;
		for (int j = 0; j <= i; j++) {
			l *= 10L;
			byte byte0 = (byte) (int) (d * (double) l - (double) l1);
			if (byte0 == 10) {
				abyte0[j] = 0;
				l1 += 10L;
				for (int k = j - 1; ++abyte0[k] == 10; abyte0[k--] = 0)
					;
			} else {
				abyte0[j] = byte0;
			}
			l1 = 10L * (l1 + (long) abyte0[j]);
		}

		return abyte0;
	}

	static int a(byte abyte0[], int i) {
		if (abyte0[i] >= 5) {
			if (i == 0)
				return 1;
			abyte0[i - 1]++;
			for (int j = i - 1; j > 0; j--)
				if (abyte0[j] == 10) {
					abyte0[j] = 0;
					abyte0[j - 1]++;
				}

			if (abyte0[0] == 10) {
				abyte0[0] = 0;
				return 1;
			}
		}
		return 0;
	}

	static String _mthif(double d, int i) {
		long l1 = 1L;
		boolean flag = d < 0.0D;
		if (flag)
			d = -d;
		long l = (long) d;
		byte abyte0[] = _mthdo(d, i);
		l += a(abyte0, i);
		StringBuffer stringbuffer = new StringBuffer();
		if (flag)
			stringbuffer.append('-');
		stringbuffer.append(String.valueOf(l));
		stringbuffer.append('.');
		for (int j = 0; j < i; j++)
			stringbuffer.append(String.valueOf(abyte0[j]));

		return stringbuffer.toString();
	}

	static String a(boolean flag, int i, int j, int k) {
		if (i < 0 || !_mthif(j))
			return null;
		String s;
		if (flag)
			s = "true";
		else
			s = "false";
		s = a(s, i, k, j, ' ', ' ');
		return s;
	}

	static final int _fldchar = 0;

	static final int a = 1;

	static final int _fldif = 2;

	static final int _fldbyte = 3;

	static int _fldfor = 0;

	static final int _fldgoto = 0;

	static final int _fldnew = 1;

	static final int _fldelse = 2;

	static final int _fldint = 3;

	static final int _fldtry = 4;

	static final int _fldcase = 8;

	static final int _flddo = 12;

}

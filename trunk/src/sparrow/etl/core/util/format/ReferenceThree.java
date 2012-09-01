package sparrow.etl.core.util.format;

class ReferenceThree {

	public ReferenceThree() {
		_fldbyte = 0;
		g = 0x80000000;
		m = 0x80000000;
		o = ' ';
		i = false;
		_fldlong = false;
		p = false;
		e = false;
		_fldgoto = false;
		_fldelse = 0;
	}

	public String a() {
		String s1;
		switch (_fldbyte) {
		case 0: // '\0'
			s1 = "unknown";
			break;

		case 1: // '\001'
			s1 = "c";
			break;

		case 2: // '\002'
			s1 = "d";
			break;

		case 14: // '\016'
			s1 = "u";
			break;

		case 3: // '\003'
			s1 = "o";
			break;

		case 4: // '\004'
			s1 = "x";
			break;

		case 6: // '\006'
			s1 = "e";
			break;

		case 8: // '\b'
			s1 = "f";
			break;

		case 9: // '\t'
			s1 = "g";
			break;

		case 11: // '\013'
			s1 = "s";
			break;

		case 12: // '\f'
			s1 = "l";
			break;

		case 13: // '\r'
			s1 = "b";
			break;

		case 5: // '\005'
		case 7: // '\007'
		case 10: // '\n'
		default:
			s1 = "error";
			break;
		}
		if (_fldelse == 2)
			s1 = s1.toUpperCase();
		return s1;
	}

	public String _mthif() {
		String s1;
		switch (g) {
		case -2147483648:
			s1 = "UNSPECIFIED";
			break;

		case -1:
			s1 = "*";
			break;

		case 0: // '\0'
			s1 = "DEFAULT";
			break;

		default:
			s1 = String.valueOf(g);
			break;
		}
		return s1;
	}

	public String _mthdo() {
		String s1;
		switch (m) {
		case -2147483648:
			s1 = "UNSPECIFIED";
			break;

		case -1:
			s1 = "*";
			break;

		case 0: // '\0'
			s1 = "DEFAULT";
			break;

		default:
			s1 = String.valueOf(m);
			break;
		}
		return s1;
	}

	public String toString() {
		return new String("type=" + a() + ", minWidth=" + _mthif()
				+ ", precision=" + _mthdo());
	}

	public int _fldbyte;

	public int g;

	public int m;

	public int _fldelse;

	public char o;

	public boolean i;

	public boolean _fldlong;

	public boolean p;

	public boolean e;

	public boolean _fldgoto;

	public static final int _fldnew = 0;

	public static final int _fldvoid = 1;

	public static final int u = 2;

	public static final int r = 3;

	public static final int d = 4;

	public static final int _fldint = 6;

	public static final int s = 8;

	public static final int f = 9;

	public static final int _fldfor = 11;

	public static final int k = 12;

	public static final int _flddo = 13;

	public static final int _fldcase = 14;

	public static final int j = 0;

	public static final int _fldchar = 1;

	public static final int _fldif = 2;

	public static final int n = 0x80000000;

	public static final int a = -1;

	public static final int _fldtry = 0;

	public static final int c = 0x80000000;

	public static final int h = -1;

	public static final int b = 0;

	public static final int q = 0;

	public static final int t = 1;

	public static final int _fldnull = 2;

	public static final int l = 3;
}

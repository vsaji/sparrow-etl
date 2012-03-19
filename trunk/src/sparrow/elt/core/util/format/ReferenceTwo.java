package sparrow.elt.core.util.format;

import java.util.Vector;

class ReferenceTwo {

	public ReferenceTwo(String s) {
		a = s.toCharArray();
		_fldnew = 0;
		_fldint = new Vector();
		_fldfor = new Vector();
	}

	public Vector _mthbyte() {
		return _fldint;
	}

	public Vector _mthchar() {
		return _fldfor;
	}

	public void _mthelse() throws ParseErrorException {
		String s = new String();
		for (boolean flag = false; !flag;)
			if (a(37)) {
				if (a(37)) {
					s = s + '%';
				} else {
					_fldif = new ReferenceThree();
					_mthnull();
					if (!_flddo) {
						_fldint.addElement(_fldif);
						_fldfor.addElement(s);
						s = new String();
						_fldif = null;
					}
				}
			} else if (_fldnew < a.length) {
				s = s + a[_fldnew++];
			} else {
				_fldfor.addElement(s);
				flag = true;
			}

	}

	private void _mthfor() throws ParseErrorException {
		_flddo = true;
		throw new ParseErrorException("Parse error. Unknown symbol: "
				+ a[_fldnew]);
	}

	private void _mthlong() {
	}

	private void _mthnull() throws ParseErrorException {
		_fldif.m = 0x80000000;
		_flddo = false;
		if (_mthnew())
			_mthtry();
		else if (_mthgoto())
			_mthvoid();
		else if (a(46))
			_mthcase();
		else if (_mthint())
			_mthlong();
		else
			_mthfor();
	}

	private void _mthtry() throws ParseErrorException {
		if (_mthnew())
			_mthtry();
		else if (_mthgoto())
			_mthvoid();
		else if (a(46))
			_mthcase();
		else if (_mthint())
			_mthlong();
		else
			_mthfor();
	}

	private void _mthvoid() throws ParseErrorException {
		if (a(46))
			_mthcase();
		else if (_mthint())
			_mthlong();
		else
			_mthfor();
	}

	private void _mthif() throws ParseErrorException {
		if (_mthint())
			_mthlong();
		else
			_mthfor();
	}

	private void _mthcase() throws ParseErrorException {
		if (_mthdo())
			_mthif();
		else if (_mthint())
			_mthlong();
		else
			_mthfor();
	}

	private int a() {
		if (_fldnew == a.length)
			return -1;
		int i = -1;
		for (boolean flag = false; !flag;)
			if (Character.isDigit(a[_fldnew])) {
				if (_fldnew == a.length)
					i = 0;
				if (i == -1)
					i = a[_fldnew] - 48;
				else
					i = 10 * i + (a[_fldnew] - 48);
				if (++_fldnew == a.length)
					flag = true;
			} else {
				flag = true;
			}

		return i;
	}

	private boolean _mthgoto() {
		if (a(42)) {
			_fldif.g = -1;
			return true;
		}
		int i = a();
		if (i != -1) {
			_fldif.g = i;
			return true;
		} else {
			return false;
		}
	}

	private boolean _mthdo() {
		if (a(42)) {
			_fldif.m = -1;
			return true;
		}
		int i = a();
		if (i != -1) {
			_fldif.m = i;
			return true;
		} else {
			return false;
		}
	}

	private boolean a(int i) {
		if (_fldnew < a.length && a[_fldnew] == i) {
			_fldnew++;
			return true;
		} else {
			return false;
		}
	}

	private boolean _mthnew() {
		boolean flag = true;
		if (a(48))
			_fldif.o = '0';
		else if (a(43))
			_fldif._fldlong = true;
		else if (a(45))
			_fldif.i = true;
		else if (a(32))
			_fldif.p = true;
		else if (a(35))
			_fldif.e = true;
		else if (a(39))
			_fldif._fldgoto = true;
		else
			flag = false;
		return flag;
	}

	private boolean _mthint() {
		boolean flag = true;
		if (a(99))
			_fldif._fldbyte = 1;
		else if (a(100) || a(105))
			_fldif._fldbyte = 2;
		else if (a(117))
			_fldif._fldbyte = 14;
		else if (a(111))
			_fldif._fldbyte = 3;
		else if (a(120)) {
			_fldif._fldbyte = 4;
			_fldif._fldelse = 1;
		} else if (a(88)) {
			_fldif._fldbyte = 4;
			_fldif._fldelse = 2;
		} else if (a(101)) {
			_fldif._fldbyte = 6;
			_fldif._fldelse = 1;
		} else if (a(69)) {
			_fldif._fldbyte = 6;
			_fldif._fldelse = 2;
		} else if (a(102))
			_fldif._fldbyte = 8;
		else if (a(103)) {
			_fldif._fldbyte = 9;
			_fldif._fldelse = 1;
		} else if (a(71)) {
			_fldif._fldbyte = 9;
			_fldif._fldelse = 2;
		} else if (a(115))
			_fldif._fldbyte = 11;
		else if (a(108)) {
			_fldif._fldbyte = 12;
			_fldif._fldelse = 1;
		} else if (a(76)) {
			_fldif._fldbyte = 12;
			_fldif._fldelse = 2;
		} else if (a(98)) {
			_fldif._fldbyte = 13;
			_fldif._fldelse = 1;
		} else if (a(66)) {
			_fldif._fldbyte = 13;
			_fldif._fldelse = 2;
		} else {
			flag = false;
		}
		return flag;
	}

	private char a[];

	private int _fldnew;

	ReferenceThree _fldif;

	Vector _fldint;

	Vector _fldfor;

	boolean _flddo;
}

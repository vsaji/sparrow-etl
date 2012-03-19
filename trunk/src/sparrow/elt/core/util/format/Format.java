package sparrow.elt.core.util.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Vector;

// Referenced classes of package com.braju.format:
//            a, ParametersAutoClear, b, c, 
//            Parameters, ParseErrorException

public class Format extends ReferenceOne {

	private Format() {
	}

	private static void a(ReferenceThree c1, Object obj) {
		boolean flag = false;
		if (c1._fldbyte == 11)
			flag = true;
		else if (c1._fldbyte == 2 || c1._fldbyte == 3 || c1._fldbyte == 4
				|| c1._fldbyte == 1 || c1._fldbyte == 8 || c1._fldbyte == 6
				|| c1._fldbyte == 9)
			flag = (obj instanceof Boolean) || (obj instanceof Character)
					|| (obj instanceof Byte) || (obj instanceof Short)
					|| (obj instanceof Integer) || (obj instanceof Long)
					|| (obj instanceof Float) || (obj instanceof Double);
		else if (c1._fldbyte == 12)
			flag = (obj instanceof Boolean) || (obj instanceof Byte)
					|| (obj instanceof Short) || (obj instanceof Integer)
					|| (obj instanceof Long);
		else if (c1._fldbyte == 13)
			flag = (obj instanceof Boolean) || (obj instanceof Character)
					|| (obj instanceof Byte) || (obj instanceof Short)
					|| (obj instanceof Integer) || (obj instanceof Long);
		else if (c1._fldbyte == 14)
			flag = (obj instanceof Byte) || (obj instanceof Short)
					|| (obj instanceof Integer);
		if (!flag) {
			String s = obj.getClass().getName();
			throw new ClassCastException(
					"One of the arguments can not be casted to the necessary data type as required by one of the format flags: %"
							+ c1.a() + " != " + s + ".");
		} else {
			return;
		}
	}

	private static int a(ReferenceThree c1) {
		return !c1.i ? 3 : 1;
	}

	private static boolean _mthif(ReferenceThree c1) {
		return c1.o == '0';
	}

	private static int _mthdo(ReferenceThree c1) {
		if (c1._fldlong)
			return 8;
		return !c1.p ? 4 : 12;
	}

	private static double _mthif(Object obj) {
		if (obj instanceof Character)
			return (double) ((Character) obj).charValue();
		else
			return ((Number) obj).doubleValue();
	}

	private static long a(Object obj) {
		if (obj instanceof Character)
			return (long) ((Character) obj).charValue();
		if (obj instanceof Boolean)
			return !((Boolean) obj).booleanValue() ? 0L : 1L;
		else
			return ((Number) obj).longValue();
	}

	private static String a(Object obj, ReferenceThree c1, boolean flag) {
		double d = _mthif(obj);
		if (c1.m == 0x80000000)
			if (obj instanceof Float)
				c1.m = 6;
			else if (obj instanceof Double)
				c1.m = 6;
			else
				c1.m = 0;
		int i = a(c1);
		String s;
		if (flag || c1.e) {
			if (c1.e) {
				long l = (long) Math.abs(d);
				if (l != 0L)
					c1.m -= (long) (Math.log(l) / _fldvoid);
			}
			if (i == 1)
				s = a(d, c1.g, c1.m, i, _mthif(c1), _mthdo(c1));
			else
				s = a(d, c1.g, c1.m, i, _mthif(c1), _mthdo(c1));
		} else {
			long l1 = (long) Math.abs(d);
			if (i == 1)
				s = a(d, c1.g, c1.m, i, _mthif(c1), _mthdo(c1));
			else
				s = a(d, c1.g, c1.m, i, _mthif(c1), _mthdo(c1));
			int j = s.length() - 1;
			int k = j;
			char c2;
			for (c2 = s.charAt(k); c2 == '0'; c2 = s.charAt(--k))
				;
			if (c2 == '.')
				k--;
			if (k != j)
				s = s.substring(0, k + 1);
			int i1 = c1.g - s.length();
			if (i1 > 0) {
				String s1;
				for (s1 = ""; i1-- > 0; s1 = s1 + " ")
					;
				if (i == 1)
					s = s + s1;
				else
					s = s1 + s;
			}
		}
		return s;
	}

	private static long a(double d) {
		long l;
		if (d < 0.0D) {
			l = (long) (Math.log(-d) / _fldvoid);
			if (l < 0L)
				l--;
			else if (-1D < d && d < 0.0D)
				l--;
		} else if (d > 0.0D) {
			l = (long) (Math.log(d) / _fldvoid);
			if (l < 0L)
				l--;
			else if (0.0D < d && d < 1.0D)
				l--;
		} else {
			l = 0L;
		}
		return l;
	}

	private static double a(double d, long l) {
		return d / Math.pow(10D, l);
	}

	private static String _mthdo(Object obj, ReferenceThree c1) {
		double d = _mthif(obj);
		long l = a(d);
		if (c1.m == 0x80000000)
			if (obj instanceof Float)
				c1.m = 6;
			else if (obj instanceof Double)
				c1.m = 6;
			else
				c1.m = 0;
		double d1 = a(d, l);
		double d2 = d1 >= 0.0D ? d1 : -d1;
		if ((int) d2 == 9) {
			byte abyte0[] = _mthdo(d2, c1.m);
			if (a(abyte0, c1.m) > 0) {
				l++;
				d1 = a(d, l);
			}
			abyte0 = null;
		} else if ((int) d2 == 10) {
			d1 /= 10D;
			l++;
		}
		return a(d1, l, c1, true);
	}

	private static String a(double d, long l, ReferenceThree c1, boolean flag) {
		String s = String.valueOf(Math.abs(l));
		int i = s.length();
		if (i == 1) {
			s = "0" + s;
			i = 2;
		}
		if (l < 0L)
			s = "-" + s;
		else
			s = "+" + s;
		i++;
		s = "e" + s;
		i++;
		if (c1.g >= i)
			c1.g -= i;
		int j = a(c1);
		String s1;
		if (flag || c1.e) {
			if (c1.e)
				c1.m--;
			if (j != 1) {
				s1 = a(d, c1.g, c1.m, j, _mthif(c1), _mthdo(c1));
				s1 = s1 + s;
			} else {
				s1 = a(d, 0, c1.m, j, _mthif(c1), _mthdo(c1));
				int k = c1.g - s1.length();
				for (s1 = s1 + s; k-- > 0; s1 = s1 + " ")
					;
			}
		} else {
			if (j != 1)
				s1 = a(d, c1.g, c1.m - 1, j, _mthif(c1), _mthdo(c1));
			else
				s1 = a(d, 0, c1.m - 1, j, _mthif(c1), _mthdo(c1));
			int i1 = s1.length() - 1;
			int j1 = i1;
			char c2;
			for (c2 = s1.charAt(j1); c2 == '0'; c2 = s1.charAt(--j1))
				;
			if (c2 == '.')
				j1--;
			if (j1 != i1)
				s1 = s1.substring(0, j1 + 1);
			s1 = s1 + s;
			int k1 = (c1.g + i) - s1.length();
			if (k1 > 0) {
				String s2;
				for (s2 = ""; k1-- > 0; s2 = s2 + " ")
					;
				if (j == 1)
					s1 = s1 + s2;
				else
					s1 = s2 + s1;
			}
		}
		return s1;
	}

	private static String _mthfor(Object obj, ReferenceThree c1) {
		double d = _mthif(obj);
		if (c1.m == 0x80000000)
			if (obj instanceof Float)
				c1.m = 6;
			else if (obj instanceof Double)
				c1.m = 6;
			else
				c1.m = 0;
		long l = a(d);
		double d1 = a(d, l);
		double d2 = d1 >= 0.0D ? d1 : -d1;
		if ((int) d2 == 9) {
			byte abyte0[] = _mthdo(d2, c1.m);
			if (a(abyte0, c1.m - 1) > 0) {
				l++;
				d1 = a(d, l);
			}
			abyte0 = null;
		} else if ((int) d2 == 10) {
			d1 /= 10D;
			l++;
		}
		if (l < -4L || l >= (long) c1.m) {
			return a(d1, l, c1, false);
		} else {
			c1.m -= l + 1L;
			return a(obj, c1, false);
		}
	}

	private static String _mthif(Object obj, ReferenceThree c1) {
		boolean flag = _mthif(c1);
		long l;
		if (obj instanceof Character) {
			l = ((Character) obj).charValue();
			String s = Long.toHexString(l);
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 16;
				else
					c1.g = 18;
		} else if (obj instanceof Byte) {
			l = (byte) ((Number) obj).intValue();
			if (l < 0L)
				l &= 255L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 8;
				else
					c1.g = 10;
		} else if (obj instanceof Short) {
			l = (short) ((Number) obj).intValue();
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 16;
				else
					c1.g = 18;
		} else if (obj instanceof Integer) {
			l = ((Integer) obj).intValue();
			if (l < 0L)
				l &= 0xffffffffL;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 32;
				else
					c1.g = 34;
		} else {
			l = ((Number) obj).longValue();
			if (l >= 0L && flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 64;
				else
					c1.g = 66;
		}
		if (c1.g == 0x80000000)
			c1.g = 0;
		if (c1.m == 0x80000000)
			c1.m = 0;
		if (c1.e) {
			c1.m -= 2;
			if (c1.g > 2)
				c1.g -= 2;
		}
		String s1 = Long.toBinaryString(l);
		if (flag) {
			if (l < 0L)
				s1 = a(s1, c1.g, 0, a(c1), '1', ' ');
			else
				s1 = a(s1, c1.g, 0, a(c1), '0', ' ');
		} else {
			s1 = a(s1, c1.g, 0, a(c1), ' ', ' ');
		}
		if (c1.e)
			s1 = "0b" + s1;
		return s1;
	}

	private static String _mthnew(Object obj, ReferenceThree c1) {
		boolean flag = _mthif(c1);
		long l;
		if (obj instanceof Character) {
			l = ((Character) obj).charValue();
			String s = Long.toHexString(l);
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 4;
				else
					c1.g = 6;
		} else if (obj instanceof Byte) {
			l = (byte) ((Number) obj).intValue();
			if (l < 0L)
				l &= 255L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 2;
				else
					c1.g = 4;
		} else if (obj instanceof Short) {
			l = (short) ((Number) obj).intValue();
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 4;
				else
					c1.g = 6;
		} else if (obj instanceof Integer) {
			l = ((Integer) obj).intValue();
			if (l < 0L)
				l &= 0xffffffffL;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 8;
				else
					c1.g = 10;
		} else {
			l = ((Number) obj).longValue();
			if (l >= 0L && flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 16;
				else
					c1.g = 18;
		}
		if (c1.g == 0x80000000)
			c1.g = 0;
		if (c1.e) {
			c1.m -= 2;
			if (c1.g > 2)
				c1.g -= 2;
		}
		String s1 = Long.toHexString(l);
		if (flag) {
			if (l < 0L)
				s1 = a(s1, c1.g, 0, a(c1), 'f', ' ');
			else
				s1 = a(s1, c1.g, 0, a(c1), '0', ' ');
		} else {
			s1 = a(s1, c1.g, 0, a(c1), ' ', ' ');
		}
		if (c1.e)
			s1 = "0x" + s1;
		return s1;
	}

	private static String a(Object obj, ReferenceThree c1) {
		boolean flag = _mthif(c1);
		long l;
		if (obj instanceof Character) {
			l = ((Character) obj).charValue();
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 6;
				else
					c1.g = 7;
		} else if (obj instanceof Byte) {
			l = (byte) ((Number) obj).intValue();
			if (l < 0L)
				l &= 255L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 3;
				else
					c1.g = 4;
		} else if (obj instanceof Short) {
			l = (short) ((Number) obj).intValue();
			if (l < 0L)
				l &= 65535L;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 6;
				else
					c1.g = 7;
		} else if (obj instanceof Integer) {
			l = ((Integer) obj).longValue();
			if (l < 0L)
				l &= 0xffffffffL;
			else if (flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 11;
				else
					c1.g = 12;
		} else {
			l = ((Long) obj).longValue();
			if (l >= 0L && flag && c1.g == 0x80000000)
				if (!c1.e)
					c1.g = 22;
				else
					c1.g = 23;
		}
		if (c1.g == 0x80000000)
			c1.g = 0;
		if (c1.e) {
			c1.m--;
			if (c1.g > 1)
				c1.g--;
		}
		String s = Long.toOctalString(l);
		if (flag) {
			if (l < 0L)
				s = a(s, c1.g, 0, a(c1), '7', ' ');
			else
				s = a(s, c1.g, 0, a(c1), '0', ' ');
		} else {
			s = a(s, c1.g, 0, a(c1), ' ', ' ');
		}
		if (c1.e)
			s = "0" + s;
		return s;
	}

	private static String _mthint(Object obj, ReferenceThree c1) {
		long l;
		if (obj instanceof Character)
			l = ((Character) obj).charValue();
		else
			l = ((Number) obj).longValue();
		if (l < 0L)
			l &= 65535L;
		if (c1.g == 0x80000000)
			c1.g = 0;
		String s = Long.toHexString(l);
		int i = s.length();
		if (i > 4)
			s = s.substring(i - 3);
		else if (i < 4)
			s = "0000".substring(4 - i) + s;
		s = "\\u" + s;
		s = a(s, c1.g, 0, a(c1), c1.o, c1.o);
		return s;
	}

	public static void setModifyLineSeparators(boolean flag) {
		_fldlong = flag;
	}

	public static boolean getModifyLineSeparators() {
		return _fldlong;
	}

	public static String sprintf(String s, Vector vector)
			throws ParseErrorException {
		String s3;
		synchronized (_fldnull) {
			String s1 = "";
			boolean flag = false;
			if (vector.size() > 0
					&& (vector.firstElement() instanceof ParametersAutoClear)) {
				flag = true;
				vector.removeElementAt(0);
			}
			ReferenceTwo b1 = new ReferenceTwo(s);
			b1._mthelse();
			Vector vector1 = b1._mthbyte();
			for (int i = 0; i < vector1.size(); i++) {
				ReferenceThree c1 = (ReferenceThree) vector1.elementAt(i);
				Object obj1 = vector.elementAt(i);
				if (c1.g == -1) {
					c1.g = ((Number) obj1).intValue();
					if (c1.g < 0) {
						c1.i = true;
						c1.g = -c1.g;
					}
					vector.removeElementAt(i);
				}
				obj1 = vector.elementAt(i);
				if (c1.m == -1) {
					c1.m = ((Number) obj1).intValue();
					vector.removeElementAt(i);
				}
			}

			Vector vector2 = b1._mthchar();
			for (int j = 0; j < vector1.size(); j++) {
				ReferenceThree c2 = (ReferenceThree) vector1.elementAt(j);
				int k = a(c2);
				boolean flag1 = _mthif(c2);
				int l = _mthdo(c2);
				String s4 = (String) vector2.elementAt(j);
				Object obj2 = vector.elementAt(j);
				a(c2, obj2);
				s1 = s1 + s4;
				String s2 = "";
				if (c2._fldbyte == 11) {
					if (c2.g == 0x80000000)
						c2.g = 0;
					if (c2.m == 0x80000000)
						c2.m = 0;
					if (obj2 == null)
						s2 = a("null", c2.g, c2.m, a(c2), c2.o, c2.o);
					else if (obj2 instanceof String)
						s2 = a((String) obj2, c2.g, c2.m, a(c2), c2.o, c2.o);
					else
						s2 = a(obj2.toString(), c2.g, c2.m, a(c2), c2.o, c2.o);
				} else if (c2._fldbyte == 12) {
					boolean flag2;
					if (obj2 instanceof Boolean)
						flag2 = ((Boolean) obj2).booleanValue();
					else
						flag2 = ((Number) obj2).longValue() != 0L;
					if (c2.g == 0x80000000)
						c2.g = 0;
					if (c2.m == 0x80000000)
						c2.m = 0;
					s2 = a(flag2, c2.g, a(c2), c2.m);
				} else if (c2._fldbyte == 1) {
					char c3;
					if (obj2 instanceof Character)
						c3 = ((Character) obj2).charValue();
					else
						c3 = (char) ((Number) obj2).intValue();
					if (c2.m == 0x80000000)
						c2.m = 0;
					if (!c2.e) {
						if (c2.g == 0x80000000)
							c2.g = 0;
						s2 = a(c3, c2.g, a(c2), c2.o, c2.o);
					} else {
						s2 = _mthint(obj2, c2);
					}
				} else if (c2._fldbyte == 2) {
					long l1;
					if (obj2 instanceof Character)
						l1 = ((Character) obj2).charValue();
					else
						l1 = ((Number) obj2).longValue();
					if (c2.g == 0x80000000)
						c2.g = 0;
					if (c2.m == 0x80000000)
						c2.m = 0;
					s2 = a(l1, c2.g, a(c2), _mthif(c2), _mthdo(c2));
				} else if (c2._fldbyte == 13)
					s2 = _mthif(obj2, c2);
				else if (c2._fldbyte == 4) {
					if (c2.m == 0x80000000)
						c2.m = 0;
					s2 = _mthnew(obj2, c2);
				} else if (c2._fldbyte == 3) {
					if (c2.m == 0x80000000)
						c2.m = 0;
					s2 = a(obj2, c2);
				} else if (c2._fldbyte == 8) {
					if (c2.g == 0x80000000)
						c2.g = 0;
					s2 = a(obj2, c2, true);
				} else if (c2._fldbyte == 6) {
					if (c2.g == 0x80000000)
						c2.g = 0;
					s2 = _mthdo(obj2, c2);
				} else if (c2._fldbyte == 9) {
					if (c2.g == 0x80000000)
						c2.g = 0;
					s2 = _mthfor(obj2, c2);
				} else if (c2._fldbyte == 14) {
					long l2;
					if (obj2 instanceof Character)
						l2 = ((Character) obj2).charValue();
					else if (obj2 instanceof Byte) {
						l2 = ((Byte) obj2).longValue();
						if (l2 < 0L)
							l2 += 256L;
					} else if (obj2 instanceof Short) {
						l2 = ((Short) obj2).longValue();
						if (l2 < 0L)
							l2 += 0x10000L;
					} else if (obj2 instanceof Integer) {
						l2 = ((Integer) obj2).longValue();
						if (l2 < 0L)
							l2 += 0x100000000L;
					} else {
						if (obj2 instanceof Long) {
							l2 = ((Long) obj2).longValue();
							throw new ClassCastException(
									"Convertion of a long into an unsigned long is not supported: "
											+ l2 + "l");
						}
						l2 = ((Number) obj2).intValue();
						if (l2 < 0L)
							l2 += 0x100000000L;
					}
					if (c2.g == 0x80000000)
						c2.g = 0;
					if (c2.m == 0x80000000)
						c2.m = 0;
					s2 = a(l2, c2.g, a(c2), _mthif(c2), _mthdo(c2));
				} else {
					System.err.println("sprintf(): Unknown type " + c2.a());
				}
				if (c2._fldelse == 2)
					s2 = s2.toUpperCase();
				s1 = s1 + s2;
			}

			s1 = s1 + (String) vector2.lastElement();
			if (vector.size() > 0
					&& (vector.firstElement() instanceof ParametersAutoClear))
				vector.removeAllElements();
			if (flag) {
				vector.removeAllElements();
				vector.addElement(new ParametersAutoClear());
			}
			if (_fldlong)
				s1 = a(s1);
			s3 = s1.toString();
		}
		return s3;
	}

	public static String sprintf(String s, Object aobj[])
			throws ParseErrorException {
		Vector vector = new Vector();
		for (int i = 0; i < aobj.length; i++)
			vector.addElement(aobj[i]);

		String s1 = sprintf(s, vector);
		vector.removeAllElements();
		vector = null;
		return s1;
	}

	public static String sprintf(String s, Parameters parameters)
			throws ParseErrorException {
		return sprintf(s, parameters.toVector());
	}

	public static void fprintf(OutputStream outputstream, String s,
			Vector vector) throws IOException, ParseErrorException {
		StringReader stringreader = new StringReader(sprintf(s, vector));
		for (int i = stringreader.read(); i != -1; i = stringreader.read())
			outputstream.write(i);

		outputstream.flush();
		stringreader.close();
	}

	public static void fprintf(OutputStream outputstream, String s,
			Parameters parameters) throws IOException, ParseErrorException {
		fprintf(outputstream, s, parameters.toVector());
	}

	public static void fprintf(OutputStream outputstream, String s,
			Object aobj[]) throws IOException, ParseErrorException {
		StringReader stringreader = new StringReader(sprintf(s, aobj));
		for (int i = stringreader.read(); i != -1; i = stringreader.read())
			outputstream.write(i);

		outputstream.flush();
		stringreader.close();
	}

	public static void fprintf(OutputStream outputstream, String s)
			throws IOException, ParseErrorException {
		fprintf(outputstream, s, new Vector());
	}

	public static void printf(String s, Vector vector)
			throws ParseErrorException {
		System.out.print(sprintf(s, vector));
	}

	public static void printf(String s, Parameters parameters)
			throws ParseErrorException {
		System.out.print(sprintf(s, parameters));
	}

	public static void printf(String s, Object aobj[])
			throws ParseErrorException {
		System.out.print(sprintf(s, aobj));
	}

	public static void printf(String s) throws ParseErrorException {
		printf(s, new Vector());
	}

	public static void fprintf(Writer writer, String s, Vector vector)
			throws IOException, ParseErrorException {
		StringReader stringreader = new StringReader(sprintf(s, vector));
		for (int i = stringreader.read(); i != -1; i = stringreader.read())
			writer.write(i);

		writer.flush();
		stringreader.close();
	}

	public static void fprintf(Writer writer, String s, Object aobj[])
			throws IOException, ParseErrorException {
		StringReader stringreader = new StringReader(sprintf(s, aobj));
		for (int i = stringreader.read(); i != -1; i = stringreader.read())
			writer.write(i);

		writer.flush();
		stringreader.close();
	}

	public static void fprintf(Writer writer, String s, Parameters parameters)
			throws IOException, ParseErrorException {
		fprintf(writer, s, parameters.toVector());
	}

	public static void fprintf(Writer writer, String s) throws IOException,
			ParseErrorException {
		fprintf(writer, s, new Vector());
	}


	protected static Parameters a(String as[], int i) throws Exception {
		Parameters parameters = new Parameters();
		ReferenceTwo b1 = new ReferenceTwo(as[i]);
		b1._mthelse();
		Vector vector = b1._mthbyte();
		for (int j = 0; j < vector.size(); j++) {
			ReferenceThree c1 = (ReferenceThree) vector.elementAt(j);
			String s = as[j + i + 1];
			if (c1.g == -1) {
				parameters.add(Long.valueOf(s));
				i++;
			}
			if (c1.m == -1) {
				parameters.add(Long.valueOf(s));
				i++;
			}
			if (c1._fldbyte == 12)
				try {
					parameters.add(Integer.valueOf(s));
				} catch (NumberFormatException numberformatexception) {
					parameters.add(Boolean.valueOf(s));
				}
			else if (c1._fldbyte == 1)
				try {
					if (s.length() != 1)
						parameters.add(Double.valueOf(s));
					else
						parameters.add(s.charAt(0));
				} catch (NumberFormatException numberformatexception1) {
					parameters.add(s.charAt(0));
				}
			else if (c1._fldbyte == 4 || c1._fldbyte == 3 || c1._fldbyte == 13)
				parameters.add(Long.valueOf(s));
			else if (c1._fldbyte == 8 || c1._fldbyte == 6 || c1._fldbyte == 9)
				parameters.add(Double.valueOf(s));
			else if (c1._fldbyte == 2 || c1._fldbyte == 14) {
				int k = s.length();
				char c2 = s.charAt(k - 1);
				try {
					s = s.substring(0, k - 1);
					if (c2 == 'b')
						parameters.add(Byte.valueOf(s));
					else if (c2 == 's')
						parameters.add(Short.valueOf(s));
					else if (c2 == 'i')
						parameters.add(Integer.valueOf(s));
					else if (c2 == 'l') {
						parameters.add(Long.valueOf(s));
					} else {
						s = s + c2;
						try {
							parameters.add(Integer.valueOf(s));
						} catch (NumberFormatException numberformatexception2) {
							parameters.add(Long.valueOf(s));
						}
					}
				} catch (NumberFormatException numberformatexception3) {
					try {
						parameters.add(Long.valueOf(s));
					} catch (NumberFormatException numberformatexception4) {
						parameters.add(Double.valueOf(s));
					}
				}
			} else {
				parameters.add(s);
			}
		}

		return parameters;
	}


	private static boolean _fldlong = true;

	private static Object _fldnull = new Object();

	private static final double _fldvoid = Math.log(10D);

}

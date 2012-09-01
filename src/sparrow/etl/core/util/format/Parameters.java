package sparrow.etl.core.util.format;

import java.util.Vector;

// Referenced classes of package com.braju.format:
//            ParametersAutoClear

public class Parameters {

	public Parameters() {
		_fldif = false;
		a = new Vector();
		a.addElement(new ParametersAutoClear());
	}

	public Parameters(boolean flag) {
		this();
		add(flag);
	}

	public Parameters(char c) {
		this();
		add(c);
	}

	public Parameters(byte byte0) {
		this();
		add(byte0);
	}

	public Parameters(short word0) {
		this();
		add(word0);
	}

	public Parameters(int i) {
		this();
		add(i);
	}

	public Parameters(long l) {
		this();
		add(l);
	}

	public Parameters(float f) {
		this();
		add(f);
	}

	public Parameters(double d) {
		this();
		add(d);
	}

	public Parameters(String s) {
		this();
		add(s);
	}

	public Parameters(Object obj) {
		this();
		add(obj);
	}

	public Parameters(Vector vector) {
		_fldif = false;
		a = vector;
	}

	public Vector toVector() {
		return a;
	}

	public Parameters add(boolean flag) {
		a.addElement(new Boolean(flag));
		return this;
	}

	public Parameters add(char c) {
		a.addElement(new Character(c));
		return this;
	}

	public Parameters add(byte byte0) {
		a.addElement(new Byte(byte0));
		return this;
	}

	public Parameters add(short word0) {
		a.addElement(new Short(word0));
		return this;
	}

	public Parameters add(int i) {
		a.addElement(new Integer(i));
		return this;
	}

	public Parameters add(long l) {
		a.addElement(new Long(l));
		return this;
	}

	public Parameters add(float f) {
		a.addElement(new Float(f));
		return this;
	}

	public Parameters add(double d) {
		a.addElement(new Double(d));
		return this;
	}

	public Parameters add(String s) {
		a.addElement(s);
		return this;
	}

	public Parameters add(Object obj) {
		a.addElement(obj);
		return this;
	}

	public Parameters autoClear(boolean flag) {
		if (isAutoClear()) {
			if (!flag)
				a.removeElementAt(0);
		} else if (flag)
			a.insertElementAt(new ParametersAutoClear(), 0);
		return this;
	}

	public boolean isAutoClear() {
		return a.firstElement() instanceof ParametersAutoClear;
	}

	public Parameters clear() {
		if (isAutoClear()) {
			a.removeAllElements();
			autoClear(true);
		} else {
			a.removeAllElements();
		}
		return this;
	}

	private Vector a;

	private boolean _fldif;
}

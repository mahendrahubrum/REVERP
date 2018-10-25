package com.webspark.common.util;

import java.io.Serializable;

/**
 * @Author Jinshad P.T.
 */

public class KeyValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7402681344789357671L;
	private long key;
	private long longValue;
	private String value;
	private double doubleValue;
	private char charKey;
	private String stringKey;
	private int intKey;

	public KeyValue(String stringKey, String value) {
		super();
		this.value = value;
		this.stringKey = stringKey;
	}

	public KeyValue(char charKey, String value) {
		super();
		this.value = value;
		this.charKey = charKey;
	}

	public KeyValue(String value) {
		super();
		this.value = value;
	}

	public KeyValue(long key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	public KeyValue(long key, long longValue) {
		super();
		this.key = key;
		this.longValue = longValue;
	}
	
	public KeyValue(long key, double doubleValue) {
		super();
		this.key = key;
		this.setDoubleValue(doubleValue);
	}
	
	public KeyValue(int intKey, String value) {
		super();
		this.value = value;
		this.intKey = intKey;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public char getCharKey() {
		return charKey;
	}

	public void setCharKey(char charKey) {
		this.charKey = charKey;
	}

	public String getStringKey() {
		return stringKey;
	}

	public void setStringKey(String stringKey) {
		this.stringKey = stringKey;
	}

	public int getIntKey() {
		return intKey;
	}

	public void setIntKey(int intKey) {
		this.intKey = intKey;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
}

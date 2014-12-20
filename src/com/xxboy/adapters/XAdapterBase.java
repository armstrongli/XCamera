package com.xxboy.adapters;

public abstract class XAdapterBase {
	public abstract int getResource();

	public abstract String[] getMFrom();

	public abstract int[] getMTo();

	public abstract Object get(String key);
}

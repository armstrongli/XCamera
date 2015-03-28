package com.xxboy.activities.mainview.adapters.xdata;

public abstract class XAdapterBase {
	public abstract int getResource();

	public abstract String[] getMFrom();

	public abstract int[] getMTo();

	public abstract Object get(String key);

	public abstract void set2Resource();

	public abstract void set2Default();

	public abstract int getBackgroundColor();
}

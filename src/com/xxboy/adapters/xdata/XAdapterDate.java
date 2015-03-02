package com.xxboy.adapters.xdata;

import java.util.Map;

import com.xxboy.photo.R;

public class XAdapterDate extends XAdapterBase {

	public static final String ID_ITEM_DATE_MONTH = "id_item_date_month";
	public static final String ID_ITEM_DATE_DAY = "id_item_date_day";
	public static final String ID_ITEM_DATE_YEAR = "id_item_date_year";

	private Map<String, ?> data;
	private int aRGB;

	public XAdapterDate(Map<String, ?> data, int aRGB) {
		super();
		this.data = data;
		this.aRGB = aRGB;
	}

	private static final int[] mTo = { R.id.id_item_date_month, R.id.id_item_date_day, R.id.id_item_date_year };
	private static final String[] mFrom = { ID_ITEM_DATE_MONTH, ID_ITEM_DATE_DAY, ID_ITEM_DATE_YEAR };

	@Override
	public int getResource() {
		return R.layout.xitem_date;
	}

	@Override
	public String[] getMFrom() {
		return mFrom;
	}

	@Override
	public int[] getMTo() {
		return mTo;
	}

	public String getMonth() {
		return (String) this.get(ID_ITEM_DATE_MONTH);
	}

	public String getDay() {
		return (String) this.get(ID_ITEM_DATE_DAY);
	}

	public String getYear() {
		return (String) this.get(ID_ITEM_DATE_YEAR);
	}

	@Override
	public Object get(String key) {
		return this.data.get(key);
	}

	@Override
	public void set2Resource() {
		// throw new RuntimeException("Camera adapter doesn't support this function");
	}

	@Override
	public void set2Default() {
		// throw new RuntimeException("Camera adapter doesn't support this function");
	}

	@Override
	public int getBackgroundColor() {
		return this.aRGB;
	}

}

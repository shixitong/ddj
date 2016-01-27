/*
 * 文件名：TableDataRow.java
 * 版权：
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改内容：
 */
package com.jdy.ddj.common.tools.exportTools;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TableDataRow {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private LinkedList<TableDataCell> cells;

	private TableData table;

	private int rowStyle = TableData.STYLE_TYPE_STRING;

	public void addCell(TableDataCell cell) {
		cells.add(cell);
	}

	public void addCell(String value) {
		TableDataCell cell = new TableDataCell(this);
		cell.setValue(value);
		cell.setCellStyle(rowStyle);
		addCell(cell);
	}

	public void addCell(Integer value) {
		TableDataCell cell = new TableDataCell(this);
		cell.setValue(value);
		cell.setCellStyle(rowStyle);
		addCell(cell);
	}

	public void addCell(Double value) {
		TableDataCell cell = new TableDataCell(this);
		cell.setValue(value);
		cell.setCellStyle(rowStyle);
		addCell(cell);
	}

	public void addCell(Object value) {
		if (value instanceof String) {
			addCell((String) value);
		} else if (value instanceof Integer) {
			addCell((Integer) value);
		} else if (value instanceof Double) {
			addCell((Double) value);
		} else if (value instanceof BigDecimal) {
			addCell(value.toString());
		} else if (value instanceof Long) {
			addCell(value.toString());
		} else if(value == null){
			addCell("");
		} else if(value instanceof Date){
            addCell(dateFormat.format(value));
        }
	}

	public TableDataCell getCellAt(int index) {
		return cells.get(index);
	}

	public List<TableDataCell> getCells() {
		return cells;
	}

	public TableData getTable() {
		return table;
	}

	public TableDataRow(TableData table) {
		cells = new LinkedList<TableDataCell>();
		this.table = table;
	}

	public void setRowStyle(int rowStyle) {
		this.rowStyle = rowStyle;
	}

	public int getRowStyle() {
		return rowStyle;
	}
}

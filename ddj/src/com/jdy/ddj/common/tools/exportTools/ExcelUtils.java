/*
 * 文件名：ExcelUtils.java
 * 版权：
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改内容：
 */
package com.jdy.ddj.common.tools.exportTools;

import com.jdy.ddj.common.utils.Multimedia;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {
	
	/**
	 * JavaBean转Map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {  
        Map<String, Object> params = new HashMap<String, Object>(0);  
        try {  
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();  
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);  
            for (int i = 0; i < descriptors.length; i++) {  
                String name = descriptors[i].getName();  
                if (!StringUtils.equals(name, "class")) {  
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));  
                }
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return params;  
    }
	
	/**
	 * 创建普通表头
	 * @param list 表头名称列表
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(List<String> list){
		 TableHeaderMetaData headMeta = new TableHeaderMetaData();
	        for(String title : list){
	        	TableColumn tc = new TableColumn();
		        tc.setDisplay(title);
		        headMeta.addColumn(tc);
	        }
	        return headMeta;
	}
	
	/**
	 * 创建普通表头
	 * @param titls 表头名称数组
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(String[] titls){
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		for(String title : titls){
			TableColumn tc = new TableColumn();
			tc.setDisplay(title);
			headMeta.addColumn(tc);
		}
		return headMeta;
	}
	
	/**
	 * 创建合并表头
	 * @param parents 父表头数组
	 * @param children 子表头数组
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(String[] parents,String[][] children){
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		TableColumn parentColumn = null;
		TableColumn sonColumn = null;
		for (int i = 0; i < parents.length; i++) {
			parentColumn = new TableColumn();
			parentColumn.setDisplay(parents[i]);
			if (children != null && children[i] != null) {
				for (int j = 0; j < children[i].length; j++) {
					sonColumn = new TableColumn();
					sonColumn.setDisplay(children[i][j]);
					parentColumn.addChild(sonColumn);
				}
			}
			headMeta.addColumn(parentColumn);
		}
		return headMeta;
	}
	
    /**
     * 拼装数据
     * 
     * @param list 数据集
     * @param headMeta 表头
     * @param fields 对象或Map属性数组（注意：顺序要与表头标题顺序对应，如数据集为List<Object[]>，则该参数可以为null）
     * @return TableData
     */
	@SuppressWarnings("unchecked")
	public static TableData createTableData(List list,TableHeaderMetaData headMeta,String[] fields){

        TableData td = new TableData(headMeta);
        TableDataRow row = null;
        if(list != null && list.size()>0){
        	if(list.get(0).getClass().isArray()){//数组类型
                for (Object obj : list){
                	row = new TableDataRow(td);
            		for(Object o : (Object[])obj){
    					row.addCell(o);
    				}
                    td.addRow(row);
                }
        	}else{//JavaBean或Map类型
                for (Object obj : list){
                	row = new TableDataRow(td);
                	Map<String, Object> map = (obj instanceof Map)?(Map<String, Object>)obj:beanToMap(obj);
                    for(String key : fields){
                        row.addCell(map.get(key));
                    }
                    td.addRow(row);
                }
        	}
        }
        return td;
    }
    
	public static void copySheetStyle(HSSFWorkbook destwb, HSSFSheet dest,
			HSSFWorkbook srcwb, HSSFSheet src) {
		if (src == null || dest == null)
			return;

		dest.setAlternativeExpression(src.getAlternateExpression());
		dest.setAlternativeFormula(src.getAlternateFormula());
		dest.setAutobreaks(src.getAutobreaks());
		dest.setDialog(src.getDialog());
		if (src.getColumnBreaks() != null) {
			for (int col : src.getColumnBreaks()) {
				dest.setColumnBreak((short) col);
			}
		}
		dest.setDefaultColumnWidth(src.getDefaultColumnWidth());
		dest.setDefaultRowHeight(src.getDefaultRowHeight());
		dest.setDefaultRowHeightInPoints(src.getDefaultRowHeightInPoints());
		dest.setDisplayGuts(src.getDisplayGuts());
		dest.setFitToPage(src.getFitToPage());
		dest.setHorizontallyCenter(src.getHorizontallyCenter());
		dest.setDisplayFormulas(src.isDisplayFormulas());
		dest.setDisplayGridlines(src.isDisplayGridlines());
		dest.setDisplayRowColHeadings(src.isDisplayRowColHeadings());
		dest.setGridsPrinted(src.isGridsPrinted());
		dest.setPrintGridlines(src.isPrintGridlines());

		for (int i = 0; i < src.getNumMergedRegions(); i++) {
			Region r = src.getMergedRegionAt(i);
			dest.addMergedRegion(r);
		}

		if (src.getRowBreaks() != null) {
			for (int row : src.getRowBreaks()) {
				dest.setRowBreak(row);
			}
		}
		dest.setRowSumsBelow(src.getRowSumsBelow());
		dest.setRowSumsRight(src.getRowSumsRight());

		short maxcol = 0;
		for (int i = 0; i <= src.getLastRowNum(); i++) {
			HSSFRow row = src.getRow(i);
			if (row != null) {
				if (maxcol < row.getLastCellNum())
					maxcol = row.getLastCellNum();
			}
		}
		for (short col = 0; col <= maxcol; col++) {
			if (src.getColumnWidth(col) != src.getDefaultColumnWidth())
				dest.setColumnWidth(col, src.getColumnWidth(col));
			dest.setColumnHidden(col, src.isColumnHidden(col));
		}
	}

	public static String dumpCellStyle(HSSFCellStyle style) {
		StringBuffer sb = new StringBuffer();
		sb.append(style.getHidden()).append(",");
		sb.append(style.getLocked()).append(",");
		sb.append(style.getWrapText()).append(",");
		sb.append(style.getAlignment()).append(",");
		sb.append(style.getBorderBottom()).append(",");
		sb.append(style.getBorderLeft()).append(",");
		sb.append(style.getBorderRight()).append(",");
		sb.append(style.getBorderTop()).append(",");
		sb.append(style.getBottomBorderColor()).append(",");
		sb.append(style.getDataFormat()).append(",");
		sb.append(style.getFillBackgroundColor()).append(",");
		sb.append(style.getFillForegroundColor()).append(",");
		sb.append(style.getFillPattern()).append(",");
		sb.append(style.getIndention()).append(",");
		sb.append(style.getLeftBorderColor()).append(",");
		sb.append(style.getRightBorderColor()).append(",");
		sb.append(style.getRotation()).append(",");
		sb.append(style.getTopBorderColor()).append(",");
		sb.append(style.getVerticalAlignment());

		return sb.toString();
	}

	public static String dumpFont(HSSFFont font) {
		StringBuffer sb = new StringBuffer();
		sb.append(font.getItalic()).append(",").append(font.getStrikeout())
				.append(",").append(font.getBoldweight()).append(",").append(
						font.getCharSet()).append(",").append(font.getColor())
				.append(",").append(font.getFontHeight()).append(",").append(
						font.getFontName()).append(",").append(
						font.getTypeOffset()).append(",").append(
						font.getUnderline());
		return sb.toString();
	}

	public static void copyCellStyle(HSSFWorkbook destwb, HSSFCell dest,
			HSSFWorkbook srcwb, HSSFCell src) {
		if (src == null || dest == null)
			return;

		HSSFCellStyle nstyle = findStyle(src.getCellStyle(), srcwb, destwb);
		if (nstyle == null) {
			nstyle = destwb.createCellStyle();
			copyCellStyle(destwb, nstyle, srcwb, src.getCellStyle());
		}
		dest.setCellStyle(nstyle);
	}

	private static boolean isSameColor(short a, short b, HSSFPalette apalette,
			HSSFPalette bpalette) {
		if (a == b)
			return true;
		HSSFColor acolor = apalette.getColor(a);
		HSSFColor bcolor = bpalette.getColor(b);
		if (acolor == null)
			return true;
		if (bcolor == null)
			return false;
		return acolor.getHexString().equals(bcolor.getHexString());
	}

	private static short findColor(short index, HSSFWorkbook srcwb,
			HSSFWorkbook destwb) {
		Integer id = new Integer(index);
		if (HSSFColor.getIndexHash().containsKey(id))
			return index;
		if (index == HSSFColor.AUTOMATIC.index)
			return index;
		HSSFColor color = srcwb.getCustomPalette().getColor(index);
		if (color == null) {
			return index;
		}

		HSSFColor ncolor = destwb.getCustomPalette().findColor(
				(byte) color.getTriplet()[0], (byte) color.getTriplet()[1],
				(byte) color.getTriplet()[2]);
		if (ncolor != null)
			return ncolor.getIndex();
		destwb.getCustomPalette().setColorAtIndex(index,
				(byte) color.getTriplet()[0], (byte) color.getTriplet()[1],
				(byte) color.getTriplet()[2]);
		return index;
	}

	public static HSSFCellStyle findStyle(HSSFCellStyle style,
			HSSFWorkbook srcwb, HSSFWorkbook destwb) {
		HSSFPalette srcpalette = srcwb.getCustomPalette();
		HSSFPalette destpalette = destwb.getCustomPalette();

		for (short i = 0; i < destwb.getNumCellStyles(); i++) {
			HSSFCellStyle old = destwb.getCellStyleAt(i);
			if (old == null)
				continue;

			if (style.getAlignment() == old.getAlignment()
					&& style.getBorderBottom() == old.getBorderBottom()
					&& style.getBorderLeft() == old.getBorderLeft()
					&& style.getBorderRight() == old.getBorderRight()
					&& style.getBorderTop() == old.getBorderTop()
					&& isSameColor(style.getBottomBorderColor(), old
							.getBottomBorderColor(), srcpalette, destpalette)
					&& style.getDataFormat() == old.getDataFormat()
					&& isSameColor(style.getFillBackgroundColor(), old
							.getFillBackgroundColor(), srcpalette, destpalette)
					&& isSameColor(style.getFillForegroundColor(), old
							.getFillForegroundColor(), srcpalette, destpalette)
					&& style.getFillPattern() == old.getFillPattern()
					&& style.getHidden() == old.getHidden()
					&& style.getIndention() == old.getIndention()
					&& isSameColor(style.getLeftBorderColor(), old
							.getLeftBorderColor(), srcpalette, destpalette)
					&& style.getLocked() == old.getLocked()
					&& isSameColor(style.getRightBorderColor(), old
							.getRightBorderColor(), srcpalette, destpalette)
					&& style.getRotation() == old.getRotation()
					&& isSameColor(style.getTopBorderColor(), old
							.getTopBorderColor(), srcpalette, destpalette)
					&& style.getVerticalAlignment() == old
							.getVerticalAlignment()
					&& style.getWrapText() == old.getWrapText()) {

				HSSFFont oldfont = destwb.getFontAt(old.getFontIndex());
				HSSFFont font = srcwb.getFontAt(style.getFontIndex());
				if (oldfont.getBoldweight() == font.getBoldweight()
						&& oldfont.getItalic() == font.getItalic()
						&& oldfont.getStrikeout() == font.getStrikeout()
						&& oldfont.getCharSet() == font.getCharSet()
						&& isSameColor(oldfont.getColor(), font.getColor(),
								srcpalette, destpalette)
						&& oldfont.getFontHeight() == font.getFontHeight()
						&& oldfont.getFontName().equals(font.getFontName())
						&& oldfont.getTypeOffset() == font.getTypeOffset()
						&& oldfont.getUnderline() == font.getUnderline()) {
					return old;
				}
			}
		}
		return null;
	}

	public static void copyCellStyle(HSSFWorkbook destwb, HSSFCellStyle dest,
			HSSFWorkbook srcwb, HSSFCellStyle src) {
		if (src == null || dest == null)
			return;
		dest.setAlignment(src.getAlignment());
		dest.setBorderBottom(src.getBorderBottom());
		dest.setBorderLeft(src.getBorderLeft());
		dest.setBorderRight(src.getBorderRight());
		dest.setBorderTop(src.getBorderTop());
		dest.setBottomBorderColor(findColor(src.getBottomBorderColor(), srcwb,destwb));
		dest.setDataFormat(destwb.createDataFormat().getFormat(srcwb.createDataFormat().getFormat(src.getDataFormat())));
		dest.setFillPattern(src.getFillPattern());
		dest.setFillForegroundColor(findColor(src.getFillForegroundColor(),srcwb, destwb));
		dest.setFillBackgroundColor(findColor(src.getFillBackgroundColor(),srcwb, destwb));
		dest.setHidden(src.getHidden());
		dest.setIndention(src.getIndention());
		dest.setLeftBorderColor(findColor(src.getLeftBorderColor(), srcwb,destwb));
		dest.setLocked(src.getLocked());
		dest.setRightBorderColor(findColor(src.getRightBorderColor(), srcwb,destwb));
		dest.setRotation(src.getRotation());
		dest.setTopBorderColor(findColor(src.getTopBorderColor(), srcwb,destwb));
		dest.setVerticalAlignment(src.getVerticalAlignment());
		dest.setWrapText(src.getWrapText());

		HSSFFont f = srcwb.getFontAt(src.getFontIndex());
		HSSFFont nf = findFont(f, srcwb, destwb);
		if (nf == null) {
			nf = destwb.createFont();
			nf.setBoldweight(f.getBoldweight());
			nf.setCharSet(f.getCharSet());
			nf.setColor(findColor(f.getColor(), srcwb, destwb));
			nf.setFontHeight(f.getFontHeight());
			nf.setFontHeightInPoints(f.getFontHeightInPoints());
			nf.setFontName(f.getFontName());
			nf.setItalic(f.getItalic());
			nf.setStrikeout(f.getStrikeout());
			nf.setTypeOffset(f.getTypeOffset());
			nf.setUnderline(f.getUnderline());
		}
		dest.setFont(nf);
	}

	private static HSSFFont findFont(HSSFFont font, HSSFWorkbook src,
			HSSFWorkbook dest) {
		for (short i = 0; i < dest.getNumberOfFonts(); i++) {
			HSSFFont oldfont = dest.getFontAt(i);
			if (font.getBoldweight() == oldfont.getBoldweight()
					&& font.getItalic() == oldfont.getItalic()
					&& font.getStrikeout() == oldfont.getStrikeout()
					&& font.getCharSet() == oldfont.getCharSet()
					&& font.getColor() == oldfont.getColor()
					&& font.getFontHeight() == oldfont.getFontHeight()
					&& font.getFontName().equals(oldfont.getFontName())
					&& font.getTypeOffset() == oldfont.getTypeOffset()
					&& font.getUnderline() == oldfont.getUnderline()) {
				return oldfont;
			}
		}
		return null;
	}

	public static void copySheet(HSSFWorkbook destwb, HSSFSheet dest,
			HSSFWorkbook srcwb, HSSFSheet src) {
		if (src == null || dest == null)
			return;

		copySheetStyle(destwb, dest, srcwb, src);

		for (int i = 0; i <= src.getLastRowNum(); i++) {
			HSSFRow row = src.getRow(i);
			copyRow(destwb, dest.createRow(i), srcwb, row);
		}
	}

	public static void copyRow(HSSFWorkbook destwb, HSSFRow dest,
			HSSFWorkbook srcwb, HSSFRow src) {
		if (src == null || dest == null)
			return;
		for (short i = 0; i <= src.getLastCellNum(); i++) {
			if (src.getCell(i) != null) {
				HSSFCell cell = dest.createCell(i);
				copyCell(destwb, cell, srcwb, src.getCell(i));
			}
		}

	}

	public static void copyCell(HSSFWorkbook destwb, HSSFCell dest,
			HSSFWorkbook srcwb, HSSFCell src) {
		if (src == null) {
			dest.setCellType(HSSFCell.CELL_TYPE_BLANK);
			return;
		}

		if (src.getCellComment() != null)
			dest.setCellComment(src.getCellComment());
		if (src.getCellStyle() != null) {
			HSSFCellStyle nstyle = findStyle(src.getCellStyle(), srcwb, destwb);
			if (nstyle == null) {
				nstyle = destwb.createCellStyle();
				copyCellStyle(destwb, nstyle, srcwb, src.getCellStyle());
			}
			dest.setCellStyle(nstyle);
		}
		dest.setCellType(src.getCellType());

		switch (src.getCellType()) {
		case HSSFCell.CELL_TYPE_BLANK:

			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			dest.setCellValue(src.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			dest.setCellFormula(src.getCellFormula());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			dest.setCellErrorValue(src.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			dest.setCellValue(src.getNumericCellValue());
			break;
		default:
			dest.setCellValue(new HSSFRichTextString(src
					.getRichStringCellValue().getString()));
			break;
		}
	}













    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     * @param sheet 要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow 结束行
     * @param firstCol   开始列
     * @param endCol  结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet,
                                              String[] textlist, int firstRow, int endRow, int firstCol,
                                              int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }


    /****
     * 小数控制
     * @param sheet
     * @param min
     * @param max
     * @param firstRow
     * @param endRow
     * @param firstCol
     * @param endCol
     * @return
     */
    public static HSSFSheet setHSSFDecimalValidation(HSSFSheet sheet,
                                                     String min, String max, int firstRow, int endRow, int firstCol,
                                                     int endCol) {
        //小数限制
        DVConstraint constraint = DVConstraint.createNumericConstraint(DataValidationConstraint.ValidationType.DECIMAL,
                DataValidationConstraint.OperatorType.BETWEEN,min,max);

        DVConstraint constraint2 = DVConstraint.createNumericConstraint(DataValidationConstraint.ValidationType.DECIMAL,
                DataValidationConstraint.OperatorType.BETWEEN,min,max);

        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);

        sheet.addValidationData(data_validation_list);
        return sheet;
    }


    /****
     * 整数控制
     * @param sheet
     * @param min
     * @param max
     * @param firstRow
     * @param endRow
     * @param firstCol
     * @param endCol
     * @return
     */
    public static HSSFSheet setHSSFIntegerValidation(HSSFSheet sheet,
                                                     String min, String max, int firstRow, int endRow, int firstCol,
                                                     int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createNumericConstraint(DataValidationConstraint.ValidationType.INTEGER,
                DataValidationConstraint.OperatorType.BETWEEN,min,max);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }



    /**
     * 设置单元格上提示
     * @param sheet  要设置的sheet.
     * @param promptTitle 标题
     * @param promptContent 内容
     * @param firstRow 开始行
     * @param endRow  结束行
     * @param firstCol  开始列
     * @param endCol  结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
                                          String promptContent, int firstRow, int endRow ,int firstCol,int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("BB1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow,firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_view = new HSSFDataValidation(regions,constraint);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }



    /**
     * 读取Excel表格表头的内容
     * @param sheet
     * @return String 表头内容的数组
     */
    public static String[] readExcelTitle(HSSFSheet sheet) {

        HSSFRow row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        System.out.println("colNum:" + colNum);
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            //title[i] = getStringCellValue(row.getCell((short) i));
            title[i] = getCellFormatValue(row.getCell((short) i));
        }
        return title;
    }



    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    public static String getStringCellValue(HSSFCell cell) {
        String strCell = "";
        if(null != cell){
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_STRING:
                    strCell = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    strCell = String.valueOf(cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    strCell = String.valueOf(cell.getBooleanCellValue());
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    strCell = "";
                    break;
                default:
                    strCell = "";
                    break;
            }
            if (strCell.equals("") || strCell == null) {
                return "";
            }
            if (cell == null) {
                return "";
            }
        }
        return strCell;
    }

    /**
     * 获取单元格数据内容为日期类型的数据
     *
     * @param cell
     *            Excel单元格
     * @return String 单元格数据内容
     */
    public static String getDateCellValue(HSSFCell cell) {
        String result = "";
        if(null != cell){
            try {
                int cellType = cell.getCellType();
                if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                    Date date = cell.getDateCellValue();
                    result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
                            + "-" + date.getDate();
                } else if (cellType == HSSFCell.CELL_TYPE_STRING) {
                    String date = getStringCellValue(cell);
                    result = date.replaceAll("[年月]", "-").replace("日", "").trim();
                } else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
                    result = "";
                }
            } catch (Exception e) {
                System.out.println("日期格式不正确!");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 根据HSSFCell类型设置数据
     * @param cell
     * @return
     */
    public static String getCellFormatValue(HSSFCell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC:
                case HSSFCell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式

                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();

                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = sdf.format(date);

                    }
                    // 如果是纯数字
                    else {
                        // 取得当前Cell的数值
                        cellvalue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }



    public static void export(HttpServletResponse response,HttpServletRequest request,List dataList,String[] colTitle,String[] colNames,String fileName) throws Exception{

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //生成表头
        Row row0 = sheet.createRow(0);

        for(int i=0;i<colTitle.length;i++){
            row0.createCell(i).setCellValue(colTitle[i]);
        }

        for(int i=0;i<dataList.size();i++) {
            Object obj=dataList.get(i);
            Row row = sheet.createRow(i + 1);
            if(obj instanceof Map){
                for(int j=0;j<colNames.length;j++){
                    String name=colNames[j];
                    row.createCell(j).setCellValue( getStr(((Map)obj).get(name)) );
                }
            }else{
                for(int j=0;j<colNames.length;j++){
                    String name=colNames[j];
                    row.createCell(j).setCellValue( getStr(BeanUtils.getProperty(obj, name)) );
                }
            }

        }

        setFileDownloadHeader(response, request, fileName + ".xls", fileName + ".xls");
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
    }

    public static String getStr(Object s){
        return s==null ? "" : s.toString();
    }


    public static void setFileDownloadHeader(HttpServletResponse response,
                                             HttpServletRequest request,
                                             String fileName,
                                             String saveName) throws UnsupportedEncodingException {

        String ext = FilenameUtils.getExtension(fileName);
        String mine_type = Multimedia.mime_types.get(ext);

        if (mine_type != null) {
            response.setContentType(mine_type);
        }
        String ua = request.getHeader("user-agent");

        String new_filename = URLEncoder.encode(saveName, "UTF8");
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的

        if (ua != null && ua.indexOf("Firefox") >= 0) {
            response.setHeader("Content-Disposition", "attachment; filename*=\"UTF-8''" + new_filename + "\"");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(saveName, "UTF-8"));
        }
    }






}

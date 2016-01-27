package com.jdy.ddj.common.tools.importTools;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 该类实现了将一组对象转换为Excel表格，并且可以从Excel表格中读取到一组List对象中
 * 该类利用了BeanUtils框架中的反射完成
 * 使用该类的前提，在相应的实体对象上通过ExcelReources来完成相应的注解
 * @author Administrator
 *
 */
@SuppressWarnings({"rawtypes"})
public class ExcelUtil {
    private DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static ExcelUtil eu = new ExcelUtil();
	private ExcelUtil(){}
	
	public static ExcelUtil getInstance() {
		return eu;
	}
	/**
	 * 处理对象转换为Excel
	 * @param template
	 * @param objs
	 * @param clz
	 * @param isClasspath
	 * @return
	 */
	private ExcelTemplate handlerObj2Excel (String template,List objs,Class clz,boolean isClasspath)  {
		ExcelTemplate et = ExcelTemplate.getInstance();
		try {
			if(isClasspath) {
				et.readTemplateByClasspath(template);
			} else {
				et.readTemplateByPath(template);
			}
			List<ExcelHeader> headers = getHeaderList(clz);
			Collections.sort(headers);
			//输出标题
			et.createNewRow();
			for(ExcelHeader eh:headers) {
				et.createCell(eh.getTitle());
			}
			//输出值
			for(Object obj:objs) {
				et.createNewRow();
				for(ExcelHeader eh:headers) {
//				Method m = clz.getDeclaredMethod(mn);
//				Object rel = m.invoke(obj);
					et.createCell(BeanUtils.getProperty(obj,getMethodName(eh) ));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return et;
	}
	/**
	 * 根据标题获取相应的方法名称
	 * @param eh
	 * @return
	 */
	private String getMethodName(ExcelHeader eh) {
		String mn = eh.getMethodName().substring(3);
		mn = mn.substring(0,1).toLowerCase()+mn.substring(1);
		return mn;
	}
	/**
	 * 将对象转换为Excel并且导出，该方法是基于模板的导出，导出到流
	 * @param datas 模板中的替换的常量数据
	 * @param template 模板路径
	 * @param os 输出流
	 * @param objs 对象列表
	 * @param clz 对象的类型
	 * @param isClasspath 模板是否在classPath路径下
	 */
	public void exportObj2ExcelByTemplate(Map<String,String> datas,String template,OutputStream os,List objs,Class clz,boolean isClasspath) {
		ExcelTemplate et = handlerObj2Excel(template, objs, clz, isClasspath);
		et.replaceFinalData(datas);
		et.wirteToStream(os);
	}
	/**
	 * 将对象转换为Excel并且导出，该方法是基于模板的导出，导出到一个具体的路径中
	 * @param datas 模板中的替换的常量数据
	 * @param template 模板路径
	 * @param objs 对象列表
	 * @param clz 对象的类型
	 * @param isClasspath 模板是否在classPath路径下
	 */
	public void exportObj2ExcelByTemplate(Map<String,String> datas,String template,String outPath,List objs,Class clz,boolean isClasspath) {
		ExcelTemplate et = handlerObj2Excel(template, objs, clz, isClasspath);
		et.replaceFinalData(datas);
		et.writeToFile(outPath);
	}
	
	/**
	 * 将对象转换为Excel并且导出，该方法是基于模板的导出，导出到流,基于Properties作为常量数据
	 * @param prop 基于Properties的常量数据模型
	 * @param template 模板路径
	 * @param os 输出流
	 * @param objs 对象列表
	 * @param clz 对象的类型
	 * @param isClasspath 模板是否在classPath路径下
	 */
	public void exportObj2ExcelByTemplate(Properties prop,String template,OutputStream os,List objs,Class clz,boolean isClasspath) {
		ExcelTemplate et = handlerObj2Excel(template, objs, clz, isClasspath);
		et.replaceFinalData(prop);
		et.wirteToStream(os);
	}
	/**
	 * 将对象转换为Excel并且导出，该方法是基于模板的导出，导出到一个具体的路径中,基于Properties作为常量数据
	 * @param prop 基于Properties的常量数据模型
	 * @param template 模板路径
	 * @param objs 对象列表
	 * @param clz 对象的类型
	 * @param isClasspath 模板是否在classPath路径下
	 */
	public void exportObj2ExcelByTemplate(Properties prop,String template,String outPath,List objs,Class clz,boolean isClasspath) {
		ExcelTemplate et = handlerObj2Excel(template, objs, clz, isClasspath);
		et.replaceFinalData(prop);
		et.writeToFile(outPath);
	}
	
	private Workbook handleObj2Excel(List objs,Class clz,boolean isXssf) {
		Workbook wb = null;
		try {
			if(isXssf) {
				wb = new XSSFWorkbook();
			} else {
				wb = new HSSFWorkbook();
			}
			Sheet sheet = wb.createSheet();
			Row r = sheet.createRow(0);
			List<ExcelHeader> headers = getHeaderList(clz);
			Collections.sort(headers);
			//写标题
			for(int i=0;i<headers.size();i++) {
				r.createCell(i).setCellValue(headers.get(i).getTitle());
			}
			//写数据
			Object obj = null;
			for(int i=0;i<objs.size();i++) {
				r = sheet.createRow(i+1);
				obj = objs.get(i);
				for(int j=0;j<headers.size();j++) {
					r.createCell(j).setCellValue(BeanUtils.getProperty(obj, getMethodName(headers.get(j))));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return wb;
	}
	/**
	 * 导出对象到Excel，不是基于模板的，直接新建一个Excel完成导出，基于路径的导出
	 * @param outPath 导出路径
	 * @param objs 对象列表
	 * @param clz 对象类型
	 * @param isXssf 是否是2007版本
	 */
	public void exportObj2Excel(String outPath,List objs,Class clz,boolean isXssf) {
		Workbook wb = handleObj2Excel(objs, clz, isXssf);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outPath);
			wb.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos!=null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 导出对象到Excel，不是基于模板的，直接新建一个Excel完成导出，基于流
	 * @param os 输出流
	 * @param objs 对象列表
	 * @param clz 对象类型
	 * @param isXssf 是否是2007版本
	 */
	public void exportObj2Excel(OutputStream os,List objs,Class clz,boolean isXssf) {
		try {
			Workbook wb = handleObj2Excel(objs, clz, isXssf);
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从类路径读取相应的Excel文件到对象列表
	 * @param path 类路径下的path
	 * @param clz 对象类型
	 * @param readLine 开始行，注意是标题所在行
	 * @param tailLine 底部有多少行，在读入对象时，会减去这些行
	 * @return
	 */
	public List<Object> readExcel2ObjsByClasspath(String path,Class clz,int readLine,int tailLine) {
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(ExcelUtil.class.getResourceAsStream(path));
			return handlerExcel2Objs(wb, clz, readLine,tailLine);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 从文件路径读取相应的Excel文件到对象列表
	 * @param path 文件路径下的path
	 * @param clz 对象类型
	 * @param readLine 开始行，注意是标题所在行
	 * @param tailLine 底部有多少行，在读入对象时，会减去这些行
	 * @return
	 */
	public List<Object> readExcel2ObjsByPath(String path,Class clz,int readLine,int tailLine) {
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(new File(path));
			return handlerExcel2Objs(wb, clz, readLine,tailLine);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 从文件路径读取相应的Excel文件到对象列表
     * @param path 文件路径下的path
     * @param clz 对象类型
     * @param readLine 开始行，注意是标题所在行 - 标题行
     * @param dataLine 实际数据开始行，注意是标题所在行
     * @param tailLine 底部有多少行，在读入对象时，会减去这些行
     * @return
     */
    public List<Object> readExcel2ObjsByPath(String path,Class clz,int readLine,int dataLine,int tailLine) {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(path));
            return handlerExcel2Objs(wb, clz, readLine, dataLine, tailLine);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件路径读取相应的Excel文件到对象列表
     * @param path 文件路径下的path
     * @param clz 对象类型
     * @param readLine 开始行，注意是标题所在行 - 标题行
     * @param dataLine 实际数据开始行，注意是标题所在行
     * @param tailLine 底部有多少行，在读入对象时，会减去这些行
     * @return ExcelDataVo
     */
    public ExcelDataVo readExcel2VoByPath(String path,Class clz,int readLine,int dataLine,int tailLine) {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(path));
            return handlerExcel2Vo(wb, clz, readLine, dataLine, tailLine);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String[] readExcelFirstCells(String path,int rowIndex) {
        String[] values = {"",""};
        try {
            Workbook wb = WorkbookFactory.create(new File(path));
            Sheet sheet = wb.getSheetAt(0);
            try {
                Row row = sheet.getRow(rowIndex);
                values[0] = row.getCell(0).getStringCellValue();
                values[1] = row.getCell(1).getStringCellValue();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("要读取的Excel的格式不正确，检查是否是要导入的Excel或者Excel格式是否正确！");
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }
	/**
	 * 从类路径读取相应的Excel文件到对象列表，标题行为0，没有尾行
	 * @param path 路径
	 * @param clz 类型
	 * @return 对象列表
	 */
	public List<Object> readExcel2ObjsByClasspath(String path,Class clz) {
		return this.readExcel2ObjsByClasspath(path, clz, 0,0);
	}
	/**
	 * 从文件路径读取相应的Excel文件到对象列表，标题行为0，没有尾行
	 * @param path 路径
	 * @param clz 类型
	 * @return 对象列表
	 */
	public List<Object> readExcel2ObjsByPath(String path,Class clz) {
		return this.readExcel2ObjsByPath(path, clz,0,0);
	}
	
	public String getCellValue(Cell c) {
        if(c==null){
            return "";
        }
        String o = null;
		switch (c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			o = ""; break;
		case Cell.CELL_TYPE_BOOLEAN:
			o = String.valueOf(c.getBooleanCellValue()); break;
		case Cell.CELL_TYPE_FORMULA:
			o = String.valueOf(c.getCellFormula()); break;
		case Cell.CELL_TYPE_NUMERIC:
            o = String.valueOf(c.getNumericCellValue()); break;
		case Cell.CELL_TYPE_STRING:
			o = c.getStringCellValue(); break;
		default:
			o = null;
			break;
		}
		return o;
	}
	
	private List<Object> handlerExcel2Objs(Workbook wb,Class clz,int readLine,int tailLine) {
		Sheet sheet = wb.getSheetAt(0);
		List<Object> objs = null;
		try {
			Row row = sheet.getRow(readLine);
			objs = new ArrayList<Object>();
			Map<Integer,String> maps = getHeaderMap(row, clz);
			if(maps==null||maps.size()<=0) throw new RuntimeException("要读取的Excel的格式不正确，检查是否设定了合适的行");
			for(int i=readLine+1;i<=sheet.getLastRowNum()-tailLine;i++) {
				row = sheet.getRow(i);
				Object obj = clz.newInstance();
                for(int j = 0;j<maps.size();j++) {  //不使用row来遍历，是因为可能在标题列以外这一行有空字符串的情况。
                    Cell c = row.getCell(j);
					String mn = maps.get(j).substring(3);
					mn = mn.substring(0,1).toLowerCase()+mn.substring(1);
                    if("mType".equals(mn)) mn = "MType";
                    if(c == null){
                        BeanUtils.copyProperty(obj,mn,"");
                    }else if(c.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = c.getDateCellValue();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else if(c.getCellType() == Cell.CELL_TYPE_BLANK && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = new Date();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else{
                        String cellValue = this.getCellValue(c);
                        BeanUtils.copyProperty(obj,mn,cellValue.trim());
                    }
				}
				objs.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
            throw new RuntimeException("要读取的Excel的格式不正确，检查是否是要导入的Excel或者Excel格式是否正确！");
		}
		return objs;
	}

    private List<Object> handlerExcel2Objs(Workbook wb,Class clz,int readLine,int dataLine,int tailLine) {
        Sheet sheet = wb.getSheetAt(0);
        List<Object> objs = null;
        try {
            Row row = sheet.getRow(readLine);
            objs = new ArrayList<Object>();
            Map<Integer,String> maps = getHeaderMap(row, clz);
            if(maps==null||maps.size()<=0) throw new RuntimeException("要读取的Excel的格式不正确，检查是否设定了合适的行");
            for(int i=dataLine;i<=sheet.getLastRowNum()-tailLine;i++) {
                row = sheet.getRow(i);
                Object obj = clz.newInstance();
                for(int j = 0;j<maps.size();j++) {  //不使用row来遍历，是因为可能在标题列以外这一行有空字符串的情况。
                    Cell c = row.getCell(j);
                    String mn = maps.get(j).substring(3);
                    mn = mn.substring(0,1).toLowerCase()+mn.substring(1);
                    if(c == null){
                        BeanUtils.copyProperty(obj,mn,"");
                    }else if(c.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = c.getDateCellValue();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else if(c.getCellType() == Cell.CELL_TYPE_BLANK && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = new Date();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else{
                        String cellValue = this.getCellValue(c);
                        BeanUtils.copyProperty(obj,mn,cellValue.trim());
                    }
                }
                objs.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("要读取的Excel的格式不正确，检查是否是要导入的Excel或者Excel格式是否正确！");
        }
        return objs;
    }

    private ExcelDataVo handlerExcel2Vo(Workbook wb,Class clz,int readLine,int dataLine,int tailLine) {
        Sheet sheet = wb.getSheetAt(0);
        List<Object> objs = null;
        ExcelDataVo excelVo = null;
        try {
            Row row = sheet.getRow(readLine);
            excelVo = new ExcelDataVo();
            excelVo.setTitle(row.getCell(0).getStringCellValue());
            excelVo.setCode(row.getCell(1).getStringCellValue());
            row = sheet.getRow(readLine + 1);
            objs = new ArrayList<Object>();
            Map<Integer,String> maps = getHeaderMap(row, clz);
            if(maps==null||maps.size()<=0) throw new RuntimeException("要读取的Excel的格式不正确，检查是否设定了合适的行");
            for(int i=dataLine;i<=sheet.getLastRowNum()-tailLine;i++) {
                row = sheet.getRow(i);
                Object obj = clz.newInstance();
                for(int j = 0;j<maps.size();j++) {  //不使用row来遍历，是因为可能在标题列以外这一行有空字符串的情况。
                    Cell c = row.getCell(j);
                    String mn = maps.get(j).substring(3);
                    mn = mn.substring(0,1).toLowerCase()+mn.substring(1);
                    if(c == null){
                        BeanUtils.copyProperty(obj,mn,"");
                    }else if(c.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = c.getDateCellValue();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else if(c.getCellType() == Cell.CELL_TYPE_BLANK && HSSFDateUtil.isCellDateFormatted(c)){
                        Date d = new Date();
                        BeanUtils.copyProperty(obj,mn,d);
                    }else{
                        String cellValue = this.getCellValue(c);
                        BeanUtils.copyProperty(obj,mn,cellValue.trim());
                    }
                }
                objs.add(obj);
            }
            excelVo.setData(objs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("要读取的Excel的格式不正确，检查是否是要导入的Excel或者Excel格式是否正确！");
        }
        return excelVo;
    }

    private Map<String,List<Object>> handlerExcel2Map(Workbook wb,Class clz,int readLine,int tailLine) {
        Map<String,List<Object>> maps = new HashMap<String,List<Object>>();

        return maps;
    }

	private List<ExcelHeader> getHeaderList(Class clz) {
		List<ExcelHeader> headers = new ArrayList<ExcelHeader>();
		Method[] ms = clz.getDeclaredMethods();
		for(Method m:ms) {
			String mn = m.getName();
			if(mn.startsWith("get")) {
				if(m.isAnnotationPresent(ExcelResources.class)) {
					ExcelResources er = m.getAnnotation(ExcelResources.class);
					headers.add(new ExcelHeader(er.title(),er.order(),mn));
				}
			}
		}
		return headers;
	}
	
	private Map<Integer,String> getHeaderMap(Row titleRow,Class clz) {
		List<ExcelHeader> headers = getHeaderList(clz);
		Map<Integer,String> maps = new HashMap<Integer, String>();
		for(Cell c:titleRow) {
			String title = c.getStringCellValue();
			for(ExcelHeader eh:headers) {
				if(eh.getTitle().equals(title.trim())) {
					maps.put(c.getColumnIndex(), eh.getMethodName().replace("get","set"));
					break;
				}
			}
		}
		return maps;
	}
}

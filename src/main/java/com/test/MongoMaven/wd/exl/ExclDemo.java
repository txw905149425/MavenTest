//package com.test.MongoMaven.wd.exl;
//
//import java.io.FileOutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
//import org.apache.poi.ss.usermodel.Cell;  
//import org.apache.poi.ss.usermodel.CellStyle;  
//import org.apache.poi.ss.usermodel.DataFormat;  
//import org.apache.poi.ss.usermodel.Row;  
//import org.apache.poi.ss.usermodel.Sheet;  
//import org.apache.poi.ss.usermodel.Workbook;  
//import org.apache.poi.ss.util.CellRangeAddress;  
//
//public class ExclDemo {
//	public static void main(String[] args) throws Exception {  
//        //创建一个EXCEL  
//        Workbook wb = new HSSFWorkbook();  
//        DataFormat format = wb.createDataFormat();  
//        CellStyle style;  
//        //创建一个SHEET  
//        Sheet sheet1 = wb.createSheet("产品清单");  
//        String[] title = {"编号","产品名称","产品价格","产品数量","生产日期","产地","是否出口"};  
//        int i=0;  
//        //创建一行  
//        Row row = sheet1.createRow((short)0);  
//        //填充标题  
//        for (String  s:title){  
//            Cell cell = row.createCell(i);  
//            cell.setCellValue(s);  
//            i++;  
//        }  
//        Row row1 = sheet1.createRow((short)1);  
//        //下面是填充数据  
//        row1.createCell(0).setCellValue(20071001);  
//        row1.createCell(1).setCellValue("金鸽瓜子");  
//        //创建一个单元格子  
//        Cell cell2=row1.createCell(2);  
//        // 填充产品价格  
//        cell2.setCellValue(2.45);  
//        style = wb.createCellStyle();  
//        style.setDataFormat(format.getFormat("#.##"));  
//        //设定样式  
//        cell2.setCellStyle(style);  
//        // 填充产品数量  
//        row1.createCell(3).setCellValue(200);  
//        /*   
//         * 定义显示日期的公共格式   
//         * 如:yyyy-MM-dd hh:mm   
//         * */  
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");     
//        String newdate = sdf.format(new Date());   
//        // 填充出产日期     
//        row1.createCell(4).setCellValue(newdate);  
//        row1.createCell(5).setCellValue("陕西西安");  
//        /*   
//         * 显示布尔值   
//         * */   
//        row1.createCell(6).setCellValue(true);  
//        /*   
//         * 合并单元格   
//         * 通过writablesheet.mergeCells(int x,int y,int m,int n);来实现的   
//         * 表示将first row, last row,first column,last column 
//         *    
//         * */    
//        Row row2 = sheet1.createRow((short) 2);  
//         Cell cell3 = row2.createCell((short) 0);  
//         cell3.setCellValue("合并了三个单元格");  
//        sheet1.addMergedRegion(new CellRangeAddress(2,2,0,2));  
//          
//        FileOutputStream fileOut = new FileOutputStream("test.xls");  
//        wb.write(fileOut);  
//        fileOut.close();  
//    }  
//	
//}

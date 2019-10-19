package com.lxkj.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.util.ID;
import com.lxkj.common.util.Strings;
import com.lxkj.entity.Order;
import com.lxkj.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Slf4j
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

 public  void exportExcel(String title,InputStream input , List<Order> dataset, String[] keyHeaders, OutputStream out) throws Exception{
//  FileInputStream input = new FileInputStream(file);
  try (input; Workbook workbook= WorkbookFactory.create(input);) {
   // 生成一个表格
   Sheet sheet = workbook.getSheetAt(0);
   Drawing<?> patriarch = sheet.getDrawingPatriarch();
   if (dataset != null&&dataset.size()>0) {
     Iterator<Order> it = dataset.iterator();
      int index = 0;
      Row row = null;
     while (it.hasNext()) {
     index++;
     row = sheet.createRow(index);
     Order t = (Order) it.next();
     Field[] fields = t.getClass().getDeclaredFields();
     for (short i = 0; i < keyHeaders.length; i++) {
      Cell cell = row.createCell(i);
      String fieldName = keyHeaders[i];
      String getMethodName = "get"
              + fieldName.substring(0, 1).toUpperCase()
              + fieldName.substring(1);
      Object value = null;
         if (t instanceof Map) {
       Map map = (Map) t;
       value = map.get(keyHeaders[i]);
          } else {
            Class tCls = t.getClass();
            Method getMethod = tCls.getMethod(getMethodName,
               new Class[]{});
            value = getMethod.invoke(t, new Object[]{});
         }

      // 判断值的类型后进行强制类型转换
      String textValue = null;
      if(value!=null) {
       if (value instanceof Integer) {
        int intValue = (Integer) value;
        cell.setCellValue(intValue);
        //} else if (value instanceof Float) {
        //float fValue = (Float) value;
        // textValue = new HSSFRichTextString(
        // String.valueOf(fValue));
        // cell.setCellValue(textValue);
        // } else if (value instanceof Double) {
        // double dValue = (Double) value;
        // textValue = new HSSFRichTextString(
        // String.valueOf(dValue));
        // cell.setCellValue(textValue);
       } else if (value instanceof Long) {
        long longValue = (Long) value;
        cell.setCellValue(longValue);
       }
       if (value instanceof Boolean) {
        boolean bValue = (Boolean) value;
        textValue = "男";
        if (!bValue) {
         textValue = "女";
        }
       } else if (value instanceof Date) {
        Date date = (Date) value;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        textValue = sdf.format(date);
       } else if (value instanceof byte[]) {
        // 有图片时，设置行高为60px;
        row.setHeightInPoints(60);
        // 设置图片所在列宽度为80px,注意这里单位的一个换算
        sheet.setColumnWidth(i, (short) (35.7 * 80));
        // sheet.autoSizeColumn(i);
        byte[] bsValue = (byte[]) value;
        ClientAnchor anchor = new HSSFClientAnchor(0, 0,
                1023, 255, (short) 6, index, (short) 6, index);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
        patriarch.createPicture(anchor, workbook.addPicture(
                bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
       } else {
        // 其它数据类型都当作字符串简单处理
        textValue = value.toString();
       }
      }
      // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
      if (textValue != null) {
       Pattern p = Pattern.compile("^//d+(//.//d+)?$");
       Matcher matcher = p.matcher(textValue);
       if (matcher.matches()) {
        // 是数字当作double处理
        cell.setCellValue(Double.parseDouble(textValue));
       } else {
        HSSFRichTextString richString = new HSSFRichTextString(
                textValue);
        Font font3 = workbook.createFont();
        font3.setFontHeight(Short.parseShort("250"));
        font3.setColor(HSSFColor.BLACK.index);
        richString.applyFont(font3);
        cell.setCellValue(richString);
       }
      }


     }
    }

   }
      workbook.write(out);
  }catch(Exception e ){
      e.printStackTrace();
      }

  }

    @Transactional
    public String batchImport(InputStream input, String userId) {
        String serial = Strings.of(ID.nextSnowflakeId());//导入批次
        // 完成任务后自动关闭输入流
        try (input; Workbook workbook = WorkbookFactory.create(input)) {
            List<Order> orders = new ArrayList<>();
            // 循环处理所有Sheet页
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) {
                    continue;
                }
                // 循环行
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    // 取数
                    Cell sequence = row.getCell(0);
                    Cell deliveryTrack = row.getCell(1);
                    // 简单验证
                    if (formatCell(sequence) == null || formatCell(deliveryTrack) == null ) {
                        log.warn("第{}行数据无法识别：找不到物流订单号、运单号", rowIndex);
                        continue;
                    }
                    if (Strings.equals(formatCell(sequence), "物流订单号") && Strings.equals(formatCell(deliveryTrack), "运单号")) {
                        continue;
                    }

                    Order order = this.getOne(Wrappers.<Order>query().eq("sequence",formatCell(sequence).trim()));
                    if(order==null){
                        log.warn("第{}行数据无法识别：通过此物流订单号，找不到订单信息", rowIndex);
                        continue;
                    }else{
                        order.setDeliveryTrack(formatCell(deliveryTrack).trim());
                        order.setStatus(3);
                        order.setDeliveryProvider("圆通速递");
                        order.setImportTime(new Date());
                        orders.add(order);
                    }
                }
            }
            // 批量更新
            if (!orders.isEmpty()) {
                this.updateBatchById(orders);
            }
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return null;
        }
        // 返回批次号
        return serial;
    }


    private String formatCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        String cellValue = "";
        switch (cell.getCellTypeEnum()) {
            case NUMERIC: // 数字
                //short s = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf;
                    // 验证short值
                    if (cell.getCellStyle().getDataFormat() == 14) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (cell.getCellStyle().getDataFormat() == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (cell.getCellStyle().getDataFormat() == 22) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else {
                        throw new RuntimeException("日期格式错误!!!");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 0) {//处理数值格式
                    cell.setCellType(CellType.STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                }
                break;
            case STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK: // 空值
                cellValue = null;
                break;
            case ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
   }
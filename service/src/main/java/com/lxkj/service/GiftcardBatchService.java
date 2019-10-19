package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.util.ID;
import com.lxkj.common.util.Strings;
import com.lxkj.entity.Giftcard;
import com.lxkj.entity.GiftcardBatch;
import com.lxkj.mapper.GiftcardBatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 卡片批量导入 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Slf4j
@Service
public class GiftcardBatchService extends ServiceImpl<GiftcardBatchMapper, GiftcardBatch> {
 @Autowired
 private GiftcardService giftcardService;

 @Transactional
 public String batchImport(InputStream input,String userId) {
  String sequence = Strings.of(ID.nextSnowflakeId());
  // 完成任务后自动关闭输入流
  try (input; Workbook workbook = WorkbookFactory.create(input)) {
   List<GiftcardBatch> batch = new LinkedList<>();
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
     Cell serial = row.getCell(0);
     Cell passcode = row.getCell(1);
     // 简单验证
     if (formatCell(serial) == null || formatCell(passcode) == null ) {
      log.warn("第{}行数据无法识别：找不到卡号、密码", rowIndex);
      continue;
     }
     if (Strings.equals(formatCell(serial), "卡号") && Strings.equals(formatCell(passcode), "密码")) {
      continue;
     }
     // 创建导入记录
     GiftcardBatch bean = new GiftcardBatch();
     bean.setId(ID.nextGUID());
     bean.setSequence(sequence);
     bean.setSerial(formatCell(serial));
     bean.setPasscode(formatCell(passcode));
     bean.setStatus(1);
     bean.setCreateBy(userId);
     bean.setCreateTime(new Date());

     // 加入列表备用
     batch.add(bean);
    }
   }
   // 根据导入数据生成用户和账号
   List<Giftcard> patients = new ArrayList<>(batch.size());
   for (GiftcardBatch e : batch) {
    String  serial = e.getSerial();
    // 查找是否已有账号
    int count = this.giftcardService.count(
            new QueryWrapper<Giftcard>().eq("serial", serial)
    );
    if (count > 0) {
     e.setStatus(-1);
     e.setReason("卡号已注册");
     continue;
    }
    Giftcard patient = new Giftcard();
    patient.setId(ID.nextGUID());
    patient.setSerial(e.getSerial());
    patient.setPasscode(e.getPasscode());
    patient.setStatus(1);
    patient.setCreateTime(new Date());

    patients.add(patient);

   }
   // 批量更新
   if (!batch.isEmpty()) {
    this.saveBatch(batch);
    this.giftcardService.saveBatch(patients);
   }
  } catch (Exception ex) {
   log.error(ex.getLocalizedMessage(), ex);
   return null;
  }
  // 返回批次号
  return sequence;
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
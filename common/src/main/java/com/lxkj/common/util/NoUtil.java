package com.lxkj.common.util;

public class NoUtil {



     public static String getNewEquipmentNo(String equipmentType, String equipmentNo){
        String newEquipmentNo = "00001";
        if(equipmentNo != null && !equipmentNo.isEmpty()){
            int newEquipment = Integer.parseInt(equipmentNo) + 1;
            newEquipmentNo = String.format(equipmentType + "%05d", newEquipment);
        }
        return newEquipmentNo;
    }

    public static void main(String[] args) {
        String equipmentNo = NoUtil.getNewEquipmentNo("SYXH", "3");
        System.out.println("生成设备编号：" + equipmentNo);
    }

}

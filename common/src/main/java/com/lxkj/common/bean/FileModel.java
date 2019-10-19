package com.lxkj.common.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileModel implements Serializable {
    private static final long serialVersionUID = 5889536391513225307L;
    private Integer code = 0;
    private String fileUrl;//访问地址
    private String fileName;//文件名称
    private String filePath;//文件路径

}

package xifu.com.file.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 文件系统对应的实体类
 * @auth wq on 2019/1/18 18:40
 **/
@Data
@Table(name = "tb_file_info")
public class FileInfo {
    @Id
    private String fileId; // varchar(32) NOT NULL COMMENT '文件id',
    private String fileName; // varchar(255) NOT NULL COMMENT '文件名称',
    private String fileExt; // varchar(500) DEFAULT NULL COMMENT '文件后缀',
    private String fileMime; // varchar(500) DEFAULT NULL COMMENT '文件类型',
    private String originalName; // varchar(500) DEFAULT NULL COMMENT '文件类型',
}

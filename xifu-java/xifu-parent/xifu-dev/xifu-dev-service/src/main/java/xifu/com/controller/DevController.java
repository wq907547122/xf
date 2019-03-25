package xifu.com.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.service.DevService;
import xifu.com.service.impl.DevServiceImpl;

/**
 * 设备的controller层
 * @auth wq on 2019/3/19 10:48
 **/
@RestController
@Slf4j
public class DevController {

    @Autowired
    private DevService devService;

    /**
     * 点表导入
     * @param file 导入的file文件
     * @return
     */
    @PostMapping("point")
    public ResponseEntity<Void> importPoint(@RequestParam("file") MultipartFile file) {
        try {
            // 解析文件
            devService.importPoint(file);
        }catch (XiFuException e) {
            log.error("[点表导入] 验证错误：", e);
            throw e;
        } catch (Exception e2) {
            log.error("[点表导入] 解析错误：", e2);
            // 点表导入解析错误
            throw new XiFuException(ExceptionEnum.POINT_IMPORT_PARSE_ERROR);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

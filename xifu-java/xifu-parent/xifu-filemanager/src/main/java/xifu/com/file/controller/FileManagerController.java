package xifu.com.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xifu.com.file.service.FileInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件系统的controller层
 * @auth wq on 2019/1/18 18:45
 **/
@RestController
@RequestMapping("fileManager")
public class FileManagerController {
    @Autowired
    private FileInfoService fileInfoService;

    /**
     * 文件上传
     * @param request
     * @return
     */
    @PostMapping("upload")
    public ResponseEntity<String> fileUpload(HttpServletRequest request) {
        return ResponseEntity.ok(fileInfoService.fileUpload(request));
    }

    /**
     * 下载文件
     * @param fileId
     * @return
     */
    @GetMapping("downloadFile")
    public ResponseEntity<Void> downloadFile(@RequestParam("fileId") String fileId, HttpServletResponse response){
        fileInfoService.downloadFile(fileId, response);
        return ResponseEntity.ok().build();
    }
}

package xifu.com.file.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.file.config.FileInfoProp;
import xifu.com.file.mapper.FileInfoMapper;
import xifu.com.file.pojo.FileInfo;
import xifu.com.utils.XifuRandomUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @auth wq on 2019/1/18 18:44
 **/
@Slf4j
@Service
@EnableConfigurationProperties(FileInfoProp.class)
public class FileInfoService {
    @Autowired
    private FileInfoProp prop;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    /**
     * 文件上传
     * @param request
     * @return
     */
    public String fileUpload(HttpServletRequest request) {
        String filePath = getFilePath(); // 获取文件的路径
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        String fileId = request.getParameter("fileId");
        // 根据fileId查询图片名称
        String fileName = null;
        boolean isNeedInsertDB = true;// 默认需要查询文件属性数据
        String oldFileExt = "";
        String oldFileName = "";
        if(StringUtils.isBlank(fileId)) {
            fileName = XifuRandomUtils.getUUIDString().replaceAll("-", "");
            fileId = XifuRandomUtils.getUUIDString().replaceAll("-", "");
        } else { // 根据fileId
            FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId);
            if(fileInfo == null || StringUtils.isBlank(fileInfo.getFileName())){
                fileName = XifuRandomUtils.getUUIDString().replaceAll("-", "");
                fileId = XifuRandomUtils.getUUIDString().replaceAll("-", "");
            } else {
                fileName = fileInfo.getFileName();
                oldFileExt = fileInfo.getFileExt();
                oldFileName = fileName + "." + oldFileExt;
                isNeedInsertDB = false;// 当数据库中存在该文件时不需要重复入库
            }
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultiValueMap<String, MultipartFile> allFiles = multipartRequest.getMultiFileMap();
        try {
            String fileMime = null;
            String fileExt = null;
            String originalName = null;
            for (List<MultipartFile> fileList : allFiles.values()) {
                if (fileList != null && fileList.size() > 0) {
                    MultipartFile mfile = fileList.get(0);
                    if (mfile.getSize() > prop.getFileMaxSize()) {
                        throw new XiFuException(ExceptionEnum.FILE_TOO_LARGE);
                    }
                    String originalFilename = mfile.getOriginalFilename();
                    if (!StringUtils.isEmpty(originalFilename)) {
                        String[] filenames = originalFilename.split("[.]");
                        originalName = filenames[0];
                        fileExt = filenames.length > 1 ? filenames[filenames.length - 1] : "";
                        fileMime = mfile.getContentType();
                        String saveFilePath = StringUtils.isEmpty(fileExt) ? (filePath
                                + "/" + fileName) : (filePath + "/" + fileName + "." + fileExt);
                        mfile.transferTo(new File(saveFilePath));
                        if (isNeedInsertDB) {// 需要新增文件数据
                            FileInfo fileM = new FileInfo();
                            fileM.setFileId(fileId);
                            fileM.setFileName(fileName);
                            fileM.setFileExt(fileExt);
                            fileM.setFileMime(fileMime);
                            fileM.setOriginalName(originalName);
                            this.fileInfoMapper.insertSelective(fileM);
                        } else {// 存在fileId，但两次上传文件类型不一致，需要更新信息
                            FileInfo fileM = new FileInfo();
                            fileM.setFileId(fileId);
                            fileM.setFileName(fileName);
                            fileM.setFileExt(fileExt);
                            fileM.setFileMime(fileMime);
                            fileM.setOriginalName(originalName);
                            this.fileInfoMapper.updateByPrimaryKeySelective(fileM);
                        }
                    }
                    if(!fileExt.equals(oldFileExt)){
                        File oldFile = new File(filePath + "/" + oldFileName);
                        oldFile.delete();
                    }
                }
            }
        } catch (Exception e){
            log.error("[文件上传] 上传文件失败", e);
            if(e instanceof XiFuException) {
                throw (XiFuException)e;
            }
            throw new XiFuException(ExceptionEnum.FILE_UPLOAD_FAILED);
        }
        return fileId;
    }

    /**
     * 判断当前系统是否是windows
     * @return
     */
    private boolean isWindows() { // 判断是否是windows系统
        return System.getProperty("os.name", "").toLowerCase().startsWith("win");
    }

    /**
     * 文件下载
     * @param fileId
     * @param response
     */
    public void downloadFile(String fileId, HttpServletResponse response) {
        if(StringUtils.isBlank(fileId)) {
            log.error("[文件下载] 参数错误");
            return;
        }
        String filePath = getFilePath();
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId);
        if(fileInfo == null) {
            log.info("file is not exist");
            return;
        }
        String filePathM = null;
        String originalName = null;
        if (StringUtils.isEmpty(fileInfo.getFileExt())) {
            filePathM = filePath + "/" + fileInfo.getFileName();
            originalName = fileInfo.getFileName();
        } else {
            filePathM = filePath + "/" + fileInfo.getFileName() + "." + fileInfo.getFileExt();
            originalName = fileInfo.getOriginalName() + "." + fileInfo.getFileExt();
        }
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=\"" + originalName + "\"");
        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(response.getOutputStream());
            byte[] fileByte = file2Bytes(filePathM);
            outStream.write(fileByte);
        } catch (Exception e) {
            log.error("download file filed.");
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    log.error("close io stream error.");
                }
            }
        }
    }
    /**
     * 文件转换为字节
     *
     * @param fileSrc
     *            文件路径
     * @return 字节
     * @throws Exception
     */
    public byte[] file2Bytes(String fileSrc) throws Exception {
        FileInputStream fis = new FileInputStream(new File(fileSrc));
        byte[] bytes = new byte[fis.available()];
        // 将文件内容写入字节数组，提供测试的case
        fis.read(bytes);
        fis.close();
        return bytes;
    }

    /**
     * 获取文件保存的路径
     * @return
     */
    private String getFilePath() {
        if(isWindows()) { // 如果是windows
            return prop.getWindowPath();
        }
        return prop.getLinuxPath();
    }
}

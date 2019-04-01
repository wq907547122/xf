package xifu.com.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 设备的service接口
 * @auth wq on 2019/3/22 15:55
 **/
public interface DevService {
    /**
     * 点表导入
     * @param file
     * @throws Exception
     */
    void importPoint(MultipartFile file) throws Exception;

    /**
     * 根据id删除点表信息
     * @param id
     */
    void deleteById(Long id);
}

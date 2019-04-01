package xifu.com.service.impl;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.DevModelVersionMapper;
import xifu.com.pojo.devService.DevModelVersion;
import xifu.com.vo.DevModelVersionRequest;

import java.util.List;

/**
 * @auth wq on 2019/3/25 14:18
 **/
@Service
@Slf4j
public class DevModelVersionService {
    @Autowired
    private DevModelVersionMapper devModelVersionMapper;

    /**
     * 查询点表的分页信息，只查询当前的第一级的版本
     * @param devModelVersionRequest
     * @return
     */
    public List<DevModelVersion> getModelList(DevModelVersionRequest devModelVersionRequest) {
        // 使用分页助手
        PageHelper.startPage(devModelVersionRequest.getPage(), devModelVersionRequest.getPageSize());
        Example example = new Example(DevModelVersion.class);
        example.createCriteria().andIsNull("parentId"); // 父节点是空的，即是第一级的
        List<DevModelVersion> devModelVersions = this.devModelVersionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(devModelVersions)) {
            log.warn("[版本信息查询] 不存在版本新");
            throw new XiFuException(ExceptionEnum.DEV_MODEL_VERSION_NOT_FOUND);
        }
        return devModelVersions;
    }
}

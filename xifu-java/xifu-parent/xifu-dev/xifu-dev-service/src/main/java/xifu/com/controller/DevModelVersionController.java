package xifu.com.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xifu.com.cache.DevModelsCache;
import xifu.com.pojo.devService.DevModelVersion;
import xifu.com.service.DevService;
import xifu.com.service.impl.DevModelVersionService;
import xifu.com.vo.DevModelVersionRequest;
import xifu.com.vo.PageInfoResult;

import java.util.List;

/**
 * 请求导入的版本的controller
 * @auth wq on 2019/3/25 14:16
 **/
@RestController
@RequestMapping("devVersion")
public class DevModelVersionController {
    @Autowired
    private DevModelVersionService devModelVersionService;

    @Autowired
    private DevService devService;
    @Autowired
    private DevModelsCache devModelsCache;

    /**
     * 查询当前版本的分页信息,只查询第一级的，不查询下面的子版本
     * @param devModelVersionRequest
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageInfoResult<DevModelVersion>> getModelList(DevModelVersionRequest devModelVersionRequest){
        List<DevModelVersion> modelList = devModelVersionService.getModelList(devModelVersionRequest);
        PageInfo<DevModelVersion> pageInfo = new PageInfo<>(modelList);
        PageInfoResult<DevModelVersion> result = new PageInfoResult<>();
        return ResponseEntity.ok(result.setList(pageInfo.getList()).setTotal(pageInfo.getTotal()));
    }

    /**
     * 根据父版本的id删除数据
     * @param id
     * @return
     */
    @DeleteMapping("ph/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        devService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 重新设置缓存信息,避免数据错误导致的
     * @return
     */
    @GetMapping("cache")
    public ResponseEntity<Void> resetDevCache() {
        devModelsCache.init();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

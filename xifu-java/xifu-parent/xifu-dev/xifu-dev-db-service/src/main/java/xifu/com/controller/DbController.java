package xifu.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xifu.com.pojo.Inverter;
import xifu.com.service.DbService;
import xifu.com.vo.PageInfoResult;

import java.util.List;

/**
 * @auth wq on 2019/1/7 18:50
 **/
@RestController
public class DbController {
    @Autowired
    private DbService dbService;

    /**
     * 获取列表数据
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Inverter>> queryList(){
        List<Inverter> list = dbService.queryAll();
        return ResponseEntity.ok(list);
    }

    /**
     * 获取分页数据
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageInfoResult<Inverter>> queryPage(
            @RequestParam(name ="page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "key", defaultValue = "", required = false) String key){
        return ResponseEntity.ok(dbService.queryByPage(key, page, pageSize));
    }

    /**
     * 查询滚动信息
     * @param page
     * @param pageSize
     * @param key
     * @param scrollId
     * @return
     */
        @GetMapping("scroll")
    public ResponseEntity<PageInfoResult<Inverter>> scrollPage(
            @RequestParam(name ="page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "key", defaultValue = "", required = false) String key,
            @RequestParam(name = "scrollId", defaultValue = "", required = false) String scrollId){
        return ResponseEntity.ok(dbService.queryByPageScorll(key, page, pageSize, scrollId));
    }
}

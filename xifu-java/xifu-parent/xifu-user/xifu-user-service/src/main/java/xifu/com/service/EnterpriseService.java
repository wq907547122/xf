package xifu.com.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import xifu.com.constant.RedisConstants;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.EnterpriseMapper;
import xifu.com.pojo.Enterprise;
import xifu.com.pojo.User;
import xifu.com.utils.JsonUtils;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 企业表对应实体类的service层
 * @auth wq on 2019/1/17 9:35
 **/
@Service
public class EnterpriseService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    public List<Enterprise> findAll() {
        List<Enterprise> enterprises = enterpriseMapper.selectAll();
        if(CollectionUtils.isEmpty(enterprises)) {
            throw new XiFuException(ExceptionEnum.ENTERPRISE_NOT_FOUND);
        }
        return enterprises;
    }

    /**
     * 新增企业，并且保存企业的企业用户
     * @param enterprise
     * @param user
     * @param request
     */
    @Transactional // 事务的同时成功和失败
    public void saveEnterprise(Enterprise enterprise, User user, HttpServletRequest request) {
        // 获取当前的登录用户
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if(StringUtils.isBlank(tokenId)) { // 无操作权限
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
        String authInfo = redisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(authInfo)) { // 登录已经过期或者无权限，就返回无权限的操作
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        AuthUserInfo authUserInfo = JsonUtils.jsonToPojo(authInfo, AuthUserInfo.class);
        if(authUserInfo == null) {
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        // 1保存企业
        enterprise.setCreateUserId(authUserInfo.getId());
        enterprise.setCreateDate(new Date());
        enterprise.setDeviceLimit(-1);
        enterprise.setUserLimit(-1);
        int i = enterpriseMapper.insertSelective(enterprise);
        if(i != 1 || enterprise.getId() == null) {
            throw new XiFuException(ExceptionEnum.ADD_ENTERPRISE_FAILED);
        }
        // 2保存企业用户
        user.setUserType((byte)0);
        user.setStatus((byte)0);
        user.setGender('0'); // 性别，默认是男
        user.setEnterpriseId(enterprise.getId());
        user.setCreateUserId(authUserInfo.getId());
        user.setNiceName(enterprise.getName() + "企业管理员"); // 昵称 企业名称 + '企业管理员'
        userService.insertUser(user);
        if(user.getId() == null) {
            throw new XiFuException(ExceptionEnum.ADD_ENTERPRISE_FAILED);
        }
        // 3添加用户与角色的关系表
        userService.insertUserRole(user.getId());
    }

    /**
     * 查询企业的分页信息
     * @param page
     * @param pageSize
     * @param enterprise
     * @return
     */
    public List<Enterprise> queryPage(Integer page, Integer pageSize, Enterprise enterprise) {
        PageHelper.startPage(page == null ? 1 : page, pageSize == null ? 10 :pageSize);
        Example example = new Example(Enterprise.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(enterprise.getName())) {
            criteria.andLike("name", "%" + enterprise.getName().trim() + "%");
        }
        if(StringUtils.isNotBlank(enterprise.getAddress())) {
            criteria.andLike("address", "%" + enterprise.getAddress().trim() + "%");
        }
        if(StringUtils.isNotBlank(enterprise.getContactPhone())) {
            criteria.andLike("contactPhone", "%" + enterprise.getContactPhone().trim() + "%");
        }
        if(StringUtils.isNotBlank(enterprise.getContactPeople())) {
            criteria.andLike("contactPeople", "%" + enterprise.getContactPeople().trim() + "%");
        }
        List<Enterprise> list = this.enterpriseMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)) {
            throw new XiFuException(ExceptionEnum.ENTERPRISE_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据id获取企业信息
     * @param id
     * @return
     */
    public Enterprise getById(Long id) {
        Enterprise enterprise = enterpriseMapper.selectByPrimaryKey(id);
        if(enterprise == null) {
            throw new XiFuException(ExceptionEnum.ENTERPRISE_NOT_FOUND);
        }
        return enterprise;
    }

    /**
     * 更新企业信息
     * @param enterprise
     * @param request
     */
    public void updateEnterprise(Enterprise enterprise, HttpServletRequest request) {
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if(StringUtils.isBlank(tokenId)) { // 无操作权限
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
        String authInfo = redisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(authInfo)) { // 登录已经过期或者无权限，就返回无权限的操作
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        AuthUserInfo authUserInfo = JsonUtils.jsonToPojo(authInfo, AuthUserInfo.class);
        if(authUserInfo == null) {
            throw new XiFuException(ExceptionEnum.NO_AUTH);
        }
        enterprise.setModifyUserId(authUserInfo.getId());
        enterprise.setModifyDate(new Date());
        enterpriseMapper.updateByPrimaryKeySelective(enterprise);
    }

    /**
     * 根据用户id获取当前用户所在的企业，正常是返回一个，这里就用list来获取
     * @param id :用户id
     * @return
     */
    public List<Enterprise> findByUserId(Long id) {
        List<Enterprise> enterprises = enterpriseMapper.findByUserId(id);
        if(CollectionUtils.isEmpty(enterprises)) {
            throw new XiFuException(ExceptionEnum.ENTERPRISE_NOT_FOUND);
        }
        return enterprises;
    }
}

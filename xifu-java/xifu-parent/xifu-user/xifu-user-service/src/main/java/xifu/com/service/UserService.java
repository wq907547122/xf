package xifu.com.service;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import xifu.com.constant.RedisConstants;
import xifu.com.dto.UserRequest;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.RoleMapper;
import xifu.com.mapper.RoleResourceMapper;
import xifu.com.mapper.UserMapper;
import xifu.com.pojo.User;
import xifu.com.utils.CodecUtil;
import xifu.com.utils.ExcelUtil;
import xifu.com.utils.JsonUtils;
import xifu.com.utils.XifuRandomUtils;
import xifu.com.vo.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户的service
 * @auth wq on 2019/1/3 16:04
 **/
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    /**
     * 查询所有用户
     * @return
     */
    public List<User> findAll() {
        List<User> users = userMapper.selectAll();
        if(CollectionUtils.isEmpty(users)) { // 没有查询到抛出404的错误
            throw new XiFuException(ExceptionEnum.USER_NOT_FOUND);
        }
        return users;
    }

    /**
     * 获取分页数据
     * @param page
     * @param pageSize
     * @return
     */
    public List<User> findPage(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize); // 启用分页的助手
        User user = new User();
        List<User> users = userMapper.select(user);
        if(CollectionUtils.isEmpty(users)) { // 没有查询到抛出404的错误
            throw new XiFuException(ExceptionEnum.USER_NOT_FOUND);
        }
        return users;
    }

    /**
     * 用户登录
     * @param name 登录名
     * @param password 密码
     * @return 返回存储的tokenId信息
     */
    public String login(String name, String password) {
        AuthUserInfo saveInfo = getAuthUserInfo(name, password);
        String useJson = JsonUtils.toJson(saveInfo); // 转换为字符串
        String tokenId = XifuRandomUtils.getUUIDString(); // 获取uuid的字符串
        String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
        redisTemplate.opsForValue().set(key, useJson, RedisConstants.TOKEN_EXPIRED, TimeUnit.MILLISECONDS);
        return tokenId;
    }

    /**
     * 获取用户信息
     * @param name
     * @param password
     * @return
     */
    private AuthUserInfo getAuthUserInfo(String name, String password) {
        if(StringUtils.isBlank(name)) { // 验证用户名是否为空
            throw new XiFuException(ExceptionEnum.USER_NAME_EMPTY);
        }
        if(StringUtils.isBlank(password)) { // 验证用户密码是否为空
            throw new XiFuException(ExceptionEnum.USER_PASSWORD_EMPTY);
        }
        // 根据用户名查询用户
        User search = new User();
        search.setLoginName(name);
        User user = userMapper.selectOne(search);
        // 1.查询用户是否存在
        if(user == null) {
            log.error("[用户信息查询] 不存在用户");
            throw new XiFuException(ExceptionEnum.USER_NAME_OR_PASSWORD_ERROR);
        }
        // 2.验证密码是否正确
        if(!StringUtils.equals(user.getPassword(), CodecUtil.md5Hex(password, user.getSalt()))) {
            log.error("[用户信息查询] 用户密码不正确");
            throw new XiFuException(ExceptionEnum.USER_NAME_OR_PASSWORD_ERROR);
        }
        // 查询角色
        // saveInfo需要保存到redis的用户信息：包括 用户id，用户对应的角色id 用户角色对应的权限id，用户对应权限的资源路径
        AuthUserInfo saveInfo = new AuthUserInfo();
        saveInfo.setId(user.getId());
        saveInfo.setUserType(user.getUserType()); // 用户类型，判断具体是 2超级管理员？ 0企业用户？ 1普通用户？
        saveInfo.setEnterpriseId(user.getEnterpriseId()); // 用户所属的角色
        saveInfo.setLoginName(user.getLoginName()); // 登录账号
        saveInfo.setNiceName(user.getNiceName()); // 登录账号的昵称
        List<Long> roleIds = userMapper.getRoleIds(user.getId());
        // 查询权限
        if(!CollectionUtils.isEmpty(roleIds)) {
            saveInfo.setRoleIds(roleIds); // 设置角色信息
            List<Long> authIdsByRoleIds = userMapper.getAuthIdsByRoleIds(roleIds);
            // 查询资源
            if(!CollectionUtils.isEmpty(authIdsByRoleIds)) {
                saveInfo.setAuthIds(authIdsByRoleIds); // 设置对应的权限
                List<String> resourceByAuthIds = userMapper.findResourceByAuthIds(authIdsByRoleIds);
                saveInfo.setResourceIds(resourceByAuthIds);
            }
        }
        return saveInfo;
    }


    /**
     * 注销用户
     * @param request
     */
    public void logout(HttpServletRequest request) {
        String tokenId = request.getHeader(RedisConstants.REQUEST_TOKEN_ID);
        if(StringUtils.isNotBlank(tokenId)) {
            String key = RedisConstants.PREFIX_LOGIN_USER_TOKEN + tokenId;
            String uStr = redisTemplate.opsForValue().get(key);
            if(StringUtils.isNotBlank(uStr)){ // 注销
                redisTemplate.delete(key);
            }
        }
    }

    /**
     * 验证用户的信息,判断数据库中是否存在
     * @param data 验证的数据
     * @param type 验证的数据类型 1：用户名验证
     * @return true: 验证通过  false：验证未通过
     */
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1: // 验证用户名是否存在
                user.setLoginName(data);
                break;
            default: // 无效的用户信息
                throw new XiFuException(ExceptionEnum.INVALID_USER_PARAM);
        }
        return userMapper.selectCount(user) == 0;
    }

    /**
     * 新增企业时候的企业用户
     * @param user
     */
    public int insertUser(User user){
        // 1.设置密码
        String salt = CodecUtil.generaSalt(); // 获取盐值
        user.setSalt(salt);
        user.setPassword(CodecUtil.md5Hex(user.getPassword(), salt));
        user.setCreateDate(new Date());
        return userMapper.insertSelective(user);
    }

    /**
     * 新增用户和角色的中间表，主要是创建企业的时候创建的,企业用户角色的角色id固定是2
     * @param userId
     */
    public void insertUserRole(Long userId){
        userMapper.insertUserOfRole(userId);
    }

    public List<User> findUserByPage(UserRequest user) {
        // 启用分页助手
        PageHelper.startPage(user.getPage(), user.getPageSize());
        // 查询数据
        List<User> users = null;
        switch (user.getUserType()) {
            case 0: // 企业用户
            case 1: // 普通用户
                users = this.userMapper.findEnterpriseUsers(user);
                break;
            case 2: // 超级管理员，管理全部
                users = this.userMapper.findAllUsers(user);
                break;
            default:
                throw new XiFuException(ExceptionEnum.INVALID_USER_PARAM);
        }
        if(CollectionUtils.isEmpty(users)) {
            throw new XiFuException(ExceptionEnum.USER_NOT_FOUND);
        }
        // 查询用户对应的角色名称
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        // 根据用户id获取对应的角色名称
        List<Map<String, Object>> userRoleMap = this.userMapper.findUserRoleNameByUserIds(userIds);
        // <用户id，角色名称的集合>
        Map<Long, List<String>> roleListMap = new HashMap<>();
        for (Map<String, Object> stringObjectMap : userRoleMap) {
            Long userId = Long.valueOf(stringObjectMap.get("userId").toString());
            if(!roleListMap.containsKey(userId)) {
                roleListMap.put(userId, new ArrayList<>());
            }
            roleListMap.get(userId).add(stringObjectMap.get("name").toString());
        }
        for (User u : users) { // 变量设置用户的角色名称
            u.setRoleNames(roleListMap.get(u.getId()).stream().collect(Collectors.joining(",")));
        }
        return users;
    }

    /**
     * 用户管理的新增用户
     * @param user
     */
    @Transactional // 统一事务提交
    public void saveUser(User user) {
        // 0.验证数据信息
        if(user.getEnterpriseId() == null) {
            log.error("[新增用户] 无企业信息");
            throw new XiFuException(ExceptionEnum.ADD_USER_FAILED);
        }
        if(StringUtils.isBlank(user.getRoleIds())) { // 如果没有角色，就不能创建
            log.error("[新增用户] 无角色信息");
            throw new XiFuException(ExceptionEnum.ADD_USER_FAILED);
        }
        // 1.新增用户
        int count = insertUser(user);
        if (count != 1) {
            log.error("[新增用户] 新增用户失败");
            throw new XiFuException(ExceptionEnum.ADD_USER_FAILED);
        }
        // 2.新增用户与角色的关系
        saveUserRole(user.getId(), user.getRoleIds(), ExceptionEnum.ADD_USER_FAILED);
        // TODO 3.新增用户与电站的关系
    }

    private void saveUserRole(Long userId, String roleIds, ExceptionEnum exceptionEnum) {
        List<Long> roleIdList = Arrays.asList(roleIds.split(","))
                .stream().map(item -> Long.valueOf(item)).collect(Collectors.toList());
        int count = this.userMapper.insertUserToRoles(userId, roleIdList);
        if (count != roleIdList.size()) {
            log.error("新增用户和角色之间关系表失败");
            throw new XiFuException(exceptionEnum);
        }
    }

    /**
     * 根据用户id获取用户
     * @param id
     * @return
     */
    public User getUserById(Long id) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if(user == null) {
            throw new XiFuException(ExceptionEnum.USER_NOT_FOUND);
        }
        // 查询用户对应的角色信息
        String roleIds = this.userMapper.findUserRoleIdByUserId(id).stream().
                map(item -> item.toString()).collect(Collectors.joining(","));
        user.setRoleIds(roleIds);
        return user;
    }

    /**
     * 更新用户信息
     * @param user
     */
    @Transactional
    public void updateUser(User user) {
        // 1.验证用户
        if(StringUtils.isBlank(user.getRoleIds())) {
            log.error("[修改用户] 无角色信息");
            throw new XiFuException(ExceptionEnum.EDITOR_USER_FAILED);
        }
        // 2.修改用户
        this.userMapper.updateByPrimaryKeySelective(user);
        // 3.修改用户与角色的关系
        // 3.1 删除原来的用户与角色的关系
        this.userMapper.deleteUserRole(user.getId());
        // 3.2 保存用户与角色的关系
        saveUserRole(user.getId(), user.getRoleIds(), ExceptionEnum.EDITOR_USER_FAILED);
        // TODO 4.修改用户与电站关系
    }

    /**
     * 根据id删除用户
     * @param id
     */
    @Transactional
    public void deleteUserById(Long id) {
        // 1.验证
        if(id == 1) { // 超级管理员
            throw new XiFuException(ExceptionEnum.CAN_NOT_DEL_ADMIN_USER);
        }
        // 2.删除用户
        int count = this.userMapper.deleteByPrimaryKey(id);
        if(count != 1) {
            throw new XiFuException(ExceptionEnum.DEL_USER_FAILED);
        }
        // 3.删除用户与角色的关系
        this.userMapper.deleteUserRole(id);
        // TODO 4.删除对应的绑定的电站关系
    }

    /**
     * 获取用户的信息
     * @param username
     * @param password
     * @return
     */
    public AuthUserInfo queryUser(String username, String password) {
        return this.getAuthUserInfo(username, password);
    }

    /**
     * 解析excel
     * @param file
     */
    public void uploadExcel(MultipartFile file) {
        ExcelUtil excelUtil = null;
        try {
            excelUtil = new ExcelUtil(file);
            List<List<String>> userList = excelUtil.read(0, 1); // 读取第一个sheet页
            TimeUnit.SECONDS.sleep(15); // 休眠15s
            for(List<String> u : userList) {
                String collect = u.stream().collect(Collectors.joining(",")); // 转换为以逗号隔开的数据
                log.info("row -->{}" , collect);
            }
            excelUtil.close(); // 关闭流
        } catch (Exception e) {
            log.error("parse excel failed:", e);
            throw new XiFuException(ExceptionEnum.INVALID_ROLE_PARAM);
        }
    }
}

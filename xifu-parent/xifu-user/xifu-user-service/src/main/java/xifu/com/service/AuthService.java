package xifu.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.mapper.AuthMapper;
import xifu.com.pojo.Auth;

import java.util.List;

/**
 * 权限表对应的service层
 * @auth wq on 2019/1/24 10:57
 **/
@Service
public class AuthService {
    @Autowired
    private AuthMapper authMapper;

    /**
     * 根据用户类型查询权限
     * @param userId 用户id
     * @param userType 0:企业用户 1:注册用户 2：系统管理员
     * @return
     */
    public List<Auth> queryAuthByUserType(Long userId, Byte userType) {
        List<Auth> list = null;
        switch (userType) { // 根据用户类型判断查询的数据
            case 0: // 企业用户
            case 1: // 普通用户
                list = this.authMapper.findAuthByUserId(userId);
                break;
            case 2: // 超级管理员
                list = this.authMapper.selectAll();
                break;
            default: // 不存在的用户角色
                throw new XiFuException(ExceptionEnum.INVALID_ROLE_PARAM);
        }
        if(CollectionUtils.isEmpty(list)) {
            throw new XiFuException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        return list;
    }
}

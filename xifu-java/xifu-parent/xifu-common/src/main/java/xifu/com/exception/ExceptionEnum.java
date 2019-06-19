package xifu.com.exception;

/**
 * 自定义异常的码和信息
 * @auth wq on 2019/1/3 16:09
 **/
public enum ExceptionEnum {
    USER_NOT_FOUND(404, "用户没有找到"),
    INVERTER_NOT_FOUND(404, "逆变器性能数据没有查询!"),
    USER_NAME_OR_PASSWORD_ERROR(404, "用户名或密码错误"),
    USER_NAME_EMPTY(404, "用户名为空"),
    USER_PASSWORD_EMPTY(404, "用户名密码为空"),
    ROLE_NOT_FOUND(404, "角色不存在"),
    ROLE_HAS_BIND_USER(400, "角色存在绑定用户"),
    ADD_ROLE_FAILED(400, "新增角色失败"),
    ADD_USER_FAILED(400, "新增用户失败"),
    DEL_USER_FAILED(400, "删除用户失败"),
    CAN_NOT_DEL_ADMIN_USER(500, "不能删除超级管理员"),
    EDITOR_USER_FAILED(400, "修改用户失败"),
    ENTERPRISE_NOT_FOUND(404, "企业不存在"),
    STATION_NOT_FOUND(404, "电站不存在"),
    STATION_FOUND_MULT(500, "存在多个电站"),
    CREATE_STATION_ERROR(400, "创建电站失败"),
    EDIT_STATION_ERROR(400, "修改电站失败"),
    DEV_TYPE_ERROR(400, "设备类型错误"),
    DOMAIN_NOT_FOUND(404, "区域不存在"),
    DOMAIN_EXSIT_CHILDREN_FOUND(400, "区域存在子区域，不能删除"),
    INVALID_USER_PARAM(400, "无效的用户参数"),
    INVALID_ROLE_PARAM(400, "无效的参数信息"),
    INVALID_PARAM(400, "无效的参数信息"),
    AUTH_NOT_FOUND(404, "角色信息不存在"),
    NO_AUTH(401, "无操作权限"),
    ADD_ENTERPRISE_FAILED(400, "新增企业失败"),
    FILE_TOO_LARGE(400, "文件过大"),
    FILE_UPLOAD_FAILED(400, "文件上传失败"),
    POINT_IMPORT_PARSE_ERROR(500, "点表解析错误"),
    DEV_MODEL_VERSION_NOT_FOUND(404, "版本信息不存在"),
    DEV_MODEL_VERSION_SIGNAL_NOT_FOUND(404, "版本信号点不存在"),
    SAVE_NORMALIZED_PARAM_ERROR(400, "保存归一化的配置信息参数错误"),
    SAVE_NORMALIZED_FALED(400, "保存归一化版本适配失败"),
    ;
    private int code;
    private String msg;
    private ExceptionEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

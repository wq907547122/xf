package xifu.com.menus;

import java.util.List;

/**
 * 当前系统支持的协议类型的枚举
 * @auth wq on 2019/3/19 11:30
 **/
public enum ProtocolEnum {
    P_104("104"), // 104协议
    SN_MODBUS("SNMODBUS"), // 上能modbus
    MQTT("MQTT"), // mqtt协议
    ;
    private String code;

    private ProtocolEnum (String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 判断当前枚举是否包含给的的字符串
     * @param code 需要验证的字符串
     * @return
     */
    public static boolean contains(String code) {
        for (ProtocolEnum c : ProtocolEnum.values()) {
            if (c.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据字符串获取对应的枚举类型
     * @param code
     * @return
     */
    public static ProtocolEnum getEnumByCode(String code) {
        for (ProtocolEnum c : ProtocolEnum.values()) {
            if (c.getCode().equals(code)) {
                return c;
            }
        }
        return null;
    }
}

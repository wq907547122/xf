package xifu.com.menus;

/**
 * 设备类型的枚举
 * @auth wq on 2019/3/19 16",38
 **/
public enum DevTypeEnum {
    // 暂时只考虑中文和英文的对应的字符串，导入点表的时候，写入的设备类型的名称只能是中文或者英文的设备类型
    INVERTER(1, "组串式逆变器", "String Inverter"),
    DATA_COLLECTOR(2, "数采", "Data Collector"),
    DATA_STICK(3, "数据棒", "Data Stick"),
    SINENG_DATA_GATEKEEPER(4, "上能数采带网闸", "SINENG Data Collection with gatekeeper"),
    NO_GATE_SINENG_DATA(5, "上能数采无网闸", "No Gate for SINENG Data Collection"),
    BOX_CHANGE(8, "箱变", "Box Change"),
    BOX_CHANGE_METER(9, "箱变电表", "Box Change Meter"),
    ENVIRONMENTAL_MONITOR(10, "环境监测仪", "Environmental Monitor"),
    AC_COMBINER_BOX(11, "交流汇流箱", "AC Combiner Box"),
    TGJ(13, "通管机", "Management machine of communication"),
    CENTRALIZED_INVERTER(14, "集中式逆变器", "Centralized Inverter"),
    DC_COMBINER_BOX(15, "直流汇流箱", "DC Combiner Box"),
    GATEWAY_METER(17, "关口电表", "Gateway Meter"),
    POOLED_LINE_METER(18, "汇集站线路电表", "Pooled Station Line Meter"),
    FACTORY_AREA_METER(19, "厂用电生产区电表", "Factory Production Area Meter"),
    FACTORY_NON_AREA_METER(21, "厂用电非生产区电表", "Factory Non-production Area Meter"),
    PID(22, "PID", "PID"),
    POWER_QUALITY_EQUIPMENT(24, "电能质量装置", "Power Quality Equipment"),
    STEP_UP_TRANSFORMER(25, "升压变", "Step-up Transformer"),
    GRID_CONNECTED_CABINET(26, "光伏并网柜", "Photovoltaic Grid-connected Cabinet"),
    GRID_CONNECTED_SCREEN(27, "光伏并网屏", "Photovoltaic Grid-connected Screen"),
    TN_DATA_COLLECTOR(37, "铁牛数采", "TN Data Collector"),
    HOUSEHOLD_INVERTER(38, "户用逆变器", "Household Inverter"),
    HOUSEHOLD_ENERGY_STORAGE(39, "户用储能", "Household Energy Storage"),
    HOUSEHOLD_METER(47, "户用电表", "Household Meter"),
    COLLECTION_STICK(50, "采集棒", "Collection Stick"),
    ;
    // 设备类型的id的字符串
    private Integer devTypeId;
    // 设备类型的名称-中文
    private String devTypeNameZh;
    // 设备类型的名称-英文
    private String devTypeNameEn;
    private DevTypeEnum(Integer devTypeId, String devTypeNameZh, String devTypeNameEn) {
        this.devTypeId = devTypeId;
        this.devTypeNameZh = devTypeNameZh;
        this.devTypeNameEn = devTypeNameEn;
    }
    // 获取设备类型
    public Integer getDevTypeId() {
        return this.devTypeId;
    }

    public String getDevTypeNameZh() {
        return devTypeNameZh;
    }

    public String getDevTypeNameEn() {
        return devTypeNameEn;
    }

    // 根据传递的字符串获取枚举
    public static DevTypeEnum getDevTypeEnumByName(String name) {
        for (DevTypeEnum d : DevTypeEnum.values()) {
            if (d.getDevTypeNameZh().equals(name) || d.getDevTypeNameEn().equals(name)) {
                return d;
            }
        }
        return null;
    }
    // 根据设备类型id获取设备类型的枚举
    public static DevTypeEnum getDevTypeEnumByDevTypeId(Integer devTypeId) {
        for (DevTypeEnum d : DevTypeEnum.values()) {
            if (d.getDevTypeId().equals(devTypeId) ) {
                return d;
            }
        }
        return null;
    }
    // 根据枚举的字符串获取枚举的设备类型的Integer
    public static Integer getDevTypeIdByName(String name) {
        DevTypeEnum devTypeEnumByName = getDevTypeEnumByName(name);
        if (devTypeEnumByName == null) {
            return null;
        }
        return devTypeEnumByName.getDevTypeId();
    }

}

package xifu.com.dto;

import lombok.Data;
import xifu.com.pojo.devService.DevInfo;
import xifu.com.pojo.devService.DevModelVersion;
import xifu.com.pojo.devService.DevSignal;
import xifu.com.pojo.devService.DevVersionSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 104扩容的信息
 * @auth wq on 2019/3/22 10:25
 **/
@Data
public class Kr104Dto {
    // 当前系统中已经存在的版本
//    private List<DevModelVersion> existDevModelList = new ArrayList<>();
    // 需要新增的版本
    private List<DevModelVersion> newDevModelList = new ArrayList<>();
    // 已经存在的设备信息
//    private List<DevInfo> exsitDevList = new ArrayList<>();
    // 新增设备的信息<设备名称， 设备信息>
    private Map<String, DevInfo> newDevMap = new HashMap<>();
    // 新增的版本的信号点模型 版本对应的模型信号点列表
    private List<DevVersionSignal> newVersionSiganlList = new ArrayList<>();
    // 已经存在的设备需要新增的信号点 <设备名称， 设备对应新增加的信号点的列表>
    private Map<String, List<DevSignal>> existSignalMap = new HashMap<>();
    // 新增加设备的信号点 <设备名称，设备对应的信号点>
    private Map<String, List<DevSignal>> newSignalMap = new HashMap<>();

}

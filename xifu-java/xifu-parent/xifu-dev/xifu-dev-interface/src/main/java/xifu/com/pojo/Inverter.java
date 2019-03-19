package xifu.com.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 逆变器设备的性能数据,应该与归一化的字段对应，一个设备类型有一个
 * @auth wq on 2019/1/7 13:45
 **/
@Data
@Document(indexName = "inverter_index", type = "inverter", replicas = 0)
public class Inverter {
    @Id
    private String id; // 这个id=devId_collectTime_stationCode
    @Field(type = FieldType.Long)
    private Long devId; // 设备id
    @Field(type = FieldType.Text, analyzer = "ik_smart") // 使用中文分词器
    private String devName; // 设备名称,这样就不用再去查询设备表的信息，有一个问题是当设备修改的时候，需要通知这些改变
    @Field(type = FieldType.Long) // 不能分词
    private Long collectTime; // 采集时间，一般是0/5的时间的时间戳
    @Field(type = FieldType.Keyword)
    private String stationCode; // 电站编号
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String stationName; // 电站名称,方便后面的显示，这里就直接查询出来，避免后面的显示数据还需要去查询数据库
    @Field(type = FieldType.Double)
    private Double abU; // 电网ab电压,就先写一个数据的字符串，与前系统的ids_inverter_string_data_t表的字段对应
}

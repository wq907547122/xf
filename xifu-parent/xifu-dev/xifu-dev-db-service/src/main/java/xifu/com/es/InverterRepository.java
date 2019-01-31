package xifu.com.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import xifu.com.pojo.Inverter;

/**
 * 逆变器设备的性能数据的操作对象
 * @auth wq on 2019/1/7 17:33
 **/
public interface InverterRepository extends ElasticsearchRepository<Inverter, String> {
}

package xifu.com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import xifu.com.es.InverterRepository;
import xifu.com.pojo.Inverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @auth wq on 2019/1/7 17:14
 **/
@SpringBootTest(classes = DbApplication.class)
@RunWith(SpringRunner.class)
public class ElasticSearchTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private InverterRepository inverterRepository;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() {
//        elasticsearchTemplate.deleteIndex(Inverter.class);
//        TimeUnit.SECONDS.sleep(5);
        elasticsearchTemplate.createIndex(Inverter.class);
//        elasticsearchTemplate.putMapping(Inverter.class);
    }

    /**
     * 新增数据
     */
    @Test
    public void insertData() {
        List<Inverter> list = null;
        Long time = System.currentTimeMillis();
        Long cont = 0L;
        Long fileMin = 5 * 60 * 1000L;
        // 添加5wan条数据
        for(int j = 0; j < 500; j++){
            list = new ArrayList<>();
            time = time + cont * fileMin;
//            long tmpCount = 10000100 + cont * 100;
//            long tmpEndCount = 10000100 + (cont + 1) * 100;
            for(long i = 1L; i <= 100L; i++) {
                Inverter inverter = new Inverter();
                inverter.setDevId(i);
                inverter.setDevName("我是很长的设备设备_" + i);
                inverter.setCollectTime(time);
                inverter.setStationCode("f60d6d78d2be40a5823792637af3af8e");
                inverter.setStationName("MQTT电站");
                inverter.setAbU(5.6d);
                inverter.setId(inverter.getDevId() + "_" + time + "_" + inverter.getStationCode());
                list.add(inverter);
            }
            inverterRepository.saveAll(list);
            cont++;
        }

    }


}

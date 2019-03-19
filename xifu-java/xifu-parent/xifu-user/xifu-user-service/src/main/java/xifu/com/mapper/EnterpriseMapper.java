package xifu.com.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import xifu.com.pojo.Enterprise;

import java.util.List;

/**
 * 企业对应的实体类的mapper
 * @auth wq on 2019/1/17 9:32
 **/
public interface EnterpriseMapper extends Mapper<Enterprise> {
    /**
     * 根据用户id获取当前的企业信息
     * @param id : 用户id
     * @return
     */
    @Select("SELECT * FROM tb_enterprise te WHERE EXISTS (SELECT id FROM tb_user WHERE id = #{value} AND te.id = enterprise_id)")
    List<Enterprise> findByUserId(Long id);
}

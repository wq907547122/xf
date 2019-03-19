package xifu.com.dto;

import lombok.Data;

import java.util.List;

/**
 * 树节点的dto
 * @auth wq on 2019/1/29 17:58
 **/
@Data
public class TreeDto {
    private String id; // 树节点的id
    private String label; // 树节点的名称
    private Boolean isLeaf; // 是否是叶子节点
    private List<TreeDto> children; // 当前节点的子节点
}

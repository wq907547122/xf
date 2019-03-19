<template>
  <!-- 树节点的封装使用 -->
  <el-container class="tree-nav-class">
    <el-aside width="300px">
      <el-input
        placeholder="输入关键字进行过滤"
        v-model="filterText">
      </el-input>
      <div style="height: 5px;"></div>
      <el-tree
        class="filter-tree"
        :data="list"
        :lazy="lazy"
        :props="myProps"
        :node-key="nodeKey"
        highlight-current
        :accordion="accordion"
        :expand-on-click-node="isLeafClick"
        :default-expanded-keys="defaultExpendKeys"
        :filter-node-method="filterNode"
        @node-click="nodeClick"
        ref="navTree">
      </el-tree>
    </el-aside>
    <el-main>
      <slot :item="selectNode">
      </slot>
    </el-main>
  </el-container>
</template>

<script type="text/ecmascript-6">
/**
 * 使用组件的api说明
 * @Author wq
 * 树节点导航的信息的说明 后面去整改
 * 1. 属性(Attributes)
 *      1) data：Array 如果不异步请求由父级传递给子节点的组件树节点的数据一般lazy===true,就需要传递这个参数
 *      2) props: Object 属性与element-ui的树节点组件的props意义相同, 默认{label: 'label',children: 'children',isLeaf: 'isLeaf' // 叶子节点取数据的属性名称}
 *      3) lazy: Boolean 是否启动懒加载，如果启动懒加载需要有查询的url等数据 默认true 意义： true->使用懒加载，false->不使用懒加载
 *      4）lazySearchUrl：String 如果是懒加载获取数据的url 默认null
 *      5) lazySearcData: Object 懒加载的情况下查询数据额外需要的参数信息 默认空对象{}
 *      6) lazySearchKey: String 如果懒加载的情况下需要传递给后台的key的字符串，默认为'id'
 *      7) accordion: Boolean 是否启用手风琴的效果,默认false; 参数意义 true:使用，false:不使用
 *      8) isLeafClick: Boolean 是否点击叶子节点才生效 默认false; 参数意义 true:点击叶子节点才触发事件，false:点击任何树节点都生效
 *      9) isFilter: Boolean 是否使用过滤的功能 默认false; 参数意义 true:有过滤的输入框  false：没有过滤的输入框
 *      10) titleText: Sring 树节点头上的标题 默认为null; 参数意义：如果设置了值就显示标题
 *      11）isDefaultSelect： 是否加载节点后自动选中
 * 2. 事件(Events)
 *      1) node-click 点击有效的树节点的回调函数 参数(data点击节点的数据, node点击的节点)
 * 3. 使用例子
 *      1) 在电站管理页面station/index.vue中的
 *      关键代码：<tree-nav
 *                :props="props"
 *                :data="treeData"
 *                @node-click="nodeClick"
 *                >
 *                <template slot-scope="selectNode">
 *                电站信息
 *                {{selectNode}}
 *                </template>
 *              </tree-nav>
 *      2) 在设备管理界面的例子，关键代码
 * */
const defaultProps = {
  children: 'children', // 子节点的属性
  label: 'label', // 显示的文本
  isLeaf: 'isLeaf' // 叶子节点取数据的属性名称
}
const props = {
  data: { // 树节点数据,这个是调用父节点的时候将显示的数据传递过来
    type: [Object, Array],
    default () {
      return []
    }
  },
  props: { // 默认的属性信息
    type: Object,
    default () {
      return defaultProps
    }
  },
  lazy: { // 是否是懒加载，默认是false,目前还没有支持懒加载数据的情况
    type: Boolean,
    default () {
      return false
    }
  },
  loadDataUrl: { // 当lazy=true的时候(即懒加载的时候)，必须要传递加载数据的url,这个一般需要传递过来
    type: String,
    default () {
      return ''
    }
  },
  accordion: { // 是否在同一级只能展开一个节点
    type: Boolean,
    default () {
      return true
    }
  },
  nodeKey: { // 树节点的唯一标识
    type: String,
    default () {
      return 'id'
    }
  },
  isLeafClick: {
    type: Boolean,
    default () {
      return true
    }
  }
}
export default {
  created () {
    this.myProps = Object.assign({}, defaultProps, this.props)
    if (!this.lazy) {
      this.list = this.data || []
      this.defaultSelectNode()
    }
  },
  props: props,
  components: {},
  data () {
    return { // 当前树节点选中的节点
      selectNode: {
      },
      filterText: '', // 过滤的字段
      myProps: {},
      list: [], // 当前树节点的展示数据
      defaultExpendKeys: [] // 默认展开的信息
    }
  },
  filters: {
  },
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    filterNode (value, data) { // 树节点过滤
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    nodeClick (data, node, tree) { // 点击树节点的事件
      let isUpdate = this.isClickLeaf ? data.isLeaf : true
      if (isUpdate) { // 调用数据
        this.selectNode = data
        this.$emit('node-click', data, node) // 调用父组件的方法,树节点选中的事件
      } else if (this.selectNode) {
        this.realSelectNode(this.selectNode)
      }
    },
    defaultSelectNode (node) { // 第一次加载默认选中的节点
      // 目前只解决是所有节点都可以选中的时候的默认选中的情况
      if (this.selectNode[this.nodeKey]) { // 如果已经有有选中了的节点就不需要再去做默认选中的事情
        return
      }
      if (!this.lazy && this.list && this.list.length > 0) {
        let tmpSelectNode
        if (!this.isLeafClick) {
          tmpSelectNode = this.list[0]
        } else {
          tmpSelectNode = this.findFristLeaf(this.list[0])
        }
        this.realSelectNode(tmpSelectNode)
      } else if (!this.isLeafClick && this.lazy && node) { // 如果是懒加载的情况
        this.realSelectNode(node)
      }
    },
    findFristLeaf (node) {
      this.defaultExpendKeys.push(node[this.nodeKey]) // 默认展开的节点
      if (node[this.myProps.isLeaf]) { // 如果当前节点是叶子节点
        return node
      }
      let cld = node[this.myProps.children]
      if (cld && cld.length > 0) { // 这里递归调用找到叶子节点
        return this.findFristLeaf(cld[0])
      } else {
        return node
      }
    },
    realSelectNode (node) { // 选中节点
      let self = this
      setTimeout(() => {
        self.$refs.navTree.setCurrentNode(node)
        self.nodeClick(node, self.$refs.navTree.getCurrentNode(), self.$refs.navTree)
      }, 10)
    }
  },
  watch: {
    filterText (val) { // 监听输入框数据变化的事件
      this.$refs.navTree.filter(val)
    },
    data (newData) { // 改变的时候重新赋值
      if (!this.lazy) {
        this.list = newData || []
        this.defaultSelectNode()
      }
    }
  },
  computed: {}
}
</script>

<style lang="less" scoped>
  .tree-nav-class{
    height: 100%;
    /deep/ .el-main{
      padding: 0 0 0 5px;
    }
    .el-aside{
      border: 1px solid #C6E9EF;
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
    }
    /deep/ .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content{
      background-color: #C6E9EF;
      color: #1E9FBF;
    }
  }
</style>

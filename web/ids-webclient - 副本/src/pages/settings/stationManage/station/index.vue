<template>
  <!-- 电站管理的界面 -->
  <tree-nav
  :props="props"
  :data="treeData"
  :isLeafClick="false"
  @node-click="nodeClick"
  >
    <!-- 具体的电站信息内容(右边的区域的内容) -->
    <template slot-scope="selectNode">
      <el-container>
        <el-header style="height: auto">
          <el-form :inline="true" :model="searchData" class="demo-form-inline" style="float: left;">
            <el-form-item label="电站名称">
              <el-input v-model="searchData.stationName" placeholder="电站名称" clearable style="width: 120px"></el-input>
            </el-form-item>
            <el-form-item label="并网时间">
              <el-date-picker v-model="searchData.produceDate" type="date" clearable
                              :format="$t('dateFormat.yyyymmdd')"
                              placeholder="并网时间"
                              value-format="yyyy-MM-dd" :editable="false" style="width: 140px" >
              </el-date-picker>
            </el-form-item>
            <el-form-item label="并网类型" >
              <el-select style="width: 110px" v-model="searchData.onlineType" placeholder="请选择">
                <el-option v-for="item in gridTypes" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="电站状态">
              <el-select style="width: 110px" v-model="searchData.stationBuildStatus" placeholder="请选择">
                <el-option v-for="item in stationStatus" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryStations">{{ $t('modules.search') }}</el-button>
            </el-form-item>
          </el-form>
          <el-form ref="btnsForm" :inline="true" style="float: right;">
            <el-form-item label="">
              <el-button type="primary" @click="addStation">新增
              </el-button>
            </el-form-item>
<!--
            <el-form-item label="">
              <el-button type="primary">修改</el-button>
            </el-form-item>

            <el-form-item label="">
              <el-button type="primary">删除</el-button>
            </el-form-item>

            <el-form-item label="">
              <el-button type="primary">设置环境监测仪</el-button>
            </el-form-item>-->
          </el-form>
        </el-header>
        <el-main>
          <el-table :data="list" ref="stationInfoTable" row-key="id" @row-click="rowClick" border max-height="600" :fit="true" @selection-change="rowSelectHandler" class="stationInfoTable">
            <el-table-column type="selection" width="40" align="center">
            </el-table-column>
            <el-table-column prop="stationName" label="电站名称" align="center">
              <template slot-scope="scope">
                <el-button type="text">
                  {{scope.row.stationName}}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column prop="installedCapacity" label="装机容量(kW)" align="center">
            </el-table-column>
            <el-table-column prop="stationAddr" label="电站地址" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="contactPeople" label="联系人" align="center">
            </el-table-column>
            <el-table-column prop="phone" label="联系方式" align="center">
            </el-table-column>
            <el-table-column prop="produceDate" label="并网时间" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.produceDate && new Date(scope.row.produceDate).format('yyyy-MM-dd') }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="emiInfoNames" label="环境监测仪" align="center">
              <template slot-scope="scope">
                <el-button-group>
                  <el-button type="primary" icon="el-icon-edit"></el-button>
                </el-button-group>
                {{scope.row.emiInfoNames}}
              </template>
            </el-table-column>
            <el-table-column prop="operate" label="操作" align="center">
              <template slot-scope="scope">
                <el-button-group>
                  <el-button type="primary" @click="editorStation(scope.row.id)" icon="el-icon-edit"></el-button>
                  <el-button type="danger" icon="el-icon-delete"></el-button>
                </el-button-group>
              </template>
            </el-table-column>
            <el-table-column prop="operate" label="设置" align="center">
              <template slot-scope="scope">
                <el-button-group>
                  <el-button type="primary">电价</el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination :current-page="searchData.page" :page-sizes="[10, 20, 30, 50]"
                         :page-size="searchData.pageSize"
                         layout="total, sizes, prev, pager, next, jumper" :total="total"
                         style="float: right; margin-top: 5px;">
          </el-pagination>
        </el-main>
      </el-container>
      <!-- 弹出框 -->
      <station-dialog
        v-if="isShowDialog"
        :isShowDialog.sync="isShowDialog"
        :enterpriseId="searchData.enterpriseId"
        :editorId="editorId"
        @stationSuccess="queryStations"
      ></station-dialog>
    </template>
  </tree-nav>
</template>

<script type="text/ecmascript-6">
export default {
  created () {
    this.$http.get('api/user/domain/tree').then(resp => {
      this.treeData = resp.data || []
    }, () => {
      this.treeData = []
    }).catch(() => {
      this.treeData = []
    })
  },
  components: {
    TreeNav: () => import('@/components/treeNav/index.vue'),
    StationDialog: () => import('./dialog/station.vue')
  },
  data () {
    return {
      searchData: { // 查询数据
        page: 1,
        pageSize: 10
      },
      props: {},
      treeData: [
      ],
      // 查询出来的电站信息列表
      list: [],
      total: 0, // 当前的总记录数
      gridTypes: [
        {
          label: '所有',
          value: ''
        },
        {
          label: '地面式',
          value: '1'
        },
        {
          label: '分布式',
          value: '2'
        },
        {
          label: '户用',
          value: '3'
        }
      ],
      stationStatus: [
        {
          label: '所有',
          value: ''
        },
        {
          label: '规划中',
          value: '1'
        },
        {
          label: '在建',
          value: '2'
        },
        {
          label: '并网',
          value: '3'
        }
      ],
      selectRows: [], // 当前选中的记录
      isShowDialog: false, // 是否显示弹出框
      editorId: null // 修改记录的id
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    // 点击树节点的事件
    nodeClick (data, node) {
      if (!data) {
        return
      }
      var arr = data.id.split('@')
      if (arr.length !== 2) {
        return
      }
      if (arr[1] === 'e') { // 企业
        this.searchData.enterpriseId = +arr[0]
      } else { // arr[1]===domain区域
        this.searchData.domainId = +arr[0]
        // 需要获取到当前节点的企业id,即就是找最顶级的数据
        this.searchData.enterpriseId = this.getDomainEnterprise(node.parent)
      }
      // 查询电站信息,在加载完成区域的信息后会自定执行一次这个方法，后面的执行靠点击树节点的事件来执行
      this.queryStations()
    },
    // 通过递归调用找到区域所属的电站
    getDomainEnterprise (node) { // 获取区域所属的区域
      if (node.data.id.endsWith('@e')) {
        return +node.data.id.split('@')[0]
      } else if (!node.parent) { // 如果父节点都不存在了，就不能再找了
        return null
      }
      return this.getDomainEnterprise(node.parent) // 递归调用
    },
    /**
     * 查询点选信息
     */
    queryStations () {
      if (!this.searchData.enterpriseId) {
        this.$message.warning('请选择对应的区域')
        return
      }
      this.$http.get('api/user/station/page', {
        params: this.searchData
      }).then(resp => {
        this.list = resp.data.list || []
        this.total = resp.data.total || 0
      }, () => { // 没有查询到结果
        this.list = []
        this.total = 0
      }).catch(() => { // 查询数据失败
        this.list = []
        this.total = 0
      })
    },
    rowSelectHandler (selection) { // 选中的事件
      this.selectRows = selection || []
    },
    rowClick (row) { // 单击行
      let find = this.selectRows.find(item => item.id === row.id)
      this.$refs.stationInfoTable.toggleRowSelection(row, !find)
    },
    addStation () { // 新增电站
      this.editorId = null
      this.isShowDialog = true
    },
    editorStation (id) { // 修改电站
      this.editorId = id
      this.isShowDialog = true
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

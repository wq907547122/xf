<template>
    <div>
      <!-- 搜索栏 -->
      <div class="" style="float: left">
        <el-form :inline="true" :model="searchData" class="demo-form-inline">
          <el-form-item :label="$t('modules.main.menu.settings.enterpriseManage.name')">
            <el-input style="width: 140px" v-model="searchData.name" :placeholder="$t('modules.main.menu.settings.enterpriseManage.name')" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('modules.main.menu.settings.enterpriseManage.address')">
            <el-input style="width: 140px" v-model="searchData.address" :placeholder="$t('modules.main.menu.settings.enterpriseManage.address')" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('modules.main.menu.settings.enterpriseManage.phone')">
            <el-input style="width: 140px" v-model="searchData.contactPhone" :placeholder="$t('modules.main.menu.settings.enterpriseManage.phone')" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('modules.main.menu.settings.enterpriseManage.contatUser')">
            <el-input style="width: 140px" v-model="searchData.contactPeople" :placeholder="$t('modules.main.menu.settings.enterpriseManage.contatUser')" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="pageDataSearch(true)">{{ $t('modules.search') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
      <!-- 按钮栏 -->
      <div class="btns" style="float: right;margin-bottom: 10px;">
        <el-button @click="showDialog()" type="primary">{{ $t('modules.add') }}</el-button>
        <el-button @click="showDialog(true)" type="primary">{{ $t('modules.edit') }}</el-button>
        <el-button @click="deleteQy" type="primary">{{ $t('modules.del') }}</el-button>
      </div>
      <!-- 数据列表栏 -->
      <div class="list-table">
        <el-table ref="singleTable" :data="list" @row-click="rowClick" highlight-current-row
                  border :maxHeight="tableContentHeight">
          <el-table-column label="" width="60">
            <template slot-scope="scope">
              &nbsp;<el-radio v-model="getRadio" class="myRadioHoverCss" :label="scope.row.id" readonly disabled>&nbsp;</el-radio>
            </template>
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.name')" prop="name" align="center">
            <template slot-scope="scope">
              <div class="text-ellipsis" :title="scope.row.name">{{ scope.row.name }}</div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.address')" prop="address" align="center">
            <template slot-scope="scope">
              <div class="text-ellipsis" :title="scope.row.address">{{ scope.row.address }}</div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.phone')" prop="contactPhone" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.email')" prop="email" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.contatUser')" prop="contactPeople" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.longitude')" prop="longitude" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.latitude')" prop="latitude" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.radius')" prop="radius" align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.enterpriseManage.op')" prop="description" align="center">
            <template slot-scope="scope">
              <el-button type="text" icon="el-icon-info" class="table-skip-text-color"
                         @click="showDetail(scope.row, $event)">{{ $t('modules.main.menu.settings.enterpriseManage.detail') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="overflow: hidden;margin-top: 10px;">
        <div style="float: right;right: 10px;">
          <el-pagination @size-change="pageSizeChange" @current-change="pageChange" :current-page="searchData.page"
                         :page-sizes="[10, 20, 30, 50]" :page-size="searchData.pageSize"
                         layout="total, sizes, prev, pager, next, jumper" :total="total">
          </el-pagination>
        </div>
      </div>
        <!-- 弹出对话框 -->
      <add-or-editor-dialog :isShow.sync="isShowDialog"
                            :isEditor="isEditor"
                            :editorId="editorId"
                            :isDetail="isDetail"
                            @enSuccess="enSuccess" v-if="isShowDialog"></add-or-editor-dialog>
    </div>
</template>

<script type="text/ecmascript-6">

import AddOrEditorDialog from './dialog/enterprise.vue'

export default {
  components: {
    AddOrEditorDialog
  },
  data () {
    return {
      isShowDialog: false,
      // 搜索栏的数据
      searchData: {
        page: 1,
        pageSize: 10
      },
      currentRow: null,
      // table的max高度
      tableContentHeight: 120,
      // 表格数据
      list: [],
      total: 0,
      isEidtor: false, // 是否是修改
      isDetail: false, // 是否是详情
      editorId: null // 如果是修改，修改的id
    }
  },
  filters: {},
  mounted: function () {
    let self = this
    this.$nextTick(function () {
      self.calcTableMaxHeight()
    })
    window.onresize = () => this.calcTableMaxHeight()
    this.listData()
  },
  methods: {
    enSuccess () { // 关闭对话框
      this.isShowDialog = false
      this.listData()
    },
    // 计算表格的最大高度
    calcTableMaxHeight () {
      let height = document.documentElement.clientHeight
      let relHeight = height - 240
      if (relHeight < 120) {
        relHeight = 120// 默认的最大高度不小于120
      }
      this.tableContentHeight = relHeight
    },
    listData () { // 查分页信息
      this.currentRow = null // 当前没有选中的记录
      this.$http.get('api/user/enterprise/page', {
        params: this.searchData
      }).then(resp => {
        this.list = resp.data.list
        this.total = resp.data.total
      }, () => { // 状态码不是正确的请求
        this.list = []
        this.total = 0
      }).catch(() => { // 服务异常
        this.list = []
        this.total = 0
      })
    },
    /**
     * 获取数据
     * @param isToFirstPage ：是否到第一页
     */
    pageDataSearch (isToFirstPage) {
      if (isToFirstPage) {
        this.searchData.page = 1
      }
      this.listData()
    },
    /**
     * 每页查询的数据量改变的事件
     * @param val
     */
    pageSizeChange (val) {
      this.searchData.pageSize = val
      this.pageDataSearch(true)
    },
    /**
     * 页码改变的事件
     * @param val
     */
    pageChange (val) {
      this.searchData.page = val
      this.pageDataSearch()
    },
    /**
     * 显示弹出框
     * @param isEditor
     */
    showDialog (isEditor) {
      isEditor = !!isEditor
      if (isEditor) { // 如果是修改
        if (!(this.currentRow && this.currentRow.id)) {
          this.$message.error('请选择需要修改的数据')
          return
        }
        this.editorId = this.currentRow.id
      }
      this.isShowDialog = true
      this.isEditor = !!isEditor
      this.isDetail = false
    },
    // 显示详情
    showDetail (row, event) {
      event && event.stopPropagation()
      this.isEditor = false
      this.isShowDialog = true
      this.editorId = row.id
      this.isDetail = true
    },
    // 删除企业
    deleteQy () {
      if (!(this.currentRow && this.currentRow.id)) {
        this.$message.error('请选择要删除的记录')
        return
      }
      this.$confirm('确定删除选中的记录？').then(() => {
        // TODO 删除企业，在删除之前需要判断企业下是否有对应的区域、用户、电站等信息，只有不存在（区域、用户、电站）的情况下才能删除
        this.$message.info('删除成功')
      })
    },
    rowClick (row) {
      let myRow = row
      if (this.currentRow && this.currentRow.id === row.id) {
        myRow = null
      }
      this.$refs.singleTable.setCurrentRow(this.currentRow = myRow)
    }
  },
  watch: {},
  computed: {
    // 点选按钮选中的项的model的初始化
    getRadio () {
      if (this.currentRow) {
        return this.currentRow.id
      }
      return 0
    }
  }
}
</script>

<style lang="less" scoped>
</style>

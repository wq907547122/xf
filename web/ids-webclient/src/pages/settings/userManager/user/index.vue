<template>
    <div>
      <!-- 查询栏 -->
      <div>
        <el-form ref="form" :model="searchBarData" label-width="80px" :inline="true" style="float: left">
          <el-form-item label="账号名称">
            <el-input v-model="searchBarData.loginName"></el-input>
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="searchBarData.phone"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadFirstPageData">查询</el-button>
          </el-form-item>
        </el-form>
        <el-button @click="createUser()" type="primary" style="float: right;margin-right: 12px;">新增</el-button>
        <el-button @click="exportUsers()" type="primary" style="float: right;margin-right: 12px;">导出</el-button>
      </div>
      <!-- 表格栏 -->
      <el-table :data="list" border style="width: 100%;" :maxHeight="tableContentHeight">
        <el-table-column prop="loginName" :label="$t('modules.main.menu.settings.userManage.loginName')" width="100px;" align="center"></el-table-column>
        <el-table-column prop="niceName" :label="$t('modules.main.menu.settings.userManage.userName')" align="center"></el-table-column>
        <el-table-column prop="enterpriseName" :label="$t('modules.main.menu.settings.userManage.enterprise')" align="center"></el-table-column>
        <!--<el-table-column prop="department" :label="$t('modules.main.menu.settings.userManage.department')" align="center"></el-table-column>-->
        <el-table-column prop="phone" :label="$t('modules.main.menu.settings.userManage.phone')" align="center"></el-table-column>
        <el-table-column prop="roleNames" :label="$t('modules.main.menu.settings.userManage.roleName')" align="center">
          <template slot-scope="scope">
            <div style="color:#63cc90">{{scope.row.roleNames}}</div>
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('modules.main.menu.settings.userManage.status')" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" @change="statusChange(scope.row,scope.row.status)" active-color="#13ce66" inactive-color="#aaa" :active-value="0" :inactive-value="1">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="operation" :label="$t('modules.main.menu.settings.userManage.op')" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small">{{$t('detail') }}</el-button>
            <el-button v-if="scope.row.userType !== 0 && scope.row.userType !== 2"
                       @click="editorUser(scope.row.id)"
                       type="text" size="small">{{$t('update') }}</el-button>
            <el-button  v-if="scope.row.userType !== 2" @click="delUser(scope.row.id, scope.row.loginName)" type="text" size="small" >{{$t('delete') }}</el-button>
            <!-- 重置密码需要当前的登录用户是超级管理员或者企业管理员 -->
            <el-button v-if="scope.row.userType !== 2" type="text" size="small" >重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="float: right;margin-top:5px" :page-sizes="[10, 20, 30, 50]" @size-change="pageSizeChange" @current-change="pageChange" :current-page="searchBarData.page" :page-size="searchBarData.pageSize"
                     layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
      <!-- 弹出框 -->
      <user-dialog :isShowDialog.sync="isShowDialog"
                   :editorId="editorId"
                   @userSuccess="getUserDatas"
                   v-if="isShowDialog"></user-dialog>
    </div>
</template>

<script type="text/ecmascript-6">
import UserDialog from './dialog/user.vue'
export default {
  created () {
    this.getUserDatas()
  },
  components: {
    UserDialog
  },
  data () {
    return {
      // table的max高度
      tableContentHeight: 120,
      // 搜索栏
      searchBarData: {
        page: 1,
        pageSize: 10
      },
      // 表格数据
      list: [],
      total: 0, // 总记录数
      isShowDialog: false,
      editorId: null // 修改的id
    }
  },
  filters: {},
  mounted: function () {
    let self = this
    this.$nextTick(function () {
      self.calcTableMaxHeight()
    })
    window.onresize = () => this.calcTableMaxHeight()
  },
  methods: {
    calcTableMaxHeight () {
      let height = document.documentElement.clientHeight
      let relHeight = height - 240
      if (relHeight < 120) {
        relHeight = 120// 默认的最大高度不小于120
      }
      this.tableContentHeight = relHeight
    },
    getUserDatas () { // 查询用户
      this.$http.get('api/user/page', {
        params: this.searchBarData
      }).then(resp => {
        this.list = resp.data.list
        this.total = resp.data.total
      }, () => {
        this.list = []
        this.total = 0
      }).catch(() => {
        this.list = []
        this.total = 0
      })
    },
    loadFirstPageData () { // 加载第一页数据
      this.searchBarData.page = 1
      this.getUserDatas() // 真正的加载数据
    },
    createUser () { // 创建用户
      this.editorId = null
      this.isShowDialog = true
    },
    statusChange () { // 状态改变
    },
    pageSizeChange (val) { // 每页显示的记录数量改变
      this.searchBarData.pageSize = val
      this.loadFirstPageData()
    },
    pageChange (val) { // 页码改变
      this.searchBarData.page = val
      this.getUserDatas()
    },
    editorUser (id) { // 修改用户
      this.editorId = id
      this.isShowDialog = true
    },
    delUser (id, loginName) { // 删除用户
      this.$confirm(`确定删除用户[${loginName}]?`).then(() => {
        this.$http.delete('api/user/ph/' + id).then(() => {
          this.$message.success('删除成功')
          if (this.list.length <= 1) {
            this.searchData.page = (this.searchData.page - 1) || 1
          }
          this.getUserDatas()
        }, error => {
          this.$message.error(error.message || '删除失败')
        }).catch(() => {
          this.$message.error('删除失败,服务异常')
        })
      })
    },
    exportUsers () { // 导入用户
      window.open('api/user/upload', '_self')
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

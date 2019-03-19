<template>
    <div>
      <el-row>
        <el-col :span="20">
          <el-form :inline="true" :model="searchData" class="demo-form-inline" style="float: left;">
            <el-form-item :label="$t('modules.main.menu.settings.personRightsManager.role.roleName')">
              <el-input v-model="searchData.name" :placeholder="$t('modules.main.menu.settings.personRightsManager.role.roleName')" clearable></el-input>
            </el-form-item>
            <el-form-item :label="$t('modules.main.menu.settings.personRightsManager.role.createDate')">
              <el-date-picker
                v-model="searchData.startDate"
                type="date"
                :format="$t('dateFormat.yyyymmdd')"
                value-format="yyyy-MM-dd" :editable="false"
                :placeholder="$t('modules.main.menu.settings.personRightsManager.role.startTime')">
              </el-date-picker> ~
              <el-date-picker
                v-model="searchData.endDate"
                type="date"
                :format="$t('dateFormat.yyyymmdd')"
                value-format="yyyy-MM-dd HH:mm:ss" :editable="false"
                :placeholder="$t('modules.main.menu.settings.personRightsManager.role.endTime')">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchRoleData(true)">{{ $t('modules.search') }}</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" @click="addRole">{{$t('create') }}</el-button>
        </el-col>
      </el-row>
      <!-- 表格数据 -->
      <div class="list-table">
        <el-table :data="list" border :maxHeight="tableContentHeight">
          <el-table-column
            prop="name"
            :label="$t('modules.main.menu.settings.personRightsManager.role.roleName')" align="center">
          </el-table-column>
          <el-table-column prop="description" :label="$t('modules.main.menu.settings.personRightsManager.role.description')"  align="center">
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.personRightsManager.role.isUse')" align="center">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.status" @change="statusChange(scope.row)" active-color="#13ce66" inactive-color="#aaa" :active-value="0" :inactive-value="1">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.personRightsManager.role.createDate')" align="center">
            <template slot-scope="scope">
              <div>{{ scope.row.createDate | dateFomart}}</div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('modules.main.menu.settings.personRightsManager.role.operate')" align="center">
            <template slot-scope="scope" v-if="scope.row.id !== 1 && scope.row.id !== 2">
              <el-button type="text" @click="editorRole(scope.row.id)">{{ $t('modules.edit') }}
              </el-button>
              <el-button type="text" @click="deleteRole(scope.row.id, scope.row.name)">{{ $t('modules.del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="overflow: hidden;margin-top: 10px;">
          <div style="float: right;right: 10px;">
            <el-pagination @size-change="pageSizeChange" @current-change="pageChange" :current-page="searchData.page"
                           :page-sizes="[10, 20, 30, 50]" :page-size="searchData.pageSize"
                           layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
          </div>
        </div>
      </div>
      <!-- 新增或者修改角色的弹出框 -->
      <role-dialog :isShowDialog.sync="isShowDialog" v-if="isShowDialog" :roleId="roleId" @roleSuccess="loadData"></role-dialog>
    </div>
</template>

<script type="text/ecmascript-6">
import RoleDialog from './dialog/role.vue'

export default {
  created () {
    this.loadData()
  },
  components: {
    RoleDialog
  },
  data () {
    return {
      searchData: { // 查询条件
        page: 1,
        pageSize: 10
      },
      tableContentHeight: 120,
      list: [], // 表格显示的数据
      total: 0, // 总记录数
      isShowDialog: false, // 是否显示对话框
      roleId: null // 修改时候的角色id
    }
  },
  filters: {
    dateFomart (val, fmt = 'yyyy-MM-dd HH:mm:ss') { // 日期的格式化
      if (!val) {
        return ''
      }
      if (!fmt) {
        fmt = 'yyyy-MM-dd HH:mm:ss'
      }
      return new Date(val).format(fmt)
    }
  },
  mounted: function () {
    let self = this
    this.$nextTick(function () {
      self.calcTableMaxHeight()
    })
    window.onresize = () => this.calcTableMaxHeight()
  },
  methods: {
    loadData () { // 加载数据
      this.$http.get('api/user/role/list', {
        params: this.searchData
      }).then(resp => {
        this.list = resp.data.list
        this.total = resp.data.total
      }, () => {
        this.list = []
        this.total = []
      }).catch(() => {
        this.list = []
        this.total = []
      })
    },
    calcTableMaxHeight () {
      let height = document.documentElement.clientHeight
      let relHeight = height - 240
      if (relHeight < 120) {
        relHeight = 120// 默认的最大高度不小于120
      }
      this.tableContentHeight = relHeight
    },
    statusChange (row) { // 改变状态
    },
    pageSizeChange (val) { // 每页显示记录数量改变的事件
      this.searchData.pageSize = val
      this.searchRoleData(true)
    },
    pageChange (val) { // 页码改变的事件
      this.searchData.page = val
      this.searchRoleData()
    },
    searchRoleData (isToFirstPage) { // 查询数据的方法
      if (isToFirstPage) {
        this.searchData.page = 1
      }
      this.loadData() // 调用真正查询数据的方法
    },
    addRole () { // 新增角色
      this.roleId = null
      this.isShowDialog = true
    },
    editorRole (roleId) { // 修改角色
      this.roleId = roleId
      this.isShowDialog = true
    },
    deleteRole (roleId, roleName) { // 删除角色
      this.$confirm(`是否删除当前角色[${roleName}]？`).then(() => {
        this.$http.delete('api/user/role/ph/' + roleId).then(() => {
          this.$message.success('删除成功')
          if (this.list.length === 1) { // 当前页只有一条数据，就需要查询上一页数据
            this.searchData.page = (this.searchData.page - 1) || 1
          }
          this.loadData() // 调用真正查询数据的方法
        }, error => {
          this.$message.error(error.message || '删除失败')
        }).catch(() => {
          this.$message.error('删除失败')
        })
      })
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

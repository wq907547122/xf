<template>
  <!-- 点表管理模块：点表导入和归一化配置的信息 -->
  <div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <!-- 点表导入的tab -->
      <el-tab-pane label="点表导入" name="port">
        <!-- 点表导入 -->
        <el-row>
          <el-col :span="12">
            <el-form :inline="true">
              <el-form-item>
                <el-input disabled :value="myFileName" placeholder="请选择文件"></el-input>
              </el-form-item>
              <el-form-item>
                <fileUpload
                  :uploadUrl="importUrl"
                  fileTypeError="请选择excl文件"
                  @on-validate-success="setInputText"
                  :isShowTip="false"
                  :userValidate="myFileType"
                  @on-upload-success="uploadSuccess"
                ></fileUpload>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="12">
            <div>
              <el-button type="primary" @click="resetCache" icon="el-icon-refresh" size="mini" v-if="isShowRest">重置缓存</el-button>
            </div>
          </el-col>
        </el-row>
        <!-- 展示以及导入了的点表信息列表 -->
        <el-table ref="versionTable"
                  :data="versionList"
                  @selection-change="handleSelectionChange"
                  @row-click="rowClick"
                  border
                  style="width: 100%">
          <el-table-column
            type="selection"
            width="55"
            align="center"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="name"
            label="点表名称">
          </el-table-column>
          <el-table-column
            align="center"
            prop="devTypeId"
            label="设备类型">
            <template slot-scope="{row}">
              {{ $t('devTypeId.' + row.devTypeId) }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="modelVersionCode"
            label="版本号">
          </el-table-column>
          <el-table-column
            align="center"
            prop="createDate"
            label="导入日期">
            <template slot-scope="{row}">
              {{row.createDate | dateFormat('yyyy-MM-dd HH:mm:ss')}}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="p"
            label="操作">
            <template slot-scope="{row}">
              <el-button type="primary" icon="el-icon-info" size="mini">详情</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click.stop="deleteVersion(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页栏 -->
        <el-pagination
          @size-change="pageSizeChange"
          @current-change="pageChange"
          :current-page="searchData.page"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="searchData.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-tab-pane>
      <!-- 归一化配置的tab -->
      <el-tab-pane label="归一化" name="normal">
        <my-normalize></my-normalize>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  components: {
    fileUpload: () => import('@/components/fileUpload/index.vue'),
    MyNormalize: () => import('./normalized.vue')
  },
  data () {
    return {
      vm: this,
      tokenId: sessionStorage.getItem('token-id'),
      importUrl: '/api/dev/point?tokenId=' + sessionStorage.getItem('token-id'),
      myFileName: null,
      // 允许的文件类型 excel
      myFileType: ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel', 'text/csv'],
      // 当前系统导入了的版本的列表
      versionList: [],
      searchData: { // 查询的数据
        page: 1,
        pageSize: 10
      },
      // 当前的总记录数
      total: 0,
      // 当前多选的记录
      multipleSelection: [],
      isShowRest: true,
      activeName: 'port'
    }
  },
  created () { // 在创建的生命钩子中执行的加载数据
    this.queryVersionList()
  },
  filters: {
    dateFormat (val, fmt = 'yyyy-MM-dd HH:mm:ss') { // 后台返回日期格式数据的格式
      if (!val) {
        return ''
      }
      // return vm.$moment(new Date(val)).format('YYYY-MM-DD HH:mm:ss')
      return new Date(val).format(fmt)
    }
  },
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    uploadSuccess (response, file, fileList) { // 点表导入成功,重新加载数据
      this.queryVersionList()
    },
    setInputText (file) { // 上传验证成功后修改显示的信息
      this.myFileName = file.name
    },
    queryVersionList (isToFirstPage) { // 查询当前系统导入的版本列表
      if (isToFirstPage) { // 是否是到第一页
        this.searchData.page = 1
      }
      this.$http.get('api/dev/devVersion/page', {
        params: this.searchData})
        .then(resp => {
          this.versionList = resp.data.list
          this.total = resp.data.total
        }, () => { // 没有查询到数据的异常
          this.versionList = []
          this.total = 0
        }).catch(() => {
          this.versionList = []
          this.total = 0
        })
    },
    pageChange (val) { // 当前页数改变的事件
      this.searchData.page = val
      this.queryVersionList()
    },
    pageSizeChange (val) { // 每页显示的记录数量改变
      this.searchData.pageSize = val
      this.queryVersionList(true) // 到第一页
    },
    handleSelectionChange (val) { // 当前选中记录改变的事件
      this.multipleSelection = val
    },
    rowClick (row, column, event) { // 点击当前行选中改变的事件
      this.$refs.versionTable.toggleRowSelection(row) // 修改选中状态后会制动调用handleSelectionChange方法
    },
    deleteVersion (row) { // 点击删除按钮的事件
      this.$confirm(`确认删除[${row.name}]的点表信息,删除后将不可恢复?`, '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(() => {
          // TODO 开始删除
          this.$http.delete('api/dev/devVersion/ph/' + row.id).then(() => {
            this.$message.success('删除成功')
            if (this.versionList.length === 1) { // 当前页只有一条数据，就需要查询上一页数据
              this.searchData.page = (this.searchData.page - 1) || 1
            }
            this.queryVersionList() // 调用真正查询数据的方法
          }, error => {
            this.$message.error(error.message || '删除失败')
          }).catch(() => {
            this.$message.error('删除失败')
          })
        })
        .catch(() => {
          console.log('已取消删除')
        })
    },
    // 重新设置缓存的信息
    resetCache () {
      this.$confirm(`确认重新加载缓存?`, '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(() => {
          // 重置redis缓存信息
          this.$http.get('api/dev/devVersion/cache').then(resp => {
            this.$message.info('重置成功')
          }).catch(() => {
            this.$message.error('重置失败')
          })
        })
    },
    handleClick (v) {
      console.log(v.name)
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

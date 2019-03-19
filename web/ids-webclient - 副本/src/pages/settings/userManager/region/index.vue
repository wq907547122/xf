<template>
    <div class="regionSetting">
      <el-row>
        <el-col :span="20">
          <el-form :inline="true" :model="searchData" class="demo-form-inline">
            <el-form-item :label="$t('modules.main.menu.settings.personRightsManager.role.ower')">
              <el-select v-model="searchData.id" placeholder="请选择" @change="handleEnterpriseChange">
                <el-option
                  v-for="item in enterprises"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" @click="addRegion">{{$t('create') }}</el-button>
        </el-col>
      </el-row>
      <!-- 树节点 -->
      <el-tree
        :data="treeData"
        show-checkbox
        node-key="id"
        default-expand-all
        :expand-on-click-node="false">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span style="flex: 0;">{{ data.name }}</span>
        <div style="flex: 1;padding: 0 10px"><div style="width: 100%;border: 1px dashed #80808052;"></div></div>
        <span style="flex: 0;">
          <el-button
            type="text"
            size="mini"
            @click="addRegion(data)"
            >
            新增
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="editorRegion(data)"
            >
            修改
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="deleteRegion(data)"
          >
            删除
          </el-button>
        </span>
      </span>
      </el-tree>
      <!-- 弹出对话框新增或者修改区域的弹出框 -->
      <region-dialog @handleSuccess="handleSuccess" :isShowDialog.sync="isShowDialog" :parentId="parentId"
                     :eidtorRow="eidtorRow"
                     :treeData="treeData" :enterpriseId="searchData.id" v-if="isShowDialog"></region-dialog>
    </div>
</template>

<script type="text/ecmascript-6">
import RegionDialog from './dialog/region.vue'

export default {
  created () {
    // 获取企业信息
    this.getEnterprise()
  },
  components: {
    RegionDialog
  },
  data () {
    return {
      // 所有的企业信息
      enterprises: [],
      // 查询条件
      searchData: {},
      isShowDialog: false, // 是否显示新增或者修改的弹出框
      treeData: [], // 树节点信息
      parentId: null, // 对区域添加子节点的节点id
      eidtorRow: null // 修改的树节点数据
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    /**
     * 获取当前登录用户管理的企业信息
     */
    getEnterprise () {
      this.$http('api/user/enterprise/list').then(resp => {
        this.enterprises = resp.data || []
        if (this.enterprises.length > 0) {
          this.$set(this.searchData, 'id', this.enterprises[0].id)
          this.handleEnterpriseChange(this.enterprises[0].id)
        }
      }, () => { // 如果是错误的请求码，就是执行的这个错误的方法
        this.enterprises = []
      }).catch(() => {
        this.enterprises = []
      })
    },
    /**
     * 选择企业改变的事件
     */
    handleEnterpriseChange (id) {
      if (!id) {
        this.treeData = []
        return
      }
      // 查询区域
      this.$http.get('api/user/domain/list', {params: {id: id}}).then(resp => {
        this.treeData = resp.data || []
      }, () => { // 返回的是错误码
        this.treeData = []
      }).catch(() => {
        this.treeData = []
      })
    },
    showDialog () { // 显示弹出框
      if (!this.searchData.id) {
        this.$message.warning('请选择企业')
        return
      }
      this.isShowDialog = true
    },
    addRegion (data) { // 新增区域
      this.parentId = data && data.id
      this.eidtorRow = null
      this.showDialog()
    },
    // 修改区域
    editorRegion (data) {
      this.eidtorRow = {
        id: data.id,
        name: data.name,
        parentId: data.parentId,
        enterpriseId: data.enterpriseId,
        longitude: data.longitude,
        latitude: data.latitude,
        radius: data.radius,
        domainPrice: data.domainPrice,
        description: data.description,
        currency: data.currency
      }
      this.showDialog()
    },
    // 删除区域
    deleteRegion (data) {
      this.$confirm('确定删除?').then(() => {
        this.$http.delete('api/user/domain/ph/' + data.id).then(resp => {
          this.$message.success('删除成功')
          this.handleSuccess()
        }, err => {
          this.$message.error(err.message || '删除失败，服务异常')
        })
      }).catch((e) => {
        this.$message.error(e.message || '删除失败，服务异常')
      })
    },
    handleSuccess () { // 新增或者修改成功之后重新加载数据
      this.handleEnterpriseChange(this.searchData.id)
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/variables';
  .regionSetting {
    height: 100%;
    /deep/ .custom-tree-node {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      padding-right: 8px;
    }

    /deep/ .el-tree-node__content{
      height: 35px;
    }

    /deep/ .el-tree-node__content:hover {
      background-color: #ebf8fc;
    }

    /deep/ .el-tree {
      padding: 20px 10px;
      height: calc(100% - 81px);
      border: 1px solid #cae7ef;
    }
  }
</style>

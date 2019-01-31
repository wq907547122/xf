<template>
  <!-- 角色新增或者修改的弹出框 -->
  <el-dialog
    title="角色"
    :visible="isShowDialog"
    width="50%"
    :close-on-click-modal="false"
    :before-close="closeDialog"
    center>
    <el-form :model="roleData" :rules="rules" ref="roleForm" class="role-form formtable" style="max-height:590px;overflow-y: auto;">
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="name" label="角色名称" :label-width="labelWidth">
            <el-input v-model="roleData.name" clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="status" label="角色状态" :label-width="labelWidth">
            <el-switch v-model="roleData.status" active-color="#13ce66" inactive-color="#aaa"
                       :active-value="0" :inactive-value="1"></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="description" label="描述" :label-width="labelWidth">
            <el-input v-model="roleData.description" type="textarea" :rows="4" placeholder="角色描述">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="enterpriseId" label="所属企业" :label-width="labelWidth">
            <el-select v-model="roleData.enterpriseId" filterable placeholder="请选择企业" :disabled="isEditor">
              <el-option
                v-for="item in enterprises"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="authIds" label="系统侧权限" :label-width="labelWidth">
            <el-checkbox-group v-model="roleData.authIds">
              <el-checkbox v-for="item in userAuths" :label="item.id" :key="item.id">
                {{item.authName}}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="authIds" label="终端侧权限" :label-width="labelWidth">
            <el-checkbox-group v-model="roleData.authIds">
              <el-checkbox v-for="item in appAuths" :label="item.id" :key="item.id">
                {{item.authName}}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitData" :disabled="isSubmiting">确 定</el-button>
      <el-button @click="closeDialog" :disabled="isSubmiting">取 消</el-button>
    </span>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
const props = {
  isShowDialog: false, // 是否显示对话框
  roleId: null // 如果是修改回传递角色id过来
}
export default {
  created () {
    if (this.roleId) {
      this.isEditor = true
      // 查询信息
      this.queryRole()
    }
    this.getLoginUserAuths() // 获取登录用户的权限
    this.queryEnterprise() // 获取登录用户的企业,admin查询所有，其他的用户查询他对应的企业
  },
  props: props,
  components: {},
  data () {
    return {
      isAdmin: false,
      labelWidth: '120px',
      isEditor: false, // 是否是修改
      roleData: {
        roleType: 'normal', // 创建角色只能创建普通的角色
        status: 0, // 角色状态默认是开启的
        authIds: []
      },
      isSubmiting: false,
      rules: {
        name: [{ required: true, message: '用户名必填', trigger: 'blur' }],
        enterpriseId: [{ required: true, message: '企业必选', trigger: 'change' }],
        authIds: [{ required: true, message: '请选择至少一个权限', trigger: 'change' }]
      },
      authCheckArr: [], // 当前角色已经选择了的权限
      // 系统侧权限的可选项
      userAuths: [],
      // 终端权限的可选项
      appAuths: [],
      enterprises: [] // 当前登录用户的企业
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    closeDialog () { // 关闭对话框
      this.$emit('update:isShowDialog', false)
    },
    // 提交数据
    submitData () {
      this.$refs.roleForm.validate(valid => {
        if (valid) { // 如果验证成功
          // 提交表单
          this.isSubmiting = true
          let url = (this.isEditor && 'api/user/role/update') || 'api/user/role/save'
          this.$http.post(url, this.$qs.stringify(this.roleData)).then(() => {
            this.$message.success('操作成功')
            this.$emit('roleSuccess')
            this.closeDialog()
            this.isSubmiting = false
          }, error => {
            this.$message.error(error.message || '操作失败')
            this.isSubmiting = false
          }).catch(() => {
            this.$message.error('系统异常')
            this.isSubmiting = false
          })
        } else { // 验证失败
          return false
        }
      })
    },
    // 获取登录用户具有的权限
    getLoginUserAuths () {
      // 获取当前登录用户具有的权限
      this.$http.get('api/user/auth/list').then(resp => {
        let data = resp.data || []
        this.userAuths = data.filter(item => item.roleType === 0)
        this.appAuths = data.filter(item => item.roleType === 1)
      }, () => { // 没有查询到任何的权限
        this.userAuths = []
        this.appAuths = []
      }).catch(() => { // 服务出现异常
        this.userAuths = []
        this.appAuths = []
      })
    },
    queryEnterprise () { // 查询企业信息
      this.$http.get('api/user/enterprise/list').then(resp => {
        this.enterprises = resp.data
      }, () => {
        this.enterprises = []
      }).catch(() => {
        this.enterprises = []
      })
    },
    queryRole () { // 查询角色信息
      this.$http.get('api/user/role/ph/' + this.roleId).then(resp => {
        let data = resp.data
        delete data.createUserId
        delete data.createDate
        delete data.modifyDate
        delete data.modifyUserId
        data.authIds = data.authIds ? data.authIds : []
        this.roleData = data
      })
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/stylus/formtable.less';
  .role-form{
    .el-checkbox {
      margin-left: 0;
      margin-right: 30px;
    }
  }
</style>

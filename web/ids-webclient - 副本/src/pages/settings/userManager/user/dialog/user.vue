<template>
  <!-- 用户新增或者修改的弹出框 TODO 还缺少用户管理电站的内容的添加 -->
  <el-dialog
    title="用户"
    :visible="isShowDialog"
    width="50%"
    :close-on-click-modal="false"
    :before-close="closeDialog"
    center>
    <el-form :model="userData" :rules="rules" ref="userForm" class="user-form formtable">
      <div style="font-weight: bold">基本信息</div>
      <div>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="niceName" label="用户昵称" :label-width="labelWidth">
              <el-input v-model="userData.niceName" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="gender" label="性别" :label-width="labelWidth">
              <el-radio v-model="userData.gender" label="0">男</el-radio>
              <el-radio v-model="userData.gender" label="1">女</el-radio>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="24">
            <el-form-item prop="enterpriseId" label="企业" :label-width="labelWidth">
              <el-select v-model="userData.enterpriseId" filterable placeholder="请选择企业" :disabled="isEditor">
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
          <el-col :span="12">
            <el-form-item prop="phone" label="联系电话" :label-width="labelWidth">
              <el-input v-model="userData.phone" maxLength="11" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="email" label="邮箱" :label-width="labelWidth">
              <el-input v-model="userData.email" clearable></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="12" class="myHeightRowClass">
            <el-form-item prop="status" label="账号状态" :label-width="labelWidth" style="line-height: 46px;">
              <el-switch style="line-height: 46px;" v-model="userData.status" active-color="#13ce66" inactive-color="#aaa" :active-value="0" :inactive-value="1">
              </el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="stationCodes" label="用户电站" :label-width="labelWidth">
              <el-input v-model="userData.stationCodes" clearable></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div style="font-weight: bold">账号信息</div>
      <div>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="loginName" label="用户名" :label-width="labelWidth">
              <el-input v-model="userData.loginName" clearable :disabled="isEditor"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="userType" label="用户类型" :label-width="labelWidth">
              <el-select v-model="userData.userType" :disabled="true">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass" v-if="!isEditor">
          <el-col :span="12">
            <el-form-item prop="password" label="密码" :label-width="labelWidth">
              <el-input type="password" v-model="userData.password" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="rePassword" label="重复密码" :label-width="labelWidth">
              <el-input type="password" v-model="userData.rePassword" clearable></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div style="font-weight: bold">角色信息(<label style="color: red">*</label>)</div>
      <div>
        <el-table ref="roleList" :max-height="200" border :data="roleList" tooltip-effect="dark" @selection-change="changeRole" style="margin-bottom: 10px;">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="角色名称" width="300" prop="name"></el-table-column>
          <el-table-column prop="description" label="角色描述"></el-table-column>
        </el-table>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitData" :disabled="isSubmiting">确 定</el-button>
      <el-button @click="closeDialog" :disabled="isSubmiting">取 消</el-button>
    </span>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import myValidate from '@/utils/validate'

// 由父组件传递过来的属性信息
const props = {
  isShowDialog: false, // 是否显示弹出框
  editorId: null // 修改用户的id
}
export default {
  props: props,
  components: {},
  created () {
    this.queryEnterprise()
    this.queryLoginUserRoles()
    if (this.editorId) { // 是否是修改
      this.isEditor = true
      // 获取当前的用户信息
      this.findUserById()
      delete this.rules.loginName // 如果是修改，就不能修改用户的登录名称
    }
  },
  data () {
    // 验证登录名称是否已经被占用
    const validateLoginName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('用户名不能为空'))
        return
      }
      // 拼接接口 api/user/check/{data}/{type}  data:验证的数据 type:验证类型  1:用户名  ....其他的暂时没有
      let url = 'api/user/check/ph/' + value + '/1'
      this.$http.get(url, {}).then(resp => {
        if (resp && resp.data) {
          callback()
        } else {
          callback(new Error('用户名已存在'))
        }
      }).catch(() => {
        callback(new Error('服务器异常，请稍后重试'))
      })
    }
    // 验证重复密码
    const validateRePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.userData.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      isSubmiting: false, // 是否正在提交
      userData: {
        status: 0, // 用户状态默认是开启的
        gender: '0', // 性别默认是男 0:男  1:女
        userType: 1 // 用户类型默认是普通用户
      }, // 用户数据
      rules: { // 验证规则
        niceName: [{ required: true, message: '昵称必填', trigger: 'blur' }],
        loginName: [{ required: true, message: '登录名必填', trigger: 'blur' },
          {validator: myValidate.validateUserName, message: this.$t('modules.main.menu.settings.enterpriseManage.userNameError'), trigger: 'blur'},
          {validator: validateLoginName, message: '用户名已存在', trigger: 'blur'} // 验证用户名是否重复
        ],
        password: [ // 密码
          {required: true, message: '密码必填', trigger: 'blur'},
          {validator: myValidate.validatePassword, trigger: 'blur'}
        ],
        rePassword: [ // 重复密码
          {validator: validateRePassword, message: '两次密码不一致', trigger: 'blur'} // 密码不一致 验证密码是否一致
        ],
        email: [
          {required: true, message: '邮箱必填', trigger: 'blur'},
          {validator: myValidate.validateEmail, trigger: 'blur'}
        ],
        enterpriseId: [{required: true, message: '企业必选', trigger: 'change'}]
      }, // 规则参数
      labelWidth: '120px', // 文本的宽度
      enterprises: [], // 企业信息
      options: [
        {
          value: 1,
          label: '普通用户'
        }, {
          value: 0,
          label: '企业管理员'
        }
      ],
      roleList: [], // 角色的列表
      isEditor: false // 是否是习惯
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    closeDialog () { // 关闭弹出框
      this.$emit('update:isShowDialog', false)
    },
    submitData () { // 表单提交
      this.$refs.userForm.validate(valid => {
        if (!valid) {
          console.log('no valid')
          return false
        }
        if (!this.userData.roleIds) { // 最少需要选择1个
          this.$message.error('请选择角色信息')
          return false
        }
        // 新增或者修改用户
        this.isSubmiting = true
        let url = (this.isEditor && 'api/user/update') || 'api/user/save'
        this.$http.post(url, this.$qs.stringify(this.userData)).then(() => {
          this.isSubmiting = false
          this.$message.success('操作成功')
          this.$emit('userSuccess')
          this.closeDialog()
        }, error => {
          this.isSubmiting = false
          this.$message.error(error.message || '操作失败')
        }).catch(() => {
          this.isSubmiting = false
          this.$message.error('操作失败,系统异常')
        })
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
    /**
     * 查询当前登录用户具有的所有启用的角色
     */
    queryLoginUserRoles () {
      this.$http.get('api/user/role/findByLoginUser').then(resp => {
        this.roleList = resp.data || []
        // 如果获取成功
        this.toggoleSelectRow()
      }, () => {
        this.roleList = []
      }).catch(() => {
        this.roleList = []
      })
    },
    changeRole (roles) { // 改变选择的角色信息
      this.userData.roleIds = roles.map(item => item.id).join(',')
    },
    /**
     * 根据用户id获取用户信息
     */
    findUserById () {
      this.$http.get('api/user/ph/' + this.editorId).then(resp => {
        let data = resp.data
        delete data.createDate
        delete data.modifyDate
        this.userData = data
        // 如果获取成功
        this.toggoleSelectRow()
      })
    },
    toggoleSelectRow () { // 获取当前选中的值
      if (this.roleList.length === 0 || !this.userData.roleIds) {
        return
      }
      // 只有两个都获取到有值的时候才去执行选中的事情
      let ids = this.userData.roleIds.split(',')
      let selectRows = this.roleList.filter(item => ids.indexOf(item.id + '') >= 0)
      if (selectRows.length > 0) { // 选中当前的值
        selectRows.forEach(row => {
          this.$refs.roleList.toggleRowSelection(row)
        })
      }
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/stylus/formtable.less';
</style>

<template>
  <!-- 企业的新增或者修改的弹出框 -->
  <el-dialog
    title="提示"
    :visible="isShow"
    :close-on-click-modal="false"
    width="60%"
    :before-close="closeDialog">
    <el-form :model="enterModel" ref="enterpriseForm" :rules="rules" :disabled="isDetail" class="enterpriseForm formtable" status-icon>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item label="企业名称" :label-width="lableWidth" prop="name">
            <el-input v-model="enterModel.name" :placeholder="$t('modules.main.menu.settings.enterpriseManage.name')">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item label="企业简介" :label-width="lableWidth" prop="description">
            <el-input type="textarea" v-model="enterModel.description" :placeholder="$t('modules.main.menu.settings.enterpriseManage.description')">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="8">
          <el-form-item label="企业LOGO" :label-width="lableWidth" prop="logo">
            <img-upload :isShowImgOnly="isDetail" style="background-color: #0A7EA5;" :fileId.sync="enterModel.logo" isValidWH></img-upload>
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-form-item label="企业缩略图" :label-width="lableWidth" prop="avatarPath">
            <img-upload :isShowImgOnly="isDetail" :fileId.sync="enterModel.avatarPath"></img-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass" v-if="!(isEditor || isDetail)">
        <el-col :span="8">
          <el-form-item label="企业管理员" :label-width="lableWidth" prop="loginName">
            <el-input v-model="enterModel.loginName" :placeholder="$t('modules.main.menu.settings.enterpriseManage.enterRole')">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="密码" :label-width="lableWidth" prop="password">
            <el-input type="password" v-model="enterModel.password" placeholder="密码">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="重复密码" :label-width="lableWidth" prop="rePassword">
            <el-input type="password" v-model="enterModel.rePassword" placeholder="重复密码">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="8">
          <el-form-item label="负责人姓名" :label-width="lableWidth" prop="contactPeople">
            <el-input v-model="enterModel.contactPeople" :placeholder="$t('modules.main.menu.settings.enterpriseManage.contatUser')">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系方式" :label-width="lableWidth" prop="contactPhone">
            <el-input v-model="enterModel.contactPhone" maxLength="11" :placeholder="$t('modules.main.menu.settings.enterpriseManage.phone')">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="企业邮箱" :label-width="lableWidth" prop="email">
            <el-input v-model="enterModel.email" :placeholder="$t('modules.main.menu.settings.enterpriseManage.email')">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item label="企业地理范围" prop="latitude" :label-width="lableWidth">
            <input-for-position
              :lat="enterModel.latitude"
              :lng="enterModel.longitude"
              :radius="enterModel.radius"
              :isPosition="false"
              :isCanChoose="!isDetail"
              @myChange="dataCirleChange"
              class="myPositionClass"
            ></input-for-position>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item label="企业地址" :label-width="lableWidth" prop="address">
            <el-input type="textarea" v-model="enterModel.address" :placeholder="$t('modules.main.menu.settings.enterpriseManage.address')">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item align="center" style="margin-top: 10px" v-if="!isDetail">
        <el-button type="primary" @click="formSubmit" :disabled="isSubmit" v-if="!isDetail">{{ $t('sure')}}</el-button>
        <el-button @click="closeDialog">{{ $t('cancel') }}</el-button>
      </el-form-item>
    </el-form>
    <div v-if="isDetail" style="margin-top: 10px; text-align: center;">
      <el-button @click="closeDialog">{{ $t('cancel') }}</el-button>
    </div>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import InputForPosition from '@/components/choosePosition/inputForPosition.vue'
import ImgUpload from '@/components/imgUpload/index.vue'
import myValidate from '@/utils/validate'

// 由父节点传递过来的属性等信息
const props = {
  // 是否显示对话框 true:显示 false：隐藏
  isShow: false,
  isEditor: false, // 是否是修改 true: 修改 false:新增
  editorId: null, // 如果是修改，企业的id
  isDetail: false // 是否是详情
}
export default {
  props: props,
  components: {
    InputForPosition,
    ImgUpload
  },
  created () {
    if (this.isEditor || this.isDetail) { // 如果是修改 或者查看详情
      this.getEidtorRow() // 获取当前的记录
    }
  },
  data () {
    // 验证登录名称是否已经被占用
    const validateLoginName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('用户名不能为空'))
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
      } else if (value !== this.enterModel.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      isSubmit: false, // 是否正在提交
      enterModel: {
      },
      // 显示文字的宽度
      lableWidth: '150px',
      rules: {
        name: [ // 企业名称
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'}
        ],
        description: [ // 企业名称
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'}
        ],
        loginName: [ // 验证企业用户
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: ['blur']},
          {validator: myValidate.validateUserName, message: this.$t('modules.main.menu.settings.enterpriseManage.userNameError'), trigger: 'blur'},
          {validator: validateLoginName, message: this.$t('modules.main.menu.settings.enterpriseManage.userNameRep'), trigger: 'blur'} // 验证用户名是否重复
        ],
        password: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'},
          {validator: myValidate.validatePassword, trigger: 'blur'}
        ],
        rePassword: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'},
          {validator: validateRePassword, message: this.$t('modules.main.menu.settings.enterpriseManage.pswNoSame'), trigger: 'blur'} // 密码不一致 验证密码是否一致
        ],
        email: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'},
          {validator: myValidate.validateEmail, trigger: 'blur'}
        ],
        address: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'}
        ],
        longitude: [
          {required: true, message: '经度必填', trigger: ['blur', 'change']}
        ],
        latitude: [
          {required: true, message: '纬度必填', trigger: ['blur', 'change']}
        ],
        radius: [
          {required: true, message: '半径必填', trigger: ['blur', 'change']}
        ]
      }
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    getEidtorRow () { // 获取修改的记录
      this.$http.get('api/user/enterprise/ph/' + this.editorId).then(resp => {
        if (resp.status === 200) {
          let data = resp.data || {}
          delete data.createUserId
          delete data.createDate
          delete data.modifyUserId
          delete data.modifyDate
          this.enterModel = data
        }
      }).catch(e => {
        console.log(e.message || '获取数据失败')
      })
    },
    closeDialog () { // 关闭对话框
      this.$emit('update:isShow', false)
    },
    dataCirleChange (lat, lng, r) { // 经纬度改变的事件
      this.$set(this.enterModel, 'latitude', lat)
      this.$set(this.enterModel, 'longitude', lng)
      this.$set(this.enterModel, 'radius', r)
    },
    formSubmit () { // 提交表单
      this.$refs.enterpriseForm.validate((valid) => {
        if (valid) {
          this.isSubmit = true // 正在提交中
          let url = (this.isEditor && 'api/user/enterprise/update') || 'api/user/enterprise/save'
          this.$http.post(url, this.$qs.stringify(this.enterModel)).then(() => {
            this.$message.success('操作成功')
            this.$emit('enSuccess')
            this.isSubmit = false
          }, err => {
            this.$message.error(err.message || '操作失败')
            this.isSubmit = false
          }).catch(e => {
            this.$message.error(e.message || '操作失败')
            this.isSubmit = false
          })
        } else {
          return false
        }
      })
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/stylus/formtable.less';
</style>

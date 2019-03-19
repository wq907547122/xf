<template>
  <div class='login'>
    <div class="login-top"></div>
    <div class="login-main">
      <div class="logo">
        <!--<img src="/assets/images/login/logo.png" alt="logo"/>-->
        <span v-text="$t('systemName')"></span>
      </div>
      <el-form class='login-form' :model="loginForm" :rules="rules" ref="loginForm">
        <div class="form-item-content">
          <el-form-item prop="loginName">
            <label class="userIcon" for="username">{{$t('modules.login.username')}}</label>
            <el-input id="username" type="text" v-model="loginForm.loginName" auto-focus auto-complete="off"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <label class="pwdIcon" for="password">{{$t('modules.login.password')}}</label>
            <el-input id="password" type="password" v-model="loginForm.password" auto-complete="off"
                      @keyup.enter.native="submitForm()"></el-input>
          </el-form-item>
        </div>

        <div class="login-buttons">
          <el-button type="primary" class="login-btn" @click="submitForm()">
            {{ $t('modules.login.loginBtnText') }}
          </el-button>
        </div>

        <div class="login-form-bottom"></div>
      </el-form>
    </div>
    <div class="login-bottom">
      <p class="inner">
        <span v-text="$t('copyright')"></span>
        <router-link :to="{path: '/about'}">{{$t('about')}}</router-link>
      </p>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
import { Message } from 'element-ui'

export default {
  components: {},
  data () {
    return {
      // 登录用户
      loginForm: {},
      rules: {
        loginName: [{ required: true, message: '用户名必填', trigger: 'blur' }],
        password: [{ required: true, message: '密码必填', trigger: 'blur' }]
      }
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    submitForm () { // 提交表单
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.$http.post('api/auth/login', this.$qs.stringify(this.loginForm)).then(resp => {
            // 登录成功
            sessionStorage.setItem('token-id', resp.data)
            Message({
              type: 'success',
              duration: 3 * 1000,
              message: this.$t('modules.login.messages.loginSuccess')
            })
            // TODO 获取当前用户的权限信息
            // 跳转到首页
            this.$router.push({path: '/'})
          }, err => {
            this.$message.error(err.message || '登录失败')
          }).catch((e) => {
            this.$message.error('服务器异常')
            console.log(e)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" src="./index.less" scoped>
</style>

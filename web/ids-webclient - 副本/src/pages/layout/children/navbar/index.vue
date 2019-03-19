<template>
  <!-- 显示当前路由的信息的面包屑:即当前的页面在哪个路由的信息 -->
  <el-menu class="navbar" mode="horizontal">
    <div class="navbar-icon el-icon-fa fa-home"></div>
    <el-breadcrumb class="app-levelbar" separator=">">
      <el-breadcrumb-item v-for="(item,index)  in levelList" :key="index">
        <router-link v-if='item.redirect === "noredirect" || index == levelList.length-1' to="" class="no-redirect">
          {{$t(item.name)}}
        </router-link>
        <router-link v-else :to="item.redirect || item.path">{{$t(item.name)}}</router-link>
      </el-breadcrumb-item>
    </el-breadcrumb>
    <div class="navbar-info">
      <div class="company-info">{{ currentCompany }}</div>
      <!--<div class="time">{{ currentDate | formatDate }}</div>-->
      <div class="time">{{ formatCurrentDate }}</div>
    </div>
  </el-menu>
</template>
<script type="text/ecmascript-6">
import { mapState } from 'vuex'

export default {
  name: 'navBar',
  data () {
    return {
      levelList: null,
      // currentCompany: this.$store.state.company,
      currentCompany: '',
      currentDate: new Date()
    }
  },
  computed: {
    ...mapState([
      'permissionRoutes'
    ]),
    formatCurrentDate () {
      return this.currentDate.format('yyyy-MM-dd hh:mm:ss')
    }
  },
  created () {
    this.getBreadcrumb()
  },
  mounted: function () {
    const _this = this
    this.timer = setInterval(function () {
      _this.currentDate = new Date()
    }, 1000)
  },
  beforeDestroy: function () {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    getBreadcrumb () {
      this.levelList = this.$route.matched.filter(item => item.name)
    }
  },
  watch: {
    $route () {
      this.getBreadcrumb()
    }
  }
}
</script>
<style lang='less' src='./index.less' scoped></style>

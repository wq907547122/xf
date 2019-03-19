<template>
  <!-- 页面的整体布局 -->
  <el-container direction="vertical" class="app-wrapper">
    <el-header height="75px">
      <header-top></header-top>
    </el-header>
    <div class="body-container">
      <!-- 如果是设置页面，需要显示左边的树结构的导航菜单栏 -->
      <el-aside v-if="showSidebar" :width="$store.state.toggleSidebar ? '228px' : '52px'">
        <div
          :class="['toggle-side-btn', sideBarOpen === false ? 'collapse' : 'expand']"
          @click="toggleSideBar">
        </div>
        <SideBar class="sidebar-wrapper" :isSubMenu="isSubMenuShow" :menuList="settingsMenuList"/>
      </el-aside>
      <el-main class="main-container">
        <!-- 面包屑：显示当前所在的页面 -->
        <NavBar/>
        <!-- 主体内容 -->
        <area-conent></area-conent>
      </el-main>
    </div>
  </el-container>
</template>

<script type="text/ecmascript-6">
import { TOGGLE_SIDEBAR } from '@/store/mutation-types'
import HeaderTop from '@/pages/layout/children/header/index'
import AreaConent from '@/pages/layout/children/content/index'
import SideBar from '@/pages/layout/children/sidebar/index'
import NavBar from '@/pages/layout/children/navbar/index'

export default {
  components: {
    HeaderTop,
    AreaConent,
    SideBar,
    NavBar
  },
  created () {
    this.getData()
  },
  data () {
    return {
      list: [],
      sideBarOpen: false,
      isSubMenuShow: false
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    /**
     * 左侧菜单 收起/展开 切换
     */
    toggleSideBar () {
      this.sideBarOpen = !this.sideBarOpen
      this.$store.commit(TOGGLE_SIDEBAR, this.sideBarOpen)
      setTimeout(window.onresize, 200)
    },
    getData () {
      this.$http.get('api/user/role/list').then(resp => {
        console.log(resp)
      }).catch(e => {
        console.log(e)
      })
      this.$http.get('api/user/page', {
        params: {
          page: 1,
          pageSize: 2
        }
      }).then(resp => {
        console.log(resp)
        this.list = resp.data.list
      }).catch((e) => {
        console.log(e)
        this.list = []
      })
    },
    showSubMenu () {
      // debugger
      // let leafRoute = this.getCurrentMenu(this.$store.state.permissionRoutes)
      // if (leafRoute && this.$route.path.indexOf(leafRoute.path) != -1) {
      //   this.$router.push(leafRoute.children && leafRoute.children[0])
      // } else {
      // }
      this.isSubMenuShow = this.$route.meta.showSidebar && this.$route.fullPath.indexOf('/settings') >= 0
    }
  },
  watch: {
    $route () { // 判断是否是设置菜单
      this.showSubMenu()
    }
  },
  computed: {
    showSidebar (vm) { // 是否显示左侧菜单
      return vm.$route.meta.showSidebar
    },
    settingsMenuList (vm) { // 设置菜单的列表
      let resultArr = []
      if (vm.$route.meta.showSidebar === true) { // 如果多年过去菜单的权限是显示左边树菜单，当前页面就是在设置界面
        resultArr = vm.$store.state.permissionRoutes.filter(item => item.type === 'settings')
        resultArr = resultArr && resultArr.length > 0 ? resultArr[0].children : []
        this.showSubMenu()
      } else if (vm.$route.meta.showSidebar === 'epc') {
        resultArr = vm.$store.state.permissionRoutes.filter(item => {
          return item.filterType === 'epc'
        })
        resultArr = resultArr && resultArr.length > 0 ? resultArr[0].children : []
      }
      return resultArr
    }
  }
}
</script>

<style lang="less" scoped>
  @import (once, reference) '~@/assets/css/lib';
  .el-container {
    height: 100vh;
    min-width: 768px;
    //background: url('~/assets/images/bg.jpg') center / 100% 100% no-repeat;

    .el-header {
      position: fixed;
      top: 0;
      width: 100%;
      padding: 0;
      border-bottom: 1px solid #608FA4;
      background: @color-primary url('~@/assets/images/layout/header_bg.jpg') center / 100% 100% no-repeat;
      z-index: 2000;
    }

    .body-container {
      position: relative;
      top: 75px;
      width: 100%;
      height: ~'calc(100% - 75px)';
      display: flex;
      flex-flow: row nowrap;

      .body-side-nav {
        position: absolute;
        left: 15px;
        top: 65px;
        width: 40px;
        min-height: 100px;
        color: #3fabc9;
        background: #f0f0f0;
        border: 1px solid #3fabc9;
        z-index: 1200;
        cursor: move;

        & > ul > li {
          min-height: 70px;
          line-height: 1.2;
          text-align: center;
          padding: 1px;
          cursor: pointer;
          a {
            color: #0FBAD8;
          }

          .side-nav-icon {
            width: 33px;
            height: 33px;
            margin: 0 auto;
            background: url("~@/assets/images/layout/side_nav_icons.png") -1px -1px no-repeat;
            display: block;

            &.icon-home {
              background-position: -1px 0;
            }
            &.icon-stationList {
              background-position: -1px -33px;
            }
          }
          .station-list-content {
            position: absolute;
            left: 38px;
            top: -1px;
            width: 240px;
            text-align: center;
            color: #000;
            background-color: #fff;
            box-sizing: border-box;
            border: 1px solid #0FBAD8;
            box-shadow: 5px 5px 12px rgba(15, 185, 215, 0.5);
            cursor: default;
            .station-search {
              padding: 5px 10px;
            }
            .list-station {
              max-height: 500px;
              overflow-y: auto;
              overflow-x: hidden;
              & > ul > li {
                width: 13vw;
                cursor: pointer;
                text-align: left;
                padding: 10px;
                vertical-align: middle;
                border-bottom: 1px solid #e2f9ff;
                &:nth-child(2n + 1) {
                  background-color: rgba(200, 200, 200, 0.3);
                }
                i.station-status {
                  position: relative;
                  top: 1px;
                  width: 18px;
                  height: 18px;
                  margin-right: 6px;
                  border-radius: 50%;
                  background: #aaa;
                  display: inline-block;
                  &.station-status-1 {
                    background: #f33;
                  }
                  &.station-status-3 {
                    background: #0f0;
                  }
                }
              }
            }
          }
        }
      }

      .el-aside {
        color: #333;
        background-color: #227aa0;
        border-right: 1px solid #095a7d;
        transition: all .2s ease-out;
        overflow: hidden;

        .toggle-side-btn {
          width: 100%;
          height: 40px;
          line-height: 40px;
          padding: 0 12px;
          font-size: 32px;
          font-weight: 100;
          text-align: center;
          color: @mainBackground;
          transition: all .2s ease;
          cursor: pointer;
          background-color: #118CC5;
          box-shadow: inset 0 6px 6px -6px #095a7d;
          border-bottom: solid 1px #095a7d;

          &::before {
            content: '';
            position: relative;
            top: 7px;
            width: 28px;
            height: 28px;
            margin: 0 auto;
            background: url("~@/assets/images/layout/side_handler_icons.png") 0 0 no-repeat;
            display: block;
          }

          &:hover {
            &::before {
              background-position-x: -30px;
            }
          }

          &.collapse {
            &::before {
              background-position-y: 0;
            }
          }
          &.expand {
            &::before {
              background-position-y: -30px;
            }
          }
        }

        .sidebar-wrapper {
          z-index: 1001;
          overflow-y: auto;
          border-right: none;
        }
      }

      .el-main {
        padding: 0;
        display: flex;
        flex-flow: column nowrap;
        flex: 1;
        transition: all .2s ease-out;
        position: relative;
      }
    }
  }
</style>

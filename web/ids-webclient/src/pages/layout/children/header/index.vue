<template>
  <!-- 头部 -->
  <div>
    <el-row class="header" :gutter="15">
      <el-col :span="8" :xs="20" :sm="9" :md="6" :lg="7" :xl="8">
        <div class="grid-content">
          <figure id="header_logo" class="header-logo">
            <!--<img class="logo" src="/assets/images/logo.png" :alt="$t('systemName')" :title="$t('systemName')"/>-->
            <div class="logoBox" :title="$t('systemName')">
              <img src="@/assets/images/logo.png" class="logoImg"/>
              <span class="logoTxt logoTopTxt">{{ $t('systemName') }}</span>
            </div>
          </figure>
        </div>
      </el-col>
      <el-col :span="8" :xs="0" :sm="2" :md="10" :lg="10" :xl="10">
        <el-menu mode="horizontal" :router="true" :default-active="activePath"
                 :unique-opened="true"
                 background-color="transparent" text-color="#FFFFFF" active-text-color="#00F6FF">
          <el-menu-item v-for="item in permissionRoutes" :key="item.path"
                        v-if="!item.hidden && item.type === 'menu' && item.children.length" :route="{path: item.path}"
                        :index="item.path + '/index'">
            <i class='el-icon-nav' :class='item.icon'></i>
            <span slot="title">{{$t(item.name)}}</span>
          </el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="8" :xs="4" :sm="13" :md="9" :lg="9" :xl="6">
        <div class="header-right grid-content">
          <!-- 待办 -->
          <router-link class="item inlineBlock" to="/todo">
            <el-badge :value="66" :max="99">
              <i class="header-icon header-icon-todo"></i>
            </el-badge>
            {{ $t('modules.main.menu.todo.title') }}
          </router-link>
          <!-- 大屏 -->
          <router-link class="item inlineBlock"
                       to="/exhibition">
            <i class="header-icon header-icon-switch"></i>
            <span>{{ $t('modules.main.header.gotoExhibition') }}</span>
          </router-link>
          <!-- 用户信息 -->
          <el-dropdown :class="['avatar-container', 'item', 'inlineBlock', 'noSingle']"
                       trigger="click">
            <div class="dropdown-wrapper">
              <!--<img class="dropdown-handler" :src="user ? user.avatar : '/assets/images/layout/user_avatar.png'">-->
              <i class="header-icon header-icon-user"></i>
              <!--<span class="dropdown-text">{{user ? user.loginName : '-'}}</span>-->
              <!--<span>{{user ? user.loginName : 'admin'}}</span>-->
              <span>222</span>
              <i class="el-icon-caret-bottom"></i>
            </div>
            <el-dropdown-menu class="user-dropdown" slot="dropdown">
              <el-dropdown-item>
                <ul class="box-content">
                  <li>
                    <span class="item-title">{{$t('modules.main.userInfo.username')}}</span>
                    <span class="item-value cm-strong">{{'-'}}</span>
                  </li>
                  <li>
                    <span class="item-title">{{$t('modules.main.userInfo.userRole')}}</span>
                    <span class="item-value">
                      <!--<ul v-if="user && user.roles && user.roles.length > 0">
                        <li v-for="role in user.roles" :key="role.id">
                           {{role.name || '-'}}
                        </li>
                      </ul>-->
                      <ul>
                        <li>
                           角色1
                        </li>
                      </ul>
                    </span>
                  </li>
                  <li>
                    <span class="item-title">{{$t('modules.main.userInfo.company')}}</span>
                    <span
                      class="item-value">{{'-'}}</span>
                  </li>
                  <li>
                    <span class="item-title">{{$t('modules.main.userInfo.address')}}</span>
                    <span class="item-value">{{'-'}}</span>
                  </li>
                </ul>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <!--<button class="left" @click="modifyPassword">{{$t('modules.main.userInfo.modifyPassword')}}</button>
                <button class="right" @click="logout">{{$t('modules.main.userInfo.cancellation')}}</button>-->
                <button class="left">{{$t('modules.main.userInfo.modifyPassword')}}</button>
                <button class="right" @click="logout">{{$t('modules.main.userInfo.cancellation')}}</button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <!-- 设置菜单 -->
          <el-dropdown
                       class="settings-container item inlineBlock" :hide-on-click="false"
                       trigger="hover">
            <div class="dropdown-wrapper">
              <i class="dropdown-handler icon-more"></i>
            </div>
            <el-dropdown-menu class="settings-dropdown" slot="dropdown">
              <router-link class="inlineBlock" to="/settings">
                <el-dropdown-item class="settings-menu">{{$t('modules.main.menu.settings.title')}}</el-dropdown-item>
              </router-link>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script type="text/ecmascript-6">
import { mapState } from 'vuex'

export default {
  data () {
    return {
      activePath: '/home/index'
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    logout () {
      this.$http.get('api/user/logout', {}).then(resp => {
        this.$message.success('登出成功')
        sessionStorage.removeItem('token-id')
        this.$router.push('/login')
      }).catch(e => {
        this.$message.error('登出失败，请重试....')
      })
    }
  },
  watch: {
    $route (newVal, oldVal) {
      this.activePath = newVal.path
    }
  },
  computed: {
    ...mapState([ // 路由菜单信息，从vuex的store中获取，将vuex的store中的permissionRoutes复制给this.permissionRoutes
      'permissionRoutes'
    ]),
    activeIndex () { // 当前路由活动的路径
      console.log(this.$route.path)
      return this.$route.path
    }
  }
}
</script>

<style lang="less" scoped>
  @import (once, reference) '~@/assets/css/lib';

  @header-height: 75px;

  .header {
    width: 100%;
    color: #333;
    height: @header-height;
    padding: 0 20px;
    display: flex;
    align-items: center;
    z-index: 2000;

    .header-logo {
      width: 25%;
      min-width: 450px;
      height: @header-height;
      flex: 1;
      display: inline-block;

      .logo {
        position: relative;
        top: -5px;
        min-width: 38px;
        max-width: 124px;
        margin: 3px 0;
        vertical-align: middle;
        display: inline-table;
      }

      .logoBox {
        position: relative;
        left: 10px;
        height: 100%;
        line-height: 90px;
        display: inline-block;
        padding-left: 27px;
        // logo标签
        & > img.logoImg {
          position: relative;
          top: 6px;
          max-width: 100px;
          max-height: 50px;
        }
        //&::after {
        //  // content: url("~/assets/images/login/login_title_top.png");
        //  // position: absolute;
        //  // top: -15px;
        //  // left: 57px;
        //  // width: 40px;
        //  // height: 38px;
        //  //content: url("~/assets/images/logo.png");
        //  content: attr(data-image);
        //  position: absolute;
        //  top: 21px;
        //  left: -16px;
        //  width: 40px;
        //  height: 38px;
        //}

        .logoTxt {
          font-size: 1.8em;
          color: #e0dfe3;
          font-weight: normal !important;
        }
      }
    }

    .el-menu {
      border: none;
      .el-menu-item, .el-submenu {
        min-width: 100px;
        height: @header-height;
        line-height: 1;
        text-align: center;
        padding: 10px 2vw;
        transition: transform 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
        border: 0;

        .el-icon-nav {
          width: 45px;
          height: 40px;
          margin: 0 auto;
          background: url("~@/assets/images/layout/header_nav_icons.png") 0 0 no-repeat;
          display: block;

          &::before {
            content: '' !important;
          }

          &.icon-home {
            background-position-y: 0px;
          }
          &.icon-io {
            background-position-y: -40px;
          }
          &.icon-pm {
            background-position-y: -80px;
          }
          &.icon-pam {
            background-position-y: -120px;
          }
          &.icon-wisdom {
            background-position-y: -280px;
          }
          &.icon-index {
            background-position-y: -160px;
          }
          &.icon-equipment {
            background-position-y: -200px;
          }
          &.icon-alarm {
            background-position-y: -240px;
          }
          &.icon-report {
            background-position-y: -80px;
          }
        }

        &:hover, &:focus {
          background: transparent !important;
        }

        &.is-active {
          color: #00F6FF;
          background: url("~@/assets/images/layout/header_nav_active.png") center no-repeat !important;
          i {
            background-position-x: -45px;
          }
        }
      }
    }

    .header-right {
      margin-right: -10px;
      text-align: right;
      color: @mainBackground;

      .item {
        position: relative;
        top: 0;
        padding: 5px;
        vertical-align: middle;
        display: inline-block;
        color: @mainBackground;

        .header-icon {
          content: "";
          position: relative;
          top: 2px;
          right: 2px;
          width: 25px;
          height: 25px;
          margin: 8px;
          background: url(~@/assets/images/layout/header_icons.png) 0 0 no-repeat;
          display: table-cell;
        }

        .header-icon-todo {
          top: 0;
          left: 0;
          background-position-x: 0;
        }
        .header-icon-switch {
          background-position-x: -25px;
        }
        .header-icon-user {
          background-position-x: -50px;
        }

        &:hover {
          color: #99b0ca;
          text-decoration: none;
          .header-icon {
            background-position-y: -25px;
          }
        }

        span {
          line-height: 27px;
          font-size: 14px;
          display: table-cell;
        }
      }

      .el-dropdown {
        position: relative;
        height: 38px;
        margin-right: 20px;

        &.noSingle {
          top: 0;
        }

        .dropdown-wrapper {
          cursor: pointer;
          position: relative;

          .dropdown-handler {
            width: 38px;
            height: 38px;
            border-radius: 50%;
            display: inline-block;
            &.icon-more {
              width: 48px;
              height: 18px;
              background: url(~@/assets/images/layout/header_more_icon.png) 0 0 no-repeat;
            }
          }

          .el-icon-caret-bottom {
            position: absolute;
            right: -20px;
            top: 8px;
            font-size: 12px;
          }
        }

        &.avatar-container {
          .dropdown-text {
            color: @white;
            height: 55px;
            vertical-align: middle;
            display: inline-block;
          }
        }

        &.settings-container {
          top: 0;
          margin-right: 0;
          .dropdown-wrapper {
            top: 6px;
          }

          &:hover {
            .icon-more {
              background-position: 0 -30px;
            }
          }
        }
      }
    }
  }

  .el-dropdown-menu {
    min-width: 168px;
    max-width: 468px;
    margin-top: 2px;
    color: @mainBackground;
    line-height: 30px;
    background: #196d9a;
    box-shadow: 0 10px 30px #2d2f33;
    border: 1px solid #196d9a;
    border-radius: 5px;

    .el-dropdown-menu__item {
      color: @mainBackground;
      cursor: text;

      .box-content {
        width: 300px;
        padding: 2px 0;
        display: table;

        > li {
          width: 100%;
          display: flex;
          .item-title {
            width: 30%;
            white-space: nowrap;
            display: table-cell;
          }
          .item-value {
            width: 70%;
            display: table-cell;
            &.cm-strong {
              color: #00c6ff;
            }
            li {
              display: inline;

              &::before {
                content: ', ';
              }
              &:first-child::before {
                content: '';
              }
            }
          }
        }
      }

      &:not(.is-disabled):hover, &:focus {
        background-color: #196d9a;
      }

      &.el-dropdown-menu__item--divided {

        &::before {
          background-color: #196d9a;
        }

        button {
          border: 1px solid #00c6ff;
          font-size: 8pt;
          height: 36px;
          line-height: 25px;
          padding: 2px 20px;
          background: transparent;
          color: @mainBackground;
          border-radius: 30px;
          opacity: 0.5;
          cursor: pointer;
          &:hover,
          &:focus {
            opacity: 1;
          }

          &.left {
            float: left;
          }
          &.right {
            float: right;
          }
        }
      }

      &.settings-menu {
        cursor: pointer;
      }
    }

    & /deep/ .popper__arrow {
      top: -7px;
      border-bottom-color: #196d9a !important;

      &::after {
        border-bottom-color: #196d9a !important;
      }
    }
  }

  .el-menu--horizontal {
    border: none !important;
    .is-active {
      background-color: #005ac0 !important;
    }
    > .el-submenu {
      .el-submenu__title {
        height: 75px !important;
        line-height: 75px !important;
        border: 0 !important;
      }
      &.is-active {
        color: #49cde5;
        background-color: #005ac0 !important;
        .el-submenu__title {
          background-color: #005ac0 !important;
        }
      }
    }
  }
</style>

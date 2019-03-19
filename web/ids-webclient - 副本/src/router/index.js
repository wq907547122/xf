import Vue from 'vue'
import Router from 'vue-router'
import {SET_PERMISSION_ROUTES} from '../store/mutation-types'
import store from '../store'
import settings from './settings'

// import HelloWorld from '@/components/HelloWorld'
const Login = resolve => require.ensure([], () => resolve(require('@/pages/login/index.vue')), 'login')
// 里面定义了require.ensure语法。webpack实现了它，作用是可以在打包的时候进行代码分片，并异步加载分片后的代码
// 此时vue会被打包成一个单独的chunk文件
const Layout = resolve => require.ensure([], () => resolve(require('@/pages/layout/index.vue')), 'layout')
const Home = resolve => require.ensure([], () => resolve(require('@/pages/home/index')), 'home')
const Io = resolve => require.ensure([], () => resolve(require('@/pages/io/index')), 'io')
const Pm = resolve => require.ensure([], () => resolve(require('@/pages/pm/index')), 'pm')
const Wisdom = resolve => require.ensure([], () => resolve(require('@/pages/wisdom/index')), 'wisdom')
const Todo = resolve => require.ensure([], () => resolve(require('@/pages/todo/index')), 'todo')
// 大屏界面
const Exhibition = resolve => require.ensure([], () => resolve(require('@/pages/exhibition/index.vue')), 'exhibition')

Vue.use(Router)
// 定义具有的所有的路由
const routes = [
  { // 首页
    path: '/home',
    icon: 'icon-home',
    type: 'menu',
    redirect: '/home/index',
    name: 'menu.home',
    component: Layout, // 后面的跳转级别都是二级跳转，所以他们的父节点的组件都是Layout
    children: [{
      path: 'index',
      component: Home,
      meta: {role: ['home'], showNav: true}
    }]
  },
  { // 运维中心
    path: '/io',
    component: Layout,
    name: 'menu.io',
    redirect: '/io/index',
    icon: 'icon-io',
    noDropdown: true,
    type: 'menu',
    children: [
      {
        path: 'index',
        component: Io,
        type: 'menu',
        meta: {role: ['io']}
      }
    ]
  },
  { // 报表管理
    path: '/pm',
    component: Layout,
    name: 'menu.pm',
    redirect: '/pm/index',
    icon: 'icon-pm',
    type: 'menu',
    children: [
      {
        path: 'index',
        component: Pm,
        icon: 'icon-reportManage',
        meta: {role: ['pm']}
      }
    ]
  },
  { // 智慧应用
    path: '/wisdom',
    component: Layout,
    name: 'menu.wisdom',
    redirect: '/wisdom/index',
    icon: 'icon-wisdom',
    noDropdown: true,
    type: 'menu',
    children: [
      {
        path: 'index',
        component: Wisdom,
        type: 'menu'
        // meta: {role: ['wisdom']}
      }
    ]
  },
  { // 待办
    path: '/todo',
    component: Layout,
    name: 'menu.todo',
    redirect: '/todo/index',
    icon: 'icon-wisdom',
    noDropdown: true,
    type: 'link',
    children: [
      {
        path: 'index',
        component: Todo,
        type: 'menu'
        // meta: {role: ['wisdom']}
      }
    ]
  },
  // 设置界面的路由
  ...settings,
  // { // 设置界面，需要有三级路由，目前只有两级，后面整改
  //   path: '/settings',
  //   name: 'settings.title',
  //   redirect: '/settings/enterprise',
  //   icon: 'icon-settings',
  //   type: 'settings',
  //   component: Layout,
  //   children: [
  //     { // 企业管理
  //       path: '/settings/enterprise',
  //       name: 'settings.enterpriseManage.title',
  //       icon: 'icon-enterpriseManage',
  //       component: EnterpriseManage
  //     }]
  // },
  { // 大屏
    path: '/exhibition',
    name: 'menu.exhibition',
    type: 'link',
    component: Exhibition
    // meta: {role: ['exhibition']}
  },
  {path: '/', redirect: '/home', name: 'ROOT', hidden: true},
  // 登录
  {path: '/login', name: 'login', component: Login}
]
const router = new Router({
  routes
})
store.commit(SET_PERMISSION_ROUTES, routes)
// 跳转之前判断是否登录，如果没有登录就到登录页面
router.beforeEach((to, from, next) => {
  if (to.path === '/login') { // 如果是跳转到登录页面，首先去掉token
    sessionStorage.removeItem('token-id')
    next()
  } else {
    let tokenId = sessionStorage.getItem('token-id')
    if (!tokenId) {
      next('/login')
    } else {
      next()
    }
  }
})
export default router

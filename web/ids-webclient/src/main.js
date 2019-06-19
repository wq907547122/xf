// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import axios from 'axios'
// QS是axios库中带的，不需要我们再npm安装一个
import Qs from 'qs'
// 引入elementui
import Element from 'element-ui'
// 引入样式
import 'normalize.css'
import 'font-awesome/css/font-awesome.min.css'
import './assets/css/index.css'
// import '../theme/index.css'
import './assets/css/stylus/main.less'
import './assets/css/lib.less'
import './assets/css/reset.less'
// 引入i18n
import i18n from './i18n'
// 引用vuex的存储信息
import store from './store'
import VueLazyload from 'vue-lazyload'
import { directive as Clickaway } from 'vue-clickaway'
import VueDND from 'awe-dnd'
import ECharts from 'vue-echarts/components/ECharts'
// 手动引入 ECharts 各模块来减小打包体积
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/pie'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/polar'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title.js'

Vue.component('v-chart', ECharts)
// 扩展Object对象和Date对象的方法
require('@/utils/prototype.js')

Vue.config.productionTip = false
// 引入axios的http请求
Vue.prototype.$http = axios
// 可以转换为form表单的方式提交
Vue.prototype.$qs = Qs

// 使用图片的懒加载的插件
Vue.use(VueLazyload, {
  error: require('@/assets/images/defaultPic.png'),
  loading: require('@/assets/images/img_loading.gif')
})
// 添加点击外部执行的事件
Vue.directive('clickoutside', Clickaway)
// 拖动组件的组件的引用
Vue.use(VueDND)
// 对时间的格式化的操作的插件
// const moment = require('moment')
// Vue.use(require('vue-moment'), {
//   moment
// })
// 添加请求拦截器
axios.interceptors.request.use(function (config) {
  let tokenId = sessionStorage.getItem('token-id')
  if (tokenId) { // 在请求的时候带上token
    config.headers.common['tokenId'] = tokenId
  }
  // 在发送请求之前做些什么
  return config
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error)
})
// 响应的请求的处理，主要是判断如果是没有权限的就直接退出到登录页面
axios.interceptors.response.use(data => {
  return data
}, err => {
  if (err.response.status === 401 || err.response.status === '401') { // 这个是没有权限的返回内容
    Element.Message.error('用户无权限')
    sessionStorage.removeItem('token-id')
    router.push('/login')
    return
  }
  if (err.response.status === 504 || err.response.status === '504') { // 超时
    err.response.data.message = '请求超时，请稍后重试...'
  }
  return Promise.reject(err.response.data) // 请求状态码不是成功的时候，封装数据只返回抛出的异常信息
})

// 引入element ui
Vue.use(Element, {
  size: 'medium',
  i18n: (key, value) => i18n.t(key, value)
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  i18n,
  components: { App },
  template: '<App/>'
})

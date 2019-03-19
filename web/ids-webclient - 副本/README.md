# ids-webclient

> Ids 前端项目

## Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report
```

#### 与后台的接口的请求
```
请求的方式使用axios
例如：
1.get：
this.$http.get(url, {params: params}).then(resp => { // 状态码正确，处理成功
}, err => { // 状态码不为正确的处理,处理失败
  this.$message.error(err.message)
}).catch(() => { // 服务器异常
  
})
2.post请求
this.$http.post(url, param).then(resp => { // 状态码正确，处理成功
}, err => { // 状态码不为正确的处理,处理失败
  this.$message.error(err.message)
}).catch(() => { // 服务器异常
  
})
```
For a detailed explanation on how things work, check out the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).

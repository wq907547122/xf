<template>
  <transition name="router-fade" mode="out-in">
    <router-view :key="key"></router-view>
  </transition>
</template>

<script type="text/ecmascript-6">
export default {
  components: {},
  data () {
    return {}
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {},
  watch: {},
  computed: {
    key () { // 获取当前路由的名称，作为他的key
      return this.$route.name !== undefined ? this.$route.name + +new Date() : this.$route + +new Date()
    }
  }
}
</script>

<style lang="less" scoped>
</style>

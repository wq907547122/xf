<template>
  <section class="content-area">
    <transition name="router-fade" mode="out-in">
      <router-view/>
    </transition>
  </section>
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
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/variables';
  .content-area {
    padding: 10px 30px;
    flex: 1;
    background: @mainBackground;
    overflow: auto;
  }
</style>

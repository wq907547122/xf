<template>
  <!-- 归一化配置的组件 -->
  <div class="normalized-tab">
    <el-row>
      <el-col :span="12">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="设备类型">
            <el-select v-model="selectDevTypeId" placeholder="请选择设备类型" :disabled="isSubmit" clearable>
              <el-option label="组串式逆变器" value="1"></el-option>
              <el-option label="箱变" value="8"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="设备版本">
            <el-select v-model="selectModelVersionCode" placeholder="请选择版本信息" :disabled="isSubmit" clearable>
              <el-option v-for="item in modelVersionList" :label="item" :value="item" :key="item"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="12">
        <el-button type="primary" @click="showSelectSignals" :disabled="isSubmit" size="mini">选择信号点</el-button>
        <el-button type="primary" @click="saveConfig" :disabled="isSubmit" size="mini">保存配置</el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <div style="overflow-y: auto; max-height: 600px;">
        <el-col :span="12">
          待配置项：
          <el-table
            border
            :data="modelSignals"
            style="width: 100%">
            <el-table-column
              type="index"
              width="50"
              align="center"
            >
            </el-table-column>
            <el-table-column
              prop="name"
              label="参数名"
              align="center"
            >
            </el-table-column>
          </el-table>
        </el-col>
        <el-col :span="12">
          配置信息：（可以拖动表格数据改变对应的数据关系）
          <table cellspacing="0" cellpadding="0" border style="width:100%; border-color: #CAE7EF;">
            <tr style="height: 44px;">
              <th width="50px" style="background-color: #e5f3f6;color: #1097bc;">#</th>
              <th width="90%" style="background-color: #e5f3f6;color: #1097bc;">版本信号点</th>
            </tr>
            <tr title="可以拖动改变对应的顺序" style="height: 44px;" v-for="(signal, index) in selectSignalList" :key="index" v-dragging="{ item: signal, list: selectSignalList, group: 'item' }">
              <td v-text="index + 1" width="50px" align="center"></td>
              <td v-text="signal.signalName" width="90%" align="center"></td>
            </tr>
            <tr v-if="!selectSignalList.length" style="height: 60px;">
              <td colspan="2" width="100%" align="center" style="color: #aaa">暂无数据</td>
            </tr>
          </table>
        </el-col>
      </div>
    </el-row>
    <conf-dialog :isShow.sync="isShow" :signalList="signalModelList"
                 @onSuccess="queryModelVersionSignal"
                 :modelSignals="modelSignals"
                 :modelVersionCode="selectModelVersionCode"
                 :oldConfigSignalList="oldConfigSignalList"
                 :maxSelectNum="modelSignals.length" :selectIds="dialogSelectSignalIds" v-if="isShow"></conf-dialog>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: 'normalized',
  components: {
    ConfDialog: () => import('./configSignalDialog.vue')
  },
  created () {
    this.oldConfigSignalList = []
  },
  data () {
    return {
      isShow: false,
      // 选择的设备类型
      selectDevTypeId: '',
      // 选择的设备版本
      selectModelVersionCode: '',
      // 模型点列表
      modelSignals: [],
      // 选择的信号点信息
      selectSignalList: [],
      // 已经配置了的信号点
      oldConfigSignalList: [],
      // 版本信息列表
      modelVersionList: [],
      // 选择版本对应的模板信号点列表
      signalModelList: [],
      // 在弹出信号点的时候获取当前选中的信号点，给表格回显
      dialogSelectSignalIds: [],
      // 是否在提交保存
      isSubmit: false
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    /**
     * 配置了的信号点
     * @param dbSignalList 数据库中版本配置了的归一化信息
     */
    configSelectSignalList (dbSignalList) {
      var modelSignals = this.modelSignals
      if (!modelSignals || modelSignals.length === 0) {
        this.selectSignalList = []
        return
      }
      let data = []
      if (!dbSignalList || dbSignalList.length === 0) { // 如果没有数据
        this.selectSignalList = data
        return
      }
      // 如果有数据
      let len = modelSignals.length
      for (let i = 0; i < len; i++) {
        let modelId = modelSignals[i].id
        var signal = dbSignalList.find(item => item.normalizedSignalId === modelId)
        if (signal) {
          data.push({
            id: signal.id,
            signalName: signal.signalName,
            signalId: signal.signalId,
            signalAddress: signal.signalAddress
          })
        } else { // 拿空的对应
          data.push({})
        }
      }
      this.selectSignalList = data
    },
    // 当版本改变去执行的事情
    queryModelVersionSignal () { // TODO 1.获取版本的模板信号点 2.已经配置好了的项
      if (!this.selectModelVersionCode) {
        return
      }
      this.$http.get('api/dev/normalized/info/ph/' + this.selectModelVersionCode).then(resp => {
        this.signalModelList = resp.data.devVersionSignals.sort((a, b) => {
          if (!a) {
            return !b ? 0 : -1
          }
          if (!b) {
            return 1
          }
          return a.id - b.id
        })
        this.oldConfigSignalList = resp.data.devNormalizedInfos || []
      }, err => {
        console.warn('has error:', err)
        this.signalModelList = []
        this.oldConfigSignalList = []
      }).catch(() => {
        this.signalModelList = []
        this.oldConfigSignalList = []
      })
    },
    // 查询版本信息
    queryModelVersionInfo () { // 需要发送ajax请求获取数据 1.获取版本 2.获取需要配置的项归一化模板
      if (!this.selectDevTypeId) {
        this.modelSignals = []
        this.modelVersionList = []
        return
      }
      // 获取信息
      this.$http.get('/api/dev/normalized/model/ph/' + this.selectDevTypeId).then(resp => {
        this.modelSignals = resp.data.normalizedModelList
        this.modelVersionList = resp.data.modelVersionCodeList || []
      }, er => {
        console.warn('has err:', er)
        this.modelSignals = []
        this.modelVersionList = []
      }).catch(() => {
        this.modelSignals = []
        this.modelVersionList = []
      })
    },
    showSelectSignals () { // 弹出选择信号点的弹出框
      if (!this.selectModelVersionCode) {
        this.$message.error('请选择设备版本')
        return
      }
      if (!this.signalModelList || this.signalModelList.length === 0) {
        this.$message.error('当前版本没有任何可选择的信号点')
        return
      }
      let selectSignalList = this.selectSignalList
      if (selectSignalList && selectSignalList.length > 0) { // 如果有了选择的就传递过去
        this.dialogSelectSignalIds = selectSignalList.map(s => s.signalId)
      }
      this.isShow = true
    },
    // 保存设置
    saveConfig () {
      let selectSignalList = this.selectSignalList
      if (!selectSignalList || selectSignalList.length === 0) {
        this.$message.warning('当前未选择任何信号点')
        return
      }
      let len = selectSignalList.length
      if (len !== this.modelSignals.length) { // 长度不一致
        this.$message.warning('选择信号点信息错误')
        return
      }
      let tmpDbData = []
      let selectModelVersionCode = this.selectModelVersionCode
      for (let i = 0; i < len; i++) {
        let tempRow = selectSignalList[i]
        if (!tempRow.signalId) {
          continue
        }
        tmpDbData.push({
          signalId: tempRow.signalId, // 信号点id
          signalName: tempRow.signalName, // 信号点名称
          signalAddress: tempRow.signalAddress, // 寄存器地址
          modelVersionCode: selectModelVersionCode, // 版本号
          normalizedSignalId: this.modelSignals[i].id // 归一化模型id
        })
      }
      if (tmpDbData.length === 0) {
        this.$message.warning('当前未选择任何信号点')
        return
      }
      this.isSubmit = true
      this.$http.post('api/dev/normalized', this.$qs.stringify({list: JSON.stringify(tmpDbData)})
      ).then(() => {
        this.queryModelVersionSignal()
        this.$message.info('保存成功')
        this.isSubmit = false
      }, err => {
        this.$message.error('保存失败,原因：' + err.message)
        this.isSubmit = false
      }).catch(() => {
        this.$message.error('服务器忙，请稍后重试')
        this.isSubmit = false
      })
    }
  },
  watch: {
    // 当版本对应的信号点内容改变触发的事件
    oldConfigSignalList (val) {
      this.configSelectSignalList(val)
    },
    selectDevTypeId (val) { // 设备类型改变的事件
      this.selectModelVersionCode = ''
      if (!val) {
        this.modelVersionList = []
        this.selectSignalList = []
        this.modelSignals = []
        return
      }
      this.queryModelVersionInfo()
    },
    selectModelVersionCode (val) {
      if (!val) { // 清空信息
        this.selectSignalList = []
      } else {
        this.queryModelVersionSignal()
      }
    }
  },
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

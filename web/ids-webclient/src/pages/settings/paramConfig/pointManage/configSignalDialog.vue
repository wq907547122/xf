<template>
  <el-dialog
    title="选择信号点"
    :visible.sync="isShow"
    width="96%"
    :before-close="handleClose">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="信号点名称">
        <el-input v-model="searchKey" placeholder="搜索信号点" clearable></el-input>
      </el-form-item>
      <el-form-item label="配置方式">
        <el-checkbox v-model="isUseDrag">拖动</el-checkbox>
      </el-form-item>
    </el-form>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-table
          :data="tableSinalList"
          ref="multipleTable"
          tooltip-effect="dark"
          style="width: 100%"
          :max-height="560"
          @selection-change="handleSelectionChange"
          @row-click="rowClick"
          border>
          <el-table-column
            type="selection"
            align="center"
            width="55">
          </el-table-column>
          <el-table-column
            align="center"
            label="信号点名称">
            <template slot-scope="scope">{{ scope.row.signalName }}</template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="move"
            width="100"
            label="操作">
            <template slot="header" slot-scope="scope">
              <el-button type="primary" class="el-icon-d-arrow-right" title="移动已勾选" @click.stop="multRowCurrent()"></el-button>
            </template>
            <template slot-scope="{row}">
              <el-button type="primary" class="el-icon-d-arrow-right" @click.stop="moveRowCurrent(row)"></el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :span="16">
        <el-row :gutter="20" style="margin-bottom: 10px;">
          <div style="text-align: center;font-size: 16px;margin-bottom: 5px;color: #00c6ff;border: 1px solid magenta;">最多选择信号点的数量: <span style="color: red" v-text="maxSelectNum"></span>, 当前选中的数量: <span style="color: red" v-text="dialogSelectIds.length"></span>
          </div>
          <el-col :span="8" v-text="'待配置项：' +maxSelectNum + '条'"></el-col>
          <el-col :span="16">配置信息(已选：<span v-text="dialogSelectIds.length"></span>),（可以拖动表格数据改变对应的数据关系）</el-col>
        </el-row>
        <el-row :gutter="20">
          <div style="overflow-y: auto; max-height: 44px;">
          <el-col :span="8">
            <table cellspacing="0" cellpadding="0" border style="width:100%; border-color: #CAE7EF;">
              <tr style="height: 44px;">
                <td width="50px" align="center" style="background-color: #e5f3f6;color: #1097bc;">#</td>
                <td align="center" style="background-color: #e5f3f6;color: #1097bc;">参数名</td>
              </tr>
            </table>
          </el-col>
          <el-col :span="16">
            <table cellspacing="0" cellpadding="0" border style="width:100%; border-color: #CAE7EF;">
              <tr style="height: 44px;">
                <td align="center" width="50px" style="background-color: #e5f3f6;color: #1097bc;">#</td>
                <td align="center" style="background-color: #e5f3f6;color: #1097bc;">版本信号点</td>
                <td align="center" width="120px" style="background-color: #e5f3f6;color: #1097bc;">
                  <el-button title="移除全部" type="danger" size="mini" icon="el-icon-delete" @click.stop="removeRowsAll()"></el-button>
                </td>
              </tr>
            </table>
          </el-col>
          </div>
        </el-row>
        <el-row :gutter="20">
          <div style="overflow-y: auto; max-height: 400px;">
            <el-col :span="8">
              <!--<el-table
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
              </el-table>-->
              <table cellspacing="0" cellpadding="0" border style="width:100%; border-color: #CAE7EF;">
                <tr style="height: 44px;" v-for="(m, index) in modelSignals" :key="index">
                  <td v-text="index + 1" width="50px" align="center"></td>
                  <td v-text="m.name" align="center"></td>
                </tr>
                <tr v-if="!modelSignals.length" style="height: 60px;">
                  <td colspan="2" width="100%" align="center" style="color: #aaa">暂无数据</td>
                </tr>
              </table>
            </el-col>
            <el-col :span="16">
              <table cellspacing="0" cellpadding="0" border style="width:100%; border-color: #CAE7EF;">
                <!--<tr style="height: 44px;">
                  <th width="50px" style="background-color: #e5f3f6;color: #1097bc;">#</th>
                  <th style="background-color: #e5f3f6;color: #1097bc;">版本信号点</th>
                  <th width="50px" style="background-color: #e5f3f6;color: #1097bc;">
                    <el-button title="移除全部" type="danger" size="mini" icon="el-icon-remove" @click.stop="removeRowsAll()"></el-button>
                  </th>
                </tr>-->
                <tr title="可以拖动改变对应的顺序" style="height: 44px;" v-for="(signal, index) in selectDatas" :key="index" v-dragging="{ item: signal, list: selectDatas, group: 'item' }">
                  <td v-text="index + 1" width="50px" align="center"></td>
                  <td v-text="signal.signalName" align="center"></td>
                  <td width="120px" align="center">
                    <el-button v-if="signal.id" title="移除并设置为空白行" type="success" size="mini" icon="el-icon-circle-close" @click.stop="removeCurrent(index, true)"></el-button>
                    <el-button v-if="signal.id" title="移除" type="success" size="mini" icon="el-icon-remove" @click.stop="removeCurrent(index)"></el-button>
                  </td>
                </tr>
                <tr v-if="!selectDatas.length" style="height: 60px;">
                  <td colspan="3" width="100%" align="center" style="color: #aaa">暂无数据</td>
                </tr>
              </table>
            </el-col>
          </div>
          <div style="text-align: center;margin-top: 10px;">
            <el-button type="primary" @click="onSelect">确 定</el-button>
            <el-button @click="handleClose">取 消</el-button>
          </div>
        </el-row>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
const props = {
  isShow: {
    type: Boolean,
    default () {
      return false
    }
  },
  signalList: { // 可选择的信号点
    type: Array,
    default () {
      return []
    }
  },
  maxSelectNum: { // 最大选择信号点的数量
    type: Number,
    default () {
      return 0
    }
  },
  selectIds: { // 选中的信号点的id
    type: Array,
    default () {
      return []
    }
  },
  modelSignals: { // 版本模型信号点
    type: Array,
    default () {
      return []
    }
  },
  modelVersionCode: { // 当前配置的版本号
    type: String,
    default () {
      return null
    }
  },
  oldConfigSignalList: {
    type: Array,
    default () {
      return []
    }
  }
}
export default {
  props,
  created () {
    this.tableSinalList = this.signalList
  },
  components: {},
  data () {
    return {
      selectDatas: [],
      selectRows: [],
      // 是否正在提交
      isSubmit: false,
      // 选中的id
      dialogSelectIds: [],
      // 搜索字段
      searchKey: '',
      // 表格的数据
      tableSinalList: [],
      // 表格已勾选的记录
      tableSelectRows: [],
      // 是否使用拖动
      isUseDrag: true
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(() => {
      let ids = this.selectIds
      if (ids && ids.length > 0) { // 获取当前选中的数据
        ids.forEach(id => {
          if (id) {
            this.dialogSelectIds.push(id)
            this.selectDatas.push(this.signalList.find(s => id === s.id) || {})
          } else {
            this.selectDatas.push({})
          }
        })
      }
    })
  },
  methods: {
    handleClose () { // 关闭弹出框
      if (this.isSubmit) {
        this.$message.info('数据提交中，请稍候。。。')
        return
      }
      this.$emit('update:isShow', false)
    },
    handleSelectionChange (rows) {
      this.tableSelectRows = rows
    },
    // 多选数据
    multRowCurrent () {
      if (this.tableSelectRows.length === 0) {
        this.$message.warning('未选择需要移动的数据')
      }
      let len = this.tableSelectRows.length
      for (let i = 0; i < len; i++) {
        if (this.moveRowCurrent(this.tableSelectRows[i], false)) {
          console.log('12121')
          return false
        }
      }
    },
    /**
     * 新增一行
     * @param row 新增的行
     * @param isTip 信号点已经存在是否的提示信息 true：显示提示 false：不显示提示
     */
    moveRowCurrent (row, isTip = true) {
      if (this.dialogSelectIds.indexOf(row.id) >= 0) {
        isTip && this.$message.warning('信号点已经存在')
        return false
      }
      if (this.dialogSelectIds.length === this.maxSelectNum) {
        this.$message.warning('信号点已经达到最大的选择，如果需要更改信号点，请先删除右边你需要替换的信号点')
        return true
      }
      this.dialogSelectIds.push(row.id)
      this.addSignal(row)
      return false
    },
    addSignal (row) { // 往右边添加数据
      if (this.selectDatas.length === 0) { // 如果当前没有任何的配置，第一次配置，需要将所有行占满
        this.selectDatas.push(row)
        let len = 1
        while (len++ < this.maxSelectNum) { // 之后的都添加空行
          this.selectDatas.push({})
        }
      } else { // 显示的数据量已经够了，只是里面有{}空的数据，只需替换第一个找到的空的数据就可以了
        let maxLength = this.selectDatas.length
        let index = -1
        let isFind = false
        for (let i = 0; i < maxLength; i++) {
          if (!this.selectDatas[i].id) { // 替换第一个找到的空的数据
            if (this.selectDatas[i].isBlank) {
              // 标记第一个手动删除的需要替换的
              (index === -1) && (index = i)
            } else { // 找到的第一个不是被点击删除的信息
              isFind = true
              this.selectDatas[i] = row
              break
            }
          }
        }
        // 如果没有找到空行的，就替换blank的
        if (!isFind && index !== -1) { // 使用第一个标记为blank的数据
          this.selectDatas[index] = row
        }
        [...this.selectDatas] = this.selectDatas
      }
    },
    removeRowsAll () { // 移除全部
      if (this.dialogSelectIds.length === 0) {
        this.$message.warning('已经没有任何信号点配置')
        return
      }
      this.dialogSelectIds = []
      this.selectDatas = []
    },
    // 删除一个信号点的事件
    removeCurrent (index, isBlank = false) {
      // 先拷贝
      let [...selectDatas] = this.selectDatas
      let data = selectDatas[index]
      if (!data || !data.id) { // 如果数据不存在,不再执行删除
        return
      }
      // 移除指定的元素，并且改变原数据组(当前选中了的数据减少1)
      this.dialogSelectIds.remove(data.id)
      if (this.dialogSelectIds.length === 0) { // 如果没有任何元素了就不给数据了
        this.selectDatas = []
      } else {
        // 当前作为空白
        selectDatas[index] = {isBlank}
        this.selectDatas = selectDatas
      }
      [...this.selectDatas] = this.selectDatas
    },
    // 点击改变选中状态
    toggleSelection (rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    // 点击行的事件
    rowClick (row) {
      this.toggleSelection([row])
    },
    // 点击确认的按钮
    onSelect () {
      // 保存信号点
      if (this.selectDatas.length === 0) {
        this.$message.warning('未选择信号点')
        return
      }
      // TODO 准备数据
      let selectDatas = this.selectDatas
      let len = selectDatas.length
      if (len !== this.modelSignals.length) { // 长度不一致
        this.$message.warning('选择信号点信息错误')
        return
      }
      let tmpDbData = []
      let selectModelVersionCode = this.modelVersionCode
      for (let i = 0; i < len; i++) {
        let tempRow = selectDatas[i]
        if (!tempRow.id) { // 如果没有版本信号点id
          continue
        }
        tmpDbData.push({
          signalId: tempRow.id, // 信号点id
          signalName: tempRow.signalName, // 信号点名称
          signalAddress: tempRow.signalAddress, // 寄存器地址
          modelVersionCode: selectModelVersionCode, // 版本号
          normalizedSignalId: this.modelSignals[i].id // 归一化模型id
        })
      }
      if (tmpDbData.length === 0) {
        this.$message.warning('未选择信号点')
        return
      }
      this.$confirm('确认提交？').then(() => {
        this.isSubmit = true
        this.$http.post('api/dev/normalized', this.$qs.stringify({list: JSON.stringify(tmpDbData)})
        ).then(() => {
          this.$message.info('保存成功')
          this.isSubmit = false
          this.$emit('onSuccess')
          this.handleClose()
        }, err => {
          this.$message.error('保存失败,原因：' + err.message)
          this.isSubmit = false
        }).catch(() => {
          this.$message.error('服务器忙，请稍后重试')
          this.isSubmit = false
        })
      })
    }
  },
  watch: {
    searchKey (val) {
      if (val && val.trim()) {
        let tmp = val.trim().toUpperCase()
        this.tableSinalList = this.signalList.filter(s => s.signalName.toUpperCase().indexOf(tmp) !== -1)
      } else {
        this.tableSinalList = this.signalList
      }
    }
  },
  computed: {
    isNormal () {
      return this.dialogSelectIds.length <= this.maxSelectNum
    }
  }
}
</script>

<style lang="less" scoped>
  .myRed{
    color: red;
  }
</style>

<template>
  <el-dialog
    title="区域"
    :visible="isShowDialog"
    width="35%"
    :close-on-click-modal="false"
    :before-close="closeDialog"
    center>
    <el-form :model="regionData" :rules="rules" ref="regionForm" class="formtable">
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="parentId" :label="$t('modules.main.menu.settings.domainManager.domainOwer')" :label-width="labelWidth">
           <!-- <el-input v-model="regionData.parentId" :placeholder="$t('modules.main.menu.settings.domainManager.domainOwer')"></el-input>-->
            <choose-tree-select :treeData="treeData"
                                :props="props"
                                accordion :isClickLeaf="false" :value-mode.sync="regionData.parentId"></choose-tree-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="name" :label="$t('modules.main.menu.settings.domainManager.name')" :label-width="labelWidth">
            <el-input v-model="regionData.name" :placeholder="$t('modules.main.menu.settings.domainManager.name')"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="currency" :label="$t('modules.main.menu.settings.domainManager.currency')" :label-width="labelWidth">
            <el-select v-model="regionData.currency" placeholder="请选择">
              <el-option
                v-for="item in currencys"
                :key="item.label"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="domainPrice" :label="$t('modules.main.menu.settings.domainManager.domainPrice')" :label-width="labelWidth">
            <el-input v-model="regionData.domainPrice" :placeholder="$t('modules.main.menu.settings.domainManager.domainPrice')"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row  class="myElRowClass">
        <el-col :span="24">
          <el-form-item label="区域范围" prop="latitude" :label-width="labelWidth">
            <input-for-position
              :lat="regionData.latitude"
              :lng="regionData.longitude"
              :radius="regionData.radius"
              :isPosition="false"
              :isCanChoose="true"
              @myChange="dataCirleChange"
              class="myPositionClass"
            ></input-for-position>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="myElRowClass">
        <el-col :span="24">
          <el-form-item prop="description" :label="$t('modules.main.menu.settings.domainManager.description')" :label-width="labelWidth">
            <el-input type="textarea" :rows="3" v-model="regionData.description" :placeholder="$t('modules.main.menu.settings.domainManager.description')"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitData" :disabled="isSubmiting">确 定</el-button>
      <el-button @click="closeDialog" :disabled="isSubmiting">取 消</el-button>
    </span>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import InputForPosition from '@/components/choosePosition/inputForPosition.vue'
import ChooseTreeSelect from '@/components/chooseTreeSelect/index.vue'

// 由父节点传递过来的属性
const props = {
  isShowDialog: false, // 是否显示对话框
  treeData: { // 选择区域的树节点的数据
    type: Array,
    default () {
      return []
    }
  },
  enterpriseId: null, // 企业id
  parentId: null, // 区域的父节点
  eidtorRow: null // 修改的时候传递过来的id
}
export default {
  created () {
    if (this.eidtorRow) { // 是修改
      this.regionData = this.eidtorRow
      this.isEditor = true
    } else {
      this.regionData.enterpriseId = this.enterpriseId
      if (this.parentId) {
        this.regionData.parentId = this.parentId
      }
    }
  },
  props: props,
  components: {
    InputForPosition,
    ChooseTreeSelect
  },
  data () {
    return {
      isEditor: false, // 是否是修改
      regionData: {},
      labelWidth: '120px', // 显示文本的宽度
      currencys: [
        {label: '人民币', value: 'rmb'},
        {label: '美元', value: '$'}
      ],
      rules: {
        name: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'blur'}
        ],
        latitude: [
          {required: true, message: this.$t('modules.main.menu.settings.enterpriseManage.req'), trigger: 'change'}
        ]
      },
      isSubmiting: false, // 是否正在提交
      props: {
        label: 'name'
      }
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    /**
     * 关闭对话框
     */
    closeDialog () {
      this.$emit('update:isShowDialog', false)
    },
    dataCirleChange (lat, lng, r) { // 经纬度改变的事件
      this.$set(this.regionData, 'latitude', lat)
      this.$set(this.regionData, 'longitude', lng)
      this.$set(this.regionData, 'radius', r)
    },
    submitData () {
      this.$refs.regionForm.validate(
        valid => {
          if (valid) {
            this.isSubmiting = true
            let url = (this.isEditor && 'api/user/domain/update') || 'api/user/domain/save'
            this.$http.post(url, this.$qs.stringify(this.regionData)).then(() => {
              this.isSubmiting = false
              this.$emit('handleSuccess')
              this.$message.success('操作成功')
              this.closeDialog()
            }, err => {
              this.$message.error(err.message || '操作失败')
              this.isSubmiting = false
            }).catch((e) => {
              this.isSubmiting = false
              this.$message.error(e.message || '操作失败,服务器异常')
            })
          } else {
            console.log('验证未通过')
          }
        }
      )
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/stylus/formtable.less';
</style>

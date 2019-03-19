<template>
  <!-- 新增或者修改电站的弹出框 -->
  <el-dialog
    title="电站"
    :visible="isShowDialog"
    :close-on-click-modal="false"
    width="60%"
    :before-close="closeDialog"
    center
  >
    <el-form :model="stationData" :rules="rules" ref="stationForm" class="station-form formtable">
      <div style="font-weight: bold">基本信息</div>
      <div>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="domainId" label="电站归属" :label-width="labelWidth">
              <choose-tree-select
                :treeData="treeData"
                :props="props"
                accordion :isClickLeaf="false" :value-mode.sync="stationData.domainId"
              ></choose-tree-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="stationName" label="电站名称" :label-width="labelWidth">
              <el-input v-model="stationData.stationName" placeholder="电站名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="installedCapacity" label="装机容量(kW)" :label-width="labelWidth">
              <!--<el-input v-model="stationData.installedCapacity"></el-input>-->
              <el-input-number v-model="stationData.installedCapacity" :precision="3" :min="0" :max="9999999999"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="produceDate" label="并网时间" :label-width="labelWidth">
              <el-date-picker :format="$t('dateFormat.yyyymmdd')" v-model="stationData.produceDate" type="date" style="width: 100%;"
                              value-format="yyyy-MM-dd" :editable="false">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="onlineType" label="并网类型" :label-width="labelWidth">
              <el-select style="width: 100%;" v-model="stationData.onlineType" placeholder="请选择">
                <el-option v-for="item in onlineTypes" :key="item.value"
                           :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="stationBuildStatus" label="电站状态" :label-width="labelWidth">
              <el-select style="width: 100%;" v-model="stationData.stationBuildStatus" placeholder="请选择">
                <el-option v-for="item in stationBuildStatusArr" :key="item.value"
                           :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="contactPeople" label="联系人" :label-width="labelWidth">
              <el-input v-model="stationData.contactPeople"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="phone" label="联系电话" :label-width="labelWidth">
              <el-input v-model="stationData.phone" :maxLength="11"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="24">
            <el-form-item prop="latitude" label="经纬度" :label-width="labelWidth">
              <input-for-position
                :lat="stationData.latitude"
                :lng="stationData.longitude"
                @myChange="dataCirleChange"
                class="myPositionClass"
              ></input-for-position>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="24">
            <el-form-item prop="prov" label="所在区域" :label-width="labelWidth">
              <el-select style="width: 90px" v-model="stationData.prov" placeholder="请选择" @change="provChange" filterable>
                <el-option v-for="(item, index) in cityInfo.prov" :key="item.code" :label="item.name"
                           :value="item.code + '_' + index">
                </el-option>
              </el-select>
              <el-select style="width: 100px" v-model="stationData.city" placeholder="请选择" @change="cityChange" filterable>
                <el-option v-for="(item, index) in citys" :key="item.code" :label="item.name"
                           :value="item.code + '_' + index">
                </el-option>
              </el-select>
              <el-select style="width: 100px" v-if="isShowDist" v-model="stationData.dist"
                         placeholder="请选择" filterable>
                <el-option v-for="(item, index) in dists" :key="item.code" :label="item.name"
                           :value="item.code + '_' + index">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="24">
              <el-form-item prop="stationAddr" label="详细地址" :label-width="labelWidth">
                <el-input v-model="stationData.stationAddr"></el-input>
              </el-form-item>
          </el-col>
        </el-row>
        <el-row class="myElRowClass">
          <el-col :span="12">
            <el-form-item prop="stationFileId" label="电站图片" :label-width="labelWidth">
              <img-upload :fileId.sync="stationData.stationFileId"></img-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="stationDesc" label="电站简介" :label-width="labelWidth">
              <el-input type="textarea" v-model="stationData.stationDesc" rows="5"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm" :disabled="isSubmiting">确 定</el-button>
      <el-button @click="closeDialog" :disabled="isSubmiting">取 消</el-button>
    </span>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import cityInfo from './cityInfo' // 城市信息

const props = {
  isShowDialog: false,
  enterpriseId: null, // 企业id
  editorId: null // 修改记录的id
}

export default {
  created () {
    if (this.enterpriseId) {
      this.handlerTreeData(this.enterpriseId)
      this.stationData.enterpriseId = this.enterpriseId
    }
    if (this.editorId) { // 查询电站信息
      this.isEditor = true
      this.queryStation()
    }
  },
  props: props,
  components: {
    ChooseTreeSelect: () => import('@/components/chooseTreeSelect/index.vue'),
    InputForPosition: () => import('@/components/choosePosition/inputForPosition.vue'),
    ImgUpload: () => import('@/components/imgUpload/index.vue')
  },
  data () {
    const validateStationName = (rule, value, callback) => {
      // 拼接接口 api/user/station/check/ph/{stationName}  stationName:验证的电站名
      let url = 'api/user/station/check/ph/' + value
      this.$http.get(url, {params: {id: this.stationData.id}}).then(resp => {
        if (resp.data) { // 验证通过
          callback()
        } else {
          callback(new Error('电站名称重复'))
        }
      }).catch(() => {
        callback(new Error('电站名称重复'))
      })
    }
    return {
      labelWidth: '130px',
      isSubmiting: false, // 是否在提交
      stationData: {
        isPovertyRelief: 0
      }, // 电站的表单数据
      // 验证规则
      rules: {
        domainId: [
          {required: true, message: '区域必选', trigger: 'change'}
        ],
        stationName: [
          {required: true, message: '电站名称必填', trigger: 'blur'},
          {validator: validateStationName, message: '电站名称已存在', trigger: 'blur'} // 验证电站名称是否重复
        ],
        installedCapacity: [
          {required: true, message: '必填', trigger: 'blur'}
        ],
        produceDate: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        onlineType: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        stationBuildStatus: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        latitude: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        contactPeople: [
          {required: true, message: '必填', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        prov: [
          {required: true, message: '必填', trigger: 'change'}
        ],
        stationAddr: [
          {required: true, message: '必填', trigger: 'blur'}
        ]
      },
      treeData: [],
      props: {
        label: 'name'
      },
      onlineTypes: [ // 并网类型 1:地面式 2:分布式 3:户用
        {
          value: 1,
          label: '地面式'
        },
        {
          value: 2,
          label: '分布式'
        },
        {
          value: 3,
          label: '户用'
        }
      ],
      stationBuildStatusArr: [ // 电站状态：1:并网 2:在建 3:规划
        {
          value: 1,
          label: '并网'
        },
        {
          value: 2,
          label: '在建'
        },
        {
          value: 3,
          label: '规划'
        }
      ],
      cityInfo, // 省市县的信息
      citys: [],
      dists: [],
      isShowDist: false, // 是否显示第三个下拉框
      isEditor: false // 是否是修改数据
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    closeDialog () {
      this.$emit('update:isShowDialog', false)
    },
    handlerTreeData (id) { // 获取选中区域的节点信息
      if (!id) {
        this.treeData = []
        return
      }
      // 查询区域
      this.$http.get('api/user/domain/list', {params: {id: id}}).then(resp => {
        this.treeData = resp.data || []
      }, () => { // 返回的是错误码
        this.treeData = []
      }).catch(() => {
        this.treeData = []
      })
    },
    queryStation () { // 查询电站信息
      this.$http.get('api/user/station/' + this.editorId).then(resp => {
        let data = resp.data
        delete data.createDate
        delete data.updateDate
        data.produceDate = new Date(data.produceDate).format('yyyy-MM-dd')
        let pcdArr = data.areaCode.split('@')
        data.prov = pcdArr[0]
        // 拼接对应的选项
        let provs = cityInfo.prov
        for (let key in provs) {
          if (provs[key].code === data.prov) {
            data.prov += '_' + key
            break
          }
        }
        data.city = pcdArr[1]
        let citys = cityInfo.city
        for (let key in citys) {
          if (citys[key].code === data.city) {
            data.city += '_' + key
            break
          }
        }
        data.dist = pcdArr[2]
        if (data.dist) {
          let dists = cityInfo.dist
          for (let key in dists) {
            if (dists[key].code === data.dist) {
              data.dist += '_' + key
              break
            }
          }
        }
        this.stationData = resp.data
        this.provChange(data.prov)
        this.cityChange(data.city)
      })
    },
    dataCirleChange (lat, lng) { // 选择位置改变后的事件
      this.$set(this.stationData, 'latitude', lat)
      this.$set(this.stationData, 'longitude', lng)
    },
    provChange (value) { // 省改变的事件
      if (value) {
        let cityId = value.split('_')[1]
        this.citys = this.cityInfo.city[cityId]
        let distId
        for (let i in this.citys) {
          distId = i
          break
        }
        this.stationData.city = this.citys[distId].code + '_' + distId
        if (this.$data.cityInfo.dist[distId]) {
          this.isShowDist = true
          this.dists = this.cityInfo.dist[distId]
          let d = ''
          for (let i in this.dists) {
            d = i
            break
          }
          this.stationData.dist = this.dists[d].code + '_' + d
        } else {
          this.isShowDist = false
          this.dists = []
        }
      }
    },
    cityChange (value) { // 市改变的事件
      if (value) {
        let distId = value.split('_')[1]
        if (this.cityInfo.dist[distId]) {
          this.isShowDist = true
          this.dists = this.cityInfo.dist[distId]
          let d = ''
          for (let i in this.dists) {
            d = i
            break
          }
          this.stationData.dist = this.dists[d].code + '_' + d
        } else {
          this.isShowDist = false
          this.dists = []
          this.stationData.dist = ''
        }
      }
    },
    submitForm () { // 提交form表单
      this.$refs.stationForm.validate(valid => {
        if (valid) {
          // 1.修改区域的位置
          console.log(this.stationData)
          let areaCode = this.stationData.prov.split('_')[0] + '@'
          areaCode += this.stationData.city.split('_')[0] + '@'
          if (this.stationData.dist) {
            areaCode += this.stationData.dist.split('_')[0]
          }
          this.stationData.areaCode = areaCode
          // 提交表单
          this.isSubmiting = true
          let url = this.isEditor ? 'api/user/station/update' : 'api/user/station/save'
          this.$http.post(url, this.$qs.stringify(this.stationData)).then(resp => {
            this.isSubmiting = false
            this.$message.success('操作成功')
            this.$emit('stationSuccess')
            this.closeDialog()
          }, error => {
            this.isSubmiting = false
            this.$message.error(error.message || '操作失败')
          }).catch(() => {
            this.$message.error('操作失败，服务异常')
          })
        } else {
          console.log('不能提交,验证不通过')
        }
      })
    }
  },
  watch: {
    enterpriseId (value) {
      if (value) { // 加载区域的数据
        this.handlerTreeData(value)
      }
    }
  },
  computed: {}
}
</script>

<style lang="less" scoped>
  @import (once) '~@/assets/css/stylus/formtable.less';
</style>

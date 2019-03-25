<template>
  <!-- 点表管理模块：点表导入和归一化配置的信息 -->
  <div>
    <!--<el-upload
      class="upload-demo"
      ref="upload"
      :action="'/api/dev/point?tokenId=' + tokenId"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      name="file"
      :auto-upload="false">
      <input type="hidden" name="tokenId" :value="tokenId"/>
      <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
      <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
    </el-upload>-->
    <el-form :inline="true" style="float: left;">
      <el-form-item>
        <el-input disabled :value="myFileName" placeholder="请选择文件"></el-input>
      </el-form-item>
      <el-form-item>
        <fileUpload
          :uploadUrl="importUrl"
          fileTypeError="请选择excl文件"
          @on-validate-success="setInputText"
          :isShowTip="false"
          :userValidate="myFileType"
          @on-upload-success="uploadSuccess"
        ></fileUpload>
      </el-form-item>
    </el-form>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  components: {
    fileUpload: () => import('@/components/fileUpload/index.vue')
  },
  data () {
    return {
      tokenId: sessionStorage.getItem('token-id'),
      importUrl: '/api/dev/point?tokenId=' + sessionStorage.getItem('token-id'),
      myFileName: null,
      // 允许的文件类型 excel
      myFileType: ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel', 'text/csv']
    }
  },
  filters: {},
  mounted: function () {
    this.$nextTick(function () {
    })
  },
  methods: {
    // submitUpload () { // 提交
    //   this.$refs.upload.submit()
    // },
    uploadSuccess (response, file, fileList) { // 上传成功的提示信息
    },
    // uploadError () { // 上传失败
    //   this.$message.error('导入失败')
    // },
    setInputText (file) { // 上传验证成功后修改显示的信息
      this.myFileName = file.name
    }
  },
  watch: {},
  computed: {}
}
</script>

<style lang="less" scoped>
</style>

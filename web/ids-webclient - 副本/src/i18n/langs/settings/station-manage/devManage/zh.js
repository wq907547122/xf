// 设备管理界面以及里面的国际化
export default {
  devComparison: { // 设备对比目录
    listModel: { // 列表页面
      staticNameList: ['厂家名称', '组件所属厂家', '组件投产日期', '组件类型', '标称组件转换效率(%)', '峰值功率温度系数(%)',
        '组串1容量(kW)', '组串2容量(kW)', '组串3容量(kW)', '组串4容量(kW)', '组串5容量(kW)', '组串6容量(kW)', '组串7容量(kW)', '组串8容量(kW)', '组串9容量(kW)',
        '组串10容量(kW)', '组串11容量(kW)', '组串12容量(kW)', '组串13容量(kW)', '组串14容量(kW)', '组串15容量(kW)', '组串16容量(kW)', '组串17容量(kW)', '组串18容量(kW)',
        '组串19容量(kW)', '组串20容量(kW)'],
      // 显示运行数据的第一列
      runDataNameList: ['发电量(kWh)', '输出功率(kW)', '组串1电压/电流', '组串2电压/电流', '组串3电压/电流', '组串4电压/电流', '组串5电压/电流',
        '组串6电压/电流', '组串7电压/电流', '组串8电压/电流', '组串9电压/电流', '组串10电压/电流', '组串11电压/电流', '组串12电压/电流',
        '组串13电压/电流', '组串14电压/电流', '组串15电压/电流', '组串16电压/电流', '组串17电压/电流', '组串18电压/电流', '组串19电压/电流',
        '组串20电压/电流'],
      // 组件类型0-9 分别依次对应1-10值得对应值
      componentType: ['多晶', '单晶', 'N型单晶', 'PERC单晶(单晶PERC)', '单晶双玻', '多晶双玻',
        '单晶四栅60片', '单晶四栅72片', '多晶四栅60片', '多晶四栅72片']
    }
  }
}

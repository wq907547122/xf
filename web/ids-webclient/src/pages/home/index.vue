<template>
    <div>
      home
      <v-chart theme="ovilia-green" :options="options" style="width: 20%;height: 300px;"></v-chart>
      <v-chart theme="ovilia-green" :options="options2" style="width: 20%;height: 300px;"></v-chart>
    </div>
</template>

<script type="text/ecmascript-6">
// 按需求添加需要的组件
import 'echarts/lib/chart/line'
import 'echarts/lib/component/dataZoom'
export default {
  components: {},
  data () {
    return {
      options: {
        title: {
          text: '会员数据统计',
          subtext: '动态数据',
          x: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          show: true,
          orient: 'vertical',
          left: 'left',
          data: ['微信访问', '公众号访问', '扫码进入', '分享进入', '搜索访问']
        },
        series: [
          {
            name: '访问来源',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              {value: 335, name: '微信访问'},
              {value: 310, name: '公众号访问'},
              {value: 234, name: '扫码进入'},
              {value: 135, name: '分享进入'},
              {value: 1548, name: '搜索访问'}
            ],
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      },
      options2: {
        noDataLoadingOption: {
          text: '无数据',
          effect: 'bubble',
          effectOption: {
            effect: {
              n: 0 // 气泡个数为0
            }
          }
        },
        color: ['#1EB05B', '#0F95EF'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            lineStyle: {
              color: '#0D96F0'
            }
          }
          // formatter: tooltips
        },
        dataZoom: [
          {
            type: 'slider',
            show: true,
            height: 6,
            backgroundColor: 'rgba(10, 159, 254, 0.2)',
            filterMode: 'empty',
            fillerColor: 'rgba(10, 159, 254, 0.5)',
            borderColor: 'rgba(10, 159, 254, 0.2)',
            borderRadius: 10,
            handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            handleSize: 20,
            handleStyle: {
              color: '#0a9ffe'
            },
            shadowBlur: 30,
            shadowColor: 'rgba(10, 159, 254, 0.5)',
            shadowOffsetX: 2,
            shadowOffsetY: 2,
            textStyle: {
              color: '#666'
            },
            left: 30,
            right: 56,
            bottom: 10,
            xAxisIndex: [0],
            start: 0,
            end: 100
          },
          {
            type: 'inside'
          }
        ],
        legend: {
          data: ['发电量', '收益'],
          textStyle: {
            fontSize: 14,
            color: '#000',
            fontWeight: 500
          },
          selectedMode: false
        },
        grid: {
          left: 20,
          right: 20,
          bottom: 24,
          top: '30%',
          containLabel: true,
          borderColor: '#EEEEEE'
        },
        xAxis: [
          {
            type: 'category',
            data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17],
            axisTick: {
              alignWithLabel: true
            },
            axisLine: {
              lineStyle: {
                color: '#3BABC4'
              }
            },
            splitLine: {
              show: false,
              lineStyle: {
                color: '#000',
                type: 'dashed'
              }
            }
          }
        ],
        yAxis: [
          {
            name: 'kWh',
            type: 'value',
            nameTextStyle: {
              color: '#000'
            },
            axisLine: {
              lineStyle: {
                color: '#3BABC4'
              }
            },
            splitLine: {
              show: false,
              lineStyle: {
                color: '#000',
                type: 'dashed'
              }
            }
          }, {
            name: '¥',
            type: 'value',
            nameTextStyle: {
              color: '#000'
            },
            axisLine: {
              lineStyle: {
                color: '#3BABC4'
              }
            },
            splitLine: {
              show: false,
              lineStyle: {
                color: '#000',
                type: 'dashed'
              }
            }
          }
        ],
        series: [
          {
            name: '发电量',
            type: 'bar',
            barCategoryGap: '50%',
            // barWidth: '30%',
            barWidth: 4,
            data: [8, 5, 6],
            yAxisIndex: 0,
            itemStyle: {
              normal: {
                barBorderRadius: 5,
                z: 0
              }
            }
          },
          {
            name: '收益',
            type: 'line',
            barWidth: '13',
            data: [15, 33, 55],
            symbol: 'circle',
            symbolSize: 8,
            yAxisIndex: 1
          }
        ]
      }
    }
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
</style>

import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
// 设备管理界面的URL到路由的那一层结束
import devManage from './settings/station-manage/devManage/zh'
import Menu from './menu/zh'
import DevTypeId from './devTypeId/zh'

export default {
  systemName: '智慧新能源管理系统',
  exhibition_systemName: '智慧新能源云中心',
  // systemName: 'Sienergy 智享能源云平台',
  // exhibition_systemName: 'Sienergy 智享能源云中心',
  copyright: '@2018 版权所有',
  about: '智慧新能源管理系统研发部',
  sure: '确定',
  cancel: '取消',
  close: '关闭',
  confirm: '提示',
  search: '查询',
  create: '新增',
  copy: '复制',
  update: '修改',
  modify: '修改',
  delete: '删除',
  detail: '详情',
  export: '导出',
  print: '打印',
  operatSuccess: '操作成功',
  operatFailed: '操作失败',
  selectOneRecord: '请选择一条您要操作的记录。',
  deleteCancel: '已取消删除',
  deleteSuccess: '删除成功!',
  deleteMsg: '此操作将永久删除该数据, 是否继续?',
  unit: {
    colon: '：',
    brackets: ['（', '）'],
    KWh: 'kWh',
    MWh: 'MWh',
    WKWh: '万kWh',
    GWh: 'GWh',
    KW: 'kW',
    MW: 'MW',
    GW: 'GW',
    RMBUnit: '元',
    KRMBUnit: '千元',
    WRMBUnit: '万元',
    co2Unit: 't',
    coalUnit: 't',
    treeUnit: '颗',
    co2WUnit: '万t',
    coalWUnit: '万t',
    treeWUnit: '万m³',
    days: '天',
    door: '户',
    inverterUnit: '台',
    temperatureUnit: '℃'
  },
  dateFormat: {
    yyyymmddhhmmss: 'yyyy-MM-dd HH:mm:ss',
    yyyymmdd: 'yyyy-MM-dd'
  },
  common: {
    stationName: '电站名称',
    startTime: '开始时间',
    endTime: '结束时间',
    devName: '设备名',
    operation: '操作',
    detail: '详情',
    timeDuration: '时间范围',
    submit_sucess: '提交成功',
    submit_fail: '提交失败',
    confirm_delte: '确认删除？',
    delete_success: '删除成功',
    delete_fail: '删除失败',
    modify_success: '更新成功',
    modify_fail: '更新失败',
    choose_file: '选择文件',
    import_success: '导入成功',
    import_fail: '导入失败'
  },
  modules: {
    add: '新增',
    edit: '修改',
    del: '删除',
    search: '查询',
    confirm: '提示',
    allSelect: '全部',
    login: {
      username: '用户名',
      password: '密码',
      placeholder: {
        username: '请输入用户名',
        password: '请输入密码'
      },
      saveUserName: '记住密码',
      loginBtnText: '登录',
      messages: {
        loginSuccess: '登录成功！',
        logoutConfirm: '确认退出吗？',
        noRole: '该用户没有登录系统权限，请更换用户',
        errorUserOrPassword: '用户名或者密码错误！',
        loginFail: '登录失败，请稍后重试！'
      }
    },

    main: {
      header: {
        gotoExhibition: '大数据可视化'
      },
      userInfo: {
        username: '用户名称 :',
        userRole: '用户角色 :',
        company: '所属公司 :',
        address: '公司地址 :',
        modifyPassword: '修改密码',
        modifyUserInfo: '个人资料',
        cancellation: '退出'
      },
      modifyPassword: {
        validate: {
          oldPasswordRequired: '请输入当前密码',
          passwordError: '输入的密码错误',
          passwordRequired: '请输入新密码',
          checkPasswordRequired: '请再次输入新密码',
          checkNotEqual: '两次输入密码不一致!'
        },
        message: {
          submitSuccess: '密码修改成功！',
          submitFail: '密码修改失败，请稍后再试！'
        }
      },
      menu: {
        home: '总览',
        io: {
          title: '运维中心',
          platform: '运维工作台',
          alarmManage: {
            alarm: {
              title: '设备告警'
            },
            smartAlarm: {
              title: '智能告警'
            }
          },
          taskManage: {
            title: '任务管理'
          }
        },
        pm: {
          title: '报表管理',
          stationReport: {
            title: '电站日月年报表'
          },
          reportManage: '报表管理'
        },
        pam: {
          title: '合作伙伴',
          assets: '资产',
          exchangeOfGoods: '退换货',
          statistics: '统计'
        },
        wisdom: {
          title: '智慧应用'
        },
        settings: {
          title: '系统设置',
          stationManage: {
            title: '电站管理',
            stationBinding: '电站绑定',
            station: {
              title: '电站信息'
            },
            devManage: {
              title: '设备管理',
              devComparision: { // 设备对比的页面国际化
                // 静态数据表格数据第一列的数据
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
            },
            devConfig: {
              title: '设备通讯',
              devName: '设备名称',
              devType: '设备类型',
              channelType: '通道类型',
              portNum: '端口号',
              devVersion: '设备版本',
              ipAddress: 'IP',
              req: '必填',
              logicAddress: '逻辑地址',
              channelType1: 'TCP客户端',
              channelType2: 'TCP服务端',
              logicAddressError: '逻辑地址不正确，请输入1~255之间的整数',
              ipError: 'IP地址不正确，请重新输入',
              portError: '端口号不正确，请输入0~65535之间的整数'
            }
          },
          userManage: {
            loginName: '用户名',
            password: '密码',
            repassword: '重复密码',
            roleName: '角色名',
            userName: '姓名',
            phone: '联系电话',
            email: '邮箱',
            gender: '性别',
            userType: '用户类型',
            stationRange: '电站管理范围',
            authorizeName: '权限管理',
            status: '账号状态',
            op: '操作',
            qq: 'qq',
            userRole: '用户角色',
            userStation: '用户电站',
            createUser: '新增用户',
            isEditStatus: '确认修改用户状态',
            enterprise: '所属企业',
            department: '部门'
          },
          systemManager: {
            systemParam: '系统参数',
            clearParam: '智能清洗参数配置',
            description: '系统简介',
            logo: '系统Logo',
            paramName: '参数名称',
            paramValue: '参数值',
            unit: '单位',
            updateName: '修改人',
            updateDate: '修改日期',
            paramDescription: '参数描述',
            systemName: '系统名称'
          },
          domainManager: {
            name: '区域名称',
            description: '区域描述',
            op: '操作',
            createDomain: '新增区域',
            updateDomain: '修改区域',
            domainOwer: '区域归属',
            currency: '货币设置',
            domainPrice: '区域电价',
            longitude: '经度',
            latitude: '纬度',
            radius: '区域范围'
          },
          enterpriseManage: {
            title: '企业管理',
            index: {
              title: '企业信息'
            },
            organization: {
              title: '组织结构'
            },
            name: '企业名称',
            address: '企业地址',
            phone: '联系方式',
            contatUser: '负责人姓名',
            devNum: '设备数量',
            persionNum: '人员数量',
            email: '企业邮箱',
            description: '企业简介',
            detail: '详情',
            smallImg: '企业缩略图',
            enterRole: '企业管理员',
            noLimit: '不限制',
            req: '必填',
            userNameError: '用户名必须是数字字母下划线长度在6-20位',
            userNameRep: '用户名已存在',
            pswNoSame: '密码不一致',
            phoneNotRight: '手机号码不正确',
            longitude: '经度',
            latitude: '纬度',
            radius: '半径',
            op: '操作'
          },
          personRightsManager: {
            title: '权限人员管理',
            region: {
              title: '区域管理'
            },
            role: {
              title: '角色管理',
              roleName: '角色名称',
              description: '描述',
              createDate: '创建时间',
              startTime: '开始时间',
              endTime: '结束时间',
              roleType: '角色类型',
              roleManage: '权限管理',
              seeRole: '查看权限',
              isUse: '角色状态',
              operate: '操作',
              normalRole: '普通角色',
              systemRole: '超级管理员',
              enterpriseRole: '企业管理员',
              use: '启用',
              noUse: '禁用',
              roleAuth: '系统侧权限',
              appAuth: '终端侧权限',
              saveSuccess: '保存成功',
              saveFailed: '保存失败',
              editRole: '修改角色',
              createRole: '创建角色',
              noEidtSystem: '不能修改系统管理员状态',
              noEidtEnter: '不能修改企业管理员状态',
              isEditStatus: '是否修改角色状态？',
              ower: '所属企业'
            },
            account: {
              title: '账号管理'
            }
          },
          dataIntegrity: {
            title: '数据完整性',
            dataRecovery: {
              title: '数据补采'
            },
            kpiCorrection: {
              title: 'KPI修正'
            },
            kpiRecalculation: {
              title: 'KPI重计算'
            }
          },
          paramConfig: {
            title: '参数配置',
            pointManage: {
              title: '点表管理',
              importPoint: '点表导入',
              uniformizationConfig: '归一化配置',
              pointName: '点表名称',
              detail: '详情',
              devVersion: '版本号',
              devType: '设备类型',
              importDate: '导入日期',
              devInfo: '设备信息',
              signalName: '信号点名称',
              signalType: '信号点类型',
              devName: '设备名称',
              sigGain: '增益',
              sigOffset: '偏移量'
            },
            alarmConfig: {
              title: '告警设置',
              alarmName: '告警名称',
              devType: '设备类型',
              alarmLevel: '告警级别',
              alarmCause: '告警原因',
              repairSuggestion: '修复建议',
              stationName: '电站名称',
              versionCode: '版本号',
              causeId: '原因码'
            },
            stationParam: {
              title: '电站参数',
              paramName: '参数名称',
              paramValue: '参数值',
              paramUnit: '参数单位',
              updateUser: '修改人',
              uodateTime: '修改时间',
              paramDesc: '参数描述'
            }
          },
          voucherManage: {
            title: '票据管理',
            oneWorkTicket: {
              title: '电气一种工作票',
              // processStatus: ['待签发', '待确认', '处理中', '待终结', '已完成', '作废']
              processStatus: {
                createFirstWork: '待提交',
                issueFirstWork: '待审核',
                chargeFirstSubmit: '待审核',
                recipientFirstSure: '待审核',
                licensorSure: '待审核',
                chargeApplyStop: '待审核',
                licensorStop: '待审核',
                licensorWorkStop: '待审核',
                processEnd: '流程结束',
                giveup: '已作废'
              }
            },
            twoWorkTicket: {
              title: '电气二种工作票'
            },
            operationTicket: {
              title: '操作票'
            },
            operationTicketTemplate: {
              title: '操作票模板'
            }
          },
          systemParam: {
            title: '系统参数'
          },
          organization: {
            title: '组织架构'
          },
          licenceManage: {
            title: 'License 管理'
          }
        },
        todo: {
          title: '个人任务'
        },
        station: {
          index: '电站概况',
          equipment: '设备管理',
          alarm: '告警管理',
          report: '报表管理'
        }
      },
      station: {
        title: '电站列表'
      }
    },
    home: {
      powerTitle: '实时功率',
      dayCapTitle: '日发电量',
      dayIncomeTitle: '日收益',
      yearIncomeTitle: '年收益',
      yearCapTitle: '年发电量',
      totalPowerTitle: '累计电量'
    },
    settings: {
      // 数据完成整性
      dataIntegrity: {
        // KPI 重算
        kpiRecalculation: {
          taskName: '任务名称',
          stationName: '电站名',
          taskStatus: '任务状态',
          taskStatus_undo: '未执行',
          taskStatus_doing: '正在执行',
          taskStatus_done: '已完成',
          taskStatus_error: '执行失败',
          process: '进度',
          execute: '执行',
          task: '任务',
          taskName_req: '任务名称不能为空',
          stationName_req: '电站不能为空',
          taskTime_req: '任务时间区间不能为空'
        }
      }
    },
    exhibition: {
      totalPower: '累计发电量',
      safeRunningDays: '安全运行',

      kpiModules: {
        powerGeneration: {
          title: '发电状态（实时）',
          realTimePower: '实时功率',
          dailyCharge: '日发电量',
          todayIncome: '今日收益'
        },
        powerTrend: {
          title: '发电趋势（本月）',
          generatingCapacity: '发电量',
          income: '收益'
        },
        powerScale: {
          title: '发电规模统计',
          installedCapacity: '电站装机容量',
          totalStation: '电站总数(个)',
          scale: ['5kW 以下', '5kW~10kW', '10kW~100kW', '100kW~500kW', '500kW 以上']
        },
        equipmentDistribution: {
          title: '设备分布统计',
          deviceTotalCount: '设备总数',
          pvCount: '组件',
          inverterConcCount: '集中式逆变器',
          inverterStringCount: '组串式逆变器',
          combinerBoxCount: '直流汇流箱'
        },
        powerSupply: {
          title: '供电负荷统计',
          currentSupply: '当前负荷：',
          supplyKey: '负荷量'
        },
        socialContribution: {
          title: '环保贡献统计',
          saveStandardCoal: '节约标准煤',
          carbonDioxideReduction: 'CO₂减排量',
          reduceDeforestation: '等效植树量',
          year: '年度',
          cumulative: '累计'
        },
        taskStatistics: {
          title: '任务工单统计',
          status: ['待分配', '处理中', '已完成'],
          total: '工单总数：'
        }
      },

      center: {
        toEarth: '地球'
      },

      stationInfo: {
        address: '电站地址：',
        installedCapacity: '装机容量：',
        gridTime: '并网时间：',
        safeRunningDays: '运行天数：',
        generatingCapacity: '发电量：',
        dayCapacity: '当日',
        monthCapacity: '当月',
        yearCapacity: '当年',
        description: '电站简介'
      },

      bottomKpi: {
        totalYearPower: '全年发电量',
        totalInstalledCapacity: '总装机容量',
        totalIncome: '发电总收益',
        equipmentQuantity: '设备数量',
        totalTask: '任务工单总数',
        untreatedTask: '未处理任务工单'
      }
    }
  },
  ...zhLocale,
  ...devManage,
  ...Menu,
  ...DevTypeId
}

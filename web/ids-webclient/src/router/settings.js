// 设置的路由
// 最外层的布局一级路由
const Layout = resolve => require.ensure([], () => resolve(require('@/pages/layout/index.vue')), 'layout')
// 二级路由(设置界面)
const SettingsLayout = resolve => require.ensure([], () => resolve(require('@/pages/layout/settings.vue')), 'settingsLayout')
// 企业
const EnterpriseManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/enterprise/index.vue')), 'enterprise')

// 组织架构
const Organization = resolve => require.ensure([], () => resolve(require('@/pages/settings/organization/index.vue')), 'organization')

// 区域管理
const RegionManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/userManager/region/index.vue')), 'regionManage')

// 角色管理
const RoleManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/userManager/role/index.vue')), 'roleManage')

// 用户管理
const UserManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/userManager/user/index.vue')), 'userManage')
// 电站管理
const StationManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/stationManage/station/index.vue')), 'stationManage')
// 点表导入
const PointManage = resolve => require.ensure([], () => resolve(require('@/pages/settings/paramConfig/pointManage/index.vue')), 'pointManage')

// 因为后面使用扩展运算符...，所以这里使用数组，最后都是把数组中的所有数据依次取出添加到路由中
export default [
  { // 设置界面，需要有三级路由
    path: '/settings',
    name: 'menu.settings.title',
    redirect: '/settings/enterprise',
    icon: 'icon-settings',
    type: 'settings',
    component: Layout,
    children: [
      { // 企业管理
        path: '/settings/enterprise',
        name: 'menu.settings.enterprise.title',
        icon: 'icon-enterpriseManage',
        redirect: '/settings/enterprise/index',
        component: SettingsLayout,
        children: [
          { // 企业的页面
            path: 'index',
            name: 'menu.settings.enterprise.info',
            component: EnterpriseManage,
            meta: {role: ['enterprise'], showSidebar: true}
          },
          {
            path: 'organization',
            name: 'menu.settings.enterprise.organization',
            component: Organization,
            meta: {role: ['organization'], showSidebar: true}
          }
        ]
      },
      { // 人员权限管理
        path: '/settings/personRightsManager',
        name: 'menu.settings.userManager.title',
        redirect: '/settings/personRightsManager/region',
        icon: 'icon-personRightsManager',
        component: SettingsLayout,
        children: [
          { // 区域管理
            path: 'region',
            name: 'menu.settings.userManager.region',
            component: RegionManage,
            meta: {
              role: ['regionManager'],
              showSidebar: true // 显示左边菜单栏
            }
          },
          { // 角色管理
            path: 'role',
            name: 'menu.settings.userManager.role',
            component: RoleManage,
            meta: {
              role: ['roleManager'],
              showSidebar: true // 显示左边菜单栏
            }
          },
          { // 账号管理
            path: 'account',
            name: 'menu.settings.userManager.user',
            component: UserManage,
            meta: {
              role: ['accountManager'],
              showSidebar: true // 显示左边菜单栏
            }
          }
        ]
      },
      { // 参数配置
        path: '/settings/paramConfig',
        name: 'menu.settings.paramConfig.title',
        redirect: '/settings/paramConfig/pointManage',
        icon: 'icon-paramConfig',
        component: SettingsLayout,
        children: [
          { // 点表管理
            path: 'pointManage',
            name: 'menu.settings.paramConfig.pointManage',
            component: PointManage,
            meta: {
              role: ['pointManage'],
              showSidebar: true // 显示左边菜单栏
            }
          }
        ]
      },
      { // 电站管理
        path: '/settings/stationManage',
        name: 'menu.settings.stationManager.title',
        redirect: '/settings/stationManage/station',
        icon: 'icon-stationManage',
        component: SettingsLayout,
        children: [
          { // 电站的页面
            path: 'station',
            name: 'menu.settings.stationManager.station',
            component: StationManage,
            meta: {role: ['stationManage'], showSidebar: true}
          }
        ]
      }
    ]
  }
]

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

// 因为后面使用扩展运算符...，所以这里使用数组，最后都是把数组中的所有数据依次取出添加到路由中
export default [
  { // 设置界面，需要有三级路由
    path: '/settings',
    name: 'settings.title',
    redirect: '/settings/enterprise',
    icon: 'icon-settings',
    type: 'settings',
    component: Layout,
    children: [
      { // 企业管理
        path: '/settings/enterprise',
        name: 'settings.enterpriseManage.title',
        icon: 'icon-enterpriseManage',
        redirect: '/settings/enterprise/index',
        component: SettingsLayout,
        children: [
          { // 企业的页面
            path: 'index',
            name: 'settings.enterpriseManage.index.title',
            component: EnterpriseManage,
            meta: {role: ['enterprise'], showSidebar: true}
          },
          {
            path: 'organization',
            name: 'settings.enterpriseManage.organization.title',
            component: Organization,
            meta: {role: ['organization'], showSidebar: true}
          }
        ]
      },
      { // 人员权限管理
        path: '/settings/personRightsManager',
        name: 'settings.personRightsManager.title',
        redirect: '/settings/personRightsManager/region',
        icon: 'icon-personRightsManager',
        component: SettingsLayout,
        children: [
          { // 区域管理
            path: 'region',
            name: 'settings.personRightsManager.region.title',
            component: RegionManage,
            meta: {
              role: ['regionManager'],
              showSidebar: true // 显示左边菜单栏
            }
          },
          { // 角色管理
            path: 'role',
            name: 'settings.personRightsManager.role.title',
            component: RoleManage,
            meta: {
              role: ['roleManager'],
              showSidebar: true // 显示左边菜单栏
            }
          },
          { // 账号管理
            path: 'account',
            name: 'settings.personRightsManager.account.title',
            component: UserManage,
            meta: {
              role: ['accountManager'],
              showSidebar: true // 显示左边菜单栏
            }
          }
        ]
      },
      { // 电站管理
        path: '/settings/stationManage',
        name: 'settings.stationManage.title',
        redirect: '/settings/stationManage/station',
        icon: 'icon-stationManage',
        component: SettingsLayout,
        children: [
          { // 电站的页面
            path: 'station',
            name: 'settings.stationManage.station.title',
            component: StationManage,
            meta: {role: ['stationManage'], showSidebar: true}
          }
        ]
      }
    ]
  }
]

package com.developerphil.adbidea

/**
 * Created by XQ Yang on 10/10/2018  3:28 PM.
 * Description :
 */

val ACTION_MAIN = "android.intent.action.MAIN"
val ACTION_VIEW = "android.intent.action.VIEW"
val ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA"
val ACTION_EDIT = "android.intent.action.EDIT"
val ACTION_PICK = "android.intent.action.PICK"
val ACTION_CHOOSER = "android.intent.action.CHOOSER"
val ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
val ACTION_DIAL = "android.intent.action.DIAL"
val ACTION_CALL = "android.intent.action.CALL"
val ACTION_SEND = "android.intent.action.SEND"
val ACTION_SENDTO = "android.intent.action.SENDTO"
val ACTION_ANSWER = "android.intent.action.ANSWER"
val ACTION_INSERT = "android.intent.action.INSERT"
val ACTION_DELETE = "android.intent.action.DELETE"
val ACTION_RUN = "android.intent.action.RUN"
val ACTION_SYNC = "android.intent.action.SYNC"
val ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY"
val ACTION_SEARCH = "android.intent.action.SEARCH"
val ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH"
val ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST"


val StartActivityActions = arrayOf("", ACTION_MAIN,
    ACTION_VIEW,
    ACTION_ATTACH_DATA,
    ACTION_EDIT,
    ACTION_PICK,
    ACTION_CHOOSER,
    ACTION_GET_CONTENT,
    ACTION_DIAL,
    ACTION_CALL,
    ACTION_SEND,
    ACTION_SENDTO,
    ACTION_ANSWER,
    ACTION_INSERT,
    ACTION_DELETE,
    ACTION_RUN,
    ACTION_SYNC,
    ACTION_PICK_ACTIVITY,
    ACTION_SEARCH,
    ACTION_WEB_SEARCH,
    ACTION_FACTORY_TEST
)


val CATEGORY_DEFAULT = "android.intent.category.DEFAULT"
val CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE"
val CATEGORY_TAB = "android.intent.category.TAB"
val CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE"
val CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE"
val CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER"
val CATEGORY_INFO = "android.intent.category.INFO"
val CATEGORY_HOME = "android.intent.category.HOME"
val CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE"
val CATEGORY_TEST = "android.intent.category.TEST"
val CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK"
val CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK"
val CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK"
val CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK"
val CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE"
val CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET"
val CATEGORY_VR_HOME = "android.intent.category.VR_HOME"


val Categorys = arrayOf("",
    CATEGORY_DEFAULT,
    CATEGORY_BROWSABLE,
    CATEGORY_TAB,
    CATEGORY_ALTERNATIVE,
    CATEGORY_SELECTED_ALTERNATIVE,
    CATEGORY_LAUNCHER,
    CATEGORY_INFO,
    CATEGORY_HOME,
    CATEGORY_PREFERENCE,
    CATEGORY_TEST,
    CATEGORY_CAR_DOCK,
    CATEGORY_DESK_DOCK,
    CATEGORY_LE_DESK_DOCK,
    CATEGORY_HE_DESK_DOCK,
    CATEGORY_CAR_MODE,
    CATEGORY_APP_MARKET,
    CATEGORY_VR_HOME
)

val BroadCastActions = arrayOf("",
    "android.net.conn.CONNECTIVITY_CHANGE",//网络连接发生变化
    "android.intent.action.SCREEN_ON",//屏幕点亮
    "android.intent.action.SCREEN_OFF",//屏幕熄灭
    "android.intent.action.BATTERY_LOW",//电量低，会弹出电量低提示框
    "android.intent.action.BATTERY_OKAY",//电量恢复了
    "android.intent.action.BOOT_COMPLETED",//设备启动完毕
    "android.intent.action.DEVICE_STORAGE_LOW",//存储空间过低
    "android.intent.action.DEVICE_STORAGE_OK",//存储空间恢复
    "android.intent.action.PACKAGE_ADDED",//安装了新的应用
    "android.net.wifi.STATE_CHANGE",//WiFi连接状态发生变化
    "android.net.wifi.WIFI_STATE_CHANGED",//WiFi状态变为启用/关闭/正在启动/正在关闭/未知
    "android.intent.action.BATTERY_CHANGED",//电池电量发生变化
    "android.intent.action.INPUT_METHOD_CHANGED",//系统输入法发生变化
    "android.intent.action.ACTION_POWER_CONNECTED",//外部电源连接
    "android.intent.action.ACTION_POWER_DISCONNECTED",//外部电源断开连接
    "android.intent.action.DREAMING_STARTED",//系统开始休眠
    "android.intent.action.DREAMING_STOPPED",//系统停止休眠
    "android.intent.action.WALLPAPER_CHANGED",//壁纸发生变化
    "android.intent.action.HEADSET_PLUG",//插入耳机
    "android.intent.action.MEDIA_UNMOUNTED",//卸载外部介质
    "android.intent.action.MEDIA_MOUNTED",//挂载外部介质
    "android.os.action.POWER_SAVE_MODE_CHANGED"//省电模式开启
)
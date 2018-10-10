package com.developerphil.adbidea.adb

import com.developerphil.adbidea.bean.BoundItemBean

/**
 * Created by XQ Yang on 10/10/2018  4:53 PM.
 * Description :
 */

fun getInteractingCommand(type: Int, action: String, category: String, name: String,
    boundData: MutableList<BoundItemBean>): CommonStringResultCommand {

    var desc = "StartActivity"
    val sb = StringBuilder(when (type) {
        0 -> "am start "
        1 -> {
            desc = "StartService"
            "am startservice "
        }
        2 -> {
            desc = "SendBroadCast"
            "am broadcast "
        }
        else -> "am start "
    })
    if (action.isNotEmpty()) {
        sb.append("-a $action ")
    }
    if (category.isNotEmpty()) {
        sb.append("-c $category ")
    }
    if (name.isNotEmpty()) {
        sb.append("-n $name ")
    }
    boundData.forEach {
        sb.append(it.toString())
    }
    return CommonStringResultCommand(sb.toString(), desc)
}

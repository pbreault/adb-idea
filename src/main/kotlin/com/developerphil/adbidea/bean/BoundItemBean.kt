package com.developerphil.adbidea.bean

/**
 * Created by XQ Yang on 9/4/2018  2:41 PM.
 * Description :
 */

data class BoundItemBean(var selected: Boolean, var key: String, var value: String,var dataType:BoundDataType = BoundDataType.STRING){
    override fun toString(): String {
        if (selected&&key.isNotEmpty()) {
            if (dataType == BoundDataType.NULL) {
                return "${dataType.prefix} \"$key\" "
            }
            return "${dataType.prefix} \"$key\" \"$value\" "
        }
        return ""
    }
}


enum class BoundDataType(val prefix:String){
    STRING("--es"),
    BOOLEAN("--ez"),
    INTEGER("--ei"),
    LONG ("--el"),
    FLOAT ("--ef"),
    URI("--eu"),
    COMPONENT_NAME("--ecn"),
    INTEGER_ARRAY("--eia"),
    LONG_ARRAY("--ela"),
    NULL("--esn")
}
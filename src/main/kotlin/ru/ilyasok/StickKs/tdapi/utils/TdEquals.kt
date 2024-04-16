package ru.ilyasok.StickKs.tdapi.utils

import ru.ilyasok.StickKs.tdapi.TdApi

enum class TdEqRelation {
    TD_EQUALS,
    TD_NOT_EQUALS,
}

class TdEquals {
    companion object {
        private const val CONSTRUCTOR_FIELD_NAME = "CONSTRUCTOR"
        fun check(obj: TdApi.Object, clazz: Class<*>): TdEqRelation {
            if (!(TdApi.Object::class.java.isAssignableFrom(clazz))) {
                return TdEqRelation.TD_NOT_EQUALS
            }
            val constructorField = clazz.getField(CONSTRUCTOR_FIELD_NAME)
            return if (obj.getConstructor() == constructorField.getInt(null))
                TdEqRelation.TD_EQUALS else TdEqRelation.TD_NOT_EQUALS
        }
    }
}
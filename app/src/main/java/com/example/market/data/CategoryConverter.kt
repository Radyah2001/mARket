package com.example.market.data

import androidx.room.TypeConverter
import com.example.market.model.Category
import com.example.market.model.Condition

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name // Use enum name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return enumValueOf<Category>(value) // Use enumValueOf
    }
}

class ConditionConverter {
    @TypeConverter
    fun fromCondition(condition: Condition): String {
        return condition.name // Use enum name
    }

    @TypeConverter
    fun toCondition(value: String): Condition {
        return enumValueOf<Condition>(value) // Use enumValueOf
    }
}
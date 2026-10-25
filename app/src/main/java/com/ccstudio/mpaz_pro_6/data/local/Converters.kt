package com.ccstudio.mpaz_pro_6.data.local

import androidx.room.TypeConverter
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import java.time.Instant

/**
 * Conversores de Room para fechas y enums.
 *
 * Las fechas se guardan como epoch en milisegundos, igual que el esquema de
 * referencia. Los enums usan el valor wire del backend.
 */
class Converters {

    @TypeConverter
    fun instantToEpoch(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun contentStatusToString(value: ContentStatus): String = value.wireValue

    @TypeConverter
    fun stringToContentStatus(value: String): ContentStatus =
        ContentStatus.fromWire(value) ?: unknown("ContentStatus", value)

    @TypeConverter
    fun activityTypeToString(value: ActivityType): String = value.wireValue

    @TypeConverter
    fun stringToActivityType(value: String): ActivityType =
        ActivityType.fromWire(value) ?: unknown("ActivityType", value)

    @TypeConverter
    fun contentOriginToString(value: ContentOrigin): String = value.wireValue

    @TypeConverter
    fun stringToContentOrigin(value: String): ContentOrigin =
        ContentOrigin.fromWire(value) ?: unknown("ContentOrigin", value)

    @TypeConverter
    fun contentResourceTypeToString(value: ContentResourceType): String = value.wireValue

    @TypeConverter
    fun stringToContentResourceType(value: String): ContentResourceType =
        ContentResourceType.fromWire(value) ?: unknown("ContentResourceType", value)

    @TypeConverter
    fun difficultyToString(value: Difficulty): String = value.wireValue

    @TypeConverter
    fun stringToDifficulty(value: String): Difficulty =
        Difficulty.fromWire(value) ?: unknown("Difficulty", value)

    @TypeConverter
    fun objectiveLevelToString(value: ObjectiveLevel): String = value.wireValue

    @TypeConverter
    fun stringToObjectiveLevel(value: String): ObjectiveLevel =
        ObjectiveLevel.fromWire(value) ?: unknown("ObjectiveLevel", value)

    @TypeConverter
    fun reportStatusToString(value: ReportStatus): String = value.wireValue

    @TypeConverter
    fun stringToReportStatus(value: String): ReportStatus =
        ReportStatus.fromWire(value) ?: unknown("ReportStatus", value)

    @TypeConverter
    fun userRoleToString(value: UserRole): String = value.wireValue

    @TypeConverter
    fun stringToUserRole(value: String): UserRole =
        UserRole.fromWire(value) ?: unknown("UserRole", value)

    private fun unknown(enumName: String, value: String): Nothing =
        throw IllegalArgumentException("Valor desconocido para $enumName: $value")
}

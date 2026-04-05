package co.edu.unicauca.dopaminah.data.mapper

import co.edu.unicauca.dopaminah.data.local.entity.AppLimitGoalEntity
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal

fun AppLimitGoalEntity.toDomain(): AppLimitGoal {
    return AppLimitGoal(
        id = id,
        goalType = goalType,
        packageName = packageName,
        appDisplayName = appDisplayName,
        maxTimeMillis = maxTimeMillis,
        maxUnlocks = maxUnlocks,
        currentStreak = currentStreak
    )
}

fun AppLimitGoal.toEntity(): AppLimitGoalEntity {
    return AppLimitGoalEntity(
        id = id,
        goalType = goalType,
        packageName = packageName,
        appDisplayName = appDisplayName,
        maxTimeMillis = maxTimeMillis,
        maxUnlocks = maxUnlocks,
        currentStreak = currentStreak
    )
}

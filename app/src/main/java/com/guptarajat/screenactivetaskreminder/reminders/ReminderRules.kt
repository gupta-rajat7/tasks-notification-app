package com.guptarajat.screenactivetaskreminder.reminders

private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_DAY = 24 * 60

data class ReminderRuleInput(
    val pendingTaskCount: Int,
    val nowMillis: Long,
    val localMinuteOfDay: Int,
    val reminderIntervalMinutes: Int,
    val lastReviewedAtMillis: Long? = null,
    val snoozedUntilMillis: Long? = null,
    val quietHours: QuietHours = QuietHours(),
) {
    init {
        require(localMinuteOfDay in 0 until MINUTES_PER_DAY) {
            "localMinuteOfDay must be between 0 and 1439."
        }
    }
}

data class QuietHours(
    val isEnabled: Boolean = false,
    val startMinuteOfDay: Int = 22 * 60,
    val endMinuteOfDay: Int = 7 * 60,
) {
    init {
        require(startMinuteOfDay in 0 until MINUTES_PER_DAY) {
            "startMinuteOfDay must be between 0 and 1439."
        }
        require(endMinuteOfDay in 0 until MINUTES_PER_DAY) {
            "endMinuteOfDay must be between 0 and 1439."
        }
    }
}

data class ReminderDecision(
    val shouldRemind: Boolean,
    val suppressionReason: ReminderSuppressionReason? = null,
    val nextEligibleAtMillis: Long? = null,
) {
    companion object {
        fun remind(): ReminderDecision = ReminderDecision(shouldRemind = true)

        fun suppress(
            reason: ReminderSuppressionReason,
            nextEligibleAtMillis: Long? = null,
        ): ReminderDecision =
            ReminderDecision(
                shouldRemind = false,
                suppressionReason = reason,
                nextEligibleAtMillis = nextEligibleAtMillis,
            )
    }
}

enum class ReminderSuppressionReason {
    NO_PENDING_TASKS,
    QUIET_HOURS,
    SNOOZED,
    RECENTLY_REVIEWED,
}

object ReminderRules {
    fun evaluate(input: ReminderRuleInput): ReminderDecision {
        if (input.pendingTaskCount <= 0) {
            return ReminderDecision.suppress(ReminderSuppressionReason.NO_PENDING_TASKS)
        }

        if (input.quietHours.contains(input.localMinuteOfDay)) {
            return ReminderDecision.suppress(ReminderSuppressionReason.QUIET_HOURS)
        }

        val snoozedUntilMillis = input.snoozedUntilMillis
        if (snoozedUntilMillis != null && snoozedUntilMillis > input.nowMillis) {
            return ReminderDecision.suppress(
                reason = ReminderSuppressionReason.SNOOZED,
                nextEligibleAtMillis = snoozedUntilMillis,
            )
        }

        val nextReviewEligibleAtMillis = input.nextReviewEligibleAtMillis()
        if (
            nextReviewEligibleAtMillis != null &&
            nextReviewEligibleAtMillis > input.nowMillis
        ) {
            return ReminderDecision.suppress(
                reason = ReminderSuppressionReason.RECENTLY_REVIEWED,
                nextEligibleAtMillis = nextReviewEligibleAtMillis,
            )
        }

        return ReminderDecision.remind()
    }
}

fun QuietHours.contains(localMinuteOfDay: Int): Boolean {
    require(localMinuteOfDay in 0 until MINUTES_PER_DAY) {
        "localMinuteOfDay must be between 0 and 1439."
    }
    if (!isEnabled) {
        return false
    }
    if (startMinuteOfDay == endMinuteOfDay) {
        return true
    }
    return if (startMinuteOfDay < endMinuteOfDay) {
        localMinuteOfDay in startMinuteOfDay until endMinuteOfDay
    } else {
        localMinuteOfDay >= startMinuteOfDay || localMinuteOfDay < endMinuteOfDay
    }
}

private fun ReminderRuleInput.nextReviewEligibleAtMillis(): Long? {
    val reviewedAtMillis = lastReviewedAtMillis ?: return null
    val intervalMillis = reminderIntervalMinutes.coerceAtLeast(1) * MILLIS_PER_MINUTE
    return reviewedAtMillis + intervalMillis
}

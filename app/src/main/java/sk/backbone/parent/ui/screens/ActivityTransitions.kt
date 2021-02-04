package sk.backbone.parent.ui.screens

import android.app.Activity
import sk.backbone.parent.R

enum class ActivityTransitions {
    LEFT_RIGHT {
        override fun setFinishActivityTransition(activity: Activity) {
            activity.overridePendingTransition(R.anim.transition_left_right_start_enter, R.anim.transition_left_right_start_exit)
        }

        override fun setStartActivityTransition(activity: Activity) {
            activity.overridePendingTransition(R.anim.transition_left_right_finish_enter, R.anim.transition_left_right_finish_exit)
        }
    },
    BOTTOM_TOP {
        override fun setFinishActivityTransition(activity: Activity) {
            activity.overridePendingTransition(R.anim.transition_bottom_top_start_enter, R.anim.transition_bottom_top_start_exit)
        }

        override fun setStartActivityTransition(activity: Activity) {
            activity.overridePendingTransition(R.anim.transition_bottom_top_finish_enter, R.anim.transition_bottom_top_finish_exit)
        }
    },
    DEFAULT {
        override fun setStartActivityTransition(activity: Activity) {

        }

        override fun setFinishActivityTransition(activity: Activity) {

        }
    },
    NONE {
        override fun setStartActivityTransition(activity: Activity) {
            activity.overridePendingTransition(0, 0)
        }

        override fun setFinishActivityTransition(activity: Activity) {
            activity.overridePendingTransition(0, 0)
        }
    };

    abstract fun setStartActivityTransition(activity: Activity)
    abstract fun setFinishActivityTransition(activity: Activity)
}
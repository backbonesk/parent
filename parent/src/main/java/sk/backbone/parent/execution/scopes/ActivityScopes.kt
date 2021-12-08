package sk.backbone.parent.execution.scopes

import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ActivityScopes @Inject constructor(): Scopes()
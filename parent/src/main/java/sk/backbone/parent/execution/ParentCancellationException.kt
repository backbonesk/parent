package sk.backbone.parent.execution

import kotlinx.coroutines.CancellationException

class ParentCancellationException: CancellationException("Operation was canceled. No problem happened.")
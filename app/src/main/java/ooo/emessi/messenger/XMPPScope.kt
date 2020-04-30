package ooo.emessi.messenger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class XMPPScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO) :
    CoroutineScope
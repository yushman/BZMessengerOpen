package ooo.emessi.messenger.xmpp.connection

class ConnectionOptions(
    var useSecurityMode: Boolean = false,
    var compressionEnabled: Boolean = false,
    var sendPresence: Boolean = true,
    var enableDefaultDebugger: Boolean = true,
    var defaultResource: String = "test",
    var useStreamManagement: Boolean = true,
    var useStreamManagementResumption: Boolean = true,
    var replyTimeout: Long = 15000L
){
    companion object{
        fun getDefaultOptions() = ConnectionOptions()
    }
}
package ooo.emessi.messenger.ui.fragments


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.managers.PushManager
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.csi.ClientStateIndicationManager


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

//        val btn = v.button
//        val miv = v.miv
//
//        btn.setOnClickListener {
//            miv.addImage(BitmapFactory.decodeResource(resources, R.drawable.flash))
//        }
//        discoverServerInfo()


        // Inflate the layout for this fragment
        return v
    }

    private fun discoverServerInfo() = CoroutineScope(Dispatchers.IO).launch {
        val hm = mutableMapOf<String, String>()
        try {
            val muc = XMPPConnectionApi.getMUCManager().mucServiceDomains.isNotEmpty()
            val pep = XMPPConnectionApi.getPepManager().isSupported
            val blockingCommand =
                XMPPConnectionApi.getBlockingCommandManager().isSupportedByServer
            val sm = XMPPConnectionApi.getConnection().isSmAvailable
            val rosterVersioning = XMPPConnectionApi.getRoster().isRosterVersioningSupported
            val carbons = XMPPConnectionApi.getCarbonManager().isSupportedByServer
            val mam = XMPPConnectionApi.getMamManager().isSupported
            val csi = ClientStateIndicationManager.isSupported(XMPPConnectionApi.getConnection())
            val push = PushManager.isSupported()
            val drm = XMPPConnectionApi.getDeliveryReceiptManager().isSupported(XMPPConnectionApi.getMyJid())


            val fileUpload = XMPPConnectionApi.getFileUpload().isUploadServiceDiscovered
            XMPPConnectionApi.getFileUpload()

//            Log.d("Service", "fileUpload is supported $fileUpload")
            val mucLight =
                XMPPConnectionApi.getMucLightManager().localServices.isNotEmpty()
            Log.d("mucLight", "muc is supported $mucLight")



            hm["muc"] = muc.toString()
            hm["pep"] = pep.toString()
            hm["blockingCommand"] = blockingCommand.toString()
            hm["sm"] = sm.toString()
            hm["rosterVersioning"] = rosterVersioning.toString()
            hm["carbons"] = carbons.toString()
            hm["mam"] = mam.toString()
            hm["csi"] = csi.toString()
            hm["push"] = push.toString()
            hm["fileUpload"] = fileUpload.toString()
            hm["mucLight"] = mucLight.toString()
            hm["drm"] = drm.toString()
            hm.forEach{
                Log.d("Service", "${it.key} is supported ${it.value}")
            }


        } catch (e: Exception){
            e.printStackTrace()
        }
//        try {
//            val discoInfo = XMPPConnectionApi.getServiceDiscoveryManager().discoverInfo(XMPPConnectionApi.getMyJidEntityBare())
//            val discoList = discoInfo.identities
//            val discoItems = XMPPConnectionApi.getServiceDiscoveryManager().discoverItems(XMPPConnectionApi.getMyJidEntityBare()).items
//
//            hm.clear()
//            discoList.forEach{
//                hm[it.category] = "" + it.type + " " + it.name
//            }
//
//            hm.forEach{
//                Log.d("Service", "${it.key} is supported ${it.value}")
//            }
//            hm.clear()
//            discoItems.forEach{
//                hm[it.name] = it.action
//            }
//            hm.forEach{
//                Log.d("Service", "${it.key} is supported ${it.value}")
//            }
//        }catch (e: Exception){
//            e.printStackTrace()
//        }


    }


}

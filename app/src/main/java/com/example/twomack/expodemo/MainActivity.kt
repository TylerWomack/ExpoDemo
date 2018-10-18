package com.example.twomack.expodemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.TextView
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import java.util.*
import android.view.MenuInflater
import android.view.MenuItem


class MainActivity : AppCompatActivity(), EMDKListener {

    companion object {
        var results : EMDKResults? = null

        //Declare a variable to store EMDKManager object
        var manager : EMDKManager? = null
    }

    //private var textViewBarcode: TextView? = null

    //Assign the profile name used in EMDKConfig.xml
    private val profileName = "DataCaptureProfile"

    //Declare a variable to store ProfileManager object
    private var mProfileManager: ProfileManager? = null


    var code: TextView? = null
    var symbology: TextView? = null
    private val profileNameBroadcastIntent = "DataCaptureProfileBroadcastIntent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hardware_scan)

        code = findViewById(R.id.barcode_text) as TextView
        symbology = findViewById(R.id.symbology) as TextView


        //The EMDKManager object will be created and returned in the callback.
        if (MainActivity.results == null){
            MainActivity.results = EMDKManager.getEMDKManager(applicationContext, this)
        }


        //Check the return status of getEMDKManager
        if (results?.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
            //Failed to create EMDKManager object

        }


        //In case we have been launched by the DataWedge intent plug-in
        val i = intent
        handleDecodeData(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.hardware_menu_item -> {
                //startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            R.id.optical_menu_item -> {
                startActivity(Intent(this, OpticalScanningActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        //respond to menu item selection

    }

    override fun onPause() {
        super.onPause()
        //manager?.release(EMDKManager.FEATURE_TYPE.BARCODE)
        manager?.release(EMDKManager.FEATURE_TYPE.PROFILE)
    }

    override fun onResume() {
        super.onResume()

        if (manager == null)
        {
            return
        }
        mProfileManager = manager?.getInstance(EMDKManager.FEATURE_TYPE.PROFILE) as ProfileManager

        if (mProfileManager != null) {
            try {

                val modifyData = arrayOfNulls<String>(1)
                //Call processPrfoile with profile name and SET flag to create the profile. The modifyData can be null.

                var results = mProfileManager?.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData)

                if (results?.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
                    //Failed to set profile
                }

                //Call processPrfoile for profile Broadcast Intent.
                results = mProfileManager?.processProfile(profileNameBroadcastIntent, ProfileManager.PROFILE_FLAG.SET, modifyData);

                if (results?.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
                    //Failed to set profile
                }

            } catch (ex: Exception) {
                // Handle any exception
            }
        }
    }


    private fun startOpticalScanner() {
        val intent = Intent(this, OpticalScanningActivity::class.java)
        startActivity(intent)
    }

    private fun updateCode(scanCode: String) {
        code?.setText(scanCode)
    }

    private fun updateSymbology(symbologyType: String) {
        symbology?.setText(symbologyType)
    }

    override fun onOpened(emdkManager: EMDKManager) {

        MainActivity.manager = emdkManager

        //Get the ProfileManager object to process the profiles
        mProfileManager = emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE) as ProfileManager

        if (mProfileManager != null) {
            try {

                val modifyData = arrayOfNulls<String>(1)
                //Call processPrfoile with profile name and SET flag to create the profile. The modifyData can be null.

                var results = mProfileManager?.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData)

                if (results?.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
                    //Failed to set profile
                }

                //Call processPrfoile for profile Broadcast Intent.
                results = mProfileManager?.processProfile(profileNameBroadcastIntent, ProfileManager.PROFILE_FLAG.SET, modifyData);

                if (results?.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
                    //Failed to set profile
                }

            } catch (ex: Exception) {
                // Handle any exception
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Clean up the objects created by EMDK manager
        manager?.release()
    }


    override fun onClosed() {
        if(manager != null){

            //barcode manager?

            manager?.release()
            manager = null
        }
    }

    //We need to handle any incoming intents, so let override the onNewIntent method
    public override fun onNewIntent(i: Intent) {
        if (i.extras != null)
        handleDecodeData(i)
    }

    //This function is responsible for getting the data from the intent
    private fun handleDecodeData(i: Intent) {


        //if (Intent.ACTION_GET_CONTENT.equals(intent.getAction())) {

            //Check the intent action is for us
            if (i.getAction().contentEquals("com.symbol.emdksample.RECVR")) {
                //Get the source of the data
                val source = i.getStringExtra("com.motorolasolutions.emdk.datawedge.source")

                //Check if the data has come from the Barcode scanner
                if (source.equals("scanner", ignoreCase = true)) {
                    //Get the data from the intent
                    val data = i.getStringExtra("com.motorolasolutions.emdk.datawedge.data_string")

                    val symbology = i.getStringExtra("com.symbol.datawedge.label_type")

                    //Check that we have received data
                    if (data != null && data.length > 0) {

                        //Display the data to the text view
                        updateCode(data)
                        updateSymbology(symbology)
                    }
                }
            }
        }
    //}
}


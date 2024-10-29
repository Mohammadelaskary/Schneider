package net.gbs.schneider.Tools

import com.honeywell.aidc.*
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import net.gbs.schneider.Base.MainActivity

class BarcodeReader(private val barcodeListener: BarcodeListener?,private val triggerListener: TriggerListener?) {
    var barcodeReader:BarcodeReader? = MainActivity.barcodeReader
    init {
        installBarcodeReader()
    }

    private fun installBarcodeReader() {
        if (barcodeReader != null) {

            // register bar code event listener
            // barcodeReader.addBarcodeListener(barcodeListener);

            // set the trigger mode to client control
            try {
                barcodeReader?.setProperty(
                    BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL
                )
            } catch (e: UnsupportedPropertyException) {
            }
            // register trigger state change listener
            val properties: MutableMap<String, Any> = HashMap()
            // Set Symbologies On/Off
            properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_GS1_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = true
            properties[BarcodeReader.PROPERTY_DATAMATRIX_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = true
            properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = true
            properties[BarcodeReader.PROPERTY_AZTEC_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODABAR_ENABLED] = true
            properties[BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED] = true
            properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = true
            // Set Max Code 39 barcode length
            properties[BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH] = 30
            // Turn on center decoding
            properties[BarcodeReader.PROPERTY_CENTER_DECODE] = true
            // Disable bad read response, handle in onFailureEvent
            properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] = true
            // Apply the settings
            properties[BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED] =
                true
            barcodeReader?.setProperties(properties)
        }
    }

    fun onTrigger(triggerStateChangeEvent: TriggerStateChangeEvent) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            barcodeReader?.aim(triggerStateChangeEvent.state)
            barcodeReader?.light(triggerStateChangeEvent.state)
            barcodeReader?.decode(triggerStateChangeEvent.state)
        } catch (e: ScannerNotClaimedException) {
            e.printStackTrace()
        } catch (e: ScannerUnavailableException) {
            e.printStackTrace()
        }
    }

    fun onResume() {
        if (barcodeReader != null) {
            try {
                barcodeReader?.claim()
                barcodeReader?.addBarcodeListener(barcodeListener)
                barcodeReader?.addTriggerListener(triggerListener)
                //                Log.d(TAG, "barcodeOnResume: fragmentName : "+fragmentName);
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
            }
        }
    }

    fun onPause() {
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader?.release()
            barcodeReader?.removeBarcodeListener(barcodeListener)
            barcodeReader?.removeTriggerListener(triggerListener)
            //            barcodeReader.close();
        }
    }

    fun scannedData(barcodeReadEvent: BarcodeReadEvent): String {
        return barcodeReadEvent.barcodeData.toString().trim { it <= ' ' }
    }
}
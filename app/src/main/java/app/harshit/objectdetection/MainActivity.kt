package app.harshit.objectdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.otaliastudios.cameraview.Frame
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView.setLifecycleOwner(this)
        cameraView.addFrameProcessor {
            extractDataFromFrame(it) { result ->
                tvDetectedObject.text = result
            }
        }
    }

    private fun extractDataFromFrame(frame: Frame, callback: (String) -> Unit) {

        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)

        objectDetector.processImage(getVisionImageFromFrame(frame))
            .addOnSuccessListener {
                var result = ""
                it.forEach { item ->
                    result += "${item.entityId}\n"  //TODO : Get the knowledge graph result for this entity
                    Log.e("TAG",item.classificationCategory.toString())
                }
                callback(result)
            }
            .addOnFailureListener {
                callback("Unable to detect an object")
            }
            .addOnCompleteListener {

            }

    }

    private fun getVisionImageFromFrame(frame : Frame) : FirebaseVisionImage{
        //ByteArray for the captured frame
        val data = frame.data

        //Metadata that gives more information on the image that is to be converted to FirebaseVisionImage
        val imageMetaData = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width)
            .build()

        val image = FirebaseVisionImage.fromByteArray(data, imageMetaData)

        return image
    }

}

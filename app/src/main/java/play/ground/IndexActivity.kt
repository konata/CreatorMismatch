package play.ground

import android.app.ActivityOptions
import android.app.ProfilerInfo
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.verticalLayout

class IndexActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "Decay"
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      button("Caption") {
        onClick {
          launch(Dispatchers.IO) {
            obfuscation()
          }
        }
      }
    }
  }


  fun obfuscation() {/* reader side (https://cs.android.com/android/platform/superproject/main/+/refs/heads/main:out/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/xref36/srcjars.xref/android/app/IActivityTaskManager.java;drc=3fa083f29412adcfbc1138c26e7d641d68960d78;l=868)

    ```java
    resolvedType = data.readString()  //  mSourcePackage ->
    resultTo = data.readStrongBinder() // mLabelRes ->

    resultWho = data.readString() // `1@i32` &  (?? 00 00 00) (which also represent the size of the next string)

    ?? 00 00 00,  -> padding will not be validated per [readInplace](https://cs.android.com/android/platform/superproject/main/+/refs/heads/main:frameworks/native/libs/binder/Parcel.cpp;drc=0e31b87a60843c10ec92e1a7045ee84dd05ae94e;l=1820)
    but the terminate sign will

    the ?? will also be the size of next string


    requestCode = data.readInt()      ->    `0@i32` // first 4 payloads of string8
    flags = data.readInt()            ->    `0@i32` // second 4 payloads of string8
    profilerInfo = data.readTypedObject(android.app.ProfilerInfo.CREATOR) -> `0@i32` // third 4 payloads of string8

    // remaining payload of string8 +  + 0@i32 + (...) +  options => new Options { launchTaskId = ?, ???}
    options = data.readTypedObject(android.os.Bundle.CREATOR)     { android.activity.launchTaskId -> 100@i32}
    data.enforceNoDataAvail()
    ```
    */

    /* writer side of `LabeledIntent`
    ```java
      dest.writeString(mSourcePackage); (String -> resolvedType)
      dest.writeInt(mLabelRes); ( 0 -> resultTo)
      // TextUtils.writeToParcel(mNonLocalizedLabel, dest, parcelableFlags);
        {
          writeInt(1)
          writeString8(string)
        }
      dest.writeInt(mIcon)
     ```
    */

    /* TextUtil.writeToParcel
      p.writeInt(1)
      p.writeString8(string)
     */


    /* writer side (https://cs.android.com/android/platform/superproject/main/+/refs/heads/main:out/soong/.intermediates/frameworks/base/framework-minus-apex-intdefs/android_common/xref36/srcjars.xref/android/app/IActivityTaskManager.java;drc=3fa083f29412adcfbc1138c26e7d641d68960d78;l=2043)
    ```java
      data.writeString(resolvedType);
      data.writeStrongBinder(resultTo);
      data.writeString(resultWho);
      data.writeInt(requestCode);
      data.writeInt(flags);
      data.writeTypedObject(profilerInfo, 0);
      data.writeTypedObject(options, 0);
     ```
     */


    val bytes = Parcel.obtain().apply {
      val mIcon = 0
      writeInt(mIcon)
      val resolvedType = null
      writeString(resolvedType)

      val resultTo = null
      writeStrongBinder(resultTo)

      val resultWho = null
      writeString(resultWho)
      val requestCode = 0
      writeInt(requestCode)
      val flags = 0
      writeInt(flags)
      val profilerInfo = null
      writeTypedObject(profilerInfo, 0)
      val options = ActivityOptions.makeBasic().toBundle()
      writeTypedObject(options, 0)
    }



    Log.e(TAG, "${bytes.dataPosition()}")
  }

  // key =>
  // (260 header) + bytes = > form to entry


}




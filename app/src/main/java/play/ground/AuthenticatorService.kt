package play.ground

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.content.pm.LabeledIntent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.os.bundleOf
import org.jetbrains.anko.intentFor

class AuthenticatorService : Service() {
  companion object {
    private const val TAG = "natsuki:s"
  }

  private val authenticator by lazy {
    object : AbstractAccountAuthenticator(this) {
      override fun editProperties(
        response: AccountAuthenticatorResponse?, accountType: String?
      ): Bundle {
        Log.d(TAG, "editProperties() called with: response = $response, accountType = $accountType")
        TODO()
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
        return bundleOf(
          AccountManager.KEY_INTENT to LabeledIntent(intentFor<AuthActivity>(), null, 0, 0)
        )
      }

      override fun confirmCredentials(
        response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?
      ): Bundle {
        Log.d(
          TAG,
          "confirmCredentials() called with: response = $response, account = $account, options = $options"
        )
        TODO()
      }

      override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
      ): Bundle {
        Log.d(
          TAG,
          "getAuthToken() called with: response = $response, account = $account, authTokenType = $authTokenType, options = $options"
        )
        TODO()
      }

      override fun getAuthTokenLabel(authTokenType: String?): String {
        TODO("Not yet implemented")
      }

      override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
      ): Bundle {
        TODO("Not yet implemented")
      }

      override fun hasFeatures(
        response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?
      ): Bundle {
        TODO("Not yet implemented")
      }
    }
  }

  override fun onBind(intent: Intent?) = authenticator.iBinder!!

}
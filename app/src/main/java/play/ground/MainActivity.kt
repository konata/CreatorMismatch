package play.ground

import android.accounts.Account
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "natsuki:c"
    const val RequestCode = 110
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      button("request permission") {
        onClick {
          requestPermissions(arrayOf(android.Manifest.permission.GET_ACCOUNTS), RequestCode)
        }
      }

      button("ChooseTypeAndAccountActivity") {
        onClick {
          val intent =
            Intent().setComponent(ComponentName.unflattenFromString("android/.accounts.ChooseTypeAndAccountActivity"))
              .putExtra("allowableAccountTypes", arrayOf(Const.AccountType))
          startActivity(intent)
        }
      }
    }
  }
}
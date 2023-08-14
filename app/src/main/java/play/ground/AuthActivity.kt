package play.ground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

class AuthActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      button("AuthActivity: $callingPackage") {
        onClick {

        }
      }
    }
  }
}
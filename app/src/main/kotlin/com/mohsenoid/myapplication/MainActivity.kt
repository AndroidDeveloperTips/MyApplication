package com.mohsenoid.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.mohsenoid.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val masterKeyAlias: String by lazy {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun loadSecret(): String? =
        sharedPreferences.getString(SECRET_KEY, null)

    private fun saveSecret(secret: String) {
        with(sharedPreferences.edit()) {
            putString(SECRET_KEY, secret)
            apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val oldSecret = loadSecret()

        setContent {
            MyApplicationTheme {
                MainScreen(secret = oldSecret, onClick = { newSecret ->
                    saveSecret(newSecret)
                })
            }
        }
    }

    companion object {
        private const val FILE_NAME = "some_shared_prefs"
        private const val SECRET_KEY = "secret"
    }
}

@Composable
fun MainScreen(secret: String?, onClick: (secret: String) -> Unit) {
    var text by remember { mutableStateOf(secret ?: "") }
    Column(Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Enter your secret") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { onClick(text) }) {
            Text(text = "Save")
        }
    }
    


}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen(secret = "Some Secret") {}
    }
}

package ru.flounder.fragments.auth
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.flounder.MainActivity
import ru.flounder.R
import ru.flounder.dto.TokenResponse
import ru.flounder.retrofit.RetrofitClient

class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var switchToRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login_button)
        switchToRegister = view.findViewById(R.id.switch_to_register_button)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            login(username, password)
        }
        switchToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    private fun login(username: String, password: String) {
        val loginRequest = mapOf("username" to username, "password" to password)
        Log.d("AUTH", loginRequest.toString())
        RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        val sharedPreferences =
                            requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("token", tokenResponse.token)
                            putString("user_id", tokenResponse.id.toString())
                            apply()
                        }
                        (activity as MainActivity).navigateToStudyModules()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Login failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
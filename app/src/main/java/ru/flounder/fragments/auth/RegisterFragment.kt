package ru.flounder.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.flounder.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.flounder.dto.SignupRequestDTO
import ru.flounder.retrofit.RetrofitClient

class RegisterFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var switchToLoginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        usernameEditText = view.findViewById(R.id.username)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        signupButton = view.findViewById(R.id.signup_button)
        switchToLoginButton = view.findViewById(R.id.switch_to_login_button)

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val signupRequest = SignupRequestDTO(username, email, password)
            RetrofitClient.apiService.signUp(signupRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Registration successful! You can login now", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("AUTH", response.message())
                        Toast.makeText(activity, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        switchToLoginButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}

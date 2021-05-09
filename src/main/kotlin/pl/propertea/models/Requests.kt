package pl.propertea.models

data class SignUpRequest(val username: String, val password: String, val email: String, val phoneNumber: String, val address: String)
data class LoginRequest(val username: String, val password: String)


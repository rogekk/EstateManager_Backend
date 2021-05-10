package pl.propertea.models

import org.joda.time.DateTime

data class SignUpRequest(val username: String, val password: String, val email: String, val phoneNumber: String, val address: String)
data class LoginRequest(val username: String, val password: String)
data class TopicRequest (val id: String, val subject: String, val createdBy: String, val communityId: String,   val description: String)
data class CommunityRequest (val id: String)

data class CreateCommentRequest(
    val createdBy: String,
    val content: String)

package org.example.securityoauth.dto

data class NaverResponse(private val attribute: MutableMap<String, Any>): OAuth2Response {

    val responseAttribute: Map<String, Any?> = attribute["response"] as? Map<String, Any?>
        ?: throw IllegalArgumentException("No response attribute found")

    override fun getProvider(): String {
        return "naver"
    }

    override fun getProviderId(): String {
        return responseAttribute["id"].toString()
    }

    override fun getEmail(): String {
        return responseAttribute["email"].toString()
    }

    override fun getName(): String {
        return responseAttribute["name"].toString()
    }
}

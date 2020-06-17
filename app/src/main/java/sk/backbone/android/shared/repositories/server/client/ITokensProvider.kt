package sk.backbone.android.shared.repositories.server.client

interface ITokensProvider<TokenWrapper> {
    fun getLocalTokens(): TokenWrapper
}
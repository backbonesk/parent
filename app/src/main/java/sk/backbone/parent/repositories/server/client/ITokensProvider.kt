package sk.backbone.parent.repositories.server.client

interface ITokensProvider<TokenWrapper> {
    fun getLocalTokens(): TokenWrapper?
}
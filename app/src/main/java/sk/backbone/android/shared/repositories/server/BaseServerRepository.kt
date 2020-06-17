package sk.backbone.android.shared.repositories.server

import android.content.Context
import sk.backbone.android.shared.repositories.server.client.ITokensProvider

abstract class BaseServerRepository<TokensWrapper>(protected val context: Context, private val tokensProvider: ITokensProvider<TokensWrapper>){
    protected fun getTokens() = tokensProvider.getLocalTokens()
}
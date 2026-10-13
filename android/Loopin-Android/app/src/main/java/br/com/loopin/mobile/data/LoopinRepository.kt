package br.com.loopin.mobile.data

import android.content.Context
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

class LoopinRepository(context: Context) {
    val sessionStore = SessionStore(context)
    private val api = ApiClient.api

    suspend fun login(email: String, senha: String): LoginResponse {
        val response = api.login(LoginRequest(email.trim(), senha))
        sessionStore.save(response)
        return response
    }

    suspend fun registerOperator(nome: String, email: String, senha: String) =
        api.registerOperator(RegisterRequest(nome.trim(), email.trim(), senha))

    suspend fun logout() = sessionStore.clear()
    suspend fun health() = api.health()
    suspend fun currentStrategy() = api.currentStrategy()
    suspend fun strategies() = api.strategies()
    suspend fun strategyHistory() = api.strategyHistory()
    suspend fun createStrategy(request: StrategyRequest) = api.createStrategy(request)
    suspend fun updateStrategy(id: String, request: StrategyRequest) = api.updateStrategy(id, request)
    suspend fun myIdeas() = api.myIdeas()
    suspend fun ideas() = api.ideas()
    suspend fun createIdea(request: IdeaRequest) = api.createIdea(request)
    suspend fun evaluateIdea(id: String, status: String, priority: String, comment: String?) =
        api.evaluateIdea(id, IdeaEvaluationRequest(status, priority, comment))
    suspend fun scoreIdea(id: String) = api.scoreIdea(id)
    suspend fun projects() = api.projects()
    suspend fun createProject(request: ProjectRequest) = api.createProject(request)
    suspend fun addProjectResult(id: String, request: ProjectResultRequest) = api.addProjectResult(id, request)
    suspend fun dashboard() = api.dashboard()
    suspend fun users() = api.users()
    suspend fun createManagedUser(request: ManagedUserRequest) = api.createManagedUser(request)

    fun errorMessage(error: Throwable): String = when (error) {
        is HttpException -> {
            val raw = runCatching { error.response()?.errorBody()?.string() }.getOrNull()
            val serverMessage = runCatching {
                val obj = JsonParser.parseString(raw).asJsonObject
                obj.get("message")?.takeIf { it.isJsonPrimitive }?.asString
            }.getOrNull()
            serverMessage ?: when (error.code()) {
                401 -> "Sua sessão expirou ou os dados de acesso estão incorretos."
                403 -> "Seu perfil não tem permissão para essa operação."
                404 -> "O recurso solicitado não foi encontrado."
                409 -> "Já existe um registro com esses dados ou há um conflito de regra."
                502 -> "O serviço externo de IA não respondeu corretamente. Tente novamente mais tarde."
                503 -> "O recurso ainda não está configurado no servidor."
                else -> "O servidor respondeu com HTTP ${error.code()}."
            }
        }
        is UnknownHostException -> "Não foi possível encontrar o servidor. Confira a URL da API e sua conexão."
        is IOException -> "Não foi possível conectar ao Loopin. Verifique se o backend está em execução."
        else -> error.localizedMessage ?: "Ocorreu um erro inesperado."
    }
}

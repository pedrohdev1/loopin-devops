package br.com.loopin.mobile.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface LoopinApi {
    @GET("health")
    suspend fun health(): HealthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("usuarios")
    suspend fun registerOperator(@Body request: RegisterRequest): UserResponse

    @GET("usuarios/me")
    suspend fun currentUser(): UserResponse

    @GET("usuarios")
    suspend fun users(): List<UserResponse>

    @POST("usuarios/administracao")
    suspend fun createManagedUser(@Body request: ManagedUserRequest): UserResponse

    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: String)

    @GET("estrategias")
    suspend fun strategies(): List<Strategy>

    @GET("estrategias/vigente")
    suspend fun currentStrategy(): Strategy

    @GET("estrategias/historico")
    suspend fun strategyHistory(): List<StrategyHistory>

    @POST("estrategias")
    suspend fun createStrategy(@Body request: StrategyRequest): Strategy

    @PUT("estrategias/{id}")
    suspend fun updateStrategy(@Path("id") id: String, @Body request: StrategyRequest): Strategy

    @DELETE("estrategias/{id}")
    suspend fun deleteStrategy(@Path("id") id: String)

    @GET("ideias/minhas")
    suspend fun myIdeas(): List<Idea>

    @GET("ideias")
    suspend fun ideas(): List<Idea>

    @GET("ideias/{id}")
    suspend fun idea(@Path("id") id: String): Idea

    @POST("ideias")
    suspend fun createIdea(@Body request: IdeaRequest): Idea

    @PUT("ideias/{id}")
    suspend fun updateIdea(@Path("id") id: String, @Body request: IdeaRequest): Idea

    @DELETE("ideias/{id}")
    suspend fun deleteIdea(@Path("id") id: String)

    @PATCH("ideias/{id}/avaliacao")
    suspend fun evaluateIdea(@Path("id") id: String, @Body request: IdeaEvaluationRequest): Idea

    @POST("ia/ideias/{id}/pontuar")
    suspend fun scoreIdea(@Path("id") id: String): AiScoreResponse

    @GET("projetos")
    suspend fun projects(): List<Project>

    @GET("projetos/{id}")
    suspend fun project(@Path("id") id: String): Project

    @POST("projetos")
    suspend fun createProject(@Body request: ProjectRequest): Project

    @PUT("projetos/{id}")
    suspend fun updateProject(@Path("id") id: String, @Body request: ProjectUpdateRequest): Project

    @DELETE("projetos/{id}")
    suspend fun deleteProject(@Path("id") id: String)

    @GET("projetos/{id}/resultados")
    suspend fun projectResults(@Path("id") id: String): List<ProjectResult>

    @POST("projetos/{id}/resultados")
    suspend fun addProjectResult(@Path("id") id: String, @Body request: ProjectResultRequest): Project

    @GET("relatorios/dashboard")
    suspend fun dashboard(): DashboardResponse
}

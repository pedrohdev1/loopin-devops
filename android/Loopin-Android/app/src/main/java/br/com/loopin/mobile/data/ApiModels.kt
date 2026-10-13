package br.com.loopin.mobile.data

data class LoginRequest(val email: String, val senha: String)
data class RegisterRequest(val nome: String, val email: String, val senha: String)
data class ManagedUserRequest(val nome: String, val email: String, val senha: String, val perfil: String)

data class LoginResponse(
    val token: String,
    val id: String,
    val nome: String,
    val email: String,
    val perfil: String,
    val tipoToken: String = "Bearer"
)

data class UserResponse(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val perfil: String = "OPERADOR"
)

data class Strategy(
    val id: String = "",
    val data: String = "",
    val categoria: String = "",
    val campanha: String = "",
    val descricao: String = "",
    val vigente: Boolean = false
)

data class StrategyHistory(
    val id: String = "",
    val estrategiaId: String = "",
    val data: String = "",
    val categoria: String = "",
    val campanha: String = "",
    val vigente: Boolean = false,
    val operacao: String = "",
    val registradoEm: String = "",
    val alteradoPor: String = ""
)

data class StrategyRequest(
    val data: String,
    val categoria: String,
    val campanha: String,
    val descricao: String
)

data class Idea(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val status: String = "PENDENTE",
    val prioridade: String = "NAO_AVALIADA",
    val estrategiaId: String = "",
    val usuarioId: String = "",
    val dataCriacao: String = "",
    val pontuacaoIa: Int? = null,
    val justificativaIa: String? = null,
    val comentarioGestor: String? = null
)

data class IdeaRequest(val titulo: String, val descricao: String)
data class IdeaEvaluationRequest(val status: String, val prioridade: String, val comentarioGestor: String?)

data class AiScoreResponse(
    val ideiaId: String = "",
    val pontuacao: Int = 0,
    val prioridadeSugerida: String = "MEDIA",
    val justificativa: String = "",
    val modelo: String = ""
)

data class Project(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val etapa: String = "PLANEJAMENTO",
    val status: String = "PLANEJADO",
    val investimento: Double = 0.0,
    val lucroObtido: Double = 0.0,
    val prazo: String = "",
    val aumentoProdutividadePercentual: Double = 0.0,
    val progressoPercentual: Int = 0,
    val estrategiaId: String = "",
    val ideiaId: String? = null,
    val gestorResponsavelId: String = "",
    val resultados: List<ProjectResult> = emptyList()
)

data class ProjectRequest(
    val nome: String,
    val descricao: String,
    val etapa: String,
    val status: String,
    val investimento: Double,
    val prazo: String,
    val aumentoProdutividadePercentual: Double,
    val progressoPercentual: Int,
    val estrategiaId: String,
    val ideiaId: String?
)

data class ProjectUpdateRequest(
    val nome: String,
    val descricao: String,
    val etapa: String,
    val status: String,
    val prazo: String,
    val aumentoProdutividadePercentual: Double,
    val progressoPercentual: Int
)

data class ProjectResult(
    val id: String = "",
    val data: String = "",
    val descricao: String = "",
    val investimentoAdicional: Double = 0.0,
    val lucroObtido: Double = 0.0,
    val aumentoProdutividadePercentual: Double? = null,
    val progressoPercentual: Int? = null
)

data class ProjectResultRequest(
    val descricao: String,
    val data: String,
    val investimentoAdicional: Double,
    val lucroObtido: Double,
    val aumentoProdutividadePercentual: Double,
    val progressoPercentual: Int
)

data class DashboardResponse(
    val totalProjetos: Long = 0,
    val projetosEmAndamento: Long = 0,
    val projetosConcluidos: Long = 0,
    val investimentoTotal: Double = 0.0,
    val lucroObtidoTotal: Double = 0.0,
    val roiPercentual: Double = 0.0,
    val prazoMedioRestanteDias: Long = 0,
    val aumentoProdutividadeMedioPercentual: Double = 0.0,
    val progressoMedioPercentual: Double = 0.0,
    val porEstrategia: List<StrategySummary> = emptyList(),
    val porProjeto: List<ProjectSummary> = emptyList()
)

data class StrategySummary(
    val estrategiaId: String = "",
    val categoria: String = "",
    val campanha: String = "",
    val quantidadeProjetos: Long = 0,
    val investimentoTotal: Double = 0.0,
    val lucroObtidoTotal: Double = 0.0,
    val roiPercentual: Double = 0.0
)

data class ProjectSummary(
    val projetoId: String = "",
    val nome: String = "",
    val status: String = "",
    val etapa: String = "",
    val investimento: Double = 0.0,
    val lucroObtido: Double = 0.0,
    val roiPercentual: Double = 0.0,
    val prazo: String? = null,
    val progressoPercentual: Int = 0
)

data class HealthResponse(val status: String = "", val application: String = "", val timestamp: String = "")

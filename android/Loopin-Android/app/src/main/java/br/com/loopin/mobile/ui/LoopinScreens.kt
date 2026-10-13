package br.com.loopin.mobile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.loopin.mobile.data.AiScoreResponse
import br.com.loopin.mobile.data.DashboardResponse
import br.com.loopin.mobile.data.Idea
import br.com.loopin.mobile.data.IdeaRequest
import br.com.loopin.mobile.data.LoopinRepository
import br.com.loopin.mobile.data.ManagedUserRequest
import br.com.loopin.mobile.data.Project
import br.com.loopin.mobile.data.ProjectRequest
import br.com.loopin.mobile.data.ProjectResultRequest
import br.com.loopin.mobile.data.Strategy
import br.com.loopin.mobile.data.StrategyHistory
import br.com.loopin.mobile.data.StrategyRequest
import br.com.loopin.mobile.data.UserResponse
import br.com.loopin.mobile.data.UserSession
import br.com.loopin.mobile.ui.theme.LoopinAmber
import br.com.loopin.mobile.ui.theme.LoopinBorder
import br.com.loopin.mobile.ui.theme.LoopinCream
import br.com.loopin.mobile.ui.theme.LoopinError
import br.com.loopin.mobile.ui.theme.LoopinForest
import br.com.loopin.mobile.ui.theme.LoopinForestDark
import br.com.loopin.mobile.ui.theme.LoopinInk
import br.com.loopin.mobile.ui.theme.LoopinLeaf
import br.com.loopin.mobile.ui.theme.LoopinMint
import br.com.loopin.mobile.ui.theme.LoopinMuted
import br.com.loopin.mobile.ui.theme.LoopinWhite
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.time.LocalDate

private const val ROLE_OPERATOR = "OPERADOR"
private const val ROLE_MANAGER = "GESTOR"
private const val ROLE_LEADER = "LIDER"

@Composable
fun LoginAndRegisterScreen(repository: LoopinRepository) {
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(LoopinCream)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(shape = RoundedCornerShape(20.dp), color = LoopinForest, modifier = Modifier.size(62.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Loop, contentDescription = null, tint = LoopinWhite, modifier = Modifier.size(34.dp))
                    }
                }
                Column {
                    Text("Loopin", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = LoopinForestDark)
                    Text("Ciclos de inovação sustentável", color = LoopinMuted, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(34.dp))
            Text(if (isRegister) "Comece um novo ciclo" else "Bem-vindo de volta", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = LoopinForestDark)
            Spacer(Modifier.height(8.dp))
            Text(
                if (isRegister) "Crie sua conta de operador para registrar ideias e oportunidades." else "Entre para transformar boas ideias em resultados sustentáveis.",
                color = LoopinMuted, lineHeight = 21.sp
            )
            Spacer(Modifier.height(24.dp))
            if (isRegister) {
                OutlinedTextField(value = name, onValueChange = { name = it; error = null }, label = { Text("Nome completo") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                Spacer(Modifier.height(12.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("E-mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Acesso seguro por perfil", color = LoopinMuted, fontSize = 12.sp)
            if (error != null) {
                Spacer(Modifier.height(12.dp))
                FeedbackBanner(text = error!!, isError = true)
            }
            if (info != null) {
                Spacer(Modifier.height(12.dp))
                FeedbackBanner(text = info!!, isError = false)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    error = null
                    info = null
                    if (email.isBlank() || password.isBlank() || (isRegister && name.isBlank())) {
                        error = "Preencha todos os campos para continuar."
                    } else if (isRegister && password.length < 8) {
                        error = "A senha precisa ter pelo menos 8 caracteres."
                    } else {
                        scope.launch {
                            busy = true
                            try {
                                if (isRegister) {
                                    repository.registerOperator(name, email, password)
                                    isRegister = false
                                    password = ""
                                    info = "Cadastro realizado. Entre com seu e-mail e senha."
                                } else {
                                    repository.login(email, password)
                                }
                            } catch (e: Throwable) {
                                error = repository.errorMessage(e)
                            } finally {
                                busy = false
                            }
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoopinForest)
            ) {
                if (busy) CircularProgressIndicator(color = LoopinWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                else Text(if (isRegister) "Criar conta de operador" else "Entrar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { isRegister = !isRegister; error = null; info = null }) {
                Text(if (isRegister) "Já tenho uma conta · Entrar" else "Ainda não tem conta? Cadastre-se", color = LoopinForest, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(20.dp))
            Surface(color = LoopinMint.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = LoopinForest, modifier = Modifier.size(20.dp))
                    Text("Uma ideia de cada vez. Um ciclo melhor para todos.", color = LoopinForestDark, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}

private data class NavItem(val key: String, val title: String, val icon: ImageVector)

@Composable
fun LoopinMainShell(session: UserSession, repository: LoopinRepository, onLogout: suspend () -> Unit) {
    val items = remember(session.perfil) { navigationItems(session.perfil) }
    var selectedKey by remember(session.perfil) { mutableStateOf(items.first().key) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = LoopinCream,
        topBar = { LoopinTopBar(session) },
        bottomBar = {
            NavigationBar(containerColor = LoopinWhite, tonalElevation = 8.dp) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = selectedKey == item.key,
                        onClick = { selectedKey = item.key },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 11.sp) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedKey) {
                "home" -> HomeScreen(session, repository, onNavigate = { selectedKey = it })
                "ideas" -> IdeasScreen(session, repository)
                "strategy" -> StrategyScreen(session, repository)
                "projects" -> ProjectsScreen(session, repository)
                "users" -> UsersScreen(repository)
                "profile" -> ProfileScreen(session, onLogout = { scope.launch { onLogout() } })
                else -> HomeScreen(session, repository, onNavigate = { selectedKey = it })
            }
        }
    }
}

private fun navigationItems(role: String): List<NavItem> = when (role.uppercase()) {
    ROLE_OPERATOR -> listOf(
        NavItem("home", "Início", Icons.Outlined.Home),
        NavItem("ideas", "Minhas ideias", Icons.Outlined.Lightbulb),
        NavItem("strategy", "Estratégia", Icons.Outlined.Campaign),
        NavItem("profile", "Perfil", Icons.Outlined.PersonOutline)
    )
    ROLE_MANAGER -> listOf(
        NavItem("home", "Início", Icons.Outlined.Home),
        NavItem("ideas", "Ideias", Icons.Outlined.Lightbulb),
        NavItem("projects", "Projetos", Icons.Outlined.FolderOpen),
        NavItem("profile", "Perfil", Icons.Outlined.PersonOutline)
    )
    else -> listOf(
        NavItem("home", "Dashboard", Icons.Outlined.BarChart),
        NavItem("strategy", "Estratégias", Icons.Outlined.Campaign),
        NavItem("users", "Usuários", Icons.Outlined.Groups),
        NavItem("profile", "Perfil", Icons.Outlined.PersonOutline)
    )
}

@Composable
private fun LoopinTopBar(session: UserSession) {
    Surface(color = LoopinCream, shadowElevation = 0.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = LoopinForest, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Loop, contentDescription = null, tint = LoopinWhite, modifier = Modifier.size(25.dp))
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Loopin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = LoopinForestDark)
                Text("Inovação em movimento", fontSize = 11.sp, color = LoopinMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(session.nome.substringBefore(" "), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LoopinInk)
                Text(roleLabel(session.perfil), fontSize = 10.sp, color = LoopinMuted)
            }
            Spacer(Modifier.width(8.dp))
            Surface(color = LoopinMint, shape = CircleShape, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(session.nome.firstOrNull()?.uppercase() ?: "L", color = LoopinForest, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun roleLabel(role: String) = when (role.uppercase()) {
    ROLE_OPERATOR -> "Operador"
    ROLE_MANAGER -> "Gestor"
    ROLE_LEADER -> "Liderança"
    else -> role
}

@Composable
private fun HomeScreen(session: UserSession, repository: LoopinRepository, onNavigate: (String) -> Unit) {
    var strategy by remember { mutableStateOf<Strategy?>(null) }
    var dashboard by remember { mutableStateOf<DashboardResponse?>(null) }
    var ideasCount by remember { mutableIntStateOf(0) }
    var pendingCount by remember { mutableIntStateOf(0) }
    var projectCount by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(session.perfil) {
        loading = true
        error = null
        try {
            strategy = runCatching { repository.currentStrategy() }.getOrNull()
            when (session.perfil.uppercase()) {
                ROLE_LEADER -> dashboard = repository.dashboard()
                ROLE_MANAGER -> {
                    val list = repository.ideas()
                    ideasCount = list.size
                    pendingCount = list.count { it.status.equals("PENDENTE", true) || it.status.equals("EM_ANALISE", true) }
                    projectCount = repository.projects().size
                }
                else -> ideasCount = repository.myIdeas().size
            }
        } catch (e: Throwable) {
            error = repository.errorMessage(e)
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Text(if (session.perfil == ROLE_LEADER) "Visão geral" else "Olá, ${session.nome.substringBefore(" ")}!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = LoopinForestDark)
        Spacer(Modifier.height(6.dp))
        Text(homeSubtitle(session.perfil), color = LoopinMuted, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(18.dp))
        FeatureBanner(session.perfil)
        Spacer(Modifier.height(20.dp))
        if (loading) {
            Box(Modifier.fillMaxWidth().padding(25.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = LoopinForest) }
        } else {
            if (error != null) FeedbackBanner(error!!, true)
            if (strategy != null) StrategyHighlight(strategy!!) else EmptyCard("Nenhuma estratégia vigente foi encontrada.", "Quando uma liderança cadastrar uma estratégia, ela aparecerá aqui.")
            Spacer(Modifier.height(18.dp))
            when (session.perfil.uppercase()) {
                ROLE_LEADER -> dashboard?.let {
                    Text("Indicadores da organização", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LoopinInk)
                    Spacer(Modifier.height(10.dp))
                    MetricGrid(
                        listOf(
                            "Projetos" to it.totalProjetos.toString(),
                            "Em andamento" to it.projetosEmAndamento.toString(),
                            "Investimento" to it.investimentoTotal.asCurrency(),
                            "Lucro obtido" to it.lucroObtidoTotal.asCurrency(),
                            "ROI" to "${it.roiPercentual.percentString()}%",
                            "Progresso médio" to "${it.progressoMedioPercentual.percentString()}%"
                        )
                    )
                    Spacer(Modifier.height(18.dp))
                    Text("Projetos em destaque", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LoopinInk)
                    it.porProjeto.take(3).forEach { summary ->
                        Spacer(Modifier.height(8.dp))
                        CardRow(title = summary.nome, subtitle = "${summary.status.replace('_', ' ')} · ${summary.progressoPercentual}% concluído", trailing = summary.investimento.asCurrency())
                    }
                }
                ROLE_MANAGER -> {
                    MetricGrid(listOf("Ideias recebidas" to ideasCount.toString(), "Aguardando análise" to pendingCount.toString(), "Projetos" to projectCount.toString()))
                    Spacer(Modifier.height(18.dp))
                    PrimaryActionButton("Revisar ideias", Icons.Outlined.Lightbulb) { onNavigate("ideas") }
                    Spacer(Modifier.height(10.dp))
                    SecondaryActionButton("Acompanhar projetos", Icons.Outlined.FolderOpen) { onNavigate("projects") }
                }
                else -> {
                    MetricGrid(listOf("Minhas ideias" to ideasCount.toString()))
                    Spacer(Modifier.height(18.dp))
                    PrimaryActionButton("Propor uma ideia", Icons.Outlined.Add) { onNavigate("ideas") }
                    Spacer(Modifier.height(10.dp))
                    SecondaryActionButton("Ver minhas ideias", Icons.Outlined.Lightbulb) { onNavigate("ideas") }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("O ciclo Loopin", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = LoopinInk)
        Spacer(Modifier.height(10.dp))
        CycleSteps()
        Spacer(Modifier.height(16.dp))
    }
}

private fun homeSubtitle(role: String) = when (role.uppercase()) {
    ROLE_LEADER -> "Indicadores claros para acompanhar o impacto das iniciativas."
    ROLE_MANAGER -> "Transforme sugestões em iniciativas com resultados mensuráveis."
    else -> "Pequenas melhorias podem iniciar grandes mudanças."
}

@Composable
private fun FeatureBanner(role: String) {
    Surface(color = LoopinForest, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("IDEIAS QUE MOVEM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LoopinLeaf, letterSpacing = 1.4.sp)
                Spacer(Modifier.height(8.dp))
                Text("Toda boa ideia inicia um novo ciclo.", fontSize = 20.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold, color = LoopinWhite)
                Spacer(Modifier.height(6.dp))
                Text("${if (role == ROLE_LEADER) "Acompanhe" else "Conecte"} ideias, estratégia e impacto.", fontSize = 12.sp, color = LoopinWhite.copy(alpha = 0.82f))
            }
            Spacer(Modifier.width(10.dp))
            Surface(color = LoopinWhite.copy(alpha = 0.12f), shape = CircleShape, modifier = Modifier.size(68.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Loop, contentDescription = null, tint = LoopinLeaf, modifier = Modifier.size(42.dp))
                }
            }
        }
    }
}

@Composable
private fun StrategyHighlight(strategy: Strategy) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = LoopinMint, shape = RoundedCornerShape(10.dp), modifier = Modifier.size(38.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Campaign, contentDescription = null, tint = LoopinForest) }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("ESTRATÉGIA VIGENTE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 10.sp, color = LoopinForest)
                    Text(strategy.categoria, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = LoopinInk)
                }
                StatusPill("VIGENTE")
            }
            Spacer(Modifier.height(14.dp))
            Text(strategy.campanha, fontWeight = FontWeight.SemiBold, color = LoopinInk)
            if (strategy.descricao.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(strategy.descricao, color = LoopinMuted, fontSize = 13.sp, lineHeight = 19.sp)
            }
            if (strategy.data.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Vigente desde ${strategy.data}", color = LoopinMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun CycleSteps() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Ideia", "Projeto", "Resultado").forEachIndexed { index, label ->
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (index == 1) LoopinMint else LoopinWhite), border = BorderStroke(1.dp, LoopinBorder)) {
                Column(Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${index + 1}", color = LoopinForest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(label, color = LoopinInk, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricGrid(metrics: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        metrics.chunked(2).forEach { rowMetrics ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowMetrics.forEach { (label, value) ->
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(17.dp)) {
                        Column(Modifier.fillMaxWidth().padding(13.dp)) {
                            Text(label, color = LoopinMuted, fontSize = 11.sp)
                            Spacer(Modifier.height(7.dp))
                            Text(value, color = LoopinForestDark, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (rowMetrics.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CardRow(title: String, subtitle: String, trailing: String) {
    Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(15.dp)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = LoopinInk, fontSize = 14.sp)
                Text(subtitle, color = LoopinMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(trailing, fontWeight = FontWeight.Bold, color = LoopinForest, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyCard(title: String, description: String) {
    Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = LoopinInk)
            Spacer(Modifier.height(4.dp))
            Text(description, color = LoopinMuted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun ScreenTitle(title: String, subtitle: String, actionLabel: String? = null, actionIcon: ImageVector = Icons.Outlined.Add, onAction: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LoopinForestDark)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = LoopinMuted, lineHeight = 18.sp)
        }
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction, shape = RoundedCornerShape(13.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 10.dp)) {
                Icon(actionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(5.dp))
                Text(actionLabel, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = LoopinForest)) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SecondaryActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, LoopinBorder)) {
        Icon(icon, contentDescription = null, tint = LoopinForest)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.SemiBold, color = LoopinForest)
    }
}

@Composable
private fun FeedbackBanner(text: String, isError: Boolean) {
    val bg = if (isError) Color(0xFFFFEAEA) else LoopinMint
    val fg = if (isError) LoopinError else LoopinForestDark
    Surface(color = bg, shape = RoundedCornerShape(12.dp)) {
        Text(text, color = fg, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.fillMaxWidth().padding(12.dp))
    }
}

@Composable
private fun StatusPill(text: String) {
    val upper = text.uppercase()
    val bg = when {
        upper.contains("APROV") || upper == "CONCLUIDO" || upper == "VIGENTE" -> LoopinMint
        upper.contains("REJEIT") || upper.contains("CANCEL") -> Color(0xFFFBE4E1)
        upper.contains("PEND") || upper.contains("ANALISE") || upper.contains("PLANEJ") -> Color(0xFFFFF0D4)
        else -> Color(0xFFE8EEE9)
    }
    val fg = when {
        bg == LoopinMint -> LoopinForest
        bg == Color(0xFFFBE4E1) -> LoopinError
        bg == Color(0xFFFFF0D4) -> LoopinAmber
        else -> LoopinMuted
    }
    Surface(color = bg, shape = CircleShape) {
        Text(text.replace('_', ' '), modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = fg, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 0.3.sp)
    }
}

@Composable
private fun LoadingBlock() {
    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = LoopinForest) }
}

@Composable
private fun FormField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier, singleLine: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(13.dp)
    )
}

@Composable
private fun DropdownField(label: String, selected: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)) {
            Text("$label: $selected", modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Composable
private fun ProfileScreen(session: UserSession, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        ScreenTitle("Meu perfil", "Informações da sua conta Loopin")
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = LoopinMint, shape = CircleShape, modifier = Modifier.size(76.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(session.nome.firstOrNull()?.uppercase() ?: "L", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = LoopinForest) }
                }
                Spacer(Modifier.height(12.dp))
                Text(session.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LoopinInk)
                Text(roleLabel(session.perfil), color = LoopinForest, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(20.dp))
                ProfileLine("E-mail", session.email)
                HorizontalDivider(color = LoopinBorder, modifier = Modifier.padding(vertical = 10.dp))
                ProfileLine("ID de usuário", session.id)
                HorizontalDivider(color = LoopinBorder, modifier = Modifier.padding(vertical = 10.dp))
                ProfileLine("Tipo de acesso", session.perfil)
            }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, Color(0xFFE9C7C3))) {
            Icon(Icons.Outlined.Logout, contentDescription = null, tint = LoopinError)
            Spacer(Modifier.width(8.dp))
            Text("Sair da conta", color = LoopinError, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
        Text("Loopin · versão inicial", color = LoopinMuted, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = LoopinMuted, fontSize = 11.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, color = LoopinInk, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private fun Double.asCurrency(): String = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
private fun Double.percentString(): String = String.format(Locale("pt", "BR"), "%.2f", this).removeSuffix(",00")

@Composable
private fun IdeasScreen(session: UserSession, repository: LoopinRepository) {
    val role = session.perfil.uppercase()
    var ideas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }
    var busyIdeaId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(role, refresh) {
        loading = true
        error = null
        try {
            ideas = if (role == ROLE_OPERATOR) repository.myIdeas() else repository.ideas()
        } catch (e: Throwable) {
            error = repository.errorMessage(e)
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
        ScreenTitle(
            title = if (role == ROLE_OPERATOR) "Minhas ideias" else "Central de ideias",
            subtitle = if (role == ROLE_OPERATOR) "Registre oportunidades e acompanhe cada etapa." else "Analise propostas e ajude a transformar ideias em iniciativas.",
            actionLabel = if (role == ROLE_OPERATOR) "Nova ideia" else null,
            onAction = if (role == ROLE_OPERATOR) ({ showCreate = true }) else null
        )
        Spacer(Modifier.height(14.dp))
        if (error != null) { FeedbackBanner(error!!, true); Spacer(Modifier.height(8.dp)) }
        if (info != null) { FeedbackBanner(info!!, false); Spacer(Modifier.height(8.dp)) }
        if (loading) {
            LoadingBlock()
        } else if (ideas.isEmpty()) {
            EmptyCard(
                if (role == ROLE_OPERATOR) "Sua primeira ideia começa aqui" else "Nenhuma ideia encontrada",
                if (role == ROLE_OPERATOR) "Cadastre uma sugestão ligada à estratégia vigente para iniciar um novo ciclo." else "Quando os operadores enviarem sugestões, elas aparecerão nesta central."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ideas, key = { it.id }) { idea ->
                    IdeaCard(
                        idea = idea,
                        canManage = role == ROLE_MANAGER,
                        busy = busyIdeaId == idea.id,
                        onScore = {
                            scope.launch {
                                busyIdeaId = idea.id
                                info = null; error = null
                                try {
                                    val score: AiScoreResponse = repository.scoreIdea(idea.id)
                                    info = "IA avaliou a ideia com ${score.pontuacao}/100 e sugeriu prioridade ${score.prioridadeSugerida}."
                                    refresh++
                                } catch (e: Throwable) { error = repository.errorMessage(e) }
                                finally { busyIdeaId = null }
                            }
                        },
                        onDecision = { status ->
                            scope.launch {
                                busyIdeaId = idea.id
                                info = null; error = null
                                try {
                                    val priority = when {
                                        idea.prioridade in listOf("BAIXA", "MEDIA", "ALTA") -> idea.prioridade
                                        status == "APROVADA" -> "ALTA"
                                        else -> "BAIXA"
                                    }
                                    repository.evaluateIdea(
                                        idea.id,
                                        status,
                                        priority,
                                        if (status == "APROVADA") "Aprovada pelo gestor no aplicativo Loopin." else "Reavaliar a proposta em um ciclo futuro."
                                    )
                                    info = if (status == "APROVADA") "Ideia aprovada." else "Ideia rejeitada."
                                    refresh++
                                } catch (e: Throwable) { error = repository.errorMessage(e) }
                                finally { busyIdeaId = null }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCreate) {
        IdeaDialog(
            onDismiss = { showCreate = false },
            onSave = { title, description ->
                scope.launch {
                    showCreate = false
                    error = null; info = null
                    try {
                        repository.createIdea(IdeaRequest(title.trim(), description.trim()))
                        info = "Ideia enviada. Ela já está vinculada à estratégia vigente."
                        refresh++
                    } catch (e: Throwable) { error = repository.errorMessage(e) }
                }
            }
        )
    }
}

@Composable
private fun IdeaCard(
    idea: Idea,
    canManage: Boolean,
    busy: Boolean,
    onScore: () -> Unit,
    onDecision: (String) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(19.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(idea.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LoopinInk)
                    if (idea.dataCriacao.isNotBlank()) Text(idea.dataCriacao, fontSize = 10.sp, color = LoopinMuted)
                }
                StatusPill(idea.status)
            }
            Spacer(Modifier.height(8.dp))
            Text(idea.descricao, color = LoopinMuted, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                StatusPill(idea.prioridade)
                if (idea.pontuacaoIa != null) StatusPill("IA ${idea.pontuacaoIa}/100")
            }
            if (!idea.justificativaIa.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Análise da IA", color = LoopinForest, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(idea.justificativaIa!!, color = LoopinMuted, fontSize = 12.sp, lineHeight = 17.sp)
            }
            if (!idea.comentarioGestor.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Comentário do gestor: ${idea.comentarioGestor}", color = LoopinMuted, fontSize = 12.sp)
            }
            if (canManage && (idea.status == "PENDENTE" || idea.status == "EM_ANALISE")) {
                Spacer(Modifier.height(12.dp))
                if (busy) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(color = LoopinForest, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Text("Processando...", fontSize = 12.sp, color = LoopinMuted)
                    }
                } else {
                    OutlinedButton(onClick = onScore, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp)); Text("Avaliar com IA")
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onDecision("APROVADA") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = LoopinForest)) { Text("Aprovar") }
                        OutlinedButton(onClick = { onDecision("REJEITADA") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Rejeitar", color = LoopinError) }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdeaDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova ideia", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Descreva a oportunidade que você identificou.", color = LoopinMuted, fontSize = 13.sp)
                FormField(title, { title = it; error = null }, "Título da ideia")
                FormField(description, { description = it; error = null }, "Descrição", singleLine = false)
                if (error != null) Text(error!!, color = LoopinError, fontSize = 12.sp)
            }
        },
        confirmButton = { Button(onClick = { if (title.isBlank() || description.isBlank()) error = "Preencha o título e a descrição." else onSave(title, description) }) { Text("Enviar ideia") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun StrategyScreen(session: UserSession, repository: LoopinRepository) {
    val isLeader = session.perfil.uppercase() == ROLE_LEADER
    var strategies by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var history by remember { mutableStateOf<List<StrategyHistory>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var selectedToEdit by remember { mutableStateOf<Strategy?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refresh) {
        loading = true; error = null
        try {
            strategies = repository.strategies()
            history = repository.strategyHistory()
        } catch (e: Throwable) { error = repository.errorMessage(e) }
        finally { loading = false }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
        ScreenTitle("Estratégias", if (isLeader) "Defina o foco que orienta ideias e projetos." else "Acompanhe o direcionamento atual da organização.", if (isLeader) "Nova" else null, onAction = if (isLeader) ({ selectedToEdit = null; showForm = true }) else null)
        Spacer(Modifier.height(14.dp))
        if (error != null) { FeedbackBanner(error!!, true); Spacer(Modifier.height(8.dp)) }
        if (info != null) { FeedbackBanner(info!!, false); Spacer(Modifier.height(8.dp)) }
        if (loading) LoadingBlock() else {
            if (strategies.isEmpty()) EmptyCard("Ainda não há estratégias", "A liderança pode cadastrar uma estratégia para iniciar o ciclo de inovação.")
            strategies.forEach { strategy ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(15.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(strategy.categoria, fontWeight = FontWeight.Bold, color = LoopinInk, fontSize = 16.sp)
                                Text(strategy.campanha, color = LoopinMuted, fontSize = 12.sp)
                            }
                            if (strategy.vigente) StatusPill("VIGENTE")
                            else StatusPill("HISTÓRICA")
                        }
                        if (strategy.descricao.isNotBlank()) {
                            Spacer(Modifier.height(8.dp)); Text(strategy.descricao, color = LoopinMuted, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Data: ${strategy.data}", color = LoopinMuted, fontSize = 11.sp)
                        if (isLeader) {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { selectedToEdit = strategy; showForm = true }, shape = RoundedCornerShape(11.dp)) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Editar estratégia")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.History, contentDescription = null, tint = LoopinForest)
                Spacer(Modifier.width(8.dp))
                Text("Histórico de alterações", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LoopinInk)
            }
            Spacer(Modifier.height(8.dp))
            if (history.isEmpty()) EmptyCard("Sem histórico disponível", "As alterações futuras serão registradas nesta seção.")
            else history.take(10).forEach { event ->
                Spacer(Modifier.height(7.dp))
                Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top) {
                        Surface(color = LoopinMint, shape = CircleShape, modifier = Modifier.size(33.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.History, null, tint = LoopinForest, modifier = Modifier.size(18.dp)) } }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(event.operacao.replace('_', ' '), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LoopinInk)
                            Text("${event.categoria} · ${event.campanha}", fontSize = 12.sp, color = LoopinMuted)
                            Text("${event.data} · ${event.alteradoPor}", fontSize = 10.sp, color = LoopinMuted)
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        StrategyDialog(
            initial = selectedToEdit,
            onDismiss = { showForm = false; selectedToEdit = null },
            onSave = { request ->
                scope.launch {
                    showForm = false; error = null; info = null
                    try {
                        if (selectedToEdit == null) repository.createStrategy(request)
                        else repository.updateStrategy(selectedToEdit!!.id, request)
                        info = if (selectedToEdit == null) "Estratégia criada e definida como vigente." else "Estratégia atualizada; histórico registrado."
                        selectedToEdit = null; refresh++
                    } catch (e: Throwable) { error = repository.errorMessage(e) }
                }
            }
        )
    }
}

@Composable
private fun StrategyDialog(initial: Strategy?, onDismiss: () -> Unit, onSave: (StrategyRequest) -> Unit) {
    var category by remember(initial?.id) { mutableStateOf(initial?.categoria.orEmpty()) }
    var campaign by remember(initial?.id) { mutableStateOf(initial?.campanha.orEmpty()) }
    var description by remember(initial?.id) { mutableStateOf(initial?.descricao.orEmpty()) }
    var date by remember(initial?.id) { mutableStateOf(initial?.data?.takeIf { it.isNotBlank() } ?: LocalDate.now().toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova estratégia" else "Editar estratégia", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                FormField(category, { category = it; error = null }, "Categoria")
                FormField(campaign, { campaign = it; error = null }, "Campanha")
                FormField(description, { description = it; error = null }, "Descrição", singleLine = false)
                FormField(date, { date = it; error = null }, "Data (AAAA-MM-DD")
                if (error != null) Text(error!!, color = LoopinError, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (category.isBlank() || campaign.isBlank() || date.length != 10) error = "Informe categoria, campanha e data no formato AAAA-MM-DD."
                else onSave(StrategyRequest(date, category.trim(), campaign.trim(), description.trim()))
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ProjectsScreen(session: UserSession, repository: LoopinRepository) {
    val isManager = session.perfil.uppercase() == ROLE_MANAGER
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var currentStrategy by remember { mutableStateOf<Strategy?>(null) }
    var approvedIdeas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var resultProject by remember { mutableStateOf<Project?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refresh, session.perfil) {
        loading = true; error = null
        try {
            projects = repository.projects()
            if (isManager) {
                currentStrategy = runCatching { repository.currentStrategy() }.getOrNull()
                approvedIdeas = runCatching { repository.ideas().filter { it.status == "APROVADA" } }.getOrDefault(emptyList())
            }
        } catch (e: Throwable) { error = repository.errorMessage(e) }
        finally { loading = false }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
        ScreenTitle(
            "Projetos e iniciativas",
            if (isManager) "Acompanhe a execução e registre os resultados." else "Acompanhe investimentos, progresso e retorno.",
            if (isManager) "Novo" else null,
            onAction = if (isManager) ({ showCreate = true }) else null
        )
        Spacer(Modifier.height(14.dp))
        if (error != null) { FeedbackBanner(error!!, true); Spacer(Modifier.height(8.dp)) }
        if (info != null) { FeedbackBanner(info!!, false); Spacer(Modifier.height(8.dp)) }
        if (loading) LoadingBlock()
        else if (projects.isEmpty()) EmptyCard("Nenhum projeto cadastrado", "Quando uma ideia aprovada virar iniciativa, o projeto aparecerá aqui.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(projects, key = { it.id }) { project ->
                ProjectCard(project, isManager, onAddResult = { resultProject = project })
            }
        }
    }

    if (showCreate) {
        if (currentStrategy == null) {
            AlertDialog(onDismissRequest = { showCreate = false }, title = { Text("Estratégia necessária") }, text = { Text("Cadastre uma estratégia vigente antes de criar um projeto.") }, confirmButton = { TextButton(onClick = { showCreate = false }) { Text("Entendi") } })
        } else {
            CreateProjectDialog(
                strategy = currentStrategy!!,
                approvedIdeas = approvedIdeas,
                onDismiss = { showCreate = false },
                onSave = { request ->
                    scope.launch {
                        showCreate = false; error = null; info = null
                        try {
                            repository.createProject(request)
                            info = "Projeto criado e vinculado à estratégia vigente."
                            refresh++
                        } catch (e: Throwable) { error = repository.errorMessage(e) }
                    }
                }
            )
        }
    }

    resultProject?.let { project ->
        ProjectResultDialog(
            project = project,
            onDismiss = { resultProject = null },
            onSave = { request ->
                scope.launch {
                    resultProject = null; error = null; info = null
                    try {
                        repository.addProjectResult(project.id, request)
                        info = "Medição registrada. Os totais do projeto foram atualizados."
                        refresh++
                    } catch (e: Throwable) { error = repository.errorMessage(e) }
                }
            }
        )
    }
}

@Composable
private fun ProjectCard(project: Project, canAddResult: Boolean, onAddResult: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(19.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(project.nome, fontWeight = FontWeight.Bold, color = LoopinInk, fontSize = 16.sp)
                    Text("Etapa: ${project.etapa.replace('_', ' ')}", color = LoopinMuted, fontSize = 11.sp)
                }
                StatusPill(project.status)
            }
            Spacer(Modifier.height(8.dp))
            Text(project.descricao, color = LoopinMuted, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniFact("Investimento", project.investimento.asCurrency(), Modifier.weight(1f))
                MiniFact("Lucro", project.lucroObtido.asCurrency(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Progresso", fontSize = 12.sp, color = LoopinMuted, modifier = Modifier.weight(1f))
                Text("${project.progressoPercentual}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LoopinForest)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(progress = (project.progressoPercentual / 100f).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth().height(7.dp), color = LoopinForest, trackColor = LoopinMint)
            Spacer(Modifier.height(10.dp))
            Text("Prazo: ${project.prazo}", fontSize = 11.sp, color = LoopinMuted)
            if (project.resultados.isNotEmpty()) {
                Spacer(Modifier.height(7.dp))
                Text("${project.resultados.size} medição(ões) registrada(s)", color = LoopinForest, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            if (canAddResult) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onAddResult, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(7.dp)); Text("Registrar resultado")
                }
            }
        }
    }
}

@Composable
private fun MiniFact(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = LoopinCream, shape = RoundedCornerShape(11.dp), modifier = modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = LoopinMuted)
            Spacer(Modifier.height(3.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LoopinForestDark)
        }
    }
}

@Composable
private fun CreateProjectDialog(
    strategy: Strategy,
    approvedIdeas: List<Idea>,
    onDismiss: () -> Unit,
    onSave: (ProjectRequest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var investment by remember { mutableStateOf("0") }
    var deadline by remember { mutableStateOf(LocalDate.now().plusMonths(2).toString()) }
    var selectedIdeaId by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo projeto", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Estratégia: ${strategy.categoria} · ${strategy.campanha}", color = LoopinForest, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                FormField(name, { name = it; error = null }, "Nome do projeto")
                FormField(description, { description = it; error = null }, "Descrição", singleLine = false)
                FormField(investment, { investment = it; error = null }, "Investimento inicial (R$)", keyboardType = KeyboardType.Decimal)
                FormField(deadline, { deadline = it; error = null }, "Prazo (AAAA-MM-DD)")
                DropdownField("Ideia aprovada", if (selectedIdeaId.isBlank()) "Sem vínculo" else approvedIdeas.firstOrNull { it.id == selectedIdeaId }?.titulo?.take(26) ?: "Selecionada", listOf("Sem vínculo") + approvedIdeas.map { it.titulo.take(45) }) { selected ->
                    selectedIdeaId = if (selected == "Sem vínculo") "" else approvedIdeas.firstOrNull { it.titulo.take(45) == selected }?.id.orEmpty()
                }
                if (error != null) Text(error!!, color = LoopinError, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = investment.replace(',', '.').toDoubleOrNull()
                if (name.isBlank() || description.isBlank() || amount == null || amount < 0 || deadline.length != 10) {
                    error = "Confira nome, descrição, investimento e prazo."
                } else {
                    onSave(ProjectRequest(name.trim(), description.trim(), "PILOTO", "PLANEJADO", amount, deadline, 0.0, 0, strategy.id, selectedIdeaId.ifBlank { null }))
                }
            }) { Text("Criar projeto") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ProjectResultDialog(project: Project, onDismiss: () -> Unit, onSave: (ProjectResultRequest) -> Unit) {
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var extraInvestment by remember { mutableStateOf("0") }
    var profit by remember { mutableStateOf("0") }
    var productivity by remember { mutableStateOf(project.aumentoProdutividadePercentual.toString()) }
    var progress by remember { mutableStateOf(project.progressoPercentual.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar resultado", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(project.nome, color = LoopinMuted, fontSize = 12.sp)
                FormField(description, { description = it; error = null }, "Descrição da medição", singleLine = false)
                FormField(date, { date = it; error = null }, "Data (AAAA-MM-DD)")
                FormField(extraInvestment, { extraInvestment = it; error = null }, "Investimento adicional (R$)", keyboardType = KeyboardType.Decimal)
                FormField(profit, { profit = it; error = null }, "Lucro obtido nesta medição (R$)", keyboardType = KeyboardType.Decimal)
                FormField(productivity, { productivity = it; error = null }, "Aumento de produtividade (%)", keyboardType = KeyboardType.Decimal)
                FormField(progress, { progress = it; error = null }, "Progresso atual (0-100)", keyboardType = KeyboardType.Number)
                if (error != null) Text(error!!, color = LoopinError, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                val investmentValue = extraInvestment.replace(',', '.').toDoubleOrNull()
                val profitValue = profit.replace(',', '.').toDoubleOrNull()
                val productivityValue = productivity.replace(',', '.').toDoubleOrNull()
                val progressValue = progress.toIntOrNull()
                if (description.isBlank() || date.length != 10 || investmentValue == null || investmentValue < 0 || profitValue == null || productivityValue == null || productivityValue < 0 || progressValue == null || progressValue !in 0..100) {
                    error = "Confira os campos. Progresso precisa ficar entre 0 e 100."
                } else {
                    onSave(ProjectResultRequest(description.trim(), date, investmentValue, profitValue, productivityValue, progressValue))
                }
            }) { Text("Salvar medição") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun UsersScreen(repository: LoopinRepository) {
    var users by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refresh) {
        loading = true; error = null
        try { users = repository.users().sortedBy { it.nome.lowercase() } }
        catch (e: Throwable) { error = repository.errorMessage(e) }
        finally { loading = false }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
        ScreenTitle("Usuários", "Gerencie os perfis de acesso da plataforma.", "Novo", onAction = { showCreate = true })
        Spacer(Modifier.height(14.dp))
        if (error != null) { FeedbackBanner(error!!, true); Spacer(Modifier.height(8.dp)) }
        if (info != null) { FeedbackBanner(info!!, false); Spacer(Modifier.height(8.dp)) }
        if (loading) LoadingBlock() else if (users.isEmpty()) EmptyCard("Nenhum usuário encontrado", "As contas cadastradas aparecerão nesta lista.") else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(users, key = { it.id }) { user ->
                    Card(colors = CardDefaults.cardColors(containerColor = LoopinWhite), border = BorderStroke(1.dp, LoopinBorder), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = LoopinMint, shape = CircleShape, modifier = Modifier.size(42.dp)) { Box(contentAlignment = Alignment.Center) { Text(user.nome.firstOrNull()?.uppercase() ?: "?", color = LoopinForest, fontWeight = FontWeight.Bold) } }
                            Spacer(Modifier.width(11.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.nome, color = LoopinInk, fontWeight = FontWeight.SemiBold)
                                Text(user.email, color = LoopinMuted, fontSize = 11.sp)
                            }
                            StatusPill(roleLabel(user.perfil).uppercase())
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        CreateUserDialog(
            onDismiss = { showCreate = false },
            onSave = { request ->
                scope.launch {
                    showCreate = false; error = null; info = null
                    try {
                        repository.createManagedUser(request)
                        info = "Usuário criado com perfil ${request.perfil}."
                        refresh++
                    } catch (e: Throwable) { error = repository.errorMessage(e) }
                }
            }
        )
    }
}

@Composable
private fun CreateUserDialog(onDismiss: () -> Unit, onSave: (ManagedUserRequest) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(ROLE_OPERATOR) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criar usuário", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                FormField(name, { name = it; error = null }, "Nome")
                FormField(email, { email = it; error = null }, "E-mail", keyboardType = KeyboardType.Email)
                OutlinedTextField(value = password, onValueChange = { password = it; error = null }, label = { Text("Senha (mínimo 8 caracteres)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(13.dp))
                DropdownField("Perfil", roleLabel(role), listOf("Operador", "Gestor", "Liderança")) { selected ->
                    role = when (selected) { "Gestor" -> ROLE_MANAGER; "Liderança" -> ROLE_LEADER; else -> ROLE_OPERATOR }
                }
                if (error != null) Text(error!!, color = LoopinError, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank() || email.isBlank() || password.length < 8) error = "Preencha os campos e use uma senha com 8 caracteres ou mais."
                else onSave(ManagedUserRequest(name.trim(), email.trim(), password, role))
            }) { Text("Criar usuário") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

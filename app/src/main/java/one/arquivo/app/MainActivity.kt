package one.arquivo.app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SystemBlue = Color(0xFF081A48)
private val ClassicGray = Color(0xFFC4C5C0)
private val Highlight = Color(0xFFEEF0E9)
private val Shadow = Color(0xFF595B58)
private val ScreenGreen = Color(0xFFB8FFBD)
private val Amber = Color(0xFFFFB840)
private val Ink = Color(0xFF101311)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ArquivoApp() }
    }
}

enum class Screen { DESKTOP, EDITOR, ARCHIVE, RECIPES }

data class Layer(val name: String, val icon: String, val color: Color)

@Composable
private fun ArquivoApp() {
    val context = LocalContext.current
    var screen by remember { mutableStateOf(Screen.DESKTOP) }
    var activeLayer by remember { mutableStateOf<Layer?>(null) }
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                sourceBitmap = BitmapFactory.decodeStream(stream)
                activeLayer = null
                screen = Screen.EDITOR
            }
        }
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = SystemBlue, surface = ClassicGray)) {
        Surface(color = ClassicGray, modifier = Modifier.fillMaxSize()) {
            when (screen) {
                Screen.DESKTOP -> Desktop(
                    onNew = { imagePicker.launch("image/*") }, onArchive = { screen = Screen.ARCHIVE },
                    onRecipes = { screen = Screen.RECIPES },
                    onAsciiMagic = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ascii-magic.com/app")))
                    },
                    onInfo = { message = "ARQUIVO.EXE v0.1\nProtótipo de interface visual." }
                )
                Screen.EDITOR -> Editor(
                    sourceBitmap = sourceBitmap, activeLayer = activeLayer, onBack = { screen = Screen.DESKTOP },
                    onArchive = { screen = Screen.ARCHIVE }, onRender = { message = "RENDERIZAÇÃO CONCLUÍDA\nProtótipo salvo na memória visual." },
                    onRemove = { activeLayer = null }
                )
                Screen.ARCHIVE -> Archive(
                    onBack = { screen = Screen.EDITOR }, onSelect = { activeLayer = it; screen = Screen.EDITOR }
                )
                Screen.RECIPES -> Recipes(onBack = { screen = Screen.DESKTOP }, onUse = { screen = Screen.EDITOR })
            }
            message?.let { RetroDialog(text = it, onClose = { message = null }) }
        }
    }
}

@Composable
private fun Desktop(
    onNew: () -> Unit,
    onArchive: () -> Unit,
    onRecipes: () -> Unit,
    onAsciiMagic: () -> Unit,
    onInfo: () -> Unit
) {
    RetroWindow(title = "ARQUIVO.EXE — Área de Trabalho", footer = "Pronto. Selecione um arquivo para começar.") {
        Text("SISTEMA CRIATIVO PESSOAL", fontSize = 12.sp, letterSpacing = 2.sp, color = SystemBlue, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        Text("ARQUIVO.EXE", fontFamily = FontFamily.Monospace, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Ink)
        Text("estúdio visual de memória digital", color = Shadow, fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DesktopIcon("▣", "NOVO\nPROJETO", onNew)
            DesktopIcon("▤", "ACERVO", onArchive)
            DesktopIcon("⌘", "ASCII MAGIC\nONLINE", onAsciiMagic)
            DesktopIcon("✦", "RECEITAS", onRecipes)
            DesktopIcon("?", "SOBRE O\nSISTEMA", onInfo)
        }
        Spacer(Modifier.weight(1f))
        RetroLabel("BETA 0.1  •  NOSTALGIA EM PROCESSAMENTO")
    }
}

@Composable
private fun DesktopIcon(symbol: String, label: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).retroRaised().background(Highlight), contentAlignment = Alignment.Center) {
            Text(symbol, color = SystemBlue, fontFamily = FontFamily.Monospace, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Text(label, fontFamily = FontFamily.Monospace, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Editor(sourceBitmap: Bitmap?, activeLayer: Layer?, onBack: () -> Unit, onArchive: () -> Unit, onRender: () -> Unit, onRemove: () -> Unit) {
    val renderedBitmap = remember(sourceBitmap, activeLayer) {
        if (sourceBitmap != null && activeLayer?.name == "Dither Game Boy") DitherEngine.gameBoy(sourceBitmap) else sourceBitmap
    }
    RetroWindow(title = "Projeto sem título.img", footer = "1080 × 1920 px  |  Camada ${if (activeLayer == null) "00" else "01"}  |  Sem salvar", onClose = onBack) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Arquivo", "Editar", "Camadas", "Ajuda").forEach { Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().weight(1f).retroSunken().background(Color(0xFF182A31)), contentAlignment = Alignment.Center) {
            if (renderedBitmap != null) {
                Image(
                    bitmap = renderedBitmap.asImageBitmap(),
                    contentDescription = "Prévia do projeto",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(10.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(132.dp).background(if (activeLayer?.color != null) activeLayer.color.copy(alpha = .75f) else Color(0xFF778890)))
                    Spacer(Modifier.height(14.dp))
                    Text("IMAGEM AGUARDANDO", color = ScreenGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Text("Use NOVO PROJETO para selecionar uma foto.", color = Color(0xFFB8C6C5), fontSize = 11.sp, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (activeLayer != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RetroLabel("CAMADA: ${activeLayer.name.uppercase()}")
                TinyButton("REMOVER", onRemove)
            }
            Spacer(Modifier.height(10.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RetroButton("ACERVO", Modifier.weight(1f), onArchive)
            RetroButton("RENDER", Modifier.weight(1f), onRender)
        }
    }
}

@Composable
private fun Archive(onBack: () -> Unit, onSelect: (Layer) -> Unit) {
    val assets = listOf(
        "DITHER GAME BOY" to Layer("Dither Game Boy", "░", Color(0xFF8BAC0F)),
        "PELÍCULA 35MM" to Layer("Película 35mm", "▧", Amber),
        "TELA CRT" to Layer("Fósforo CRT", "▦", Color(0xFF58C7A3)),
        "PAPEL XEROX" to Layer("Xerox gasto", "▤", Color(0xFFE6D6A8)),
        "CROMO MOLHADO" to Layer("Cromo molhado", "◇", Color(0xFF9CB6D5)),
        "ARQUIVO 2007" to Layer("Compressão 2007", "▥", Color(0xFFCB7AE5)),
        "CONCRETO" to Layer("Concreto vivo", "▩", Color(0xFF868984))
    )
    RetroWindow(title = "Gerenciador de Estéticas", footer = "Selecione um recurso para aplicar.", onClose = onBack) {
        Text("ACERVO LOCAL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = SystemBlue)
        Text("Materiais para composições fora do padrão.", fontSize = 12.sp, color = Shadow)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            assets.forEach { (description, layer) ->
                Row(Modifier.fillMaxWidth().retroRaised().clickable { onSelect(layer) }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).background(layer.color), contentAlignment = Alignment.Center) { Text(layer.icon, color = Ink, fontSize = 23.sp) }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) { Text(description, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("toque para adicionar como camada", fontSize = 11.sp, color = Shadow) }
                    Text("+", fontSize = 22.sp, color = SystemBlue)
                }
            }
        }
    }
}

@Composable
private fun Recipes(onBack: () -> Unit, onUse: () -> Unit) {
    RetroWindow(title = "Receitas Salvas", footer = "0 arquivos pessoais  |  3 referências do sistema", onClose = onBack) {
        Text("RECEITAS DE REFERÊNCIA", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = SystemBlue)
        Spacer(Modifier.height(12.dp))
        listOf("CIDADE ARQUIVADA" to "CRT + concreto + película", "NATUREZA SINTÉTICA" to "verde monitor + xerox", "NOITE DE DADOS" to "cromo + compressão 2007").forEach { (name, desc) ->
            Column(Modifier.fillMaxWidth().retroRaised().clickable(onClick = onUse).padding(12.dp)) { Text(name, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold); Text(desc, fontSize = 12.sp, color = Shadow) }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.weight(1f)); RetroLabel("EM BREVE: SALVE SUAS PRÓPRIAS COMBINAÇÕES")
    }
}

@Composable
private fun RetroWindow(title: String, footer: String, onClose: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(10.dp).border(2.dp, Ink, RectangleShape).background(ClassicGray)) {
        Row(Modifier.fillMaxWidth().background(SystemBlue).padding(horizontal = 8.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("▣  $title", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (onClose != null) Text("×", color = Ink, modifier = Modifier.retroRaised().background(ClassicGray).clickable(onClick = onClose).padding(horizontal = 7.dp, vertical = 1.dp), fontWeight = FontWeight.Black)
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(18.dp), content = content)
        Text(footer, Modifier.fillMaxWidth().retroSunken().padding(6.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Ink)
    }
}

@Composable private fun RetroButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) = Text(text, modifier.retroRaised().clickable(onClick = onClick).padding(vertical = 11.dp), textAlign = TextAlign.Center, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
@Composable private fun TinyButton(text: String, onClick: () -> Unit) = Text(text, Modifier.retroRaised().clickable(onClick = onClick).padding(horizontal = 7.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
@Composable private fun RetroLabel(text: String) = Text(text, Modifier.background(Color(0xFF273038)).padding(horizontal = 7.dp, vertical = 5.dp), color = ScreenGreen, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
private fun Modifier.retroRaised() = this.border(2.dp, Highlight).border(2.dp, Shadow)
private fun Modifier.retroSunken() = this.border(2.dp, Shadow).border(2.dp, Highlight)

@Composable
private fun RetroDialog(text: String, onClose: () -> Unit) {
    AlertDialog(onDismissRequest = onClose, confirmButton = { RetroButton("OK", onClick = onClose) }, title = { Text("ARQUIVO.EXE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }, text = { Text(text, fontFamily = FontFamily.Monospace) }, containerColor = ClassicGray, shape = RectangleShape)
}

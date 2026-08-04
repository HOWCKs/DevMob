# ARQUIVO.EXE

Protótipo Android de um laboratório visual com uma interface original inspirada em software criativo clássico. A primeira versão testa a sensação de interface: área de trabalho, janelas, acervo de recursos visuais, camadas e receitas.

## Estado do protótipo

Esta versão é um protótipo navegável com a primeira ferramenta local funcional: **DITHER GAME BOY**. Em **Novo Projeto**, selecione uma imagem do aparelho; abra o **Acervo** e aplique a camada de dither para convertê-la localmente em uma paleta de quatro tons de verde. A imagem não é enviada para servidor. Exportação e demais efeitos continuam em prototipação.

## Atalho de inspiração

A área de trabalho inclui o atalho **ASCII MAGIC ONLINE**, que abre `https://www.ascii-magic.com/app` no navegador padrão do aparelho. É uma ligação externa para referência e experimentação; o ARQUIVO.EXE não envia imagens para esse serviço, não incorpora seu código e não possui afiliação com o ASCII Magic.

## Executar / instalar

O workflow **Build Android APK** gera um APK de debug como artefato em cada push para a branch do projeto. Baixe `arquivo-exe-debug-apk` no GitHub Actions e instale `app-debug.apk` no Android.

O aplicativo usa Kotlin, Jetpack Compose e uma linguagem visual própria — não reproduz nem inclui ativos do Windows ou de outro software proprietário.

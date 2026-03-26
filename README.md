<div align="center">

![Acolhe Pet](docs/banner-acolhe-pet.png)

<br/>

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/Status-Concluído-brightgreen?style=for-the-badge)]()

<br/>

> Implementação independente do desafio criado por [**Lucas Carrilho (@devmagro)**](https://github.com/karilho/desafioCadastro) e [**Alberto Huber**](https://github.com/karilho/desafioCadastro).
> Repositório original: [karilho/desafioCadastro](https://github.com/karilho/desafioCadastro)

</div>

---

## 📋 Sobre o projeto

**Acolhe Pet** é um sistema de cadastro de pets via **CLI (linha de comando)** desenvolvido em Java puro, com orientação a objetos, persistência em arquivos `.txt` e validações de domínio. O objetivo é permitir que um abrigo de animais gerencie seus pets disponíveis para adoção cadastrando, buscando, alterando e deletando registros além de permitir a customização dinâmica do formulário de perguntas.

---

## 🖥️ Demo

![Print Terminal](docs/print-terminal.png)

---

## ✅ Progresso do desafio

| Passo | Funcionalidade | Status |
|-------|----------------|--------|
| 1 | Leitura do `formulario.txt` e exibição no terminal | ✅ Concluído |
| 2 | Menu inicial com validação de entrada | ✅ Concluído |
| 3 | Cadastro de novo pet com validações de domínio | ✅ Concluído |
| 4 | Persistência em arquivo `.txt` por pet | ✅ Concluído |
| 5 | Busca por múltiplos critérios | ✅ Concluído |
| 6 | Alteração de dados do pet cadastrado | ✅ Concluído |
| 7 | Deleção de pet com confirmação | ✅ Concluído |
| 8 | Encerramento do sistema | ✅ Concluído |
| Extra | Gerenciamento dinâmico do formulário de perguntas | ✅ Concluído |

---

## 🚀 Funcionalidades

### 🐾 Cadastro de pets
- Lê as perguntas dinamicamente do arquivo `formulario.txt`
- Coleta respostas via terminal e cria um objeto `Pet` validado
- Suporta perguntas extras adicionadas pelo usuário, salvas no formato `[EXTRA - PERGUNTA] - RESPOSTA`

**Validações aplicadas:**
- Nome completo obrigatório (mínimo nome + sobrenome, somente letras)
- `TipoPet` (`CACHORRO` / `GATO`) e `Sexo` (`MACHO` / `FEMEA`) via `enum` com `fromInput()`
- Peso entre **0,5 kg** e **60 kg**
- Idade entre **0** e **20 anos** (valores < 1 representados como `0.x`)
- Campos em branco preenchidos automaticamente com a constante `NÃO INFORMADO`

### 🔍 Busca por critérios
- Filtro obrigatório por tipo de animal (Cachorro / Gato)
- Combinação de até 2 critérios: nome/sobrenome, sexo, idade, peso, raça ou endereço
- Busca parcial e sem distinção de maiúsculas/minúsculas e acentos

### ✏️ Alteração de pet
- Busca o pet pelos mesmos critérios de busca
- Permite alterar todos os campos exceto tipo e sexo
- Deixar em branco mantém o valor atual

### 🗑️ Deleção de pet
- Busca o pet pelos mesmos critérios de busca
- Exige confirmação digitando `SIM` antes de deletar

### 📋 Listagem
- Lista todos os pets cadastrados em formato resumido

### 📝 Gerenciamento do formulário (Extra)
- **Criar** nova pergunta — numerada automaticamente
- **Alterar** texto de perguntas extras (originais são protegidas)
- **Deletar** perguntas extras com renumeração automática das restantes
- Respostas das perguntas extras são salvas no arquivo do pet

---

## 💾 Formato dos arquivos gerados

Cada pet é salvo em um arquivo `.txt` individual na pasta `petsCadastrados/`, com o nome no formato:

```
20240315T1423-FLORZINHADASILVA.txt
```

**Exemplo de conteúdo:**
```
1 - Florzinha da Silva
2 - GATO
3 - FEMEA
4 - Rua das Flores, 42, Centro, Araras
5 - 3.0 ano(s)
6 - 4.5kg
7 - Siamês
8 - [EXTRA - Qual a cor do pet?] - Laranja
9 - [EXTRA - Nível de fofura] - 10
```

---

## 🗂️ Estrutura do projeto

```
desafio-cadastro-pets/
│
├── formulario.txt                  # Perguntas do cadastro (lidas em runtime)
├── petsCadastrados/                # Gerado em runtime — ignorado pelo Git
├── examples/                       # Exemplos de arquivos gerados (versionado)
├── docs/
│   ├── banner-acolhe-pet.png
│   └── print-terminal.png
│
├── src/
│   └── main/java/br/com/guisleri/petsistema/
│       ├── cli/
│       │   ├── Main.java           # Ponto de entrada — menus e fluxo CLI
│       │   └── IO.java             # Utilitário de entrada/saída no terminal
│       ├── domain/
│       │   ├── Pet.java            # Entidade principal com factory method
│       │   ├── Endereco.java       # Value Object para endereço
│       │   ├── TipoPet.java        # Enum: CACHORRO, GATO
│       │   └── Sexo.java           # Enum: MACHO, FEMEA
│       └── repository/
│           ├── PetRepository.java  # Persistência e leitura dos arquivos
│           └── PetArquivo.java     # Associação entre Pet e seu arquivo
│
├── pom.xml
└── .gitignore
```

---

## ⚙️ Como executar

### Pré-requisitos

- **Java 21+** (testado com Temurin 25)
- **Maven 3.9+**

### Via terminal

```bash
# Clone o repositório
git clone https://github.com/marcosguisleri/desafio-cadastro-pets.git
cd desafio-cadastro-pets

# Compile e execute
mvn clean package
java -cp target/classes br.com.guisleri.petsistema.cli.Main
```

### Via IntelliJ IDEA

Abra o projeto como projeto Maven e execute a classe `Main` diretamente.

> ⚠️ **Importante:** o arquivo `formulario.txt` deve estar na **raiz do projeto** (diretório de trabalho ao executar).

---

## 🏗️ Decisões de implementação

- **Factory method (`Pet.createPet`)** — garante que um `Pet` nunca seja criado em estado inválido; todas as validações ficam centralizadas no domínio
- **Enums com `fromInput()`** — `TipoPet` e `Sexo` encapsulam a conversão e normalização da entrada do usuário
- **Value Object `Endereco`** — isola a lógica de normalização de campos (trim, `NÃO INFORMADO`)
- **Constantes centralizadas** — `FORMULARIO` e `TOTAL_PERGUNTAS_ORIGINAIS` evitam magic numbers e strings espalhadas pelo código
- **Persistência via filesystem** — alinhada ao enunciado do desafio, sem dependências externas
- **Utilitário `IO`** — abstrai as operações de leitura e escrita no terminal

---

## 🔮 Possíveis melhorias futuras

- Migração da persistência em arquivos `.txt` para banco de dados relacional (PostgreSQL + JDBC/JPA)
- Interface web com Spring Boot
- Busca por data de cadastro
- Destacar termo buscado nos resultados
- Testes automatizados com JUnit 5

---

## 👤 Autor

**Marcos Guisleri**
- GitHub: [@marcosguisleri](https://github.com/marcosguisleri)
- LinkedIn: [marcosguisleri](https://www.linkedin.com/in/marcosguisleri/)

---

<div align="center">
  <sub>Desafio proposto por <a href="https://github.com/karilho">Lucas Carrilho (@devmagro)</a> · Implementação independente</sub>
</div>

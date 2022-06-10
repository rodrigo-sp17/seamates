import 'package:i18n_extension/i18n_extension.dart';

extension Localization on String {
  static var _t = Translations("en_us") +
      {
        "en_us": "Hello, how are you?",
        "pt_br": "Olá, como vai você?",
        "es": "¿Hola! Cómo estás?",
        "fr": "Salut, comment ca va?",
        "de": "Hallo, wie geht es dir?",
      } +
      {"en_us": "Notifications", "pt_br": "Notificações"} +
      {"en_us": "Personal Data", "pt_br": "Dados Pessoais"} +
      {
        "en_us": "No notifications to show",
        "pt_br": "Nenhuma notificação para exibir"
      } +
      {"en_us": "%s Profile", "pt_br": "Perfil de %s"} +
      {
        "en_us": "Are you sure you want to unfriend %s?",
        "pt_br": "Desfazer amizade com %s?"
      } +
      {"en_us": "Unfriended %s!", "pt_br": "Desfeita amizade com %s!"} +
      {
        "en_us": "%s requested your friendship!",
        "pt_br": "%s solicitou sua amizade!"
      } +
      {
        "en_us": "%s accepted your friendship request!",
        "pt_br": "%s aceitou sua amizade!"
      } +
      {"en_us": "About", "pt_br": "Sobre"} +
      {
        "en_us": 'SeaMates is an app for monitoring your work shifts,'
            ' sharing with friends and easily checking who is available.\n\n',
        "pt_br":
            'SeaMates é um app para monitorar suas escalas de trabalho, compartilhar com amigos e checar quem está disponível.\n\n'
      } +
      {"en_us": 'Online website at ', "pt_br": 'Versão web em '} +
      {"en_us": "'%s\nRequested on %s'", "pt_br": "%s\nSolicitado em %s"} +
      {"en_us": "Day %s", "pt_br": "Dia %s"} +
      {"en_us": "Not an auth user!", "pt_br": "Você não está logado!"} +
      {"en_us": "User is local", "pt_br": "Usuário é local"} +
      {
        "en_us":
            "Could not connect to server. Please check your internet connection",
        "pt_br":
            "Não foi possível conectar ao servidor. Por favor, cheque sua conexão"
      } +
      {"en_us": "Invalid data", "pt_br": "Dados inválidos"} +
      {
        "en_us": "The username and email already exist",
        "pt_br": "O nome de usuário e email já existem"
      } +
      {
        "en_us": "The username already exists",
        "pt_br": "O nome de usuário já existe"
      } +
      {"en_us": "The email already exists", "pt_br": "O email já existe"} +
      {
        "en_us": "Ops, something is wrong with the server!",
        "pt_br": "Ops, algo está errado com o servidor!"
      } +
      {
        "en_us": "Ops, the server responded unexpectedly!",
        "pt_br": "Ops, o servidor respondeu de forma inesperada!"
      } +
      {"en_us": "Request timed out", "pt_br": "Requisição expirada"} +
      {
        "en_us": "Failed to fetch user info",
        "pt_br": "Falha ao buscar informações do usuário"
      } +
      {
        "en_us": "Something seem wrong with the server...",
        "pt_br": "Algo parece errado com o servidor"
      } +
      {
        "en_us": "Email already exists. Choose another one",
        "pt_br": "O email já existe. Por favor, escolha outro"
      } +
      {
        "en_us": "Oops...something is wrong with the server!",
        "pt_br": "Ops...algo está errado com o servidor"
      } +
      {
        "en_us": "Sync is unavailable in local mode",
        "pt_br": "Sincronização indisponível no modo local"
      } +
      {
        "en_us": "Sync forbidden - please log in again",
        "pt_br": "Sincronização proibida - por favor, entre novamente"
      } +
      {
        "en_us": "Failed to fetch data",
        "pt_br": "Falha ao buscar informações do usuário"
      } +
      {
        "en_us": "Failed to sync locally",
        "pt_br": "Falha ao sincronizar localmente"
      } +
      {
        "en_us": "Synced shifts cannot be deleted when in local mode",
        "pt_br": "Escalas sincronizadas não podem ser deletadas em modo local"
      } +
      {
        "en_us": "Some items could not be deleted",
        "pt_br": "Alguns itens não puderam ser deletados"
      } +
      {
        "en_us": "Could not reach server. Are you online?",
        "pt_br": "Não foi possível conectar com o servidor. Você está online?"
      } +
      {
        "en_us": "You are not unauthorized",
        "pt_br": "Você não está autorizado(a)"
      } +
      {
        "en_us": "Something went wrong...",
        "pt_br": "Algo de errado aconteceu..."
      } +
      {"en_us": "Sync failed!", "pt_br": "Falha na sincronização!"} +
      {"en_us": "Request unauthorized", "pt_br": "Requisição não autorizada"} +
      {"en_us": "The user does not exist!", "pt_br": "O usuário não existe!"} +
      {
        "en_us": "Trying to be friends with yourself? Nice!",
        "pt_br": "Tentando ser amigo de si mesmo? Legal!"
      } +
      {"en_us": "Request failed!", "pt_br": "Falha na requisição!"} +
      {
        "en_us": "The request does not exist!",
        "pt_br": "A solicitação não existe!"
      } +
      {"en_us": "Could not accept!", "pt_br": "Não foi possível aceitar!"} +
      {"en_us": "Unfriending failed!", "pt_br": "Falha ao desfazer amizade!"} +
      {"en_us": "The friend does not exist!", "pt_br": "O amigo não existe!"} +
      {
        "en_us": "Could not remove friend!",
        "pt_br": "Não foi possível remover o amigo!"
      } +
      {"en_us": "Available Friends", "pt_br": "Amigos Disponíveis"} +
      {
        "en_us": "Not available in local mode",
        "pt_br": "Não disponível no modo local"
      } +
      {"en_us": "No friends available", "pt_br": "Nenhum amigo disponível"} +
      {
        "en_us": "Error loading friends! Please, try again!",
        "pt_br": "Erro ao carregar amigos!"
      } +
      {"en_us": "Enter a new value", "pt_br": "Digite um novo valor"} +
      {"en_us": "CANCEL", "pt_br": "CANCELAR"} +
      {"en_us": "SAVE", "pt_br": "SALVAR"} +
      {"en_us": "SAVE CHANGES", "pt_br": "SALVAR ALTERAÇÕES"} +
      {"en_us": "Edition failed!", "pt_br": "Falha na edição!"} +
      {"en_us": "Edition failed", "pt_br": "Falha na edição!"} +
      {
        "en_us": "The email already exists. Please choose another one",
        "pt_br": "O email já existe. Por favor, escolha outro"
      } +
      {
        "en_us": "Unexpected server response",
        "pt_br": "Resposta inesperada do servidor"
      } +
      {"en_us": "Deletion failed", "pt_br": "Falha ao deletar"} +
      {
        "en_us": "Sorry, the deletion was not authorized",
        "pt_br": "Desculpe, a remoção não foi autorizada"
      } +
      {
        "en_us":
            "Sorry, deletion could not be performed at this moment. Could you try logging in again?",
        "pt_br":
            "Desculpe, não foi possível deletar nesse momento. Poderia tentar logar novamente?"
      } +
      {"en_us": "Profile Info", "pt_br": "Informações do Perfil"} +
      {"en_us": "Logged as Local User", "pt_br": "Logado como Usuário Local"} +
      {"en_us": "Username", "pt_br": "Nome de Usuário"} +
      {"en_us": "Name", "pt_br": "Nome"} +
      {"en_us": "Email", "pt_br": "Email"} +
      {"en_us": "DELETE ACCOUNT", "pt_br": "APAGAR CONTA"} +
      {"en_us": "UPGRADE ACCOUNT", "pt_br": "SINCRONIZAR CONTA"} +
      {
        "en_us":
            "Are you sure you want to logout?\nAll your un-synced modifications will be discarded.",
        "pt_br":
            "Tem certeza que deseja sair?\nTodas suas alterações não-sincronizadas serão descartadas."
      } +
      {"en_us": "YES, LOG ME OUT", "pt_br": "SIM, QUERO SAIR"} +
      {"en_us": "Account deletion", "pt_br": "Apagar conta"} +
      {
        "en_us":
            "This actions is permanent.\nPlease confirm your password on the field below to allow deletion:",
        "pt_br":
            "Esta ação é permanente.\nPor favor, confirme sua senha no campo abaixo para prosseguir:"
      } +
      {"en_us": "YES, DELETE MY ACCOUNT", "pt_br": "SIM, APAGUE MINHA CONTA"} +
      {"en_us": "Login failed", "pt_br": "Falha no login"} +
      {
        "en_us": "Incorrect username/email and/or password",
        "pt_br": "Nome de usuário/email e/ou senha incorretos"
      } +
      {
        "en_us": "Something went wrong!\n",
        "pt_br": "Algo de errado aconteceu"
      } +
      {"en_us": "Email or Username", "pt_br": "Email ou Nome de Usuário"} +
      {"en_us": "Password", "pt_br": "Senha"} +
      {"en_us": "Upgrade Successful", "pt_br": "Sincronização realizada!"} +
      {
        "en_us":
            "Do you want to synchronize your local data with your online account?",
        "pt_br": "Deseja sincronizar seus dados locais com sua conta online?"
      } +
      {
        "en_us": "Sorry, we could not sync your data :(",
        "pt_br": "Desculpe, não conseguimos sincronizar seus dados :("
      } +
      {"en_us": "YES", "pt_br": "SIM"} +
      {"en_us": "Calendar", "pt_br": "Calendário"} +
      {
        "en_us": "Start of unavailability",
        "pt_br": "Começo da indisponibilidade"
      } +
      {"en_us": "Boarding day", "pt_br": "Dia de embarque"} +
      {"en_us": "Leaving day", "pt_br": "Dia de desembarque"} +
      {"en_us": "End of unavailability", "pt_br": "Fim da indisponibilidade"} +
      {"en_us": "Shift", "pt_br": "Escala"} +
      {"en_us": "Add a shift", "pt_br": "Adicionar escala"} +
      {"en_us": "Invalid date", "pt_br": "Data inválida"} +
      {
        "en_us": "Please check your boarding date",
        "pt_br": "Por favor, cheque sua data de embarque"
      } +
      {
        "en_us": "Unavailability can't start after boarding date",
        "pt_br": "Indisponibilidade não pode começar após o embarque"
      } +
      {
        "en_us": "Boarding date is mandatory",
        "pt_br": "Data de embarque é obrigatória"
      } +
      {
        "en_us": "Please check your leaving date",
        "pt_br": "Por favor, cheque sua data de desembarque"
      } +
      {
        "en_us": "You can't board after you leave",
        "pt_br": "Você não pode embarcar depois de desembarcar"
      } +
      {
        "en_us": "Leaving date is mandatory if cycle is not fulfilled",
        "pt_br":
            "Data de desembarque é obrigatória se o ciclo não foi preenchido"
      } +
      {
        "en_us": "Please check your dates",
        "pt_br": "Por favor, cheque as datas"
      } +
      {
        "en_us": "You can't be available before you leave",
        "pt_br": "Você não pode estar disponível antes de desembarcar"
      } +
      {
        "en_us": "Are you sure you typed numbers?",
        "pt_br": "Tem certeza que digitou números?"
      } +
      {
        "en_us": "Isn't 3 years too much for a shift?",
        "pt_br": "3 anos não é demais para uma escala?"
      } +
      {
        "en_us": "You can't have a negative cycle day",
        "pt_br": "Você não pode ter um ciclo negativo"
      } +
      {
        "en_us": "Sorry, only 10 repeats are allowed for each input",
        "pt_br":
            "Desculpe, apenas 10 repetições são permitidas para cada escala"
      } +
      {
        "en_us": "You can't have negative repeats",
        "pt_br": "Repetições negativas não são permitidas"
      } +
      {
        "en_us": "Pre-boarding meetings, trainings, quarantines, etc...",
        "pt_br": "Reuniões de pré-embarque, treinamentos, quarentenas, etc..."
      } +
      {"en_us": "Boarding date", "pt_br": "Data de embarque"} +
      {
        "en_us": "The date you will actually board the vehicle",
        "pt_br": "Dia que embarcar de fato"
      } +
      {"en_us": "Days on board", "pt_br": "Dias a bordo"} +
      {
        "en_us": "These days will be added to your dates",
        "pt_br": "Estes dias serão adicionados a suas datas"
      } +
      {"en_us": "Leaving date", "pt_br": "Data de desembarque"} +
      {
        "en_us": "The date you will actually leave the vehicle",
        "pt_br": "Dia que desembarcar de fato"
      } +
      {
        "en_us":
            "The date (exclusive) after leaving in which you will be available for events",
        "pt_br":
            "A data (não inclusa) após desembarcar na qual estará disponível para eventos"
      } +
      {"en_us": "Times to repeat", "pt_br": "Vezes para repetir"} +
      {
        "en_us": "Use 0 or blank to not repeat the shift",
        "pt_br": "Use 0 ou em branco para não repetir a escala"
      } +
      {"en_us": "Add shift", "pt_br": "Adicionar escala"} +
      {"en_us": "Friends", "pt_br": "Amizades"} +
      {"en_us": "Friendship requested!", "pt_br": "Amizade solicitada!"} +
      {"en_us": "Friendship accepted!", "pt_br": "Amizade aceita!"} +
      {"en_us": "Requests", "pt_br": "Solicitações"} +
      {"en_us": "ACCEPT", "pt_br": "ACEITAR"} +
      {"en_us": "On land", "pt_br": "Em terra"} +
      {"en_us": "On sea", "pt_br": "A bordo"} +
      {"en_us": "Remove friendship", "pt_br": "Desfazer amizade"} +
      {"en_us": "Request friendship", "pt_br": "Solicitar amizade"} +
      {
        "en_us": "Type the username of the friend you want to add:",
        "pt_br": "Digite o nome de usuário do amigo que quer adicionar:"
      } +
      {"en_us": "REQUEST FRIENDSHIP", "pt_br": "SOLICITAR AMIZADE"} +
      {"en_us": "UNFRIEND", "pt_br": "DESFAZER AMIZADE"} +
      {
        "en_us": "Could not fetch user information",
        "pt_br": "Não foi possível buscar informações do usuário"
      } +
      {
        "en_us": "Sorry, something went wrong. Please try again!",
        "pt_br": "Desculpe, algo deu errado. Por favor, tente novamente!"
      } +
      {
        "en_us": "You are not authorized to signup",
        "pt_br": "Você não está autorizado a se cadastrar"
      } +
      {"en_us": "Signup failed", "pt_br": "Falha no cadastro"} +
      {"en_us": "SIGNUP", "pt_br": "CADASTRAR"} +
      {"en_us": "Shifts", "pt_br": "Escalas"} +
      {"en_us": "Profile", "pt_br": "Perfil"} +
      {"en_us": "Continue with Facebook", "pt_br": "Continue com Facebook"} +
      {"en_us": "Login with email", "pt_br": "Login com email"} +
      {"en_us": "Signup", "pt_br": "Cadastro"} +
      {"en_us": "Continue offline", "pt_br": "Continuar offline"} +
      {"en_us": "Remain in local mode", "pt_br": "Permanecer em modo local"} +
      {"en_us": "Forgot your password?", "pt_br": "Esqueceu sua senha?"} +
      {"en_us": "Entering in Local mode", "pt_br": "Entrando em modo Local"} +
      {
        "en_us": "You are now going in local mode.\n"
            "\n"
            "In this mode, you will not be able to:\n"
            "- Add friends\n"
            "- View friends shifts\n"
            "- Invite friends to events\n"
            "- Sync with the cloud\n"
            "\n"
            "However, don't worry!\n"
            "You can add an account later and sync your shifts! :)",
        "pt_br":
            "Você está entrando no modo local.\n\nNeste modo, você não poderá:\n- Adicionar amigos\n- Ver escalas dos amigos\n- Convidar amigos para eventos\n- Sincronizar com a nuvem\n\nNo entanto, não se preocupe! Você pode adicionar uma conta mais tarde e sincronizar suas escalas! :)"
      } +
      {"en_us": "GOT IT!", "pt_br": "ENTENDI!"} +
      {"en_us": "YES, LOG ME OUT!", "pt_br": "SIM, QUERO SAIR"} +
      {"en_us": "Deleted!", "pt_br": "Removido!"} +
      {
        "en_us": "Please login to confirm your registration",
        "pt_br": "Por favor, faça login para confirmar seu cadastro"
      } +
      {
        "en_us": "Password confirmation is mandatory",
        "pt_br": "Confirmação de senha é obrigatória"
      } +
      {
        "en_us": "Confirm password does not match password",
        "pt_br": "Confirmação e senha não são iguais"
      } +
      {"en_us": "Confirm Password", "pt_br": "Confirmar Senha"};

  String get i18n => localize(this, _t);

  String fill(List<Object> params) => localizeFill(this, params);
}

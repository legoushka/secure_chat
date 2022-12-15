import Util.PhoneNumber
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class ViewModel {
    private val client: Client = Client(this)
    var clientUser: User? = null
    val serverAddress = mutableStateOf("localhost")

    val serverPort = mutableStateOf("8080")

    val isLoginScreenActive = mutableStateOf(true)
    val isDarkThemeActivated = mutableStateOf(true)
    val isRegisterModeActive = mutableStateOf(false)
    val isServerAddressValid = mutableStateOf(true)
    val isPhoneNumberValid = mutableStateOf(true)
    val isUserDataValid = mutableStateOf(true)

    val loginScreenPhoneNumber = mutableStateOf("")
    val loginScreenName = mutableStateOf("")
    val loginScreenSurname = mutableStateOf("")
    val loginScreenGender = mutableStateOf(true)
    val loginScreenIconUrl = mutableStateOf("")
    val loginScreenPassword = mutableStateOf("")

    val userList = mutableStateListOf<User>()
    val messageList = mutableStateListOf<Message>()

    fun onLoginPressButton() {

        loginScreenPhoneNumber.value = PhoneNumber.formatPhoneNumber(loginScreenPhoneNumber.value)
        if (loginScreenPhoneNumber.value.length != 11) {
            isPhoneNumberValid.value = false
            return
        }

        client.connectToServer()
        client.userLogIn()
        client.receiveMessageFromServer()
    }

    fun onRegisterPressButton() {
        var flag: Boolean = true
        if (
            (loginScreenName.value.length !in (3..16)) or
            (loginScreenSurname.value.length !in (3..16)) or
            (loginScreenPassword.value.length !in (3..16))
        ) {
            flag = false
            isUserDataValid.value = false
        }

        loginScreenPhoneNumber.value = PhoneNumber.formatPhoneNumber(loginScreenPhoneNumber.value)
        if (loginScreenPhoneNumber.value.length != 11) {
            flag = false
            isPhoneNumberValid.value = false
        }
        if (flag){
            client.connectToServer()
            client.userRegistration()
        }
    }

    fun sendMessage(message: Message) {
        client.sendMessage(message.messageContent)
    }

    fun closeApp() {
        client.disableClient(ClientDisableType.SUCCESSFUL_EXIT)
    }

}

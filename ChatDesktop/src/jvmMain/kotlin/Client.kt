import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

class Client(
    private val viewModel: ViewModel,
    private val encryptionController: EncryptionController = EncryptionController()
) {

    private var isConnected = false
    private var socketConnection: SocketConnection? = null
    private var socket: Socket? = null

    fun connectToServer() {
        viewModel.messageList.clear()
        viewModel.userList.clear()
        if (!isConnected) {
            try {
                socket = Socket(viewModel.serverAddress.value, viewModel.serverPort.value.toInt())
                socketConnection = SocketConnection(socket)
                isConnected = true
            } catch (e: IOException) {
                viewModel.isServerAddressValid.value = false
            } catch (e: UnknownHostException) {
                viewModel.isServerAddressValid.value = false
            } catch (e: NumberFormatException) {
                viewModel.isServerAddressValid.value = false
            }
        }
    }

    fun userLogIn() {
        encryptionController.setPhoneNum(viewModel.loginScreenPhoneNumber.value)
        encryptionController.generatePrivateKey()
        while (isConnected) {
            try {
                viewModel.clientUser = User(
                    0,
                    viewModel.loginScreenPhoneNumber.value,
                    "",
                    "",
                    true,
                    "",
                    encryptionController.publicKey
                )
                val message = socketConnection?.receive()
                if (message?.messageType == MessageType.LOGIN_REQUEST) {
                    socketConnection?.send(
                        Message(
                            MessageType.USER_DATA,
                            EncryptionController.hash(viewModel.loginScreenPassword.value, viewModel.clientUser?.phonenum),
                            viewModel.clientUser,
                            null
                        )
                    )
                }
                if (message?.messageType == MessageType.LOGIN_REJECTED) {
                    viewModel.isUserDataValid.value = false
                    disableClient(ClientDisableType.REJECTED)
                    break
                }
                if (message?.messageType == MessageType.LOGIN_ACCEPTED) {
                    println("LOGIN ACCEPTED")
                    viewModel.isLoginScreenActive.value = false
                    viewModel.userList.addAll(message.userList)
                    viewModel.clientUser = message.user
                    break
                }
            } catch (exception: IOException) {
                try {
                    disableClient(ClientDisableType.REJECTED)
                    viewModel.isLoginScreenActive.value = true
                } catch (exception: IOException) {
                    disableClient(ClientDisableType.REJECTED)
                }
            }
        }
    }

    fun userRegistration() {
        while (isConnected) {
            try {
                viewModel.clientUser = User(
                    0, viewModel.loginScreenPhoneNumber.value,
                    viewModel.loginScreenName.value,
                    viewModel.loginScreenSurname.value,
                    viewModel.loginScreenGender.value,
                    viewModel.loginScreenIconUrl.value,
                    null)
                println("RECEIVE MESSAGE")
                val message = socketConnection?.receive()
                if (message?.messageType == MessageType.LOGIN_REQUEST) {
                    println("LOGIN REQUEST")
                    socketConnection?.send(
                        Message(
                            MessageType.REGISTRATION_REQUEST,
                            EncryptionController.hash(viewModel.loginScreenPassword.value, viewModel.clientUser?.phonenum),
                            viewModel.clientUser,
                            null
                        )
                    )
                }
                if (message?.messageType == MessageType.REGISTER_REJECTED) {
                    viewModel.isUserDataValid.value = false
                    disableClient(ClientDisableType.REJECTED)
                    break
                }
                if (message?.messageType == MessageType.REGISTER_ACCEPTED) {
                    println("REGISTER ACCEPTED")
                    viewModel.isRegisterModeActive.value = false
                    viewModel.isUserDataValid.value = true
                    viewModel.isPhoneNumberValid.value = true
                    socketConnection?.close()
                    isConnected = false
                    break
                }
            } catch (exception: IOException) {
                exception.printStackTrace();
                try {
                    socketConnection?.close();
                    isConnected = false;
                    viewModel.isLoginScreenActive.value = true
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
            }
        }
    }

    fun sendMessage(text: String?) {
        try {
            socketConnection?.send(Message(
                MessageType.TEXT_MESSAGE,
                encryptionController.encrypt(text, viewModel.userList),
                viewModel.clientUser,
                null
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun receiveMessageFromServer() {
        Thread {
            while (isConnected) {
                try {
                    val message = socketConnection?.receive()
                    if (message?.messageType == MessageType.TEXT_MESSAGE) {
                        message.messageContent = encryptionController.decrypt(message.messageContent)
                        viewModel.messageList.add(message)
                    }
                    if (message?.messageType == MessageType.USER_JOINED) {
                        println("USER JOINED")
                        viewModel.isLoginScreenActive.value = false
                        if (message.user != viewModel.clientUser) {
                            viewModel.userList.add(message.user)
                        }
                        viewModel.messageList.add(message)
                    }
                    if (message?.messageType == MessageType.USER_LEFT) {
                        println("USER LEFT")
                        viewModel.userList.remove(message.user)
                        viewModel.messageList.add(message)
                    }
                } catch (e: java.lang.Exception) {
                    disableClient(ClientDisableType.CRASHED)
                    viewModel.isLoginScreenActive.value = true
                    break
                }
            }
        }.start()
    }

    fun disableClient(clientSuccessfulExit: ClientDisableType) {
        try {
            if (isConnected) {
                if (clientSuccessfulExit == ClientDisableType.SUCCESSFUL_EXIT
                    || clientSuccessfulExit == ClientDisableType.CRASHED)
                {
                    socketConnection?.send(Message(MessageType.USER_LEFT, null, viewModel.clientUser, null))
                }
                viewModel.messageList.clear()
                viewModel.userList.clear()
                isConnected = false
                viewModel.clientUser = null
                viewModel.isLoginScreenActive.value = true
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

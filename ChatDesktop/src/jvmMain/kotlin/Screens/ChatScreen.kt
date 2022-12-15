package Screens

import Message
import MessageType
import User
import ViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.succlz123.lib.imageloader.ImageAsyncImageUrl
import org.succlz123.lib.imageloader.core.ImageCallback
import java.net.MalformedURLException
import java.net.URL

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: ViewModel,
) {
    val listState = rememberLazyListState()
    val text = remember { mutableStateOf("") }

    val surfaceBorderColor = MaterialTheme.colors.primary
    val surfaceShape = RoundedCornerShape(6.dp)
    Surface() {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.weight(5f)) {
                Surface(
                    modifier = Modifier.weight(3f).fillMaxHeight(),
                    border = BorderStroke(1.dp, surfaceBorderColor),
                    shape = surfaceShape
                ) {
                    MessagesWindow(viewModel.messageList, listState, viewModel.clientUser)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    border = BorderStroke(1.dp, surfaceBorderColor),
                    shape = surfaceShape
                ) {
                    UserList(viewModel.userList)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(modifier = Modifier.weight(1f), shape = surfaceShape) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = text.value,
                        onValueChange = { text.value = it },
                        maxLines = 1,
                        modifier = Modifier.weight(3f).height(50.dp).onPreviewKeyEvent { KeyEvent ->
                            when {
                                (KeyEvent.key == Key.Enter && KeyEvent.type == KeyEventType.KeyDown) -> {
                                    sendMessage(viewModel, text, viewModel.clientUser)
                                    true
                                }

                                else -> false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        sendMessage(viewModel, text, viewModel.clientUser)
                    }, modifier = Modifier.weight(1f).height(50.dp)) {
                        Text(text = "Оптравить")
                    }
                }
            }

        }
    }

}


fun sendMessage(
    viewModel: ViewModel,
    text: MutableState<String>,
    user: User?,
) {
    if (text.value != "") {
        viewModel.sendMessage(Message(MessageType.TEXT_MESSAGE, text.value, user, null))
        text.value = ""
    }
}

@Composable
fun MessagesWindow(messages: List<Message>, listState: LazyListState, clientUser: User?) {
    LazyColumn(
        state = listState,
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(messages) { message ->
            if (message.messageType == MessageType.TEXT_MESSAGE) {
                Card(
                    elevation = 4.dp, modifier = Modifier.background(
                        color = MaterialTheme.colors.surface,
                        shape = MaterialTheme.shapes.large
                    ).fillMaxWidth().wrapContentWidth(
                        if(message.user != clientUser) Alignment.Start else Alignment.End
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        UserComponent(message.user)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = message.messageContent, style = MaterialTheme.typography.body2)
                    }
                }
            }
            if (message.messageType == MessageType.USER_JOINED || message.messageType == MessageType.USER_LEFT) {
                Card(
                    elevation = 4.dp, modifier = Modifier.background(
                        color = MaterialTheme.colors.surface,
                        shape = MaterialTheme.shapes.large
                    )
                )
                {
                    Text(
                        text = "Пользователь ${message.user.name + " " + message.user.surnname} " +
                                if(message.messageType == MessageType.USER_JOINED)
                                {
                                    if(message.user.gender) {"подключился к чату."} else {"подключилась к чату."}}
                                else { if(message.user.gender) {"отключился от чата."} else {"отключилась от чата."}},
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
    }
}


@Composable
fun UserList(userList: List<User>, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Пользователи",
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )
        Divider(thickness = 1.5.dp, color = MaterialTheme.colors.primary)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
            items(userList) { user ->
                UserComponent(user)
            }
        }
    }
}


@Composable
fun UserComponent(user: User) {
    Row(modifier = Modifier) {
        ImageAsyncImageUrl(
            user.iconURL,
            imageCallback = ImageCallback(
                placeHolderView = { LoadingImage() },
                errorView = { ErrorImage() }) {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(percent = 10))
                        .border(2.dp, MaterialTheme.colors.primary),
                    painter = it,
                    contentDescription = "Image"
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = user.name + " " + user.surnname, style = MaterialTheme.typography.body1)
    }
}


@Composable
fun ErrorImage() {
    Image(
        painter = painterResource("avatar_placeholder.jpg"),
        contentDescription = "Placeholder User image",
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(percent = 10))
            .border(2.dp, MaterialTheme.colors.primary),
    )
}

@Composable
fun LoadingImage() {
    Image(
        painter = painterResource("loadingIcon.gif"),
        contentDescription = "Placeholder User image",
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(percent = 10))
            .border(2.dp, MaterialTheme.colors.primary),
    )
}



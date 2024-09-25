import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poc.currencyexchange.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {

    val exchangeRateData by viewModel.exchangeRateData.collectAsState()
    val currencyList by viewModel.currencyList.collectAsState()
    var selectedCurrency by remember { mutableStateOf("USD") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isDoneButtonEnabled by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var recallMethod by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(exchangeRateData) {
        exchangeRateData?.let {
            isLoading = it.loading ?: false
            isDoneButtonEnabled = !isLoading
            it.e?.let { exception ->
                errorMessage = exception.message ?: "An unknown error occurred"
                showErrorDialog = true
                recallMethod = "rate"
            }
        }
    }

    LaunchedEffect(currencyList) {
        currencyList?.let {
            isLoading = it.loading ?: false
            isDoneButtonEnabled = !isLoading
            it.e?.let { exception ->
                errorMessage = exception.message ?: "An unknown error occurred"
                showErrorDialog = true
                recallMethod = "currencies"
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.Gray))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Currency Converter") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { expanded = true },
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = selectedCurrency,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currencyList?.data?.currencies?.forEach { (currencyCode, currencyName) ->
                            DropdownMenuItem(
                                text = { Text(text = "$currencyCode - $currencyName") },
                                onClick = {
                                    selectedCurrency = currencyCode
                                    expanded = false
                                    val defaultAmount = 1.0
                                    viewModel.calculateExchangeRate(defaultAmount, selectedCurrency)
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Enter Amount") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }



            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (amountDouble != null) {
                        viewModel.calculateExchangeRate(amountDouble, selectedCurrency)
                    }
                },
                enabled = isDoneButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 1.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Currency") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (!isLoading) {
                exchangeRateData?.data?.quotes?.let { quotes ->
                    val filteredQuotes = quotes.filter { (currency, _) ->
                        currency.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredQuotes.entries.toList()) { (currency, rate) ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Gray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = currency,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = String.format("%.5f", rate),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
    if (showErrorDialog) {
        ErrorDialog(
            errorMessage = errorMessage,
            onConfirm = {
                showErrorDialog = false
                if (recallMethod == "rate") viewModel.getExchangeRate() else viewModel.getCurrencyList()
            },
            onDismiss = { showErrorDialog = false }
        )
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    disabledBackgroundColor: Color = Color.Gray,
    disabledContentColor: Color = Color.DarkGray,
    enabledBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    enabledContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(8.dp),
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .background(
                if (enabled) enabledBackgroundColor else disabledBackgroundColor,
                shape = shape
            )
            .then(if (!enabled) Modifier.alpha(0.3f) else Modifier),
        shape = shape,
        enabled = enabled,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 35.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                "Convert",
                color = if (enabled) enabledContentColor else disabledContentColor
            )
        }

    }
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Warning!") },
        text = { Text(text = errorMessage) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("RETRY")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}

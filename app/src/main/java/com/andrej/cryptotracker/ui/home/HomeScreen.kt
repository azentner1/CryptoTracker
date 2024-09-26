package com.andrej.cryptotracker.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrej.cryptotracker.R
import com.andrej.cryptotracker.data.model.CryptoTicker
import com.andrej.cryptotracker.domain.intent.HomeIntent
import com.andrej.cryptotracker.domain.model.HomeState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SearchBar(
            query = query,
            onQueryChanged = { newQuery ->
                query = newQuery
                viewModel.sendIntent(HomeIntent.SearchTicker(query))
            },
            onClearQuery = {
                query = ""
                viewModel.sendIntent(HomeIntent.SearchTicker(query))
            }
        )

        when (state) {
            is HomeState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }

            is HomeState.Success -> {
                CryptoList(
                    tickers = (state as HomeState.Success).tickers,
                    onItemClick = { }
                )
            }

            is HomeState.NetworkError -> {
                CryptoListNetworkError(
                    cachedTickers = (state as HomeState.NetworkError).cachedTickers,
                    message = stringResource( id = (state as HomeState.NetworkError).messageResId)
                )
            }

            is HomeState.Error -> {
                HomeScreenGeneralError(
                    message = stringResource( id = (state as HomeState.Error).messageResId),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun CryptoList(
    tickers: List<CryptoTicker>,
    onItemClick: (CryptoTicker) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(tickers.size) { index ->
            CryptoItem(ticker = tickers[index], onItemClick = onItemClick)
        }
    }
}


@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(stringResource(R.string.search_for_tickers)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_text))
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onQueryChanged(query) }
        )
    )
}

@Composable
fun CryptoItem(
    ticker: CryptoTicker,
    onItemClick: (CryptoTicker) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(ticker) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Align the entire row vertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ticker.formattedSymbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ticker.formattedPrice,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                val changeColor = if (ticker.dailyChange > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
                val changeIcon = if (ticker.dailyChange > 0) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }


                Text(
                    text = ticker.formattedDailyChange,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = changeColor
                )
                Icon(
                    imageVector = changeIcon,
                    contentDescription = null,
                    tint = changeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CryptoListNetworkError(
    cachedTickers: List<CryptoTicker>,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CryptoList(
            tickers = cachedTickers,
            onItemClick = { }
        )

        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.9f),
            shadowElevation = 6.dp,
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun HomeScreenGeneralError(
    modifier: Modifier = Modifier,
    message: String
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}


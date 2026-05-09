package com.pos.portablebilling.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.domain.model.TransactionItem
import com.pos.portablebilling.ui.theme.appThemes
import com.pos.portablebilling.ui.theme.cardAccents
import com.pos.portablebilling.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BillingViewModel,
    onNavigateToSettings: () -> Unit
) {
    val catalogItems by viewModel.catalogItems.collectAsState()
    val cartItems    by viewModel.cartItems.collectAsState()
    val cartTotal    by viewModel.cartTotal.collectAsState()
    val themeIndex   by viewModel.themeIndex.collectAsState()
    val langCode     by viewModel.langCode.collectAsState()
    val theme = appThemes[themeIndex]

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, catalogItems) {
        if (searchQuery.isBlank()) catalogItems
        else catalogItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var quickAddPrice by remember { mutableStateOf("") }
    var quickAddUnit by remember { mutableStateOf("") }

    val headerBrush = Brush.linearGradient(colors = listOf(theme.start, theme.end))

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(headerBrush)) {
                TopAppBar(
                    title = {
                        Text(viewModel.getString("app_name"), fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 20.sp)
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            if (cartTotal > 0) {
                FloatingActionButton(
                    onClick = { viewModel.printAndSaveTransaction() },
                    containerColor = Color(0xFF00C853),
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp).height(60.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, viewModel.getString("print_bill"))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${viewModel.getString("print_bill")} • ₹$cartTotal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F3F5))
                .padding(paddingValues)
        ) {
            // ── Cart ──
            if (cartItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        viewModel.getString("current_bill"),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text(viewModel.getString("clear_all"), color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(cartItems, key = { it.productId }) { item ->
                            CartItemRow(
                                item = item,
                                viewModel = viewModel,
                                accentColor = cardAccents[catalogItems.indexOfFirst { it.id == item.productId }.coerceAtLeast(0) % cardAccents.size],
                                onIncrease = {
                                    val product = catalogItems.find { it.id == item.productId }
                                    if (product != null) viewModel.addToCart(product, 1.0)
                                },
                                onDecrease = { viewModel.removeProductFromCart(item.productId) },
                                onSetQuantity = { qty -> viewModel.setProductQuantity(item.productId, qty) }
                            )
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(viewModel.getString("tap_to_start"), color = Color(0xFF9CA3AF), fontSize = 15.sp)
                }
            }

            // ── Products Panel ──
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.weight(if (cartItems.isEmpty()) 2f else 1.5f).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (searchQuery.isBlank()) "PRODUCTS (${catalogItems.size})"
                            else "RESULTS (${filteredItems.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6B7280),
                            letterSpacing = 1.sp
                        )
                    }

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(viewModel.getString("search_placeholder"), color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = theme.start) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.start,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )

                    if (filteredItems.isEmpty() && searchQuery.isNotBlank()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                viewModel.getString("product_not_found"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(searchQuery, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = theme.start)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = quickAddPrice,
                                            onValueChange = { quickAddPrice = it },
                                            label = { Text(viewModel.getString("price")) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = quickAddUnit,
                                            onValueChange = { quickAddUnit = it },
                                            placeholder = { Text("Unit") },
                                            modifier = Modifier.weight(0.8f),
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                            singleLine = true
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            val price = quickAddPrice.toDoubleOrNull() ?: 0.0
                                            viewModel.addNewProduct(searchQuery, quickAddUnit, price)
                                            quickAddPrice = ""
                                            // searchQuery is kept so the new product shows up instantly
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                                    ) {
                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(viewModel.getString("create_product"), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (filteredItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(viewModel.getString("no_products"), color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredItems, key = { it.id }) { product ->
                                val accent = cardAccents[filteredItems.indexOf(product) % cardAccents.size]
                                ProductCard(product, accent, viewModel) {
                                    viewModel.addToCart(product, 1.0)
                                    searchQuery = ""
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Soft Product Card ──
@Composable
fun ProductCard(product: ProductItem, accent: Color, viewModel: BillingViewModel, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(100), label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale)
            .shadow(if (isPressed) 1.dp else 4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(5.dp)
                .background(accent)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                product.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color(0xFF111827),
                maxLines = 2,
                lineHeight = 18.sp
            )
            Column {
                Text(
                    "₹${product.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = accent
                )
                Text(
                    "${viewModel.getString("per")} ${product.unit}",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Cart Item Row ──
@Composable
fun CartItemRow(
    item: TransactionItem, 
    accentColor: Color, 
    viewModel: BillingViewModel,
    onIncrease: () -> Unit, 
    onDecrease: () -> Unit,
    onSetQuantity: (Double) -> Unit
) {
    val qtyDisplay = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left small accent circle
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentColor))
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1F2937))
                    Text("₹${item.pricePerUnit} / ${viewModel.getString("unit")}", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                }

                // Qty controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(50))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Clear, null, modifier = Modifier.size(16.dp), tint = Color(0xFF4B5563))
                    }
                    Text(
                        qtyDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color(0xFF4B5563))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "₹${item.totalPrice}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
            }

            // Quick Fraction Chips (25%, 50%, 75%, 100%)
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.25, 0.5, 0.75, 1.0).forEach { fraction ->
                    val isSelected = item.quantity == fraction
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .clickable { onSetQuantity(fraction) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, if (isSelected) accentColor else Color(0xFFE5E7EB))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                when(fraction) {
                                    0.25 -> "25%"
                                    0.5 -> "50%"
                                    0.75 -> "75%"
                                    else -> "100%"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }
        }
    }
}

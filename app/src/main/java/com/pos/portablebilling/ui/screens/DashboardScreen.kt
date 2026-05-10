package com.pos.portablebilling.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DismissDirection
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
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val printerConnected by viewModel.printerConnected.collectAsState()
    val themeIndex by viewModel.themeIndex.collectAsState()
    val langCode by viewModel.langCode.collectAsState()
    val theme = appThemes[themeIndex]

    var productToQty by remember { mutableStateOf<ProductItem?>(null) }
    var selectedAccent by remember { mutableStateOf(theme.start) }
    var qtyInput by remember { mutableStateOf("1") }
    var isEditingCart by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, catalogItems) {
        if (searchQuery.isBlank()) catalogItems
        else catalogItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var quickAddPrice by remember { mutableStateOf("") }
    var quickAddUnit by remember { mutableStateOf("") }

    val headerBrush = Brush.linearGradient(colors = listOf(theme.start, theme.end))

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTransactionForDetail by remember { mutableStateOf<com.pos.portablebilling.domain.model.Transaction?>(null) }

    var showBillPreview by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemePickerDialog by remember { mutableStateOf(false) }

    val bizName by viewModel.businessName.collectAsState()
    val bizPhone by viewModel.businessPhone.collectAsState()
    val bizAddr by viewModel.businessAddress.collectAsState()

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(headerBrush)) {
                TopAppBar(
                    title = {
                        Text(
                            if (bizName.isBlank()) "Portable Billing" else bizName,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Profile") },
                                    onClick = { showMenu = false; showProfileDialog = true },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            null,
                                            tint = theme.start
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Language") },
                                    onClick = { showMenu = false; showLanguageDialog = true },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Language,
                                            null,
                                            tint = theme.start
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Theme Color") },
                                    onClick = { showMenu = false; showThemePickerDialog = true },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Favorite,
                                            null,
                                            tint = theme.start
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Inventory") },
                                    onClick = { showMenu = false; onNavigateToSettings() },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Inventory,
                                            null,
                                            tint = theme.start
                                        )
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            if (cartTotal > 0 && (searchQuery.isBlank() || filteredItems.isNotEmpty())) {
                Surface(
                    onClick = { showBillPreview = true },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .fillMaxWidth(0.92f)
                        .height(64.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)) // Darker, more professional greens
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Print, // Changed to Print icon
                                    null, 
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    viewModel.getString("print_bill", langCode),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    "₹${String.format("%.2f", cartTotal)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 0.dp,
                shadowElevation = 25.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                border = BorderStroke(1.dp, Color(0xFFF3F4F6))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { 
                            Icon(
                                Icons.Default.Receipt,
                                null,
                                modifier = Modifier.size(26.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                viewModel.getString("billing", langCode),
                                fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.SemiBold,
                                fontSize = 12.sp
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = theme.start,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = theme.start
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { 
                            Icon(
                                Icons.Default.History,
                                null,
                                modifier = Modifier.size(26.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                viewModel.getString("history", langCode),
                                fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.SemiBold,
                                fontSize = 12.sp
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = theme.start,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = theme.start
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF1F3F5))
                    ) {
                        // ── Cart ──
                        if (cartItems.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    viewModel.getString("current_bill", langCode),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF6B7280),
                                    letterSpacing = 1.sp
                                )
                                TextButton(onClick = { viewModel.clearCart() }) {
                                    Text(
                                        viewModel.getString("clear_all", langCode),
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                items(cartItems, key = { it.productId }) { item ->
                                    val dismissState = rememberDismissState(
                                        confirmValueChange = {
                                            if (it == DismissValue.DismissedToStart) {
                                                viewModel.setProductQuantity(item.productId, 0.0)
                                                true
                                            } else false
                                        }
                                    )

                                    SwipeToDismiss(
                                        state = dismissState,
                                        directions = setOf(DismissDirection.EndToStart),
                                        background = {
                                            val color by animateColorAsState(
                                                when (dismissState.targetValue) {
                                                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.8f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            Box(
                                                Modifier.fillMaxSize()
                                                    .background(color, RoundedCornerShape(16.dp))
                                                    .padding(horizontal = 20.dp),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                Icon(Icons.Default.Delete, null, tint = Color.White)
                                            }
                                        },
                                        dismissContent = {
                                            CartItemRow(
                                                item = item,
                                                viewModel = viewModel,
                                                langCode = langCode,
                                                accentColor = cardAccents[catalogItems.indexOfFirst { it.id == item.productId }
                                                    .coerceAtLeast(0) % cardAccents.size],
                                                onIncrease = {
                                                    val product = catalogItems.find { it.id == item.productId }
                                                    if (product != null) viewModel.addToCart(product, 1.0)
                                                },
                                                onDecrease = { viewModel.removeProductFromCart(item.productId) },
                                                onSetQuantity = { qty: Double ->
                                                    viewModel.setProductQuantity(
                                                        item.productId,
                                                        qty
                                                    )
                                                },
                                                onItemClick = {
                                                    val product = catalogItems.find { it.id == item.productId }
                                                    if (product != null) {
                                                        productToQty = product
                                                        selectedAccent =
                                                            cardAccents[catalogItems.indexOfFirst { it.id == item.productId }
                                                                .coerceAtLeast(0) % cardAccents.size]
                                                        val baseUnitValue = product.unit.toDoubleOrNull() ?: 1.0
                                                        qtyInput = (item.quantity * baseUnitValue).toString()
                                                        isEditingCart = true
                                                    }
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    viewModel.getString("tap_to_start", langCode),
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 15.sp
                                )
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
                                        if (searchQuery.isBlank()) "${
                                            viewModel.getString(
                                                "products",
                                                langCode
                                            )
                                        } (${catalogItems.size})"
                                        else "${
                                            viewModel.getString(
                                                "results",
                                                langCode
                                            )
                                        } (${filteredItems.size})",
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
                                    placeholder = {
                                        Text(
                                            viewModel.getString("search_placeholder", langCode),
                                            color = Color.Gray
                                        )
                                    },
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
                                            viewModel.getString("product_not_found", langCode),
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
                                                Text(
                                                    searchQuery,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 18.sp,
                                                    color = theme.start
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    OutlinedTextField(
                                                        value = quickAddPrice,
                                                        onValueChange = { quickAddPrice = it },
                                                        label = {
                                                            Text(
                                                                viewModel.getString(
                                                                    "price",
                                                                    langCode
                                                                )
                                                            )
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number,
                                                            imeAction = ImeAction.Next
                                                        ),
                                                        singleLine = true
                                                    )
                                                    var showQuickUnitDropdown by remember { mutableStateOf(false) }
                                                    Box(modifier = Modifier.weight(0.8f)) {
                                                        OutlinedTextField(
                                                            value = if (quickAddUnit == "1") "100%" else if (quickAddUnit.toDoubleOrNull() != null) "${(quickAddUnit.toDouble() * 100).toInt()}%" else quickAddUnit,
                                                            onValueChange = { },
                                                            label = {
                                                                Text(
                                                                    viewModel.getString(
                                                                        "unit",
                                                                        langCode
                                                                    )
                                                                )
                                                            },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(12.dp),
                                                            readOnly = true,
                                                            trailingIcon = {
                                                                Icon(Icons.Default.ArrowDropDown, null)
                                                            }
                                                        )
                                                        Box(
                                                            modifier = Modifier.matchParentSize()
                                                                .clickable { showQuickUnitDropdown = true })

                                                        DropdownMenu(
                                                            expanded = showQuickUnitDropdown,
                                                            onDismissRequest = {
                                                                showQuickUnitDropdown = false
                                                            },
                                                            modifier = Modifier.fillMaxWidth(0.3f)
                                                        ) {
                                                            listOf("100", "75", "50", "25").forEach { valPct ->
                                                                DropdownMenuItem(
                                                                    text = {
                                                                        Text(
                                                                            if (valPct == "100") "100% (Full)" else "$valPct%",
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                    },
                                                                    onClick = {
                                                                        quickAddUnit =
                                                                            if (valPct == "100") "1" else (valPct.toDouble() / 100.0).toString()
                                                                        showQuickUnitDropdown = false
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Button(
                                                    onClick = {
                                                        val price = quickAddPrice.toDoubleOrNull() ?: 0.0
                                                        viewModel.addNewProduct(
                                                            searchQuery,
                                                            quickAddUnit,
                                                            price
                                                        )
                                                        quickAddPrice = ""
                                                        // searchQuery is kept so the new product shows up instantly
                                                        focusManager.clearFocus()
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                                    shape = RoundedCornerShape(50),
                                                    colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Add,
                                                        null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        viewModel.getString("create_product", langCode),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else if (filteredItems.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(viewModel.getString("no_products", langCode), color = Color.Gray)
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        contentPadding = PaddingValues(bottom = 120.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(filteredItems, key = { it.id }) { product ->
                                            val accent =
                                                cardAccents[filteredItems.indexOf(product) % cardAccents.size]
                                            ProductCard(product, accent, viewModel, langCode) {
                                                productToQty = product
                                                selectedAccent = accent
                                                qtyInput = product.unit
                                                isEditingCart = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    HistoryTab(viewModel, theme, langCode) {
                        selectedTransactionForDetail = it
                    }
                }
            }

            // ── History Detail Dialog ──
            selectedTransactionForDetail?.let { transaction ->
                Dialog(
                    onDismissRequest = { selectedTransactionForDetail = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.85f)
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                bizName.ifBlank { viewModel.getString("receipt", langCode) },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111827)
                            )
                            if (bizAddr.isNotBlank()) {
                                Text(
                                    bizAddr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            if (bizPhone.isNotBlank()) {
                                Text(
                                    "Ph: $bizPhone",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                val date = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(transaction.timestamp))
                                Text(viewModel.getString("bill_label", langCode) + " # ${transaction.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(date, fontSize = 12.sp, color = Color.Gray)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Scrollable items for full bill view
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                transaction.items.forEach { item ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${item.productName} x ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                                        Text("₹${String.format("%.2f", item.totalPrice)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(viewModel.getString("grand_total", langCode), fontWeight = FontWeight.Black, fontSize = 16.sp, color = theme.start)
                                Text("₹${String.format("%.2f", transaction.totalAmount)}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = theme.start)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { 
                                        val shareText = "Bill from $bizName\n" +
                                                transaction.items.joinToString("\n") { "${it.productName}: ₹${it.totalPrice}" } +
                                                "\nTotal: ₹${transaction.totalAmount}"
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                        }
                                        viewModel.getApplication<android.app.Application>().startActivity(
                                            android.content.Intent.createChooser(intent, "Share Bill")
                                                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, theme.start)
                                ) {
                                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp), tint = theme.start)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(viewModel.getString("share", langCode), color = theme.start)
                                }
                                Button(
                                    onClick = { viewModel.printReceipt(transaction) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                                ) {
                                    Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(viewModel.getString("print", langCode))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = { selectedTransactionForDetail = null }) {
                                Text(viewModel.getString("close", langCode), color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // ── Language Picker Dialog ──
        if (showLanguageDialog) {
            Dialog(onDismissRequest = { showLanguageDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Select Language",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        com.pos.portablebilling.util.availableLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        lang.name,
                                        fontWeight = if (lang.code == langCode) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    if (lang.code == langCode) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = theme.start
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.setLanguage(lang.code)
                                    showLanguageDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── Theme Picker Dialog ──
        if (showThemePickerDialog) {
            Dialog(onDismissRequest = { showThemePickerDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Change Theme",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        appThemes.forEachIndexed { idx, t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = {
                                    viewModel.setThemeIndex(idx)
                                    showThemePickerDialog = false
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier.size(20.dp).clip(CircleShape)
                                            .background(t.start)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── Bill Preview Dialog ──
        if (showBillPreview) {
            Dialog(
                onDismissRequest = { showBillPreview = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "BILL PREVIEW",
                            fontWeight = FontWeight.Black,
                            color = Color.Gray,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Shop Info
                        Text(
                            bizName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        if (bizPhone.isNotBlank()) Text(
                            bizPhone,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (bizAddr.isNotBlank()) Text(
                            bizAddr,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFF3F4F6)
                        )

                        // Items list
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(cartItems) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${item.productName} x ${item.quantity}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "₹${String.format("%.2f", item.totalPrice)}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFE5E7EB),
                            thickness = 1.dp
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF9FAFB),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFF3F4F6))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    viewModel.getString("grand_total", langCode),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "₹${String.format("%.2f", cartTotal)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 26.sp,
                                    color = theme.start
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Share Button
                            OutlinedButton(
                                onClick = {
                                    // Share logic
                                    val shareText = "Bill from $bizName\n" +
                                            cartItems.joinToString("\n") { "${it.productName}: ₹${it.totalPrice}" } +
                                            "\nTotal: ₹$cartTotal"
                                    val intent =
                                        android.content.Intent(android.content.Intent.ACTION_SEND)
                                            .apply {
                                                type = "text/plain"
                                                putExtra(
                                                    android.content.Intent.EXTRA_TEXT,
                                                    shareText
                                                )
                                            }
                                    viewModel.getApplication<android.app.Application>()
                                        .startActivity(
                                            android.content.Intent.createChooser(
                                                intent,
                                                "Share Bill"
                                            )
                                                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, theme.start)
                            ) {
                                Icon(Icons.Default.Share, null, tint = theme.start)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share", color = theme.start)
                            }

                            // Print Button
                            Button(
                                onClick = {
                                    viewModel.printAndSaveTransaction()
                                    showBillPreview = false
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                            ) {
                                Icon(Icons.Default.Print, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Print")
                            }
                        }

                        TextButton(
                            onClick = { showBillPreview = false },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                }
            }
        }

        // ── Profile Dialog ──
        if (showProfileDialog) {
            var tempName by remember { mutableStateOf(bizName) }
            var tempPhone by remember { mutableStateOf(bizPhone) }
            var tempAddr by remember { mutableStateOf(bizAddr) }

            Dialog(onDismissRequest = { showProfileDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Business Profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Business Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                if (tempName.isNotEmpty()) {
                                    IconButton(onClick = { tempName = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tempPhone,
                            onValueChange = { tempPhone = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tempAddr,
                            onValueChange = { tempAddr = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.updateBusinessProfile(tempName, tempPhone, tempAddr)
                                showProfileDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                        ) {
                            Text("Save Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── Premium Quantity Dialog ──
        productToQty?.let { product ->
            val baseUnit = product.unit.toDoubleOrNull() ?: 1.0
            val unitPrice = product.price

            // Real-time total calculation based on portions
            val currentPortionInput = qtyInput.toDoubleOrNull() ?: 0.0
            val totalForDialog = (currentPortionInput / baseUnit) * unitPrice

            Dialog(onDismissRequest = { productToQty = null }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Box(
                            modifier = Modifier.size(60.dp)
                                .background(selectedAccent.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = selectedAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            product.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "₹$unitPrice / ${if (baseUnit == 1.0) "Unit" else baseUnit.toString()}",
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Quantity Input
                        OutlinedTextField(
                            value = qtyInput,
                            onValueChange = {
                                if (it.length <= 6 && (it.isEmpty() || it.toDoubleOrNull() != null)) {
                                    qtyInput = it
                                }
                            },
                            label = { Text("Quantity / Portion") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Black
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedAccent,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedLabelColor = selectedAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Percentage Chips in Dialog
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.25, 0.5, 0.75, 1.0).forEach { fraction ->
                                val label = when (fraction) {
                                    0.25 -> "25%"
                                    0.5 -> "50%"
                                    0.75 -> "75%"
                                    else -> "100%"
                                }
                                val isSelectedChip =
                                    if (qtyInput.toDoubleOrNull() != null) qtyInput.toDouble() == fraction else false
                                Surface(
                                    modifier = Modifier.weight(1f).height(40.dp).clickable {
                                        qtyInput =
                                            fraction.toString() // Set the portion amount directly (0.25, 0.5, etc.)
                                        if (qtyInput.length > 6) qtyInput = qtyInput.take(6)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelectedChip) selectedAccent.copy(alpha = 0.1f) else Color(
                                        0xFFF3F4F6
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelectedChip) selectedAccent else Color(0xFFE5E7EB)
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelectedChip) selectedAccent else Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Final Price & Add Button
                        Button(
                            onClick = {
                                val currentPortion = qtyInput.toDoubleOrNull() ?: baseUnit
                                val itemsToAddOrSet = currentPortion / baseUnit
                                if (isEditingCart) {
                                    viewModel.setProductQuantity(product.id, itemsToAddOrSet)
                                } else {
                                    viewModel.addToCart(product, itemsToAddOrSet)
                                }
                                productToQty = null
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = selectedAccent)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (isEditingCart) "UPDATE BILL" else "ADD TO BILL",
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "₹${String.format("%.2f", totalForDialog)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = { productToQty = null },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductItem,
    accent: Color,
    viewModel: BillingViewModel,
    langCode: String,
    onClick: () -> Unit
) {
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
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
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
                    "${viewModel.getString("per", langCode)} ${product.unit}",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: TransactionItem,
    accentColor: Color,
    viewModel: BillingViewModel,
    langCode: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onSetQuantity: (Double) -> Unit,
    onItemClick: () -> Unit
) {
    val qtyDisplay = if (item.quantity % 1.0 == 0.0) item.quantity.toInt()
        .toString() else item.quantity.toString()

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(8.dp).clip(CircleShape).background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "₹${item.pricePerUnit} / ${viewModel.getString("unit", langCode)}",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(50))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4B5563)
                        )
                    }
                    val baseUnitValue = item.unit.toDoubleOrNull()
                    val displayQty = if (baseUnitValue != null) {
                        val total = item.quantity * baseUnitValue
                        if (total % 1.0 == 0.0) total.toInt()
                            .toString() else total.toString()
                    } else {
                        qtyDisplay
                    }
                    val displayUnit = if (baseUnitValue != null) "" else item.unit

                    Text(
                        if (displayUnit.isBlank()) displayQty else "$displayQty $displayUnit",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp)) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4B5563)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "₹${String.format("%.2f", item.totalPrice)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.25, 0.5, 0.75, 1.0).forEach { fraction ->
                    val baseUnitValue = item.unit.toDoubleOrNull() ?: 1.0
                    val currentPortion = item.quantity * baseUnitValue
                    val isSelected = currentPortion == fraction
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .clickable {
                                val newQuantity = fraction / baseUnitValue
                                onSetQuantity(newQuantity)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color(
                            0xFFF9FAFB
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else Color(0xFFE5E7EB)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                when (fraction) {
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

@Composable
private fun HistoryTab(
    viewModel: BillingViewModel,
    theme: com.pos.portablebilling.ui.theme.AppTheme,
    langCode: String,
    onTransactionClick: (com.pos.portablebilling.domain.model.Transaction) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F5))
            .padding(16.dp)
    ) {
        Text(
            viewModel.getString("transaction_history", langCode),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color(0xFF111827),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(viewModel.getString("no_transactions", langCode), color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItemCard(transaction, theme, viewModel, langCode, onTransactionClick)
                }
            }
        }
    }
}

@Composable
private fun TransactionItemCard(
    transaction: com.pos.portablebilling.domain.model.Transaction,
    theme: com.pos.portablebilling.ui.theme.AppTheme,
    viewModel: BillingViewModel,
    langCode: String,
    onClick: (com.pos.portablebilling.domain.model.Transaction) -> Unit
) {
    val date = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault())
        .format(java.util.Date(transaction.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(transaction) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(theme.start.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Receipt, null, tint = theme.start, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        viewModel.getString("bill_label", langCode) + " #${transaction.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF111827)
                    )
                    Text(date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                "₹${String.format("%.2f", transaction.totalAmount)}",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = theme.start
            )
        }
    }
}

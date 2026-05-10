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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AllInclusive
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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    var quickAddUnit by remember { mutableStateOf("1") }

    val forestGreen = Color(0xFF006337)
    val headerBrush = Brush.linearGradient(colors = listOf(forestGreen, forestGreen))

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTransactionForDetail by remember {
        mutableStateOf<com.pos.portablebilling.domain.model.Transaction?>(
            null
        )
    }

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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 6.dp
            ) {
                Box(modifier = Modifier.background(headerBrush)) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Store,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    if (bizName.isBlank()) "Portable Billing" else bizName,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        actions = {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, null, tint = Color.White)
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
                                        Icon(Icons.Default.Person, null, tint = theme.start)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Language") },
                                    onClick = { showMenu = false; showLanguageDialog = true },
                                    leadingIcon = {
                                        Icon(Icons.Default.Language, null, tint = theme.start)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Theme Color") },
                                    onClick = { showMenu = false; showThemePickerDialog = true },
                                    leadingIcon = {
                                        Icon(Icons.Default.Favorite, null, tint = theme.start)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Inventory") },
                                    onClick = { showMenu = false; onNavigateToSettings() },
                                    leadingIcon = {
                                        Icon(Icons.Default.Inventory, null, tint = theme.start)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {},
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {}
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F3F5))) {
            // ── Main Content Scrollable Area ──
            Box(
                modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())
            ) {
                when (selectedTab) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF1F3F5))
                        ) {
                            // ── Cart ──
                            if (cartItems.isNotEmpty()) {
                                // Summary widget removed for space optimization - consolidated into bottom bar
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth().weight(1f)
                                        .padding(horizontal = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) {
                                    items(cartItems, key = { it.productId }) { item ->
                                        val dismissState = rememberDismissState(
                                            confirmValueChange = {
                                                if (it == DismissValue.DismissedToStart) {
                                                    viewModel.setProductQuantity(
                                                        item.productId,
                                                        0.0
                                                    )
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
                                                        DismissValue.DismissedToStart -> Color.Red.copy(
                                                            alpha = 0.8f
                                                        )

                                                        else -> Color.Transparent
                                                    }
                                                )
                                                Box(
                                                    Modifier.fillMaxSize()
                                                        .background(
                                                            color,
                                                            RoundedCornerShape(16.dp)
                                                        )
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        null,
                                                        tint = Color.White
                                                    )
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
                                                        val product =
                                                            catalogItems.find { it.id == item.productId }
                                                        if (product != null) viewModel.addToCart(
                                                            product,
                                                            1.0
                                                        )
                                                    },
                                                    onDecrease = {
                                                        viewModel.removeProductFromCart(
                                                            item.productId
                                                        )
                                                    },
                                                    onSetQuantity = { qty: Double ->
                                                        viewModel.setProductQuantity(
                                                            item.productId,
                                                            qty
                                                        )
                                                    },
                                                    onItemClick = {
                                                        val product =
                                                            catalogItems.find { it.id == item.productId }
                                                        if (product != null) {
                                                            productToQty = product
                                                            selectedAccent =
                                                                cardAccents[catalogItems.indexOfFirst { it.id == item.productId }
                                                                    .coerceAtLeast(0) % cardAccents.size]
                                                            val baseUnitValue =
                                                                product.unit.toDoubleOrNull() ?: 1.0
                                                            qtyInput =
                                                                (item.quantity * baseUnitValue).toString()
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
                                color = Color(0xFFF9FAFB), // Softer background to distinguish from white cards
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                                shadowElevation = 12.dp,
                                modifier = Modifier.weight(if (cartItems.isEmpty()) 2f else 1.5f)
                                    .fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(top = 14.dp, bottom = 6.dp),
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
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Search,
                                                null,
                                                tint = theme.start
                                            )
                                        },
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
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(
                                                        0xFFF9FAFB
                                                    )
                                                ),
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
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            8.dp
                                                        )
                                                    ) {
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
                                                        var showQuickUnitDropdown by remember {
                                                            mutableStateOf(
                                                                false
                                                            )
                                                        }
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
                                                                    Icon(
                                                                        Icons.Default.ArrowDropDown,
                                                                        null
                                                                    )
                                                                }
                                                            )
                                                            Box(
                                                                modifier = Modifier.matchParentSize()
                                                                    .clickable {
                                                                        showQuickUnitDropdown = true
                                                                    })

                                                            DropdownMenu(
                                                                expanded = showQuickUnitDropdown,
                                                                onDismissRequest = {
                                                                    showQuickUnitDropdown = false
                                                                },
                                                                modifier = Modifier.fillMaxWidth(
                                                                    0.3f
                                                                )
                                                            ) {
                                                                listOf(
                                                                    "100",
                                                                    "75",
                                                                    "50",
                                                                    "25"
                                                                ).forEach { valPct ->
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
                                                                            showQuickUnitDropdown =
                                                                                false
                                                                        }
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Button(
                                                        onClick = {
                                                            val price =
                                                                quickAddPrice.toDoubleOrNull()
                                                                    ?: 0.0
                                                            viewModel.addNewProduct(
                                                                searchQuery,
                                                                quickAddUnit,
                                                                price
                                                            )
                                                            quickAddPrice = ""
                                                            quickAddUnit = "1"
                                                            // searchQuery is kept so the new product shows up instantly
                                                            focusManager.clearFocus()
                                                        },
                                                        modifier = Modifier.fillMaxWidth()
                                                            .height(48.dp),
                                                        shape = RoundedCornerShape(50),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = theme.start
                                                        )
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Add,
                                                            null,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            viewModel.getString(
                                                                "create_product",
                                                                langCode
                                                            ),
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
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    Icons.Default.Inventory,
                                                    null,
                                                    modifier = Modifier.size(48.dp),
                                                    tint = Color.LightGray
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    viewModel.getString("no_products", langCode),
                                                    color = Color.Gray
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Button(
                                                    onClick = { onNavigateToSettings() },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = theme.start
                                                    )
                                                ) {
                                                    Icon(
                                                        Icons.Default.Add,
                                                        null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        viewModel.getString(
                                                            "manage_inventory",
                                                            langCode
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(3),
                                            contentPadding = PaddingValues(bottom = 200.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(filteredItems, key = { it.id }) { product ->
                                                val accent =
                                                    cardAccents[filteredItems.indexOf(product) % cardAccents.size]
                                                val haptic =
                                                    androidx.compose.ui.platform.LocalHapticFeedback.current
                                                ProductCard(
                                                    product = product,
                                                    accent = accent,
                                                    viewModel = viewModel,
                                                    langCode = langCode,
                                                    onLongClick = {
                                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                        viewModel.setItemToEdit(product)
                                                        onNavigateToSettings()
                                                    },
                                                    onClick = {
                                                        productToQty = product
                                                        selectedAccent = accent
                                                        qtyInput =
                                                            if (product.unit.isBlank()) "1" else product.unit
                                                        isEditingCart = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        HistoryTab(viewModel, theme, langCode, onTabChange = { selectedTab = it }) {
                            selectedTransactionForDetail = it
                        }
                    }
                }
            }

                // ── Floating Action Bars Overlay ──
                // ── Sticky Summary Bar ──
                if (selectedTab == 0 && cartItems.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 16.dp,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable { viewModel.clearCart() },
                                    shape = CircleShape,
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFFECACA))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "${cartItems.size} Items",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6B7280)
                                    )
                                    Text(
                                        "Total: ₹${String.format("%.2f", cartTotal)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = forestGreen
                                    )
                                }
                            }
                            Button(
                                onClick = { showBillPreview = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = forestGreen),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Icon(Icons.Default.Print, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Print Receipt", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Premium Bottom Navigation Pill (Only if no bill or not on billing tab)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            shape = RoundedCornerShape(36.dp),
                            color = forestGreen,
                            shadowElevation = 15.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavPillItem(
                                    icon = Icons.Default.Receipt,
                                    label = viewModel.getString("billing", langCode),
                                    isSelected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )
                                Spacer(modifier = Modifier.width(60.dp))
                                NavPillItem(
                                    icon = Icons.Default.History,
                                    label = viewModel.getString("history", langCode),
                                    isSelected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )
                            }
                        }

                        // Central Action Button (Add)
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    selectedTab = 0
                                    searchQuery = ""
                                },
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 12.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Add,
                                    null,
                                    tint = forestGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
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

                                Divider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFF3F4F6)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val date = java.text.SimpleDateFormat(
                                        "dd MMM yyyy, HH:mm",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date(transaction.timestamp))
                                    Text(
                                        viewModel.getString(
                                            "bill_label",
                                            langCode
                                        ) + " # ${transaction.id}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
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
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "${item.productName} x ${item.quantity}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                "₹${String.format("%.2f", item.totalPrice)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Divider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFF3F4F6)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        viewModel.getString("grand_total", langCode),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = theme.start
                                    )
                                    Text(
                                        "₹${String.format("%.2f", transaction.totalAmount)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        color = theme.start
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.loadTransactionToCart(transaction)
                                            selectedTab = 0
                                            selectedTransactionForDetail = null
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, Color(0xFF6B7280))
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF6B7280)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            viewModel.getString("modify_bill", langCode),
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.deleteTransaction(transaction.id)
                                            selectedTransactionForDetail = null
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, Color.Red)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color.Red
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            viewModel.getString("delete_bill", langCode),
                                            color = Color.Red
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val shareText = "Bill from $bizName\n" +
                                                    transaction.items.joinToString("\n") { "${it.productName}: ₹${it.totalPrice}" } +
                                                    "\nTotal: ₹${transaction.totalAmount}"
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
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, theme.start)
                                    ) {
                                        Icon(
                                            Icons.Default.Share,
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = theme.start
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            viewModel.getString("share", langCode),
                                            color = theme.start
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.printReceipt(transaction) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                                    ) {
                                        Icon(
                                            Icons.Default.Print,
                                            null,
                                            modifier = Modifier.size(18.dp)
                                        )
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
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = { productToQty = null },
                                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                            ) {
                                Icon(
                                    androidx.compose.material.icons.Icons.Default.Close,
                                    null,
                                    tint = Color.Gray
                                )
                            }

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

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        onClick = {
                                            val current = qtyInput.toDoubleOrNull() ?: 0.0
                                            if (current >= 1.0) {
                                                qtyInput = (current - 1.0).let {
                                                    if (it % 1.0 == 0.0) it.toInt()
                                                        .toString() else String.format("%.2f", it)
                                                }
                                            } else if (current > 0.0) {
                                                qtyInput = (current - 0.25).let {
                                                    if (it < 0) "0" else if (it % 1.0 == 0.0) it.toInt()
                                                        .toString() else String.format("%.2f", it)
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3F4F6),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                androidx.compose.material.icons.Icons.Default.Remove,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = qtyInput,
                                        onValueChange = {
                                            if (it.length <= 6 && (it.isEmpty() || it.toDoubleOrNull() != null)) {
                                                qtyInput = it
                                            }
                                        },
                                        label = { Text("Quantity / Portion") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        textStyle = MaterialTheme.typography.headlineSmall.copy(
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

                                    Surface(
                                        onClick = {
                                            val current = qtyInput.toDoubleOrNull() ?: 0.0
                                            qtyInput = (current + 1.0).let {
                                                if (it % 1.0 == 0.0) it.toInt()
                                                    .toString() else String.format("%.2f", it)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3F4F6),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Add,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

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
                                                if (isSelectedChip) selectedAccent else Color(
                                                    0xFFE5E7EB
                                                )
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
                                            viewModel.setProductQuantity(
                                                product.id,
                                                itemsToAddOrSet
                                            )
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
    }

    @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
    @Composable
    private fun ProductCard(
        product: ProductItem,
        accent: Color,
        viewModel: BillingViewModel,
        langCode: String,
        onLongClick: () -> Unit,
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
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onLongClick = onLongClick,
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

        // Simple helper to Title Case names
        val titleCasedName = item.productName.split(" ").joinToString(" ") { 
            it.lowercase().replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } 
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = accentColor.copy(alpha = 0.08f)
            ),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.15f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Product name and small grey unit price
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        titleCasedName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "₹${item.pricePerUnit} / ${viewModel.getString("unit", langCode)}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Center: Compact, pill-shaped quantity selector [- 1 +]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(50))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4B5563)
                        )
                    }
                    
                    val baseUnitValue = item.unit.toDoubleOrNull()
                    val displayQty = if (baseUnitValue != null) {
                        val total = item.quantity * baseUnitValue
                        if (total % 1.0 == 0.0) total.toInt().toString() else total.toString()
                    } else {
                        qtyDisplay
                    }

                    Text(
                        displayQty,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color(0xFF1F2937)
                    )
                    
                    IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4B5563)
                        )
                    }
                }

                // Right: Bold black price
                Text(
                    "₹${String.format("%.2f", item.totalPrice)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End
                )
            }
        }
    }

    @OptIn(
        ExperimentalMaterial3Api::class,
        androidx.compose.foundation.layout.ExperimentalLayoutApi::class
    )
    @Composable
    private fun HistoryTab(
        viewModel: BillingViewModel,
        theme: com.pos.portablebilling.ui.theme.AppTheme,
        langCode: String,
        onTabChange: (Int) -> Unit,
        onTransactionClick: (com.pos.portablebilling.domain.model.Transaction) -> Unit
    ) {
        val transactions by viewModel.filteredTransactions.collectAsState()
        val activeFilter by viewModel.historyFilter.collectAsState()
        val customDate by viewModel.customDate.collectAsState()

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.setHistoryFilter(BillingViewModel.HistoryFilter.CUSTOM, it)
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                viewModel.getString("transaction_history", langCode),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filter Chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BillingViewModel.HistoryFilter.values().forEach { filter ->
                    val isSelected = activeFilter == filter
                    val (labelKey, icon) = when (filter) {
                        BillingViewModel.HistoryFilter.ALL -> "filter_all" to Icons.Default.AllInclusive
                        BillingViewModel.HistoryFilter.TODAY -> "filter_today" to Icons.Default.Today
                        BillingViewModel.HistoryFilter.YESTERDAY -> "filter_yesterday" to Icons.Default.History
                        BillingViewModel.HistoryFilter.THIS_WEEK -> "filter_this_week" to Icons.Default.CalendarMonth
                        BillingViewModel.HistoryFilter.CUSTOM -> "filter_custom" to Icons.Default.CalendarToday
                    }

                    val label =
                        if (filter == BillingViewModel.HistoryFilter.CUSTOM && customDate != null) {
                            java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
                                .format(java.util.Date(customDate!!))
                        } else {
                            viewModel.getString(labelKey, langCode)
                        }

                    val bgColor by animateColorAsState(
                        if (isSelected) theme.start else Color(
                            0xFFF3F4F6
                        )
                    )
                    val contentColor by animateColorAsState(
                        if (isSelected) Color.White else Color(
                            0xFF6B7280
                        )
                    )

                    Surface(
                        onClick = {
                            if (filter == BillingViewModel.HistoryFilter.CUSTOM) {
                                showDatePicker = true
                            } else {
                                viewModel.setHistoryFilter(filter)
                            }
                        },
                        color = bgColor,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = if (isSelected) 6.dp else 0.dp,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = contentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                label,
                                color = contentColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
                    contentPadding = PaddingValues(bottom = 200.dp)
                ) {
                    items(transactions, key = { it.id }) { transaction ->
                        val dismissState = rememberDismissState(
                            confirmValueChange = {
                                when (it) {
                                    DismissValue.DismissedToEnd -> {
                                        viewModel.deleteTransaction(transaction.id)
                                        true
                                    }

                                    DismissValue.DismissedToStart -> {
                                        viewModel.loadTransactionToCart(transaction)
                                        onTabChange(0)
                                        true
                                    }

                                    else -> false
                                }
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            background = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.Transparent
                                        DismissValue.DismissedToEnd -> Color.Red.copy(alpha = 0.8f)
                                        DismissValue.DismissedToStart -> theme.start.copy(alpha = 0.8f)
                                    }
                                )
                                val alignment = when (dismissState.dismissDirection) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    DismissDirection.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                                val icon = when (dismissState.dismissDirection) {
                                    DismissDirection.StartToEnd -> Icons.Default.Delete
                                    DismissDirection.EndToStart -> Icons.Default.Edit
                                    else -> null
                                }
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.2f
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    if (icon != null) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            modifier = Modifier.scale(scale),
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            dismissContent = {
                                TransactionItemCard(
                                    transaction,
                                    theme,
                                    viewModel,
                                    langCode,
                                    onTransactionClick
                                )
                            }
                        )
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
                        Icon(
                            Icons.Default.Receipt,
                            null,
                            tint = theme.start,
                            modifier = Modifier.size(20.dp)
                        )
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

    @Composable
    private fun NavPillItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                label,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
            )
        }
    }


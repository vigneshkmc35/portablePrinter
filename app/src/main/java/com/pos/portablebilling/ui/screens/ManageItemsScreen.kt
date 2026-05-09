package com.pos.portablebilling.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.ui.theme.appThemes
import com.pos.portablebilling.ui.viewmodel.BillingViewModel
import com.pos.portablebilling.util.availableLanguages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageItemsScreen(
    viewModel: BillingViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Edit state
    var editingItem by remember { mutableStateOf<ProductItem?>(null) }
    var editName by remember { mutableStateOf("") }
    var editUnit by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }

    val catalogItems by viewModel.catalogItems.collectAsState()
    val themeIndex by viewModel.themeIndex.collectAsState()
    val langCode by viewModel.langCode.collectAsState()
    val theme = appThemes[themeIndex]

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, catalogItems) {
        if (searchQuery.isBlank()) catalogItems
        else catalogItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var showThemePicker by remember { mutableStateOf(false) }
    var showLangPicker by remember { mutableStateOf(false) }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(theme.start, theme.end)
    )

    // Edit Dialog
    editingItem?.let { item ->
        Dialog(onDismissRequest = { editingItem = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(viewModel.getString("edit_product", langCode), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(viewModel.getString("product_name", langCode)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editPrice,
                            onValueChange = { editPrice = it },
                            label = { Text(viewModel.getString("price", langCode)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = editUnit,
                            onValueChange = { editUnit = it },
                            label = { Text(viewModel.getString("unit", langCode)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { editingItem = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(viewModel.getString("clear_all", langCode)) // Reusing clear_all or just "Cancel"
                        }
                        Button(
                            onClick = {
                                if (editName.isNotBlank() && editPrice.isNotBlank()) {
                                    viewModel.updateProduct(
                                        item,
                                        editName,
                                        editUnit,
                                        editPrice.toDoubleOrNull() ?: item.price
                                    )
                                    editingItem = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                        ) {
                            Text(viewModel.getString("save_product", langCode))
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(gradientBrush)) {
                TopAppBar(
                    title = { Text(viewModel.getString("manage_inventory", langCode), fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showLangPicker = true }) {
                                Icon(Icons.Default.Public, "Language", tint = Color.White)
                            }
                            DropdownMenu(expanded = showLangPicker, onDismissRequest = { showLangPicker = false }) {
                                availableLanguages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang.name, fontWeight = if (lang.code == langCode) FontWeight.Bold else FontWeight.Normal) },
                                        trailingIcon = {
                                            if (lang.code == langCode) {
                                                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = theme.start)
                                            }
                                        },
                                        onClick = {
                                            viewModel.setLanguage(lang.code)
                                            showLangPicker = false
                                        }
                                    )
                                }
                            }
                        }
                        Box {
                            IconButton(onClick = { showThemePicker = !showThemePicker }) {
                                Icon(Icons.Default.Favorite, "Theme", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = showThemePicker,
                                onDismissRequest = { showThemePicker = false }
                            ) {
                                Text(
                                    viewModel.getString("app_theme", langCode),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                                appThemes.forEachIndexed { idx, t ->
                                    DropdownMenuItem(
                                        text = { Text(t.name) },
                                        onClick = {
                                            viewModel.setThemeIndex(idx)
                                            showThemePicker = false
                                        },
                                        leadingIcon = {
                                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(t.start))
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
                .padding(paddingValues)
        ) {
            // Add new product form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(viewModel.getString("add_new_product", langCode), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(viewModel.getString("search_placeholder", langCode)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text(viewModel.getString("price", langCode)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text(viewModel.getString("unit", langCode)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && price.isNotBlank()) {
                                viewModel.addNewProduct(name, unit, price.toDoubleOrNull() ?: 0.0)
                                name = ""; unit = ""; price = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.start)
                    ) {
                        Text(viewModel.getString("save_product", langCode), fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(viewModel.getString("existing_inventory", langCode) + " (${filteredItems.size})", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))

            // ── Search bar ──
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(viewModel.getString("search_placeholder", langCode)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF6200EA))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.start,
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                )
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
                                Text("Rs.${item.price} ${viewModel.getString("per", langCode)} ${item.unit}", fontSize = 14.sp, color = Color.Gray)
                            }

                            // Edit button
                            IconButton(onClick = {
                                editingItem = item
                                editName = item.name
                                editUnit = item.unit
                                editPrice = item.price.toString()
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = theme.start)
                            }

                            // Delete button
                            IconButton(onClick = { viewModel.deleteProduct(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.75f))
                            }
                        }
                    }
                }
            }

            // Connect Printer Button at Bottom
            Surface(color = Color.White, shadowElevation = 16.dp) {
                Button(
                    onClick = { viewModel.connectPrinter("00:11:22:33:44:55") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(viewModel.getString("connect_printer", langCode), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

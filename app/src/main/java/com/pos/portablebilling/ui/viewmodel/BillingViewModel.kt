package com.pos.portablebilling.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pos.portablebilling.PortableBillingApplication
import com.pos.portablebilling.bluetooth.BluetoothPrinterManager
import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.domain.model.Transaction
import com.pos.portablebilling.domain.model.TransactionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val PREF_THEME = "pref_theme_index"
const val PREF_LANG = "pref_lang_code"

class BillingViewModel(
    application: Application,
    private val useCases: com.pos.portablebilling.domain.usecase.BillingUseCases
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("billing_prefs", android.content.Context.MODE_PRIVATE)
    private val printerManager = BluetoothPrinterManager(application)
    private val languageManager = com.pos.portablebilling.util.LanguageManager(application)

    private val _langCode = MutableStateFlow(prefs.getString(PREF_LANG, "en") ?: "en")
    val langCode: StateFlow<String> = _langCode.asStateFlow()

    init {
        languageManager.loadLanguage(_langCode.value)
    }

    fun setLanguage(code: String) {
        _langCode.value = code
        prefs.edit().putString(PREF_LANG, code).apply()
        languageManager.loadLanguage(code)
    }

    fun getString(key: String, currentLang: String): String {
        return languageManager.getString(key)
    }

    // Theme index (persisted)
    private val _themeIndex = MutableStateFlow(prefs.getInt(PREF_THEME, 0))
    val themeIndex: StateFlow<Int> = _themeIndex.asStateFlow()

    fun setThemeIndex(index: Int) {
        _themeIndex.value = index
        prefs.edit().putInt(PREF_THEME, index).apply()
    }

    // Catalog State
    val catalogItems: StateFlow<List<ProductItem>> = useCases.getItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cart State
    private val _cartItems = MutableStateFlow<List<TransactionItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _printerConnected = MutableStateFlow(false)
    val printerConnected = _printerConnected.asStateFlow()

    val cartTotal: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addNewProduct(name: String, unit: String, price: Double) {
        val exists = catalogItems.value.any { it.name.trim().equals(name.trim(), ignoreCase = true) }
        if (exists) return

        viewModelScope.launch {
            useCases.saveItem(ProductItem(name = name.trim(), unit = unit, price = price))
        }
    }

    fun updateProduct(item: ProductItem, name: String, unit: String, price: Double) {
        val exists = catalogItems.value.any { it.id != item.id && it.name.trim().equals(name.trim(), ignoreCase = true) }
        if (exists) return

        viewModelScope.launch {
            useCases.saveItem(item.copy(name = name.trim(), unit = unit, price = price))
        }
    }

    fun deleteProduct(item: ProductItem) {
        viewModelScope.launch {
            useCases.removeItem(item)
        }
    }

    fun addToCart(product: ProductItem, quantity: Double) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == product.id }

        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            val newQty = existing.quantity + quantity
            currentList[existingIndex] = existing.copy(
                quantity = newQty,
                totalPrice = newQty * existing.pricePerUnit
            )
        } else {
            currentList.add(
                TransactionItem(
                    productId = product.id,
                    productName = product.name,
                    quantity = quantity,
                    pricePerUnit = product.price,
                    unit = product.unit,
                    totalPrice = quantity * product.price
                )
            )
        }
        _cartItems.value = currentList
    }

    fun setProductQuantity(productId: Long, quantity: Double) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == productId }
        if (existingIndex != -1) {
            if (quantity <= 0) {
                currentList.removeAt(existingIndex)
            } else {
                val existing = currentList[existingIndex]
                currentList[existingIndex] = existing.copy(
                    quantity = quantity,
                    totalPrice = quantity * existing.pricePerUnit
                )
            }
            _cartItems.value = currentList
        }
    }

    fun removeProductFromCart(productId: Long) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == productId }
        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            if (existing.quantity > 1.0) {
                val newQty = existing.quantity - 1.0
                currentList[existingIndex] = existing.copy(
                    quantity = newQty,
                    totalPrice = newQty * existing.pricePerUnit
                )
            } else {
                currentList.removeAt(existingIndex)
            }
        }
        _cartItems.value = currentList
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun connectPrinter(macAddress: String) {
        viewModelScope.launch {
            val success = printerManager.connectToPrinter(macAddress)
            _printerConnected.value = success
        }
    }

    fun printAndSaveTransaction() {
        if (_cartItems.value.isEmpty()) return

        val transaction = Transaction(
            timestamp = System.currentTimeMillis(),
            totalAmount = cartTotal.value,
            items = _cartItems.value
        )

        // Generate ESC/POS String
        val receiptBuilder = java.lang.StringBuilder()
        receiptBuilder.append("Item       Qty   Price\n")
        receiptBuilder.append("--------------------------------\n")
        
        _cartItems.value.forEach { item ->
            val name = item.productName.take(10).padEnd(10, ' ')
            val qty = item.quantity.toString().padEnd(4, ' ')
            val price = "Rs.${item.totalPrice}".padEnd(10, ' ')
            receiptBuilder.append("$name $qty $price\n")
        }
        receiptBuilder.append("--------------------------------\n")
        receiptBuilder.append("TOTAL: Rs.${cartTotal.value}\n")

        // Print
        printerManager.printReceipt(receiptBuilder.toString())

        // Save to DB
        viewModelScope.launch {
            useCases.saveTransaction(transaction)
            clearCart()
        }
    }

    override fun onCleared() {
        super.onCleared()
        printerManager.closeConnection()
    }
}

class BillingViewModelFactory(
    private val application: Application,
    private val useCases: com.pos.portablebilling.domain.usecase.BillingUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillingViewModel(application, useCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

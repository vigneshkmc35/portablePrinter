package com.pos.portablebilling.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.OutputStream
import java.util.UUID

class BluetoothPrinterManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // Standard SPP UUID for Bluetooth Serial Printers
    private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun connectToPrinter(deviceAddress: String): Boolean {
        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                return false
            }
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothSocket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)
            bluetoothAdapter.cancelDiscovery()
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            closeConnection()
            return false
        }
    }

    fun printReceipt(receiptText: String) {
        try {
            if (outputStream == null) return

            // ESC/POS Initialization
            outputStream?.write(byteArrayOf(0x1B, 0x40))

            // Center Align
            outputStream?.write(byteArrayOf(0x1B, 0x61, 0x01))
            outputStream?.write("PORTABLE BILLING\n".toByteArray())
            outputStream?.write("--------------------------------\n".toByteArray())
            
            // Left Align
            outputStream?.write(byteArrayOf(0x1B, 0x61, 0x00))
            outputStream?.write(receiptText.toByteArray())
            
            outputStream?.write("--------------------------------\n".toByteArray())
            
            // Center Align for Footer
            outputStream?.write(byteArrayOf(0x1B, 0x61, 0x01))
            outputStream?.write("Thank you for your visit!\n\n\n".toByteArray())

            // Cut Paper (if supported)
            outputStream?.write(byteArrayOf(0x1D, 0x56, 0x41, 0x10))
            
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

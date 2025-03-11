package com.example.exchangeratechartclient

import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var currencySpinner: Spinner
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var fetchButton: Button
    private lateinit var chart: LineChart

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var activeDateEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Asegurarse de usar un tema que derive de Theme.AppCompat
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)

        // Crear layout principal programáticamente
        val mainLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // Configurar Spinner
        currencySpinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        setupCurrencySpinner()

        // Configurar contenedor de fechas
        val dateContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }

        // Configurar EditTexts para fechas
        startDateEditText = createDateEditText("Fecha inicial")
        endDateEditText = createDateEditText("Fecha final")

        dateContainer.addView(startDateEditText)
        dateContainer.addView(endDateEditText)

        // Configurar botón
        fetchButton = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            text = "Generar Gráfico"
            setBackgroundColor(Color.BLUE)
            setTextColor(Color.WHITE)
        }

        // Configurar gráfico
        chart = LineChart(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                800
            ).apply {
                topMargin = 32
            }
        }
        setupChart()

        mainLayout.addView(currencySpinner)
        mainLayout.addView(dateContainer)
        mainLayout.addView(fetchButton)
        mainLayout.addView(chart)

        setContentView(mainLayout)

        setupDatePickers()
        setupFetchButton()
    }

    private fun createDateEditText(hint: String): EditText {
        return EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            this.hint = hint
            isFocusable = false
            isClickable = true
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf(
            "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN",
            "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD",
            "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC",
            "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR",
            "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ",
            "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD",
            "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF",
            "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD",
            "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MYR",
            "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK",
            "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD",
            "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SLL", "SOS", "SRD", "SSP", "STN",
            "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TVD", "TWD",
            "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF",
            "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter
    }

    private fun setupDatePickers() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            val selectedDate = calendar.timeInMillis / 1000
            activeDateEditText?.let { editText ->
                editText.setText(dateFormatter.format(calendar.time))
                editText.tag = selectedDate
            }
        }

        startDateEditText.setOnClickListener {
            activeDateEditText = startDateEditText
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDateEditText.setOnClickListener {
            activeDateEditText = endDateEditText
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupChart() {
        with(chart) {
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            // Configurar el formateador del eje X para mostrar la fecha en formato "dd/MM"
            xAxis.valueFormatter = object : ValueFormatter() {
                private val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
                override fun getFormattedValue(value: Float): String {
                    // Se asume que el timestamp está en segundos, se multiplica para obtener milisegundos
                    return sdf.format(Date((value.toLong() * 1000)))
                }
            }
            axisRight.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun setupFetchButton() {
        fetchButton.setOnClickListener {
            val currency = currencySpinner.selectedItem.toString()
            val startDate = startDateEditText.tag as? Long ?: 0L
            val endDate = endDateEditText.tag as? Long ?: 0L

            if (validarFechas(startDate, endDate)) {
                fetchDataFromProvider(currency, startDate, endDate)
            } else {
                Toast.makeText(this, "Rango de fechas inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarFechas(start: Long, end: Long): Boolean {
        return start > 0 && end > 0 && start <= end
    }

    private fun fetchDataFromProvider(currency: String, startDate: Long, endDate: Long) {
        val uri = Uri.parse("content://com.example.proyectodivisa.provider/exchange_rates/$currency")
            .buildUpon()
            .appendQueryParameter("start", startDate.toString())
            .appendQueryParameter("end", endDate.toString())
            .build()
        Log.d("ContentProviderTest", "URI de consulta: $uri")

        try {
            val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                "date ASC"
            )

            val entries = mutableListOf<Entry>()
            cursor?.use {
                val rateIndex = it.getColumnIndex("rate")
                val dateIndex = it.getColumnIndex("date")  // Se asume que existe una columna "date" con el timestamp
                while (it.moveToNext()) {
                    val timestamp = it.getLong(dateIndex)  // Timestamp en segundos
                    val rate = it.getDouble(rateIndex)
                    entries.add(Entry(timestamp.toFloat(), rate.toFloat()))
                }
            }

            if (entries.isNotEmpty()) {
                // Si hay datos, aseguramos que el gráfico esté visible y lo actualizamos
                chart.visibility = View.VISIBLE
                updateChart(entries, currency)
            } else {
                // Si no se encuentran datos, se oculta el gráfico
                chart.clear()
                chart.visibility = View.GONE
                Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ContentProviderTest", "Error al consultar el ContentProvider", e)
            Toast.makeText(this, "Error al consultar el ContentProvider", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChart(entries: List<Entry>, currency: String) {
        // Limpiar el gráfico para quitar data anterior
        chart.clear()

        // Crear un nuevo dataset con los datos obtenidos
        val dataSet = LineDataSet(entries, "Tipo de cambio $currency/MXN").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(Color.RED)
            circleRadius = 4f
            valueTextSize = 16f  // Aumenta el tamaño del texto de los puntos

            // Formatear los valores para mostrar 3 decimales
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.3f", value)
                }
            }
        }
        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}

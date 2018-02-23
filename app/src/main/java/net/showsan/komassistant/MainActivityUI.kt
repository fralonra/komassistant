package net.showsan.komassistant

import android.graphics.Color
import android.serialport.SerialPortFinder
import android.widget.*
import org.jetbrains.anko.*

/**
 * Created by zoron on 17-11-23.
 */
class MainActivityUI : AnkoComponent<MainActivity> {
    var recDataTxt: TextView? = null
    var radioButtonTxt: RadioButton? = null
    var radioButtonHex: RadioButton? = null

    var comSpinner: Spinner? = null
    var comBaudrateSpinner: Spinner? = null
    var comToggleButton: ToggleButton? = null

    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
        verticalLayout {
            padding = dip(20)
            linearLayout {
                recDataTxt = textView {
                    textSize = 24f
                    backgroundColor = Color.LTGRAY
                }.lparams(0, matchParent, 1.toFloat())
                verticalLayout {
                    button("Clear") {
                        setOnClickListener { ui.owner.clear() }
                    }
                    radioGroup {
                        radioButtonTxt = radioButton {
                            id = 0
                            text = "Txt"
                            isChecked = true
                        }
                        radioButtonHex = radioButton {
                            id = 1
                            text = "Hex"
                        }
                    }
                }
            }.lparams(matchParent, 0, 1.toFloat())
            linearLayout {
                val comTxt = editText("D&I00040101") {
                    textSize = 24f
                }.lparams(0, matchParent, 1.toFloat())
                comSpinner = spinner()
                comBaudrateSpinner = spinner()
                comToggleButton = toggleButton {
                    textOn = "Close"
                    textOff = "Open"
                    setOnClickListener {
                        ui.owner.toggleCom(comSpinner?.selectedItem.toString(),
                                comBaudrateSpinner?.selectedItem.toString(),
                                comToggleButton)
                    }
                }
                button("Send") {
                    setOnClickListener { ui.owner.send(comTxt.text.toString()) }
                }
            }
        }
    }.view

    fun bindView(ui: MainActivity) {
        initDevice(ui)
        initBaudrate(ui)
    }

    fun initDevice(ui: MainActivity) {
        val serialPortFinder = SerialPortFinder()
        val entryValues = serialPortFinder.allDevicesPath
        val allDevices = entryValues.indices.map { entryValues[it] }
        val devicesAdapter = ArrayAdapter(ui, android.R.layout.simple_spinner_item, allDevices)
        devicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (allDevices.isNotEmpty()) {
            comSpinner?.adapter = devicesAdapter
            comSpinner?.setSelection(6)
        }
    }

    fun initBaudrate(ui: MainActivity) {
        val baudrates = ui.resources.getStringArray(R.array.baudrates_value)
        val baudrateAdapter = ArrayAdapter(ui, android.R.layout.simple_spinner_item, baudrates)
        baudrateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        comBaudrateSpinner?.adapter = baudrateAdapter
        comBaudrateSpinner?.setSelection(16);
    }
}
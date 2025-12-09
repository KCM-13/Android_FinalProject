package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.model.InstitutionData
import com.example.finalproject.ui.institution.InstitutionAdapter
import com.example.finalproject.util.CsvUtil

class InstitutionFragment : Fragment() {

    private lateinit var adapter: InstitutionAdapter

    // 🔹 글로벌로 공유할 즐겨찾기 Set
    companion object {
        val globalFavoriteSet = mutableSetOf<String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_institution, container, false)

        // 🔹 Spinner
        val spinner = view.findViewById<Spinner>(R.id.spinner_district)
        val items = listOf("서울특별시 강북구", "서울특별시 종로구")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // 🔹 RadioGroup
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_type)

        // 🔹 RecyclerView + Adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = InstitutionAdapter(
            requireContext(),
            emptyList(),
            globalFavoriteSet,
            { item: InstitutionData ->
                if (globalFavoriteSet.contains(item.name)) {
                    globalFavoriteSet.remove(item.name)
                } else {
                    globalFavoriteSet.add(item.name)
                }
                adapter.notifyDataSetChanged()
            },
            isReadOnly = false
        )
        recyclerView.adapter = adapter

        // 🔹 검색 버튼
        val searchBtn = view.findViewById<Button>(R.id.btn_search)
        searchBtn.setOnClickListener {
            val selectedRegion = spinner.selectedItem.toString()
            val selectedType = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_care -> "돌봄지원기관"
                R.id.radio_facility -> "복지관/이용시설"
                else -> ""
            }

            val fileName = when {
                selectedRegion.contains("강북") && selectedType == "돌봄지원기관" -> "gangbuk_care.csv"
                selectedRegion.contains("강북") -> "gangbuk_facility.csv"
                selectedRegion.contains("종로") && selectedType == "돌봄지원기관" -> "jongno_care.csv"
                selectedRegion.contains("종로") -> "jongno_facility.csv"
                else -> ""
            }

            val data = CsvUtil.readCsv(requireContext(), fileName)
            adapter.updateList(data)
        }

        return view
    }
}

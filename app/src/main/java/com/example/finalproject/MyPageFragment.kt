package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.model.InstitutionData
import com.example.finalproject.ui.institution.InstitutionAdapter
import com.example.finalproject.util.CsvUtil

class MyPageFragment : Fragment() {

    private lateinit var profileCircle: TextView
    private lateinit var nameTextView: TextView
    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var favoriteAdapter: InstitutionAdapter
    private var favoriteList: MutableList<InstitutionData> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        profileCircle = view.findViewById(R.id.profileCircle)
        nameTextView = view.findViewById(R.id.nameTextView)
        favoriteRecyclerView = view.findViewById(R.id.recycler_favorites)

        // 이름에서 성씨만 표시
        val fullName = nameTextView.text.toString()
        if (fullName.isNotEmpty()) {
            profileCircle.text = fullName.first().toString()
        }

        // 전체 데이터 로드 후 favoriteSet 기준으로 필터링
        favoriteList = getDummyInstitutionData().filter {
            InstitutionFragment.globalFavoriteSet.contains(it.name)
        }.toMutableList()

        // 어댑터 설정
        favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoriteAdapter = InstitutionAdapter(
            requireContext(),
            favoriteList,
            InstitutionFragment.globalFavoriteSet,
            onFavoriteClick = { item ->
                InstitutionFragment.globalFavoriteSet.remove(item.name)
                favoriteList.removeAll { it.name == item.name }
                favoriteAdapter.notifyDataSetChanged()
            }
        )
        favoriteRecyclerView.adapter = favoriteAdapter

        return view
    }

    // 전체 기관 데이터 로드
    private fun getDummyInstitutionData(): List<InstitutionData> {
        val allFiles = listOf("gangbuk_care.csv", "gangbuk_facility.csv", "jongno_care.csv", "jongno_facility.csv")
        val allData = mutableListOf<InstitutionData>()
        for (file in allFiles) {
            allData += CsvUtil.readCsv(requireContext(), file)
        }
        return allData
    }
}

package com.example.meditime_local.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.R

/*********************************
 * 화면 #2 관리
 * 약품 복용 일지 확인
 *********************************/


class ManageFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_manage, container, false)
        recyclerView = root.findViewById(R.id.manage_recyclerView)
        return root
    }

    //DataBase에서 복용한 약품에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.


}
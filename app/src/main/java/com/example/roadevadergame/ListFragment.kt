package com.example.roadevadergame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val listViewScores = view.findViewById<ListView>(R.id.listViewScores)


        val dataManager = DataManager(requireContext())
        val highScores = dataManager.getHighScores()


        val scoresAsStrings = highScores.mapIndexed { index, entry ->
            "${index + 1}. מרחק: ${entry.score} מטר | בתאריך: ${entry.date}"
        }


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            scoresAsStrings
        )
        listViewScores.adapter = adapter

        listViewScores.setOnItemClickListener { _, _, position, _ ->
            val selectedScore = highScores[position]

            android.util.Log.d("DEBUG_TEST", "List clicked! Sending: Lat=${selectedScore.latitude}, Lng=${selectedScore.longitude}")

            (activity as? ScoresActivity)?.updateMapFromList(
                selectedScore.latitude,
                selectedScore.longitude,
                selectedScore.score
            )
        }

        return view

    }
}
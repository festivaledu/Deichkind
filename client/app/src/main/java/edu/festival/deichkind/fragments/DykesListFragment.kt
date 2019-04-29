package edu.festival.deichkind.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import edu.festival.deichkind.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

class DykesListFragment : Fragment() {

    var dykesResponse: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dykes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listDykes()

        view.findViewById<TextView>(R.id.dykes_list_test).text = dykesResponse
    }

    private fun listDykes() = runBlocking(Dispatchers.Default) {
        launch {
            dykesResponse = URL("https://test.festival.ml/deichkind/api/dykes").readText()
        }
    }

}

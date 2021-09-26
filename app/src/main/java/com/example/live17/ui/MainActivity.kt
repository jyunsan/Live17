package com.example.live17.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.live17.databinding.ActivityMainBinding
import com.example.live17.view.UsersAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<UserListViewModel>()
    private lateinit var adapter: UsersAdapter
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = UsersAdapter(viewModel)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchUser(query)

                try {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG)
        viewModel.responseStatus.observe(this, {
            binding.btnNext.isEnabled = !it
            binding.btnPrevious.isEnabled = !it

            if(!it){
                toast.cancel()
                toast.setText(viewModel.responseErrorMessage.value)
                toast.show()
            }
        })

        binding.btnPrevious.setOnClickListener {
            viewModel.onPrevious()
        }

        binding.btnNext.setOnClickListener {
            viewModel.onNext(binding.searchView.query.toString())
        }

        viewModel.isLoading.observe(this, {
            binding.icRefresh.visibility = if(it) View.VISIBLE else View.GONE
        })

        viewModel.userListData.observe(this, {
            binding.txtMsg.text =
                if (viewModel.totalUserCount > 0) "${viewModel.currentPageIndex}/${viewModel.totalPageCount}" else "-/-"

            adapter.notifyDataSetChanged()
            binding.recyclerView.scrollToPosition(0)
        })
    }
}
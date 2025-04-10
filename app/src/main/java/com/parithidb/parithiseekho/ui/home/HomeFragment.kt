package com.parithidb.parithiseekho.ui.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.parithidb.parithiseekho.R
import com.parithidb.parithiseekho.data.api.errorHandling.ApiStatusResponse
import com.parithidb.parithiseekho.data.api.errorHandling.Resource
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.databinding.FragmentHomeBinding
import com.parithidb.parithiseekho.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresh.setOnRefreshListener {
            viewModel.callGetTopAnimeApi().observe(viewLifecycleOwner, ::handleGetTopAnimeApi)
        }

        binding.cvRefresh.setOnClickListener {
            viewModel.callGetTopAnimeApi().observe(viewLifecycleOwner, ::handleGetTopAnimeApi)
        }

        viewModel.callGetTopAnimeApi().observe(viewLifecycleOwner, ::handleGetTopAnimeApi)
        viewModel.getAllAnimes().observe(viewLifecycleOwner, ::handleAllAnimes)
    }

    private fun handleAllAnimes(animeEntities: List<AnimeEntity>?) {
        if (!animeEntities.isNullOrEmpty()) {
            binding.tvWrong.visibility = View.INVISIBLE
            binding.cvRefresh.visibility = View.INVISIBLE
            binding.ivRefresh.visibility = View.INVISIBLE
            val animeAdapter = AnimeAdapter(
                onClick = { anime ->
                    val bundle = Bundle()
                    bundle.putInt("animeId", anime.animeId)
                    bundle.putString("title", anime.title)
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_homeFragment_to_animeDetailsFragment, bundle)
                }
            )
            val rvAnime = binding.rvAnime
            rvAnime.adapter = animeAdapter
            rvAnime.layoutManager = GridLayoutManager(requireContext(), 2)
            animeAdapter.submitList(animeEntities)
        } else {
            binding.tvWrong.visibility = View.VISIBLE
            binding.cvRefresh.visibility = View.VISIBLE
            binding.ivRefresh.visibility = View.VISIBLE
        }
    }

    private fun handleGetTopAnimeApi(resource: Resource<ApiStatusResponse>?) {
        binding.refresh.isRefreshing = false
        dismissProgressBar()
        if (resource == null) {
            return
        }

        when (resource.status) {
            Resource.Status.ERROR -> {
                Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            Resource.Status.SUCCESS -> {
                if (resource.data?.status == Constants.STATUS_CODE_SUCCESS) {
                    Snackbar.make(binding.root, "Anime fetched successfully", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

            Resource.Status.LOADING -> {
                if (!binding.refresh.isRefreshing) {
                    showProgressDialog("Fetching animes...")
                }
            }
        }
    }

    private fun showProgressDialog(content: String) {
        mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog!!.setMessage(content)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
    }

    private fun dismissProgressBar() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
        }
    }
}
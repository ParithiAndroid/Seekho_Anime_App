package com.parithidb.parithiseekho.ui.animeDetails

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import com.parithidb.parithiseekho.R
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.databinding.FragmentAnimeDetailsBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAnimeDetailsBinding
    private val viewModel: AnimeDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        viewModel.getAnimeById().observe(viewLifecycleOwner, ::handleAnime)
    }

    private fun handleAnime(animeEntity: AnimeEntity?) {
        if (animeEntity != null) {

            if (animeEntity.trailerUrl != null) {
                binding.ytPlayerView.visibility = View.VISIBLE
                binding.ivPoster.visibility = View.INVISIBLE
                setUpYtPlayer(animeEntity.trailerUrl.toString())
            } else {
                binding.ytPlayerView.visibility = View.INVISIBLE
                binding.ivPoster.visibility = View.VISIBLE
                Picasso.get()
                    .load(animeEntity.imageUrl)
                    .placeholder(R.drawable.ic_broken_image)
                    .error(R.drawable.ic_broken_image)
                    .into(binding.ivPoster)
            }

            binding.tvTitle.text = animeEntity.title
            binding.tvRating.text = animeEntity.rating

            binding.genreChips.removeAllViews()

            animeEntity.genres.forEach { genre ->
                val chip = Chip(requireContext()).apply {
                    text = genre
                    isClickable = false
                    isCheckable = false
                }
                binding.genreChips.addView(chip)
            }

            binding.tvScore.text = "${animeEntity.score} / 10"

            binding.tvProducers.text = if (animeEntity.producers.isNotEmpty()) {
                animeEntity.producers.joinToString(", ")
            } else {
                "Details not available"
            }

            setExpandableTextView(
                binding.tvSynopsis,
                animeEntity.synopsis.toString(),
                5
            )
        }
    }

    private fun setUpYtPlayer(
        videoUrl: String
    ) {
        val videoId: String = extractYoutubeVideoId(videoUrl).toString()

        val webView = getWebView()

        val html = "<html>" +
                "  <body>" +
                "    <div id='player'></div>" +
                "    <script>" +
                "      var tag = document.createElement('script');" +
                "      tag.src = 'https://www.youtube.com/iframe_api';" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);" +
                "      var player;" +
                "      function onYouTubeIframeAPIReady() {" +
                "        player = new YT.Player('player', {" +
                "          height: '100%'," +
                "          width: '100%'," +
                "          videoId: '" + videoId + "'," +
                "          playerVars: {" +
                "            'autoplay': 1," +
                "          }," +
                "          events: {" +
                "            'onReady': onPlayerReady," +
                "            'onStateChange': onPlayerStateChange" +
                "          }" +
                "        });" +
                "      }" +
                "      function onPlayerReady(event) {" +
                "        event.target.playVideo();" +
                "      }" +
                "      function onPlayerStateChange(event) {" +
                "        if (event.data == YT.PlayerState.ENDED) {" +
                "          alert('Video finished');" +
                "        }" +
                "      }" +
                "    </script>" +
                "  </body>" +
                "</html>"

        webView.loadData(html, "text/html", "UTF-8")
    }

    private fun getWebView(): WebView {
        val webView: WebView = binding.ytPlayerView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalSystemUiVisibility = 0

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val fullScreenContainer =
                    requireActivity().findViewById<View>(android.R.id.content) as FrameLayout
                originalSystemUiVisibility =
                    requireActivity().window.decorView.systemUiVisibility
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN

                customView = view
                customViewCallback = callback
                fullScreenContainer.addView(
                    customView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val fullScreenContainer =
                    requireActivity().findViewById<View>(android.R.id.content) as FrameLayout
                requireActivity().window.decorView.systemUiVisibility =
                    originalSystemUiVisibility

                fullScreenContainer.removeView(customView)
                customView = null
                customViewCallback!!.onCustomViewHidden()
                webView.visibility = View.VISIBLE
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun getDefaultVideoPoster(): Bitmap {
                return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            }
        }

        binding.getRoot()
            .addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    webView.onResume()
                    webView.post { webView.loadUrl("javascript:if(player){player.playVideo();}") }
                }

                override fun onViewDetachedFromWindow(v: View) {
                    webView.loadUrl("javascript:if(player){player.pauseVideo();}")
                    webView.onPause()
                }
            })

        return webView
    }

    private fun extractYoutubeVideoId(videoUrl: String): String? {
        var videoId: String? = null
        if (videoUrl.contains("youtu.be/")) {
            videoId = videoUrl.substring(videoUrl.lastIndexOf("/") + 1)
        } else if (videoUrl.contains("youtube.com/watch?v=")) {
            videoId = videoUrl.substring(videoUrl.indexOf("v=") + 2)
            val ampersandPosition = videoId.indexOf('&')
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition)
            }
        }
        return videoId
    }

    private fun setExpandableTextView(textView: MaterialTextView, text: String, maxLines: Int) {
        val seeMoreText = " see more"
        val seeLessText = " see less"

        textView.text = text

        textView.post {
            if (textView.layout != null && textView.lineCount > maxLines) {
                val endIndex = textView.layout.getLineEnd(maxLines - 1)

                if (endIndex > seeMoreText.length) {
                    val safeEndIndex =
                        max(0.0, (endIndex - seeMoreText.length - 3).toDouble()).toInt()
                    val trimmedText = text.substring(0, safeEndIndex) + "..."

                    val spannable = SpannableString(trimmedText + seeMoreText)
                    val clickableSpan: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            if (textView.maxLines == maxLines) {
                                textView.maxLines = Int.MAX_VALUE
                                applySeeLessSpan(textView, text, seeLessText, maxLines)
                            } else {
                                textView.maxLines = maxLines
                                textView.text = spannable
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color =
                                ContextCompat.getColor(textView.context, R.color.colorOnSurface)
                            ds.isUnderlineText = false
                        }
                    }

                    spannable.setSpan(
                        clickableSpan,
                        trimmedText.length,
                        spannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textView.text = spannable
                    textView.movementMethod = LinkMovementMethod.getInstance()
                }
            }
        }
    }

    private fun applySeeLessSpan(
        textView: MaterialTextView,
        fullText: String,
        seeLessText: String,
        maxLines: Int
    ) {
        val seeLessSpan = SpannableString(fullText + seeLessText)
        val seeLessClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                textView.maxLines = maxLines
                setExpandableTextView(textView, fullText, maxLines)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(textView.context, R.color.colorOnSurface)
                ds.isUnderlineText = false
            }
        }

        seeLessSpan.setSpan(
            seeLessClick,
            fullText.length,
            seeLessSpan.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = seeLessSpan
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

}
package com.example.meuzk.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.meuzk.CurrentSongHelper
import com.example.meuzk.R
import com.example.meuzk.Songs
import java.lang.Exception
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    var myActivity: Activity ?= null
    var mediaPlayer: MediaPlayer ?= null
    var startTimeText: TextView ?= null
    var endTimeText: TextView ?= null
    var playpauseImageButton: ImageButton ?= null
    var previousImageButton: ImageButton ?= null
    var nextImageButton: ImageButton ?= null
    var loopImageButton: ImageButton ?= null
    var seekbar: SeekBar ?= null
    var songArtistView: TextView ?= null
    var songTitleView: TextView ?= null
    var shuffleImageButton: ImageButton ?= null

    var currentPossition: Int = 0
    var fetchSongs: ArrayList<Songs> ?= null
    var currentSongHelper: CurrentSongHelper ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)

        seekbar = view?.findViewById(R.id.seekBar)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)



        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false

        var path: String ?= null
        var _songTitle: String ?= null
        var _songArtist: String ?= null
        var songId: Long ?= null
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")?.toLong()
            currentPossition = arguments?.getInt("songPosition")!!
            fetchSongs = arguments?.getParcelableArrayList("songData")

            currentSongHelper?.songPath = path
            currentSongHelper?.songId = songId
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.currentPosition = currentPossition


        }
        catch (e: Exception){
            e.printStackTrace()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
            mediaPlayer?.prepare()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        mediaPlayer?.start()
        if (currentSongHelper?.isPlaying as Boolean){
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        }
        else{
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
    }

    fun clickHandler (){
        shuffleImageButton?.setOnClickListener({

        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if( currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
            }
            else{
                playNext("PlayNextNormal")
            }
        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if( currentSongHelper?.isLoop as Boolean){
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        loopImageButton?.setOnClickListener({
            if( currentSongHelper?.isLoop as Boolean){
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            else{
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }
        })
        playpauseImageButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean){
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true
                playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun playNext(check: String){
        if( check.equals("PlayNextNormal", true)){
            currentPossition += 1
        }
        else if( check.equals("PlayNextLikeNormalShuffle", true)){
            var randomObject = Random(1)
            var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1 ) as Int)
            currentPossition = randomPosition
        }
        if(currentPossition >= fetchSongs!!.size) {
            currentPossition = 0
        }

        currentSongHelper?.isLoop = false

        val nextSong = fetchSongs?.get(currentPossition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songId = nextSong?.songID as Long
        currentSongHelper?.currentPosition = currentPossition
        currentSongHelper?.songTitle = nextSong.songTitle
        currentSongHelper?.songArtist = nextSong.artist

        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(activity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }
    fun playPrevious(){

        currentPossition -= 1
        if(currentPossition < 0){
            currentPossition = fetchSongs?.size as Int
        }

        if (currentSongHelper?.isPlaying as Boolean){
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        }
        else{
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        currentSongHelper?.isLoop = false

        val nextSong = fetchSongs?.get(currentPossition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songId = nextSong?.songID as Long
        currentSongHelper?.currentPosition = currentPossition
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist

        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }
}

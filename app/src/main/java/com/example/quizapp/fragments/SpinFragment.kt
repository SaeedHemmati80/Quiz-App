package com.example.quizapp.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.quizapp.WithdrawalFragment
import com.example.quizapp.databinding.FragmentSpinBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.random.Random


class SpinFragment : Fragment() {
    private var _binding: FragmentSpinBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    val itemSpin = arrayOf("100", "Try Again", "200", "Try Again", "500", "Try Again")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun showResult(itemSpin: String){
        Toast.makeText(requireContext(), itemSpin, Toast.LENGTH_SHORT).show()
        binding.btnSpin.isEnabled = true // Enable the btn again
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

        binding.tvCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

        binding.btnSpin.setOnClickListener {
            binding.btnSpin.isEnabled = false // Disable btn spin while the wheel is spinning

            val spin = Random.nextInt(6)
            val degrees = 360f * 3 + 60f * spin

            try{
                timer = object : CountDownTimer(5000, 50){
                    var rotation = 0f
                    override fun onTick(p0: Long) {
                        rotation += 30f
                        if(rotation >= degrees){
                            rotation = degrees
                            timer?.cancel()
                            showResult(itemSpin[spin])
                        }
                        binding.imgSpin.rotation = rotation
                    }

                    override fun onFinish() {}
                }.start()
            }catch (e:Exception){
                Log.e("7233","Error in countdown timer")
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSpinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel() // Cancel the timer to prevent crash
        _binding = null
    }


}
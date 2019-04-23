package com.myetherwallet.mewconnect.feature.main.utils

import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.myetherwallet.mewconnect.R

class FragmentTransactor {

    private val handler = Handler()
    private val actions = mutableListOf<Action>()

    fun resume(fragmentManager: FragmentManager) {
        for (action in actions) {
            when (action.id) {
                ActionId.ADD -> addFragment(fragmentManager, action.fragment!!)
                ActionId.REPLACE -> replaceFragment(fragmentManager, action.fragment!!)
                ActionId.ADD_OR_REPLACE -> addOrReplaceFragment(fragmentManager, action.fragment!!, action.tag!!)
                ActionId.POP -> popFragment(fragmentManager)
                ActionId.POP_TO_FIRST -> popFragmentToFirst(fragmentManager)
            }
            actions.remove(action)
        }
    }

    fun add(fragmentManager: FragmentManager, fragment: Fragment) {
        if (fragmentManager.isStateSaved) {
            actions.add(Action(ActionId.ADD, fragment))
        } else {
            addFragment(fragmentManager, fragment)
        }
    }

    fun replace(fragmentManager: FragmentManager, fragment: Fragment) {
        if (fragmentManager.isStateSaved) {
            actions.add(Action(ActionId.REPLACE, fragment))
        } else {
            replaceFragment(fragmentManager, fragment)
        }
    }

    fun addOrReplace(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        if (fragmentManager.isStateSaved) {
            actions.add(Action(ActionId.ADD_OR_REPLACE, fragment, tag))
        } else {
            addOrReplaceFragment(fragmentManager, fragment, tag)
        }
    }

    fun pop(fragmentManager: FragmentManager) {
        if (fragmentManager.isStateSaved) {
            actions.add(Action(ActionId.POP))
        } else {
            popFragment(fragmentManager)
        }
    }

    fun popToFirst(fragmentManager: FragmentManager) {
        if (fragmentManager.isStateSaved) {
            actions.add(Action(ActionId.POP_TO_FIRST))
        } else {
            popFragmentToFirst(fragmentManager)
        }
    }

    private fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        handler.post {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.toString())
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            fragmentTransaction.commit()
        }
    }

    private fun addFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        handler.post {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.toString())
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun addOrReplaceFragment(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        handler.post {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val previous = fragmentManager.findFragmentByTag(tag)
            if (previous == null) {
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                fragmentTransaction.add(R.id.main_fragment_container, fragment, tag)
                fragmentTransaction.addToBackStack(tag)
            } else {
                fragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag)
                fragmentTransaction.addToBackStack(tag)
            }
            fragmentTransaction.commit()
        }
    }

    private fun popFragment(fragmentManager: FragmentManager) {
        handler.post {
            fragmentManager.popBackStackImmediate()
        }
    }

    private fun popFragmentToFirst(fragmentManager: FragmentManager) {
        handler.post {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private data class Action(val id: ActionId, val fragment: Fragment? = null, val tag: String? = null)

    private enum class ActionId {
        ADD, REPLACE, ADD_OR_REPLACE, POP, POP_TO_FIRST
    }
}

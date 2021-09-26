package com.example.live17.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.live17.model.SearchResult
import com.example.live17.model.SearchResult.User
import com.example.live17.repository.UserRepository

class UserListViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    companion object {
        private const val UNDEFINED_ZERO = 0
        private const val UNDEFINED_ONE = 1
        private const val DEFAULT_FIRST_PAGE = UNDEFINED_ONE
        private const val DEFAULT_PAGE_ITEM_COUNT = 30
        private const val PAGE_TYPE_CURRENT = 1
        private const val PAGE_TYPE_NEXT = 2
    }

    var responseStatus = MutableLiveData<Boolean>()
    var responseErrorMessage = MutableLiveData<String>()
    var userListData = MutableLiveData<ArrayList<User>>()
    private var userList = ArrayList<User>()
    private var pageMap = HashMap<Int, ArrayList<User>>()
    var currentPageIndex = UNDEFINED_ONE
    var totalUserCount = UNDEFINED_ZERO
    var totalPageCount = UNDEFINED_ONE
    var isLoading = MutableLiveData<Boolean>()

    fun getUserList() = userList

    fun searchUser(name: String) {
        pageMap.clear()
        pageLoadHandler(name, DEFAULT_FIRST_PAGE)
    }

    private fun pageLoadHandler(name: String, pageIndex: Int) {
        isLoading.value = true

        if (pageMap.containsKey(pageIndex)) {
            updateCurrentUserListData(pageIndex)
        } else {
            getPageUserList(name, pageIndex, PAGE_TYPE_CURRENT)
        }
    }

    private fun getPageUserList(name: String, pageIndex: Int, type: Int) {
        userRepository.getUserList(
            name,
            pageIndex,
            DEFAULT_PAGE_ITEM_COUNT,
            object : UserRepository.OnUserListCallback {
                override fun getUserList(result: SearchResult) {
                    if (result.isSuccess) {
                        val userList = ArrayList<User>()
                        userList.addAll(result.items)
                        pageMap[pageIndex] = userList

                        when (pageIndex) {
                            DEFAULT_FIRST_PAGE -> {
                                totalUserCount = result.totalCount
                                totalPageCount =
                                    Companion.DEFAULT_FIRST_PAGE + (result.totalCount / DEFAULT_PAGE_ITEM_COUNT)
                            }
                        }

                        if (type == PAGE_TYPE_CURRENT) {
                            updateCurrentUserListData(pageIndex)

                            if (!pageMap.containsKey(pageIndex + 1)) {
                                getPageUserList(name, pageIndex + 1, PAGE_TYPE_NEXT)
                            }
                        }
                    } else {
                        updateErrorResponseMessage(result)
                    }
                }
            }
        )
    }

    private fun updateCurrentUserListData(pageIndex: Int) {
        userList.clear()
        currentPageIndex = pageIndex
        pageMap[pageIndex]?.let { userList.addAll(it) }
        userListData.value = userList
        isLoading.value = false
    }

    private fun updateErrorResponseMessage(result: SearchResult) {
        responseErrorMessage.value = result.errorMessage
        responseStatus.value = result.isSuccess
        isLoading.value = false
    }

    fun onPrevious() {
        if (currentPageIndex > 1) {
            currentPageIndex--
            updateCurrentUserListData(currentPageIndex)
        }
    }

    fun onNext(name: String) {
        if (currentPageIndex < totalPageCount - 1) {
            pageLoadHandler(name, currentPageIndex + 1)
        }
    }
}
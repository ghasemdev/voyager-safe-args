package com.parsuomash.sample.ui.screens.detail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

class PostDetailsScreenModel : StateScreenModel<PostDetailsScreenModel.State>(State.Init) {
  sealed class State {
    data object Init : State()
    data object Loading : State()
    data class Result(val post: Post) : State()
  }

  fun getPost(id: String) {
    screenModelScope.launch {
      mutableState.value = State.Loading
      mutableState.value = State.Result(post = Post(id))
    }
  }
}

data class Post(val foo: String)

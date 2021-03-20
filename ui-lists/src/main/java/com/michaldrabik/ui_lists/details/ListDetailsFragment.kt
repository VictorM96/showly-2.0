package com.michaldrabik.ui_lists.details

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaldrabik.ui_base.BaseFragment
import com.michaldrabik.ui_base.utilities.extensions.doOnApplyWindowInsets
import com.michaldrabik.ui_base.utilities.extensions.fadeIf
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_lists.R
import com.michaldrabik.ui_lists.details.di.UiListDetailsComponentProvider
import com.michaldrabik.ui_lists.details.recycler.ListDetailsAdapter
import com.michaldrabik.ui_model.CustomList
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_navigation.java.NavigationArgs
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_LIST
import kotlinx.android.synthetic.main.fragment_list_details.*

class ListDetailsFragment :
  BaseFragment<ListDetailsViewModel>(R.layout.fragment_list_details) {

  override val viewModel by viewModels<ListDetailsViewModel> { viewModelFactory }

  private val list by lazy { requireArguments().getParcelable<CustomList>(ARG_LIST)!! }

  private var adapter: ListDetailsAdapter? = null
  private var layoutManager: LinearLayoutManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    (requireActivity() as UiListDetailsComponentProvider).provideListDetailsComponent().inject(this)
    super.onCreate(savedInstanceState)
    setupBackPressed()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    setupRecycler()

    viewModel.run {
      uiLiveData.observe(viewLifecycleOwner, { render(it) })
      loadDetails(list.id)
      loadItems()
    }
  }

  override fun onResume() {
    super.onResume()
    hideNavigation()
  }

  private fun setupView() {
    fragmentListDetailsRoot.doOnApplyWindowInsets { view, insets, padding, _ ->
      view.updatePadding(top = padding.top + insets.systemWindowInsetTop)
    }
    with(fragmentListDetailsToolbar) {
      title = list.name
      if (!list.description.isNullOrBlank()) {
        subtitle = list.description
      }
      setNavigationOnClickListener { activity?.onBackPressed() }
    }
    fragmentListDetailsEditButton.onClick { showEditDialog() }
    fragmentListDetailsDeleteButton.onClick { showDeleteDialog() }
//    fragmentListDetailsSortButton.onClick {
//      viewModel.loadSortOrder()
//    }
  }

  private fun setupRecycler() {
    layoutManager = LinearLayoutManager(context, VERTICAL, false)
    adapter = ListDetailsAdapter().apply {
//      itemClickListener = { openMovieDetails(it.movie) }
    }
    fragmentListDetailsRecycler.apply {
      adapter = this@ListDetailsFragment.adapter
      layoutManager = this@ListDetailsFragment.layoutManager
      (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
      setHasFixedSize(true)
    }
  }

  private fun setupBackPressed() {
    val dispatcher = requireActivity().onBackPressedDispatcher
    dispatcher.addCallback(this) {
      remove()
      findNavControl()?.popBackStack()
    }
  }

  private fun showSortOrderDialog(order: SortOrder) {
//    val options = listOf(NAME, NEWEST, DATE_UPDATED)
//    val optionsStrings = options.map { getString(it.displayString) }.toTypedArray()
//
//    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
//      .setTitle(R.string.textSortBy)
//      .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
//      .setSingleChoiceItems(optionsStrings, options.indexOf(order)) { dialog, index ->
//        viewModel.setSortOrder(options[index])
//        dialog.dismiss()
//      }
//      .show()
  }

  private fun showDeleteDialog() {
    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
      .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
      .setTitle(R.string.textConfirmDeleteListTitle)
      .setMessage(R.string.textConfirmDeleteListSubtitle)
      .setPositiveButton(R.string.textYes) { _, _ -> viewModel.deleteList(list.id) }
      .setNegativeButton(R.string.textNo) { _, _ -> }
      .show()
  }

  private fun showEditDialog() {
    setFragmentResultListener(NavigationArgs.REQUEST_CREATE_LIST) { _, _ ->
      viewModel.loadDetails(list.id)
    }
    val bundle = bundleOf(ARG_LIST to list)
    navigateTo(R.id.actionListDetailsFragmentToEditListDialog, bundle)
  }

  private fun render(uiModel: ListDetailsUiModel) {
    uiModel.run {
      details?.let {
        with(fragmentListDetailsToolbar) {
          title = it.name
          if (!it.description.isNullOrBlank()) {
            subtitle = it.description
          }
        }
      }
      items?.let {
        fragmentListDetailsEmptyView.fadeIf(it.isEmpty())
//        fragmentListDetailsSortButton.visibleIf(it.isNotEmpty())
        adapter?.setItems(it, true)
      }
      deleteEvent?.let { event -> event.consume()?.let { activity?.onBackPressed() } }
      sortOrderEvent?.let { event -> event.consume()?.let { showSortOrderDialog(it) } }
    }
  }

  override fun onDestroyView() {
    adapter = null
    layoutManager = null
    super.onDestroyView()
  }
}
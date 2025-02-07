package org.oppia.android.app.help.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.help.faq.faqItemViewModel.FAQContentViewModel
import org.oppia.android.app.help.faq.faqItemViewModel.FAQHeaderViewModel
import org.oppia.android.app.help.faq.faqItemViewModel.FAQItemViewModel
import org.oppia.android.app.recyclerview.BindableAdapter
import org.oppia.android.databinding.FaqContentBinding
import org.oppia.android.databinding.FaqItemHeaderBinding
import org.oppia.android.databinding.FaqListFragmentBinding
import javax.inject.Inject

/** The presenter for [FAQListFragment]. */
@FragmentScope
class FAQListFragmentPresenter @Inject constructor(
  private val activity: AppCompatActivity,
  private val fragment: Fragment,
  private val faqListViewModel: FAQListViewModel,
  private val multiTypeBuilderFactory: BindableAdapter.MultiTypeBuilder.Factory
) {
  private lateinit var binding: FaqListFragmentBinding

  fun handleCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
    binding = FaqListFragmentBinding.inflate(
      inflater,
      container,
      /* attachToRoot= */ false
    )

    binding.faqFragmentRecyclerView.apply {
      layoutManager = LinearLayoutManager(activity.applicationContext)
      adapter = createRecyclerViewAdapter()
    }

    binding.let {
      it.lifecycleOwner = fragment
      it.viewModel = faqListViewModel
    }
    return binding.root
  }

  private fun createRecyclerViewAdapter(): BindableAdapter<FAQItemViewModel> {
    return multiTypeBuilderFactory.create<FAQItemViewModel, ViewType> { viewModel ->
      when (viewModel) {
        is FAQHeaderViewModel -> ViewType.VIEW_TYPE_HEADER
        is FAQContentViewModel -> ViewType.VIEW_TYPE_CONTENT
        else -> throw IllegalArgumentException("Encountered unexpected view model: $viewModel")
      }
    }
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_HEADER,
        inflateDataBinding = FaqItemHeaderBinding::inflate,
        setViewModel = FaqItemHeaderBinding::setViewModel,
        transformViewModel = { it as FAQHeaderViewModel }
      )
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_CONTENT,
        inflateDataBinding = FaqContentBinding::inflate,
        setViewModel = FaqContentBinding::setViewModel,
        transformViewModel = { it as FAQContentViewModel }
      )
      .build()
  }

  private enum class ViewType {
    VIEW_TYPE_HEADER,
    VIEW_TYPE_CONTENT
  }
}

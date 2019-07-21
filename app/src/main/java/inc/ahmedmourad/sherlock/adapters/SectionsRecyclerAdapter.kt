package inc.ahmedmourad.sherlock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.model.AppSection
import inc.ahmedmourad.sherlock.view.model.TaggedController

class SectionsRecyclerAdapter(private val sectionsList: List<AppSection>, private val onSectionSelectedListener: (Lazy<out TaggedController>?) -> Unit) : RecyclerView.Adapter<SectionsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.item_section, container, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(sectionsList[position])

    override fun getItemCount() = sectionsList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.section_name)
        internal lateinit var nameTextView: TextView

        @BindView(R.id.section_image)
        internal lateinit var imageView: ImageView

        init {
            ButterKnife.bind(this, view)
        }

        internal fun bind(section: AppSection) {
            nameTextView.text = section.name
            imageView.setImageResource(section.imageDrawable)
            itemView.setOnClickListener { onSectionSelectedListener(section.controller) }
        }
    }
}

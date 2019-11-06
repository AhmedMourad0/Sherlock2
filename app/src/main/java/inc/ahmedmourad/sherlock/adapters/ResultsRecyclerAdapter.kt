package inc.ahmedmourad.sherlock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.formatter.Formatter
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import splitties.init.appCtx
import java.util.*

internal class ResultsRecyclerAdapter(
        private val dateManager: Lazy<DateManager>,
        private val formatter: Lazy<Formatter>,
        private val onResultSelectedListener: (Pair<AppSimpleRetrievedChild, Int>) -> Unit
) : RecyclerView.Adapter<ResultsRecyclerAdapter.ViewHolder>() {

    private val resultsList = ArrayList<Pair<AppSimpleRetrievedChild, Int>>()

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.item_result, container, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(resultsList[position])

    override fun getItemCount() = resultsList.size

    fun updateList(list: List<Pair<AppSimpleRetrievedChild, Int>>) {
        resultsList.clear()
        resultsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.result_date)
        internal lateinit var dateTextView: TextView

        @BindView(R.id.result_picture)
        internal lateinit var pictureImageView: ImageView

        @BindView(R.id.result_notes)
        internal lateinit var notesTextView: TextView

        @BindView(R.id.result_location)
        internal lateinit var locationTextView: TextView

        private val glide: RequestManager

        init {
            ButterKnife.bind(this, view)
            glide = Glide.with(appCtx)
        }

        internal fun bind(result: Pair<AppSimpleRetrievedChild, Int>) {

            glide.load(result.first.pictureUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(pictureImageView)

            //TODO: this needs to change with time
            dateTextView.text = dateManager.get().getRelativeDateTimeString(result.first.publicationDate)
            notesTextView.text = formatter.get().formatNotes(result.first.notes)
            locationTextView.text = formatter.get().formatLocation(result.first.locationName, result.first.locationAddress)

            itemView.setOnClickListener { onResultSelectedListener(result) }
        }
    }
}

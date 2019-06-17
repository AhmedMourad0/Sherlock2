package inc.ahmedmourad.sherlock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.device.DateManager
import inc.ahmedmourad.sherlock.model.AppUrlChild
import inc.ahmedmourad.sherlock.utils.Formatter
import java.util.*

class ResultsRecyclerAdapter(
        private val dateManager: Lazy<DateManager>,
        private val formatter: Lazy<Formatter<String>>,
        private val onResultSelectedListener: (Pair<AppUrlChild, Int>) -> Unit
) : RecyclerView.Adapter<ResultsRecyclerAdapter.ViewHolder>() {

    private val resultsList = ArrayList<Pair<AppUrlChild, Int>>()

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.item_result, container, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(resultsList[position])

    override fun getItemCount() = resultsList.size

    fun updateList(list: List<Pair<AppUrlChild, Int>>) {
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

        private val picasso: Picasso

        init {
            ButterKnife.bind(this, view)
            picasso = Picasso.get()
        }

        internal fun bind(result: Pair<AppUrlChild, Int>) {

            picasso.load(result.first.pictureUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(pictureImageView)

            //TODO: this needs to change with time
            dateTextView.text = dateManager.get().getRelativeDateTimeString(result.first.timeMillis)
            notesTextView.text = formatter.get().formatNotes(result.first.notes)
            locationTextView.text = formatter.get().formatLocation(result.first.location)

            itemView.setOnClickListener { onResultSelectedListener(result) }
        }
    }
}

package mad.s10361649.hkweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import mad.s10361649.myapplication.R

internal class CustomAdapter(private var itemsList: List<String>,
                             private var wForecastIcon: Array<Int>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.itemTextView)
        var imageIcon: ImageView = view.findViewById(R.id.imageIcon)
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemTextView.text = itemsList[position]
        holder.imageIcon.setImageResource(wForecastIcon[position])
    }
    override fun getItemCount(): Int {
        return itemsList.size
    }
}

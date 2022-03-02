package riper.housebuilder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.projects_list_item.view.*

class ProjectsListAdapter(private val data: ArrayList<Project>, private val context: Context) : RecyclerView.Adapter<ProjectsListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.textName
        var textDescription: TextView = itemView.textDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.projects_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textName.text = data[position].name
        viewHolder.textDescription.text = data[position].description
        viewHolder.itemView.setOnClickListener {
            ProjectDetailsActivity.start(context, data[position].id)
        }
    }

    override fun getItemCount() = data.size
}

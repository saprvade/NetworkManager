/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.codelab.networkmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
    implements DeleteTask.DeleteTaskListener {

  private static final String TAG = TaskAdapter.class.getSimpleName();

  private final LayoutInflater inflater;
  private final List<TaskItem> taskItems;

  public TaskAdapter(Context context, List<TaskItem> taskItems) {
    this.inflater = LayoutInflater.from(context);
    this.taskItems = taskItems;
  }

  @Override
  public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View v = inflater.inflate(R.layout.item_task, viewGroup, false /* attachToRoot */);
    final ViewHolder viewHolder = new ViewHolder(v);
    viewHolder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, position + " - " + taskItems.size());
        TaskItem taskItem = taskItems.get(position);
        Log.d(TAG, "Clicked button at position " + position);
        new DeleteTask(view.getContext(), TaskAdapter.this).execute(taskItem);
      }
    });
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(TaskAdapter.ViewHolder viewHolder, final int position) {
    TaskItem taskItem = taskItems.get(position);
    viewHolder.getLabelTextView().setText(taskItem.getType());
    viewHolder.getStatusTextView().setText(taskItem.getStatus());
  }

  @Override
  public int getItemCount() {
    return taskItems.size();
  }

  public void setTaskItems(List<TaskItem> taskItems) {
    this.taskItems.clear();
    this.taskItems.addAll(taskItems);
    notifyDataSetChanged();
  }

  public void addTaskItem(TaskItem taskItem) {
    taskItems.add(0, taskItem);
    notifyItemInserted(0);
  }

  public void updateTaskItemStatus(String id, String status) {
    for (int i = 0; i < taskItems.size(); i++) {
      TaskItem taskItem = taskItems.get(i);
      if (taskItem.getId().equals(id)) {
        taskItem.setStatus(status);
        notifyItemChanged(i);
        break;
      }
    }
  }

  @Override
  public int getTaskCount() {
    return taskItems.size();
  }

  @Override
  public TaskItem getTask(int position) {
    return taskItems.get(position);
  }

  @Override
  public void onTaskDeleted(int position) {
    taskItems.remove(position);
    notifyItemRemoved(position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView labelTextView;
    private final TextView statusTextView;
    private final Button deleteButton;

    public ViewHolder(View v) {
      super(v);
      labelTextView = (TextView) v.findViewById(R.id.taskLabel);
      statusTextView = (TextView) v.findViewById(R.id.taskStatus);
      deleteButton = (Button) v.findViewById(R.id.deleteButton);
    }

    public TextView getLabelTextView() {
      return labelTextView;
    }

    public TextView getStatusTextView() {
      return statusTextView;
    }

    public Button getDeleteButton() {
      return deleteButton;
    }
  }
}

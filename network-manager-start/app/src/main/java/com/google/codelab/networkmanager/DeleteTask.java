package com.google.codelab.networkmanager;


import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

// AsyncTask to delete from file off the UI thread.
public class DeleteTask extends AsyncTask<TaskItem, Void, Integer> {

  private final Context context;
  private final WeakReference<DeleteTaskListener> listenerReference;

  public DeleteTask(Context context, DeleteTaskListener listener) {
    this.context = context;
    this.listenerReference = new WeakReference<>(listener);
  }

  @Override
  protected Integer doInBackground(TaskItem... taskItems) {
    TaskItem taskItem = taskItems[0];
    CodelabUtil.deleteTaskItemFromFile(context, taskItems[0]);
    return getTaskItemPosition(taskItem.getId());
  }

  @Override
  protected void onPostExecute(Integer position) {
    if (position == -1) {
      return;
    }
    DeleteTaskListener listener = listenerReference.get();
    if (listener == null) {
      return;
    }
    listener.onTaskDeleted(position);
  }

  private int getTaskItemPosition(String id) {
    DeleteTaskListener listener = listenerReference.get();
    if (listener == null) {
      return -1;
    }
    for (int i = 0; i < listener.getTaskCount(); i++) {
      TaskItem taskItem = listener.getTask(i);
      if (taskItem.getId().equals(id)) {
        return i;
      }
    }
    return -1;
  }

  public interface DeleteTaskListener {

    int getTaskCount();

    TaskItem getTask(int position);

    void onTaskDeleted(int position);
  }
}
package com.google.codelab.networkmanager;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

// AsyncTask used to add tasks to file off the UI thread.
public class AddTask extends AsyncTask<TaskItem, Void, TaskItem> {

  private final Context context;
  private final WeakReference<AddTaskListener> taskListenerReference;

  public AddTask(Context context, AddTaskListener listener) {
    this.context = context;
    taskListenerReference = new WeakReference<>(listener);
  }

  @Override
  protected TaskItem doInBackground(TaskItem... taskItems) {
    // Items are being added to the list before being scheduled/executed to allow their state to be
    // visible in the UI.
    TaskItem taskItem = taskItems[0];
    CodelabUtil.addTaskItemToFile(context, taskItem);
    return taskItem;
  }

  @Override
  protected void onPostExecute(TaskItem taskItem) {
    AddTaskListener listener = taskListenerReference.get();
    if (listener == null) {
      return;
    }
    listener.onTaskAdded(taskItem);
  }

  public interface AddTaskListener {

    void onTaskAdded(TaskItem item);
  }
}

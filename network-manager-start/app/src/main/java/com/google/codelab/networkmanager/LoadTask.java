package com.google.codelab.networkmanager;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadTask extends AsyncTask<Void, Void, List<TaskItem>> {

  private final Context context;
  private final WeakReference<LoadTaskListener> taskListenerReference;

  public LoadTask(Context context, LoadTaskListener listener) {
    this.context = context;
    taskListenerReference = new WeakReference<>(listener);
  }

  @Override
  protected List<TaskItem> doInBackground(Void... voids) {
    return CodelabUtil.getTaskItemsFromFile(context);
  }

  @Override
  protected void onPostExecute(List<TaskItem> taskItems) {
    LoadTaskListener listener = taskListenerReference.get();
    if (listener == null) {
      return;
    }
    listener.onItemsLoaded(taskItems);
  }

  public interface LoadTaskListener {

    void onItemsLoaded(List<TaskItem> taskItems);
  }
}

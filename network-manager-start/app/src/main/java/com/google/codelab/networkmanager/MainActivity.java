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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Controller of main/only UI for this sample.
 */
public class MainActivity extends AppCompatActivity implements LoadTask.LoadTaskListener,
    AddTask.AddTaskListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String TASK_ID_PREFIX = "task-id";

  private LocalBroadcastManager localBroadcastManager;
  private BroadcastReceiver broadcastReceiver;
  private GcmNetworkManager gcmNetworkManager;

  private TaskAdapter taskAdapter;
  private RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    gcmNetworkManager = GcmNetworkManager.getInstance(this);

    taskAdapter = new TaskAdapter(this, new ArrayList<TaskItem>());

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(taskAdapter);
    Button bestTimeButton = (Button) findViewById(R.id.bestTimeButton);
    bestTimeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String taskId = TASK_ID_PREFIX + Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "Scheduling oneoff task. " + taskId);
        TaskItem taskItem = new TaskItem(taskId, TaskItem.ONEOFF_TASK, TaskItem.PENDING_STATUS);
        new AddTask(view.getContext(), MainActivity.this).execute(taskItem);
      }
    });

    Button nowButton = (Button) findViewById(R.id.nowButton);
    nowButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String taskId = TASK_ID_PREFIX + Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "Creating a Now Task. " + taskId);
        TaskItem taskItem = new TaskItem(taskId, TaskItem.NOW_TASK, TaskItem.PENDING_STATUS);
        new AddTask(view.getContext(), MainActivity.this).execute(taskItem);
      }
    });

    localBroadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String taskId = intent.getStringExtra(CodelabUtil.TASK_ID);
        String status = intent.getStringExtra(CodelabUtil.TASK_STATUS);
        taskAdapter.updateTaskItemStatus(taskId, status);
      }
    };
  }

  @Override
  public void onItemsLoaded(List<TaskItem> taskItems) {
    taskAdapter.setTaskItems(taskItems);
    taskAdapter.notifyDataSetChanged();
  }

  @Override
  public void onTaskAdded(TaskItem taskItem) {
    taskAdapter.addTaskItem(taskItem);
    recyclerView.scrollToPosition(0);

    if (taskItem.getType().equals(TaskItem.ONEOFF_TASK)) {
      Bundle bundle = new Bundle();
      bundle.putString(CodelabUtil.TASK_ID, taskItem.getId());

      // Schedule oneoff task.
      OneoffTask oneoffTask = new OneoffTask.Builder()
          .setService(BestTimeService.class)
          .setTag(taskItem.getId())
          .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
          // Use an execution window of 30 seconds or more. Less than 30
          // seconds would not allow GcmNetworkManager enough time to
          // optimize the next best time to execute your task.
          .setExecutionWindow(0, 30)
          .setExtras(bundle)
          .build();
      gcmNetworkManager.schedule(oneoffTask);
    } else {
      // Immediately make network call.
      Intent nowIntent = new Intent(this, NowIntentService.class);
      nowIntent.putExtra(CodelabUtil.TASK_ID, taskItem.getId());
      startService(nowIntent);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    localBroadcastManager.registerReceiver(
        broadcastReceiver, new IntentFilter(CodelabUtil.TASK_UPDATE_FILTER));
    new LoadTask(this, this).execute();
  }

  @Override
  public void onPause() {
    super.onPause();
    localBroadcastManager.unregisterReceiver(broadcastReceiver);
  }
}

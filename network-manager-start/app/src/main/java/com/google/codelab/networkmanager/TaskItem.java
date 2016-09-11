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

public class TaskItem {

  public static final String ONEOFF_TASK = "Oneoff Task";
  public static final String NOW_TASK = "Now Task";
  public static final String PENDING_STATUS = "Pending";
  public static final String EXECUTED_STATUS = "Executed";
  public static final String FAILED_STATUS = "Failed";

  private final String id;
  private final String type;

  private String status;

  public TaskItem(String id, String label, String status) {
    this.id = id;
    type = label;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

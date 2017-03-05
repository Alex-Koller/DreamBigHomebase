package com.dreambig.homebase.iosapn;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import java.util.List;

/**
 * Push Notification coordinator. It uses Task Queues for push notification tasks, pre-processing
 * tasks and clean up of invalid or inactive device tokens.
 *
 *  It can be used from either front-end instances or back-end instances. In particular it can be
 * called from user request handlers in front-ends and backends instances.
 *
 */
public class PushNotificationUtility {

  /**
   * @constructor Private constructor as this is a utility class
   */
  private PushNotificationUtility() {}

  static void enqueueRemovingDeviceTokens(List<String> deviceTokens) {
    Queue deviceTokenCleanupQueue = QueueFactory.getQueue("notification-device-token-cleanup");
    deviceTokenCleanupQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.POST)
        .url("/admin/push/device/cleanup")
        .param("devices", new Gson().toJson(deviceTokens)));
  }
}

/*
 * ShiftLeft, Inc. CONFIDENTIAL
 * Unpublished Copyright (c) 2017 ShiftLeft, Inc., All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of ShiftLeft, Inc.
 * The intellectual and technical concepts contained herein are proprietary to ShiftLeft, Inc.
 * and may be covered by U.S. and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained
 * from ShiftLeft, Inc. Access to the source code contained herein is hereby forbidden to
 * anyone except current ShiftLeft, Inc. employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure
 * of this source code, which includeas information that is confidential and/or proprietary, and
 * is a trade secret, of ShiftLeft, Inc.
 *
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY
 * OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ShiftLeft, Inc.
 * IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES.
 * THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT
 * CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS
 * CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package io.shiftleft.bctrace;

import io.shiftleft.bctrace.asm.Transformer;
import io.shiftleft.bctrace.debug.DebugInfo;
import io.shiftleft.bctrace.impl.InstrumentationImpl;
import io.shiftleft.bctrace.runtime.Callback;
import io.shiftleft.bctrace.runtime.listener.Listener;
import io.shiftleft.bctrace.runtime.listener.info.StartListener;
import io.shiftleft.bctrace.runtime.listener.min.MinStartListener;
import io.shiftleft.bctrace.spi.AgentLoggerFactory;
import io.shiftleft.bctrace.spi.Hook;
import io.shiftleft.bctrace.spi.Instrumentation;
import io.shiftleft.bctrace.spi.SystemProperty;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Framework entry point.
 *
 * @author Ignacio del Valle Alles idelvall@shiftleft.io
 */
public final class Bctrace {

  static Bctrace instance;

  private static final Logger LOGGER = createLogger();
  private static final StartListener CALL_COUNTER_LISTENER;

  static {
    if (DebugInfo.isEnabled()) {
      CALL_COUNTER_LISTENER = new StartListener() {
        @Override
        public void onStart(int methodId, Object instance) {
          DebugInfo.getInstance().increaseCallCounter(methodId);
        }
      };
    } else {
      CALL_COUNTER_LISTENER = null;
    }
  }

  private final Transformer transformer;
  private final InstrumentationImpl instrumentation;
  private final Hook[] hooks;
  // This listeners are additionally added to those methods being listened.
  private final Listener[] additionalListeners;

  Bctrace(java.lang.instrument.Instrumentation javaInstrumentation, Hook[] hooks) {
    this.instrumentation = new InstrumentationImpl(javaInstrumentation);
    this.transformer = new Transformer(this.instrumentation);
    this.hooks = hooks;
    if (DebugInfo.isEnabled()) {
      // Add a call debugging counter listener for those methods being listened
      additionalListeners = new Listener[]{new MinStartListener() {
        @Override
        public void onStart(int methodId) {
          DebugInfo.getInstance().increaseCallCounter(methodId);
        }
      }};
    } else {
      additionalListeners = new Listener[0];
    }
  }

  void init() {
    if (hooks != null) {
      Listener[] listeners = new Listener[hooks.length];
      for (int i = 0; i < hooks.length; i++) {
        hooks[i].init(this.instrumentation);
        listeners[i] = this.hooks[i].getListener();
      }
      Callback.listeners = new Listener[listeners.length + additionalListeners.length];
      System.arraycopy(listeners, 0, Callback.listeners, 0, listeners.length);
      System.arraycopy(additionalListeners, 0, Callback.listeners, listeners.length,
          additionalListeners.length);

      if (instrumentation != null && instrumentation.getJavaInstrumentation() != null) {
        instrumentation.getJavaInstrumentation()
            .addTransformer(transformer, true);
      }

      for (int i = 0; i < hooks.length; i++) {
        hooks[i].afterRegistration();
      }
    }
  }

  private static Logger createLogger() {
    Logger logger = AgentLoggerFactory.getInstance().getLogger();
    String logLevel = System.getProperty(SystemProperty.LOG_LEVEL);
    if (logLevel != null) {
      Level level = Level.parse(logLevel);
      logger.setLevel(level);
      Handler[] handlers = logger.getHandlers();
      if (handlers != null) {
        for (Handler handler : handlers) {
          handler.setLevel(level);
        }
      }
    }
    return logger;
  }

  public static Bctrace getInstance() {
    return instance;
  }

  public Instrumentation getInstrumentation() {
    return this.instrumentation;
  }

  public static boolean isThreadNotificationEnabled() {
    return Callback.isThreadNotificationEnabled();
  }

  public static void enableThreadNotification() {
    Callback.enableThreadNotification();
  }

  public static void disableThreadNotification() {
    Callback.disableThreadNotification();
  }

  public Hook[] getHooks() {
    return this.hooks;
  }

  public Listener[] getAdditionalListeners() {
    return additionalListeners;
  }

  public static Logger getAgentLogger() {
    return LOGGER;
  }
}

package dieg0407.tools.jcli.services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class LongOperationWrapper {
  private LongOperationWrapper() {}

  /**
   * This method wraps operations that may take too long and displays a message while waiting for them to finish
   * @param operation
   * @param message
   * @param <T>
   */
  public static <T> T wrap(Supplier<T> operation, String message) {
    final var isFinished = new AtomicBoolean(false);
    final var waiterThread = CompletableFuture.supplyAsync(() -> {
      var direction = false;
      while(!isFinished.get()) {
        try {
          direction = !direction;
          var character = direction ? "\\" : "/";
          System.err.print(message + character + "\r");

          TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
          throw new RuntimeException("Time wrapper failed to wait, this should not be happening!", e);
        }
      }
      System.err.println(message + " done!");
      return (Void) null;
    });

    final var operationThread = CompletableFuture.supplyAsync(() -> {
      try {
        return operation.get();
      } catch (Exception e) {
        throw new RuntimeException("Operation failed!", e);
      } finally {
        isFinished.set(true);
      }
    });

    CompletableFuture.allOf(waiterThread, operationThread).join();
    return operationThread.join();
  }


}

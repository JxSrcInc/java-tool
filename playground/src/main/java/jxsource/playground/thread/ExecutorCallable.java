package jxsource.playground.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorCallable {

	public static void main(String... args) {
		Callable<Integer> task = () -> {
		    try {
		        TimeUnit.SECONDS.sleep(1);
		        return 123;
		    }
		    catch (InterruptedException e) {
		        throw new IllegalStateException("task interrupted", e);
		    }
		};
		Callable<Integer> taskException = () -> {
		        throw new RuntimeException("Thread Task Exception");
		};
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Integer> future = executor.submit(task);

		System.out.println("future done? " + future.isDone());

		try {
			// block call
			Integer result = future.get();
			System.out.println("task result: " + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("... end task");
		future = executor.submit(taskException);

		System.err.println("future done? " + future.isDone());

		try {
			// block call
			Integer result = future.get();
			System.out.println("task exception result: " + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println("... end task Exception");
	}
}

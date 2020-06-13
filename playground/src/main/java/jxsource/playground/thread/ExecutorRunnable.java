package jxsource.playground.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorRunnable {

	public static void main(String... args) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			RunnableImpl.print();
		});
		executor.submit(new RunnableImpl());
		System.out.println("...end");
	}
}

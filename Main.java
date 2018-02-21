package uk.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
	

	public static ExecutorService service = Executors.newFixedThreadPool(2);
	public static int[] hist = new int[20];
	public volatile int x = 1;
	public volatile int y = 1;

	public void actor1() {
	    long c = 0;
	    // Keep the thread busy
	    for (int i = 0; i < 12450; i++) {
	        c += i;
	    }
	    // (c % 3 == 0) Just keep it to avoid JIT optimization
	    x = (int) (c % 3 + 2);
	    
	    c = 0;
	    for (int i = 0; i < 12450; i++) {
	        c += i;
	    }
	    y = (int) (c % 3 + 3);
	}

	public void actor2() {
	    // Keep the thread busy
	    int y = this.y;
	    long c = 0;
	    for (int i = 0; i < 12450; i++) {
	        c += i;
	    }
	    int x = (int) (c % 3 + this.x);

	    int z = y * x;
	    hist[z]++;
	}

	public static void main(final String[] args) throws Exception {
	    System.out.println("hello world");

	    for (int i = 0; i < 100_000; i++) {
	        Main main = new Main();
	        Future<?> future1 = service.submit(main::actor1);
	        Future<?> future2 = service.submit(main::actor2);

	        future1.get();	
	        future2.get();
	    }

	    for (int i = 0; i < hist.length; i++) {
	        if (hist[i] > 0) {
	            System.out.println("" + i + ": " + hist[i]);
	        }
	    }
	    
	    service.shutdownNow();
	}

	



}


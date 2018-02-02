package com.baojie.zk.example.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ConcurrentPoolTest implements Runnable{
    private final Logger log = LoggerFactory.getLogger(ConcurrentPoolTest.class);

    private final String name;

    private ConcurrentPoolTest(String name){
        this.name=name;
    }

    public static ConcurrentPoolTest create(String name){
        return new ConcurrentPoolTest(name);
    }

    @Override
    public void run(){
        int i=0;
        for(;;){
            i++;
            try {
                TimeUnit.MICROSECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.error("name="+name+", has print="+i);
            if(i==26000){
                log.error("name="+name+", has print="+i+", break");
                break;
            }
        }
    }

    public static void main(String args[]){
        ConcurrentPool pool=new ConcurrentPool(1,4,10,TimeUnit.SECONDS,UnitedThreadFactory.create
                ("concurrent_test"));
        ConcurrentPoolTest t=null;
        for(int i=0;i<3;i++){
            t=ConcurrentPoolTest.create("baojie_"+i);
            pool.submit(t);
        }

        LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(3,TimeUnit.MINUTES));
        t=ConcurrentPoolTest.create("baojie_Other");
        pool.submit(t);

        LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(2,TimeUnit.MINUTES));
        pool.shutdown();
        LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(2,TimeUnit.MINUTES));
        pool.shutdownNow();

    }

}
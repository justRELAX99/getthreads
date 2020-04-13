package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MyTimer
{
    Master NewMaster;
    private boolean is_active;

    MyTimer(Master NewMaster)
    {
        is_active = true;
        this.NewMaster = NewMaster;

        Thread my_timer = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (is_active)
                {
                    try
                    {
                        NewMaster.update_tasks();
                        NewMaster.create_slaves_from_tasks();
                        System.out.println("==========================");
                        System.out.println("Timer обновил tasks");
                        System.out.println("==========================");
                        Thread.sleep(10000); //1000 - 1 сек
                    } catch (InterruptedException ex)
                    {
                        break;
                    }
                }
            }
        });

        my_timer.start(); // заводим
    }

    boolean get_active()
    {
        return is_active;
    }


    void disable()
    {
        is_active = false;
    }
}

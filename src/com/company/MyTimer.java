package com.company;

public class MyTimer
{
    Master newMaster;
    private boolean is_active;

    MyTimer(Master newMaster)
    {
        is_active = true;
        this.newMaster = newMaster;

        Thread my_timer = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (is_active)
                {
                    try
                    {
                        newMaster.update_tasks();
                        newMaster.create_slaves_from_tasks();
                        System.out.println("==========================");
                        System.out.println("Timer обновил tasks");
                        System.out.println("==========================");
                        Thread.sleep(60000); //1000 - 1 сек
                    }
                    catch (InterruptedException ex)
                    {
                        System.out.println("Ошибка обновления таймера");
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

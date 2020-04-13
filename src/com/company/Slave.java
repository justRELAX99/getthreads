package com.company;

public class Slave extends Thread
{
    JsonHandler task_json;
    int time;

    private boolean is_active;
    private boolean finished;

    Slave(JsonHandler task_json, int time)
    {
        is_active = true;
        finished = false;
        this.task_json = task_json;
        this.time = time;
    }

    synchronized boolean get_finished()
    {
        return finished;
    }

    synchronized void set_unfinished()
    {
        finished = false;
    }

    boolean get_active()
    {
        return is_active;
    }

    void disable()
    {
        is_active = false;
    }

    public void run()
    {
        System.out.printf("%s started... \n", task_json.id);
        int counter = 1; // счетчик циклов

        /*ChromeDriver driver=new ChromeDriver();
        driver.get("http://demo.guru99.com/");
        */
        while (is_active)
        {
            if (!finished)
            {
                try
                {
                    Thread.sleep(time);
                    System.out.println("Loop " + counter++ + " id=" + task_json.id);
                    finished = true;
                } catch (InterruptedException e)
                {
                    System.out.println("Thread has been interrupted");
                }
            } else
            {
                Thread.yield();
            }
        }
        System.out.printf("%s fiished... \n", task_json.id);
    }
}

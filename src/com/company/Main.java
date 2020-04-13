package com.company;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main
{

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException
    {
        // write your code here
        System.setProperty("webdriver.chrome.driver", "src/resources/chromedriver.exe");

        Master NewMaster = new Master();
        MyTimer NewTimer = new MyTimer(NewMaster);

        int count = 0;
        while (NewMaster.get_active())
        {
            if (NewMaster.slaves.size() != 0)
            {
                for (int i = 0; i < NewMaster.slaves.size(); i++)
                {
                    if (NewMaster.slaves.get(i).get_finished())
                    {
                        //NewMaster.update_tasks();//обновляем задачи
                        NewMaster.update_slave(i);//обновляем,либо удаляем раба
                        NewMaster.create_slaves_from_tasks();//создаем новые потоки из задач

                        count++;
                        System.out.println("Done");
                    }
                }
                if (count == 3)
                    NewMaster.disable();
            }
            Thread.yield();
        }

        NewTimer.disable();

        for (int i = 0; i < NewMaster.tasks.size(); i++)
            System.out.println(NewMaster.tasks.get(i));

        NewMaster.kill_all_threads();
    }
}

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

        Master newMaster = new Master();
        MyTimer newTimer = new MyTimer(newMaster);


        int count = 0;
        while (newMaster.get_active())
        {
            if (newMaster.slaves.size() != 0)
            {
                for (int i = 0; i < newMaster.slaves.size(); i++)
                {
                    if (newMaster.slaves.get(i).get_finished())
                    {
                        //newMaster.update_tasks();//обновляем задачи
                        newMaster.update_slave(i);//обновляем,либо удаляем раба
                        newMaster.create_slaves_from_tasks();//создаем новые потоки из задач
                        count++;
                        System.out.println("Done");
                    }
                }
                if (count == 2)
                    newMaster.disable();
            }
            Thread.yield();
        }

        newMaster.slaves.get(0).set_unfinished();
        newTimer.disable();
        newMaster.kill_all_threads();
    }
}

package com.company;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Slave extends Thread
{
    JsonHandler taskJson;
    int time;

    private String old_url;
    private boolean is_active;
    private boolean finished;

    Slave(JsonHandler taskJson, int time)
    {
        is_active = true;
        finished = false;
        this.taskJson = taskJson;
        this.time = time;
    }

    boolean get_finished()
    {
        return finished;
    }

    synchronized void set_unfinished()
    {
        finished = false;
        notify();
    }

    synchronized void set_finished() throws InterruptedException
    {
        finished=true;
        wait();
    }

    boolean get_active()
    {
        return is_active;
    }

    void disable()
    {
        is_active = false;
    }

    synchronized ChromeDriver get_browser(String string_proxy,String string_user_agent)
    {
        ChromeOptions options = new ChromeOptions();

        if(string_proxy!="")
        {
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(string_proxy);
            options.setCapability("proxy", proxy);
        }
        if(string_user_agent!="")
        {
            options.addArguments("user-agent="+string_user_agent);
        }
        ChromeDriver driver = new ChromeDriver(options);

        return driver;
    }

    void execute_task(ChromeDriver driver)
    {

        driver.get("http://demo.guru99.com/");//проверять,если юрл изменился,то не переходить

        if(taskJson.time_js1!=0)
        {
            try
            {
                Thread.sleep(taskJson.time_js1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            if ((taskJson.js1 != "") && (taskJson.js1!=null))
                driver.executeScript("");
        }
        catch (Exception e)
        {
            System.out.println("js1 не выолнился,id="+taskJson.id);
        }

        if(taskJson.time_js2!=0)
        {
            try
            {
                Thread.sleep(taskJson.time_js2);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            if ((taskJson.js2 != "") && (taskJson.js2!=null))
                driver.executeScript("");
        }
        catch (Exception e)
        {
            System.out.println("js2 не выолнился,id="+taskJson.id);
        }

    }

    public void run()
    {
        System.out.printf("%s started... \n", taskJson.id);
        int counter = 1; // счетчик циклов
        ChromeDriver driver;

        String user_agent="";
        String proxy="";

        driver=get_browser(proxy,user_agent);
        while (is_active)
        {
            if (!finished)
            {
                try
                {
                    Thread.sleep(time);
                    System.out.println("Loop " + counter++ + " id=" + taskJson.id);


                    if(is_active)//если во время выполнения мы не убили поток,но останавливаем его полностью
                        set_finished();
                    else //если убили,то устанавливаем значение finished и на следующей итерации поток закончится
                        finished=true;

                } catch (InterruptedException e)
                {
                    System.out.println("Thread has been interrupted");
                }
            } else
            {
                Thread.yield();
            }
        }
        driver.quit();
        System.out.printf("%s fiished... \n", taskJson.id);
    }
}

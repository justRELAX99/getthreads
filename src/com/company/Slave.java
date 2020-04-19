package com.company;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Slave extends Thread
{
    JsonHandler taskJson;

    private String old_url="";
    private boolean is_active;
    private boolean finished;
    private Object LOCK_THREAD = new Object();

    Slave(JsonHandler taskJson)
    {
        is_active = true;
        finished = false;
        this.taskJson = taskJson;
    }

    boolean get_finished()
    {
        return finished;
    }

    void set_unfinished() throws InterruptedException
    {

        finished = false;
        synchronized (LOCK_THREAD)
        {
            LOCK_THREAD.notify();
        }
    }

    void set_finished() throws InterruptedException
    {
        finished = true;
        synchronized (LOCK_THREAD)
        {
            LOCK_THREAD.wait();
        }
    }

    boolean get_active()
    {
        return is_active;
    }

    void disable()
    {
        is_active = false;
    }

    synchronized void save_cookies(Set<Cookie> cookies, String cookies_path)
    {
        File file = new File(cookies_path);
        try
        {
            // Delete old file if exists
            file.delete();
            file.createNewFile();
            FileWriter fileWrite = new FileWriter(file);
            BufferedWriter Bwrite = new BufferedWriter(fileWrite);
            // loop for getting the cookie information
            for (Cookie ck : cookies)
            {
                Bwrite.write((ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";" + ck.getExpiry() + ";" + ck.isSecure()));
                Bwrite.newLine();
            }
            Bwrite.close();
            fileWrite.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    synchronized ArrayList<Cookie> get_cookies(String cookies_path) throws IOException
    {
        ArrayList<Cookie> new_cookies = new ArrayList<>();
        try
        {
            File file = new File(cookies_path);
            FileReader fileReader = new FileReader(file);
            BufferedReader Buffreader = new BufferedReader(fileReader);

            String strline;
            while ((strline = Buffreader.readLine()) != null)
            {
                StringTokenizer token = new StringTokenizer(strline, ";");

                while (token.hasMoreTokens())
                {
                    String name = token.nextToken();
                    String value = token.nextToken();
                    String domain = token.nextToken();
                    String path = token.nextToken();
                    Date expiry = null;

                    String val;
                    if (!(val = token.nextToken()).equals("null"))
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        expiry = dateFormat.parse(val);
                    }

                    Boolean isSecure = Boolean.valueOf(token.nextToken());
                    Cookie ck = new Cookie(name, value, domain, path, expiry, isSecure);
                    new_cookies.add(ck);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new_cookies;
    }

    ChromeDriver get_browser(String string_proxy, String string_user_agent, String cookies_path)
    {
        ChromeOptions options = new ChromeOptions();

        if ((string_proxy != null) && (string_proxy.length() != 0))
        {
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(string_proxy);
            options.setCapability("proxy", proxy);
        }
        if ((string_user_agent != null) && (string_user_agent.length() != 0))
        {
            options.addArguments("user-agent=" + string_user_agent);
        }
        ChromeDriver driver = new ChromeDriver(options);
        return driver;
    }

    void execute_task(ChromeDriver driver)
    {
        try
        {
            if(old_url.length()!=0)//проверять,если юрл изменился,то переходить
            {
                old_url=taskJson.url;
                driver.get(taskJson.url);
            }
            else
            {
                if(old_url!=taskJson.url)
                {
                    old_url=taskJson.url;
                    driver.get(taskJson.url);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Не удалось перейти по адресу,id="+taskJson.id);
        }

        try
        {
            if ((taskJson.js1 != null) && (taskJson.js1.length() != 0))
                driver.executeScript(taskJson.js1);
        }
        catch (Exception e)
        {
            System.out.println("js1 не выолнился,id=" + taskJson.id);
        }

        if (taskJson.time_js1 != 0)
        {
            try
            {
                Thread.sleep(taskJson.time_js1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            if ((taskJson.js2 != null) && (taskJson.js2.length() != 0))
                driver.executeScript(taskJson.js2);
        }
        catch (Exception e)
        {
            System.out.println("js2 не выолнился,id=" + taskJson.id);
        }

        if (taskJson.time_js2 != 0)
        {
            try
            {
                Thread.sleep(taskJson.time_js2);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    void get_cookies_in_file(ChromeDriver driver, String cookies_path) throws InterruptedException
    {
        driver.get("http://vk.com/");
        Thread.sleep(10000);
        driver.executeScript("document.getElementById('index_email').value = 'monstr541@yandex.ru' " + "\n" +
                "document.getElementById('index_pass').value = 'bzr7quj991331AA'" + "\n" +
                "document.getElementById('index_login_button').click()");
        Thread.sleep(10000);
        save_cookies(driver.manage().getCookies(), cookies_path);
        driver.get("http://vk.com/");
    }

    void set_cookie_from_file(ChromeDriver driver, String cookies_path) throws IOException, InterruptedException
    {

        driver.get("http://vk.com/");
        ArrayList<Cookie> cookies = get_cookies(cookies_path);
        for (Cookie cookie : cookies)
        {
            System.out.println(cookie);
            driver.manage().addCookie(cookie);
        }
        driver.get("http://vk.com/");
        Thread.sleep(10000);
    }

    public void run()
    {
        System.out.printf("%s started... \n", taskJson.id);
        ChromeDriver driver;
        String proxy = "";
        String user_agent = "";
        String cookies_path = "Cookies.data";
        //driver=get_browser(proxy,user_agent,cookies_path);
        driver = get_browser(taskJson.proxy, taskJson.user_agent, taskJson.cookies_path);
        while (is_active)
        {
            if (!finished)
            {
                try
                {
                    System.out.printf("%s started... \n", taskJson.id);
                    execute_task(driver);
                    if (is_active)//если во время выполнения мы не убили поток,но останавливаем его полностью
                    {
                        synchronized (Main.LOCK_MAIN)
                        {
                            Main.LOCK_MAIN.notify();
                        }
                        set_finished();
                    } else //если убили,то устанавливаем значение finished и на следующей итерации поток закончится
                        finished = true;
                }
                catch (InterruptedException e)
                {
                        System.out.println("Thread был прерван,id="+taskJson.id);
                }
            }
        }


        driver.quit();
        System.out.printf("%s fiished... \n", taskJson.id);
    }
}
